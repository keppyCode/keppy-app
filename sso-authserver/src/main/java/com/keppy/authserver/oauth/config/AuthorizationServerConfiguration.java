package com.keppy.authserver.oauth.config;

import com.keppy.authserver.oauth.serverice.BaseClientDetailService;
import com.keppy.authserver.oauth.serverice.MyUserDetailsService;
import com.keppy.authserver.oauth.serverice.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

/**
 * 授权认证服务配置 AuthorizationServerConfiguration
 * @ author liuqiuping
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Resource
    private DataSource dataSource;

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;
    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private RedisConnectionFactory connectionFactory;

    @Autowired
    private TokenService tokenService;

    /**
     * 用来配置令牌端点(Token Endpoint)的安全约束.
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //enable client to get the authenticated when using the /oauth/token to get a access token
        //there is a 401 authentication is required if it doesn't allow form authentication for clients when access /oauth/token
        //security.checkTokenAccess("permitAll()").tokenKeyAccess("permitAll()").allowFormAuthenticationForClients();
        security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    /**
     * 客户端认证配置
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //这里通过实现 ClientDetailsService接口
        clients.withClientDetails(new BaseClientDetailService());
//        //super.configure(clients);
//        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        String secret = passwordEncoder.encode("secret");
//        //clients.jdbc()
//        // 使用in-memory存储
//        clients.inMemory()
//                // client_id
//                .withClient("client")
//                // client_secret
//                .secret(secret)
//                .redirectUris(redirectUris)
//                //如果为true　则不会跳转到授权页面，而是直接同意授权返回code
//                //.autoApprove(true)　　
//                // 该client允许的授权类型　authorization_code,password,refresh_token,client_credentials
//                .authorizedGrantTypes("authorization_code","password","refresh_token","client_credentials")
//                // 允许的授权范围
//                .scopes("app")
//                //Access token is only valid for 2 minutes.
//                .accessTokenValiditySeconds(120)
//                //Refresh token is only valid for 10 minutes.
//                .refreshTokenValiditySeconds(600);

    }

    /**
     * 缓存授权码 serverice 实例化
     * @return
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new RedisAuthenticationCodeServices(connectionFactory);
    }

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {


        endpoints.authenticationManager(authenticationManager).userDetailsService(userDetailsService)
                .accessTokenConverter(accessTokenConverter())
                //支持GET  POST  请求获取token;
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);

//        endpoints.authenticationManager(authenticationManager).userDetailsService(userDetailsService)
//                .accessTokenConverter(accessTokenConverter())
//                .authorizationCodeServices(authorizationCodeServices())
//                //支持GET  POST  请求获取token;
//                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST,HttpMethod.OPTIONS);
//        //设置缓存token 授权类型
//        tokenService.setTokenGranter(endpoints.getTokenGranter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter() {
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                String userName = authentication.getUserAuthentication().getName();
                final Map<String, Object> additionalInformation = new HashMap<String, Object>();
                additionalInformation.put("user_name", userName);
                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
                OAuth2AccessToken token = super.enhance(accessToken, authentication);
                return token;
            }
        };
        //converter.setSigningKey("bcrypt");
//        KeyPair keyPair = new KeyStoreKeyFactory(new ClassPathResource("kevin_key.jks"), "123456".toCharArray())
//                .getKeyPair("kevin_key");
//        converter.setKeyPair(keyPair);
        return converter;
    }
}
