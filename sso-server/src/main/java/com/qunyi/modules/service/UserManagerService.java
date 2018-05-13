package com.qunyi.modules.service;


import com.qunyi.modules.model.User;

/**
 * @author liuqiuping
 */
public interface UserManagerService {

	/**
	 * 通过用户名与密码查询用户对象
	 * @param loginName
	 * @param password
	 * @return
	 */

	User getUserInfo(String loginName, String password);

	/**
	 * 通过用户名查询用户对象
	 * @param name
	 * @return
	 */
	User findByLoginName(String name);


    /**
     * 授权处理
     */
	boolean authorization(String userName, String passWord, boolean rememberMe);

	/**
	 * 测试
	 * @param loginName
	 * @return
	 */
	User findByLoginNameTest(String loginName);


}
