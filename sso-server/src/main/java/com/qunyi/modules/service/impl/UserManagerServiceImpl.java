package com.qunyi.modules.service.impl;

import com.qunyi.commom.config.dataSource.DS;
import com.qunyi.modules.dao.RoleMapper;
import com.qunyi.modules.dao.UserMapper;
import com.qunyi.modules.model.User;
import com.qunyi.modules.service.UserManagerService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 
 * 
 *  @author liuqiuping
 *
 */
@Service
public class UserManagerServiceImpl implements UserManagerService {

	private final static Logger lOGGER=LoggerFactory.getLogger(UserManagerServiceImpl.class);
	@Resource
	UserMapper userMapper;
	@Resource
	RoleMapper roleMapper;

	@DS("Master")
	@Override
	public User getUserInfo(String loginName, String password){
		
		return userMapper.getUserInfo(loginName);
	}

	/**
	 * 使用数据源Master
	 * @param name
	 * @return
	 */
	@DS("Masterdb")
	@Override
	public User findByLoginName(String name) {
		User user = userMapper.findByLoginName(name);
		if(null!=user){
			user.setRoles(roleMapper.selectByUserId(user.getId()));
		}

		return user;
	}

	/**
	 * 使用数据源Cluster
	 * @param loginName
	 * @return
	 */
	@DS("Clusterdb")
	@Override
	public User findByLoginNameTest(String loginName) {
		return userMapper.findByLoginNameTest(loginName);
	}


	@DS("Masterdb")
	@Override
	public boolean authorization(String userName,String passWord,boolean rememberMe) {

		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(userName, passWord);
		usernamePasswordToken.setRememberMe(rememberMe);
		boolean isAuthenticated = false;
		try {
			//登录认证
			subject.login(usernamePasswordToken);
			//授权
			//boolean roleadmin = subject.hasRole("ROLE_ADMIN");
			isAuthenticated = subject.isAuthenticated();
			lOGGER.info("登录认证成功，userName账号：[{}],passWord密码：[{}]，rememberMe记住用户可选参数值：[{}],登录认证[{}]",userName,passWord,rememberMe,isAuthenticated);
		}catch (AuthenticationException e){
			e.printStackTrace();
			lOGGER.info("登录认证失败！，userName账号：[{}],passWord密码：[{}]，rememberMe记住用户可选参数值：[{}]",userName,passWord,rememberMe);
			return isAuthenticated;
		}

		return isAuthenticated;
	}


}
