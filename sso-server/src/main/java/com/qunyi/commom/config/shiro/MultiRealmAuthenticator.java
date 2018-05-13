package com.qunyi.commom.config.shiro;

import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 重写多个认证realam处理器，自定义认证器
 * @author liuqiuping
 */
public class MultiRealmAuthenticator extends ModularRealmAuthenticator {


    /**
     * 存放realm
     */
    private Map<String, Object> definedRealms = new HashMap<>();


    /**
     * 多个realm实现
     */
    @Override
    protected AuthenticationInfo doMultiRealmAuthentication(
            Collection<Realm> realms, AuthenticationToken token) {
        return super.doMultiRealmAuthentication(realms, token);
    }


    @Override
    protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken) throws AuthenticationException {

        super.assertRealmsConfigured();
        Collection<Realm> realms = getRealms();
        Realm realm = null;
        // 使用自定义Token
        DefaultUsernamepasswordToken token = (DefaultUsernamepasswordToken) authenticationToken;

       if(definedRealms.isEmpty()){

           for (Realm SuperRealm : realms) {
               if(SuperRealm.getClass().getName().indexOf("ShiroRealm")!=-1){
                   definedRealms.put("shiroRealm",SuperRealm);
               }else{
                   definedRealms.put("ladpRealm",SuperRealm);
               }
           }
       }

        if(null==token.getLoginType()||"".equals(token.getLoginType())){
            token.setLoginType("1");
        }


        // 判断用户类型
        if ("1".equals(token.getLoginType())) {

            realm = (Realm) this.definedRealms.get("shiroRealm");
        } else if ("2".equals(token.getLoginType())) {

            realm = (Realm) this.definedRealms.get("ladpRealm");
        }
        return this.doSingleRealmAuthentication(realm, authenticationToken);
    }




    public Map<String, Object> getDefinedRealms() {
        return this.definedRealms;
    }

    public void setDefinedRealms(Map<String, Object> definedRealms) {
        this.definedRealms = definedRealms;
    }
}
