package com.mpm.foundation.authserver;

import java.io.IOException;
import java.security.Principal;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.mpm.foundation.authserver.cfg.Constants;
import com.mpm.foundation.authserver.security.bean.MpmUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.mpm.foundation.authserver.security.crypto.password.MpmPasswordEncoder;
import com.mpm.foundation.authserver.security.oauth2.provider.code.RedisAuthenticationCodeServices;
import com.mpm.foundation.authserver.security.oauth2.provider.token.MpmTokenEnhancer;
import com.mpm.foundation.authserver.service.MpmTokenService;

@SpringBootApplication
@Controller
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 7200)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Value("${mpm.portalUri}")
	private String portalUri;

	/**
	 * 默认路由跳转配置
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "/")
	public String index(Principal principal) {
		String redirectUrl = portalUri;
		if(principal instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken token  = (UsernamePasswordAuthenticationToken)principal;
			Object obj = token.getPrincipal();
			MpmUser user = (MpmUser)obj;
			if(Constants.BELONGTO_ENTERPRISE.equals(user.getBelongto())){
				redirectUrl = user.getProfile().getUrl();
			}
		}
		return "redirect:" + redirectUrl;
	}

	@RequestMapping(value = "/login")
	public String login() {
		return "login";
	}

	@Bean
	public CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedMethod("*");
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(urlBasedCorsConfigurationSource);
	}

	@Configuration
	@EnableAuthorizationServer
	protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {
		@Resource
		private DataSource dataSource;
		@Autowired
		@Qualifier("authenticationManagerBean")
		private AuthenticationManager authenticationManager;
		@Autowired
		private RedisConnectionFactory connectionFactory;
		@Autowired
		private MpmTokenService mpmTokenService;

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			// 使用JdbcClientDetailsService客户端详情服务
			clients.jdbc(dataSource);
		}


		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.authenticationManager(authenticationManager)// .tokenServices(tokenServices())
					.tokenStore(tokenStore()).tokenEnhancer(tokenEnhancer()).reuseRefreshTokens(false)
					// .approvalStore(approvalStore()).
					.authorizationCodeServices(authorizationCodeServices());
			mpmTokenService.setTokenGranter(endpoints.getTokenGranter());
		}

		@Bean
		public TokenEnhancer tokenEnhancer() {
			return new MpmTokenEnhancer();
		}

		@Bean
		public AuthorizationCodeServices authorizationCodeServices() {
			return new RedisAuthenticationCodeServices(connectionFactory);
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
			// 开启/oauth/token_key验证端口无权限访问
			// 开启/oauth/check_token验证端口认证权限访问
			security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
		}

		// @Bean
		// public ApprovalStore approvalStore() {
		// return null;
		// }

		@Bean
		@Primary
		public DefaultTokenServices tokenServices() {
			DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
			// defaultTokenServices.setAuthenticationManager(authenticationManager);
			defaultTokenServices.setTokenStore(tokenStore());
			defaultTokenServices.setSupportRefreshToken(true);
			defaultTokenServices.setReuseRefreshToken(false);
			defaultTokenServices.setAccessTokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(2)); // 30天
			defaultTokenServices.setRefreshTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(1));
			defaultTokenServices.setTokenEnhancer(tokenEnhancer());
			return defaultTokenServices;
		}

		@Bean
		public TokenStore tokenStore() {
			return new RedisTokenStore(connectionFactory);
		}

	}

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfig extends ResourceServerConfigurerAdapter {
		@Autowired
		private ResourceServerTokenServices tokenServices;
		public static final String RESOURCE_ID = "api";

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
			resources.resourceId(RESOURCE_ID).tokenServices(tokenServices);
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.requestMatchers().antMatchers("/api/**").and().authorizeRequests().anyRequest().authenticated();
		}
	}

	@Configuration
	public static class WebConfig extends WebMvcConfigurerAdapter {

		/**
		 * 通过cors协议解决跨域问题
		 * 
		 * @param registry
		 */
		@Override
		public void addCorsMappings(CorsRegistry registry) {
			registry.addMapping("/**") // **代表所有路径
					.allowedOrigins("*") // allowOrigin指可以通过的ip，*代表所有，可以使用指定的ip，多个的话可以用逗号分隔，默认为*
					.allowedMethods("GET", "POST", "HEAD", "PUT", "DELETE", "OPTIONS") // 指请求方式 默认为*
					.allowCredentials(false) // 支持证书，默认为true
					.maxAge(3600) // 最大过期时间，默认为-1
					.allowedHeaders("*");
		}
	}

	@Configuration
	@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
	// @Order(-1)
	protected static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
		@Autowired
		private UserDetailsService userDetailsService;

		private static final String[] AUTH_LIST = {
				// -- swagger ui
				"**/swagger-resources/**", "/swagger-ui.html", "/v2/api-docs", "/webjars/**", "/img/**", "/css/**",
				"/js/**", "/kaptcha/**", "/keyword/**", "/valid/**", "/workweixin/**", "/mpm/**" };

		@Autowired
		public void globalUserDetails(final AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailsService).passwordEncoder(new MpmPasswordEncoder());
		}

		@Override
		@Bean
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
		}

		@Override
		protected void configure(final HttpSecurity http) throws Exception {
			http.headers().frameOptions().disable().and().cors().and().csrf().disable();
			http.authorizeRequests().antMatchers(HttpMethod.OPTIONS, "**").permitAll();
			// http.requestMatchers().antMatchers(HttpMethod.OPTIONS,
			// "/oauth/token").and().cors().and().csrf().disable();
			http.authorizeRequests().antMatchers(AUTH_LIST).permitAll().anyRequest().authenticated();
			http.formLogin().loginPage("/login").permitAll().and().logout().logoutUrl("/logout")
					.deleteCookies("JSESSIONID").addLogoutHandler(new LogoutHandler() {

						@Override
						public void logout(HttpServletRequest request, HttpServletResponse response,
								Authentication authentication) {
							String userid = null;
							String url = request.getParameter("redirect_uri");
							if (authentication != null) {
								userid = authentication.getName();
							} else if (SecurityContextHolder.getContext().getAuthentication() != null) {
								UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
										.getAuthentication().getPrincipal();
								userid = userDetails.getUsername();
							}

//							Enumeration em = request.getSession().getAttributeNames();
//							while (em.hasMoreElements()) {
//								request.getSession().removeAttribute(em.nextElement().toString());
//							}
							request.getSession().invalidate();
							SecurityContext context = SecurityContextHolder.getContext();
							context.setAuthentication(null);
							SecurityContextHolder.clearContext();
							Cookie[] cookies = request.getCookies();
							if (cookies != null) {
								for (Cookie ck : cookies) {
									ck.setMaxAge(0);
									ck.setValue(null);
									response.addCookie(ck);
								}
							}
							if (StringUtils.isNotBlank(url)) {
								try {
									response.sendRedirect(url);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}).permitAll();

		}

	}

}
