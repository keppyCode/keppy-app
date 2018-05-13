package com.qunyi.commom.config.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

public class DefaultUsernamepasswordToken extends UsernamePasswordToken {

    /**
     * 判断登录类型
     */
    private String loginType;

    /**
     * 登录验证码
     */
    private String captchaCode;

    /**
     * 公司代码
     */
    private String companyCode;

    /**
     * 自定义令牌
     */
    private String ticket;

    /**
     * Shiro 构造方法
     *
     * @author : tanzhen
     * @date ：2016年7月28日
     */
    public DefaultUsernamepasswordToken(String username, String password) {
        super(username, password);
    }

    /**
     * Shiro 构造方法
     *
     * @author : tanzhen
     * @date ：2016年7月28日
     */
    public DefaultUsernamepasswordToken(String username, String password,String loginType,boolean remember) {
        super(username, password,remember);
        this.setLoginType(loginType);
    }

    public DefaultUsernamepasswordToken() {

    }


    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getCaptchaCode() {
        return captchaCode;
    }

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
