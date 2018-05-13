package com.qunyi.modules.model;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.qunyi.modules.utils.CustomDateSerializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author liuqiuping
 */
public class User {

	private static final long serialVersionUID = -8284823303352539870L;


	public final static String TABLE_NAME="USER";                   //表名字

	public final static String LOGINNAME = "login_name";			//登录账号

	public final static String PASSWORD = "password";				//密码

	public final static String ID = "ID";

	private  String id;				//编号
	private String companyId;			//归属公司
	private String employeeId;		//员工ID，外键关联(sys_employee.id)
	private String loginName;			//登录名
	private  String passWord;			//密码
	private String name;				//姓名
	private String userType;			//用户类型
	private String loginIp;			//最后登陆IP
	private String loginDate;			//最后登陆时间
	private String loginFlag;			//是否可登录
	private String remarks;			//备注信息
	private String createBy;			//创建者
	private Date createDate;			//创建时间
	private String updateBy;			//更新者
	private Date updateDate;			//更新时间
	private String delFlag;			//删除标记

	private String loginType;			//登录类型


	List<Role> roles = new ArrayList<Role>();

	public User() {
	}

	public User(String id, String companyId, String employeeId, String loginName, String passWord,
                String name, String userType, String loginIp, String loginDate, String loginFlag,
                String remarks, String createBy, Date createDate, String updateBy, Date updateDate, String delFlag) {
		this.id = id;
		this.companyId = companyId;
		this.employeeId = employeeId;
		this.loginName = loginName;
		this.passWord = passWord;
		this.name = name;
		this.userType = userType;
		this.loginIp = loginIp;
		this.loginDate = loginDate;
		this.loginFlag = loginFlag;
		this.remarks = remarks;
		this.createBy = createBy;
		this.createDate = createDate;
		this.updateBy = updateBy;
		this.updateDate = updateDate;
		this.delFlag = delFlag;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	public String getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(String loginDate) {
		this.loginDate = loginDate;
	}

	public String getLoginFlag() {
		return loginFlag;
	}

	public void setLoginFlag(String loginFlag) {
		this.loginFlag = loginFlag;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}


	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}


	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
}
