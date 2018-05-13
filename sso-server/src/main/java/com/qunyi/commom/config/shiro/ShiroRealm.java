package com.qunyi.commom.config.shiro;

import com.qunyi.modules.model.Role;
import com.qunyi.modules.model.User;
import com.qunyi.modules.service.UserManagerService;
import com.qunyi.modules.utils.CommonUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限分配实现类
 * @author liuqiuping
 *
 */

public class ShiroRealm extends AuthorizingRealm {

    private Logger logger =  LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserManagerService userManagerService;


    /**
     * 添加角色
     * @param username
     * @param info
     */
   /* private void addRole(String username, SimpleAuthorizationInfo info) {
        List<Role> roles = roleDao.findByUser(username);
        if(roles!=null&&roles.size()>0){
            for (Role role : roles) {
                info.addRole(role.getRoleName());
            }
        }
    }*/

    /**
     * 添加权限
     * @param username
     * @param info
     * @return
     */
   /* private SimpleAuthorizationInfo addPermission(String username,SimpleAuthorizationInfo info) {
        List<Permission> permissions = permissionDao.findPermissionByName(username);
        for (Permission permission : permissions) {
            info.addStringPermission(permission.getUrl());//添加权限
        }
        return info;
    }*/



    /**
     * 重写shiro授权执行方法,授权方式有三种【只要调用其中任何一种，就会执行doGetAuthorizationInfo方法】
     * 1.基于角色授权表，调用subject.hasRole("role1")或subject.hasAllRoles(Arrays.asList("role1", "role2", "role3"));方法
     * 2.基于资源授权，调用subject.isPermittedAll("user:create:1", "user:delete");方法
     * 3.使用check方法进行授权，调用subject.checkPermission("items:create:1");如果授权不通过会抛出异常
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        logger.info("doGetAuthorizationInfo+"+principalCollection.toString());

        Subject subject = SecurityUtils.getSubject();
        //获取用户与角色信息（服务器端暂时用session缓存用户权限等相关信息，可以每次从数据库查询权限）
        //User user = userManagerService.findByLoginName((String) principalCollection.getPrimaryPrincipal());
        User user = (User)SecurityUtils.getSubject().getSession().getAttribute("user");

        //把principals放session中 key=userId value=principals
        SecurityUtils.getSubject().getSession().setAttribute(String.valueOf(user.getId()),SecurityUtils.getSubject().getPrincipals());

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        List<String> roleIds =  new ArrayList<String>();
        for(Role rol:user.getRoles()){
            //赋予角色
            info.addRole(rol.getRoleType());
            roleIds.add(rol.getId());
        }

        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        DefaultUsernamepasswordToken token = (DefaultUsernamepasswordToken) authenticationToken;

        logger.info("ShiroRealm认证》》认证名：[{}],认证密码：[{}],认证类型[{}]MD5认证",token.getUsername(),token.getPassword(),token.getLoginType());

        if(!CommonUtil.isObjNullOrEmp(token.getUsername())){
            //本地库验证
            User user = userManagerService.findByLoginName(token.getUsername());

            if (user != null){
                //存放session
                SecurityUtils.getSubject().getSession().setAttribute("user", user);
                //让shiro框架去验证账号密码
                return new SimpleAuthenticationInfo(token.getUsername(),user.getPassWord(),getName());
            }
        }
         return new SimpleAuthenticationInfo();
    }

}