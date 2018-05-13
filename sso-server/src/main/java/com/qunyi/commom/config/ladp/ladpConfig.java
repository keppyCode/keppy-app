package com.qunyi.commom.config.ladp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * ladp配置
 */
@Configuration
public class ladpConfig {

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

    /*
     *SpringLdap的javaConfig注入方式
     */
    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl(ldapUrl);
        ldapContextSource.setBase(ldapBase);
        ldapContextSource.setUserDn(ldapUserDn);
        ldapContextSource.setPassword(ldapUserPwd);
        ldapContextSource.setReferral(ldapReferral);
        return ldapContextSource;
    }




    @Bean("ldapTemplate")
    public LdapTemplate ldapTemplate(ContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }







}
