package com.keppy.authserver.oauth.config;

import com.keppy.authserver.oauth.model.Oauth2User;
import com.keppy.authserver.oauth.serverice.BaseClientDetailService;
import com.keppy.authserver.oauth.serverice.BaseUserDetailsService;
import com.keppy.authserver.oauth.serverice.Oauth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    BaseClientDetailService baseClientDetailService;
    @Autowired
    BaseUserDetailsService baseUserDetailsService;
    @Autowired
    RedisConnectionFactory redisConnectionFactory;
    @Autowired
    MyPasswordEncoder myPasswordEncoder;

    /**
     * 注册一个AuthenticationManager用来password模式下用户身份认证
     * 直接使用上面注册的UserDetailsService来完成用户身份认证
     * @return
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(baseUserDetailsService);
        provider.setPasswordEncoder(myPasswordEncoder);
        return new ProviderManager(Collections.singletonList(provider));
    }

    /**
     * 注册一个TokenStore以保存token信息
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }


    /**
     * 缓存授权码 serverice 实例化
     * 注册一个AuthorizationCodeServices以保存authorization_code的授权码code
     * @return
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        RedisTemplate<String, OAuth2Authentication> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        return new RandomValueAuthorizationCodeServices() {
            @Override
            protected void store(String code, OAuth2Authentication authentication) {
                redisTemplate.boundValueOps(code).set(authentication, 10, TimeUnit.MINUTES);
            }
            @Override
            protected OAuth2Authentication remove(String code) {
                OAuth2Authentication authentication = redisTemplate.boundValueOps(code).get();
                redisTemplate.delete(code);
                return authentication;
            }
        };
    }


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
        security.checkTokenAccess("permitAll()").tokenKeyAccess("isAuthenticated()").allowFormAuthenticationForClients();;
}

    /**
     * 客户端认证配置
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //这里通过实现 ClientDetailsService接口
        clients.withClientDetails(baseClientDetailService);
    }

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        endpoints.userDetailsService(baseUserDetailsService);
        endpoints.tokenStore(tokenStore());
        endpoints.authorizationCodeServices(authorizationCodeServices());
        endpoints.authenticationManager(authenticationManager());
    }


}
