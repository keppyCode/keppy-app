package com.mpm.foundation.authserver.cfg;

/**
 * 认证常量类
 * 
 * @author zhangzhongmin
 *
 */
public class Constants {

	public static final String USER_INFO = "USER";

	/**
	 * 企业模式登录
	 */
	public static final String LOGIN_TYPE_ENTERPRISE = "enterprise";
	/**
	 * 本地模式登录
	 */
	public static final String LOGIN_TYPE_LOCAL = "local";
	/**
	 * 第三方模式登录
	 */
	public static final String LOGIN_TYPE_THIRD_PARTY = "third_party";
	/**
	 * 企业模式登录
	 */
	public static final String LOGIN_TYPE_WORKWEIXIN = "workweixin";
	/**
	 * 企业状态可用值
	 */
	public static final int COMPANY_STATE_AVAILABLE = 1;
	
	
	/**
	 * 登录名类型1 email 2mobile 3name(一般对于admin的配置)
	 */
	public static final int LOGIN_NAME_TYPE_MOBILE = 1;
	public static final int LOGIN_NAME_TYPE_EMAIL = 2;
	public static final int LOGIN_NAME_TYPE_NAME = 3;


	/**
	 *路由归属1 mpm企业版
	 */
	public static final String BELONGTO_ENTERPRISE = "1";

	/**
	 *路由归属,2 通用版
	 */
	public static final String BELONGTO_COMMON = "2";

	/**
	 *路由归属,两者兼有
	 */
	public static final String BELONGTO_BOTH= "3";



    /**
     *企业登录是否关联手机号，0未关联，1已经关联
     */
    public static final int  ISRELATED = 1;

    /**
     *是否首次登录初始化密码，0未验证，1已经验证
     */
    public static final int  ISVALIDATED = 1;

}
