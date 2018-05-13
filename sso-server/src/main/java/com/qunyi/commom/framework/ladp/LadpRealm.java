package com.qunyi.commom.framework.ladp;

import com.qunyi.commom.config.shiro.DefaultUsernamepasswordToken;
import org.apache.shiro.authc.*;

import org.apache.shiro.ldap.UnsupportedAuthenticationMechanismException;

import org.apache.shiro.realm.ldap.JndiLdapContextFactory;
import org.apache.shiro.realm.ldap.JndiLdapRealm;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.realm.ldap.LdapUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;

import javax.naming.AuthenticationNotSupportedException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;


/**
 * ladp认证策略
 * @author liuqiuping
 *
 */

public class LadpRealm extends JndiLdapRealm{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String rootDN="dc=maxcrc,dc=com";

    @Autowired
    LdapTemplate ldapTemplate;

    public LadpRealm() {
        super();
    }

    public String getRootDN() {
        return rootDN;
    }

    public void setRootDN(String rootDN) {
        this.rootDN = rootDN;
    }

    @Override
    /***
     * 认证
     */
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken token) throws AuthenticationException {
        AuthenticationInfo info;
        try {
            info = queryForAuthenticationInfo(token, getContextFactory());
        } catch (AuthenticationNotSupportedException e) {
            String msg = "Unsupported configured authentication mechanism";
            throw new UnsupportedAuthenticationMechanismException(msg, e);
        } catch (NamingException e) {
            String msg = "LDAP naming error while attempting to authenticate user.";
            throw new AuthenticationException(msg, e);
        } catch (UnknownAccountException e) {
            String msg = "UnknownAccountException";
            throw new UnknownAccountException(msg, e);
        } catch (IncorrectCredentialsException e) {
            String msg = "IncorrectCredentialsException";
            throw new IncorrectCredentialsException(msg, e);
        }

        return info;
    }

    @Override
    protected AuthenticationInfo queryForAuthenticationInfo(
            AuthenticationToken authenticationToken, LdapContextFactory ldapContextFactory)
            throws NamingException {
        DefaultUsernamepasswordToken token = (DefaultUsernamepasswordToken) authenticationToken;
        Object principal = token.getPrincipal();
        Object credentials = token.getCredentials();

        logger.info("ladp认证》》认证名：[{}],认证密码：[{}],认证类型[{}]ladp认证",token.getUsername(),token.getPassword(),token.getLoginType());
        LdapContext systemCtx = null;
        LdapContext ctx = null;
        try {
            systemCtx = ldapContextFactory.getSystemLdapContext();

            SearchControls constraints = new SearchControls();

            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

            NamingEnumeration results = systemCtx.search(rootDN, "cn="
                    + principal, constraints);
            if (results != null && !results.hasMore()) {
                throw new UnknownAccountException();
            } else {
                while (results.hasMore()) {
                    SearchResult si = (SearchResult) results.next();
                    principal = si.getName() + "," + rootDN;
                }
                logger.info("DN=" + principal);
                try {
                    ctx = ldapContextFactory.getLdapContext(principal,
                            credentials);
                } catch (NamingException e) {
                    throw new IncorrectCredentialsException();
                }
                return createAuthenticationInfo(token, principal, credentials,
                        ctx);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            LdapUtils.closeContext(systemCtx);
            LdapUtils.closeContext(ctx);
            return null;
        }

    }



}