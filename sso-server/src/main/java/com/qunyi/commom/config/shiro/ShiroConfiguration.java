package com.qunyi.commom.config.shiro;

import com.qunyi.commom.framework.ladp.LadpRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.ldap.JndiLdapContextFactory;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.naming.Context;
import javax.servlet.Filter;
import java.util.*;

/**
 * shiro配置类
 * @author liuqiuping
 * @date 2018-4-15
 */
@Configuration
public class ShiroConfiguration{

    @Value("${ldap.contextSource.url}")
    private String ldapUrl;

    @Value("${ldap.contextSource.base}")
    private String ldapBase;

    @Value("${ldap.contextSource.userDn}")
    private String ldapUserDn;

    @Value("${ldap.contextSource.userPwd}")
    private String ldapUserPwd;

    @Value("${ldap.contextSource.referral}")
    private String ldapReferral;

    private Map<String, Object> env = new HashMap();

    /**
     * LifecycleBeanPostProcessor，这是个DestructionAwareBeanPostProcessor的子类，
     * 负责org.apache.shiro.util.Initializable类型bean的生命周期的，初始化和销毁。
     * 主要是AuthorizingRealm类的子类，以及EhCacheManager类。
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * HashedCredentialsMatcher，这个类是为了对密码进行编码的，
     * 防止密码在数据库里明码保存，当然在登陆认证的时候，
     * 这个类也负责对form里输入的密码进行编码。
     */
    @Bean(name = "hashedCredentialsMatcher")
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName("MD5");
        credentialsMatcher.setHashIterations(2);
        credentialsMatcher.setStoredCredentialsHexEncoded(true);
        return credentialsMatcher;
    }

    /**ShiroRealm，这是个自定义的认证类，继承自AuthorizingRealm，
     * 负责用户的认证和权限的处理，可以参考JdbcRealm的实现。
     */
    @Bean(name = "shiroRealm")
    @DependsOn("lifecycleBeanPostProcessor")
    public ShiroRealm shiroRealm() {
        ShiroRealm realm = new ShiroRealm();
//        realm.setCredentialsMatcher(hashedCredentialsMatcher());
        return realm;
    }


    /**ShiroRealm，这是个自定义的认证类，继承自AuthorizingRealm，
     * 负责用户的认证和权限的处理，可以参考JdbcRealm的实现。
     */
    @Bean(name = "ladpRealm")
    @DependsOn("lifecycleBeanPostProcessor")
    public LadpRealm ladpRealm() {
        LadpRealm realm = new LadpRealm();

        JndiLdapContextFactory defultLdapContextFactory= new JndiLdapContextFactory();
        defultLdapContextFactory.setEnvironment(setEnvironment());
        realm.setContextFactory(defultLdapContextFactory);
//        realm.setCredentialsMatcher(hashedCredentialsMatcher());
        return realm;
    }




//    /**
//     * EhCacheManager，缓存管理，用户登陆成功后，把用户信息和权限信息缓存起来，
//     * 然后每次用户请求时，放入用户的session中，如果不设置这个bean，每个请求都会查询一次数据库。
//     */
//    @Bean(name = "ehCacheManager")
//    @DependsOn("lifecycleBeanPostProcessor")
//    public EhCacheManager ehCacheManager() {
//        return new EhCacheManager();
//    }

    /**
     * 系统自带的Realm管理，主要针对多realm，只要一个成功就表示登录成功
     * */
    @Bean
    public ModularRealmAuthenticator modularRealmAuthenticator(){
        //自己重写的ModularRealmAuthenticator
        MultiRealmAuthenticator modularRealmAuthenticator = new MultiRealmAuthenticator();
        modularRealmAuthenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
        return modularRealmAuthenticator;
    }



    /**
     * SecurityManager，权限管理，这个类组合了登陆，登出，权限，session的处理，是个比较重要的类。
     //     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //启动多realm认证
        securityManager.setAuthenticator(modularRealmAuthenticator());
        //添加shiroRealm集合
        List<Realm> realmList = new ArrayList<Realm>();
        realmList.add(shiroRealm());
        realmList.add(ladpRealm());
        //securityManager.setRealms();
//        securityManager.setCacheManager(ehCacheManager());
        securityManager.setRealms(realmList);

        return securityManager;
    }

    /**
     * ShiroFilterFactoryBean，是个factorybean，为了生成ShiroFilter。
     * 它主要保持了三项数据，securityManager，filters，filterChainDefinitionManager。
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager());

        Map<String, Filter> filters = new LinkedHashMap<>();
        LogoutFilter logoutFilter = new LogoutFilter();
        //设置登录跳转url
       shiroFilterFactoryBean.setLoginUrl("/login.html");
//        //设置登出跳转url
//        logoutFilter.setRedirectUrl("/loginAction");
//
//        shiroFilterFactoryBean.setFilters(filters);
        Map<String, String> filterChainDefinitionManager = new LinkedHashMap<String, String>();
//        //静态资源处理
        filterChainDefinitionManager.put("/loginAction","anon");
        filterChainDefinitionManager.put("/css/**","anon");
        filterChainDefinitionManager.put("/js/**","anon");
        filterChainDefinitionManager.put("/img/**","anon");
        filterChainDefinitionManager.put("/static/**","anon");
//        //swagger统一api接口管理资源处理
//        filterChainDefinitionManager.put("/swagger-ui.html","anon");//放行swagger URL
//        filterChainDefinitionManager.put("/webjars/**","anon");//放行swagger 相关公共资源
//        filterChainDefinitionManager.put("/swagger-resources/**","anon");//放行swagger 相关公共资源
//        filterChainDefinitionManager.put("/v2/**","anon");//放行swagger 相关公共资源

        //未认证处理
        shiroFilterFactoryBean.setUnauthorizedUrl("/401.html");
        //所有请求必须认证
        filterChainDefinitionManager.put("/**", "authc");//authc

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionManager);

        return shiroFilterFactoryBean;
    }

    /**
     * DefaultAdvisorAutoProxyCreator，Spring的一个bean，由Advisor决定对哪些类的方法进行AOP代理。
     */
    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
        defaultAAP.setProxyTargetClass(true);
        return defaultAAP;
    }

    /**
     * AuthorizationAttributeSourceAdvisor，shiro里实现的Advisor类，
     * 内部使用AopAllianceAnnotationsAuthorizingMethodInterceptor来拦截用以下注解的方法。
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor aASA = new AuthorizationAttributeSourceAdvisor();
        aASA.setSecurityManager(securityManager());
        return aASA;
    }




    public Map setEnvironment() {
        System.out.println("==========================ldapUrl:"+ldapUrl);
        //使用Sun的LDAP服务提供者
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        //指定Sun的文件系统服务提供者
        //env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.fscontext.RefFSContextFactory");
        //使用389端口，根辨别名是“o=qysm”，修改目录不需要认证
        env.put(Context.PROVIDER_URL, "ldap://localhost:389/o=qysm");

        env.put(Context.DNS_URL,"ldap://localhost:389/o=qysm");


        //安全访问用户
        env.put(Context.SECURITY_PRINCIPAL, "cn=Manager,dc=maxcrc,dc=com");

        //安全用户密码安全认证强度
        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        //安全访问密码
        env.put(Context.SECURITY_CREDENTIALS, "secret");

        env.put(Context.REFERRAL,"follow");


        return env;
    }


}