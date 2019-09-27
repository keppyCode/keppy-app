package com.mpm.foundation.authserver.security.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;

@ApiModel(value = "用户")
@JsonIgnoreProperties({ "authorities"})
public class MpmUser extends User {
	@ApiModelProperty(value = "id")
	@JSONField(name = "user_id")
	private String id;
	@ApiModelProperty(value = "用户名")
	@JSONField(name = "name")
	private String name;
	@ApiModelProperty(value = "登录类型")
	@JSONField(name = "login_type")
	private String logintype;
	@ApiModelProperty(value = "头像")
	@JSONField(name = "face")
	private String face;
	@ApiModelProperty(value = "公司编码")
	@JSONField(name = "company_code")
	private String companycode;
	@ApiModelProperty(value = "登录名")
	@JSONField(name = "login_name")
	private String loginname;
	@ApiModelProperty(value = "部署环境")
	private DeployProfile profile;

	@ApiModelProperty(value = "归属1 mpm2 社区版3 社区版和mpm兼有")
	private String belongto;

	@ApiModelProperty(value = "手机号")
	private String mobile;

	@ApiModelProperty(value = "账号(手机和邮箱，针对互联网模式登录)是否验证0没有验证1已验证")
	private Integer isValidated;

	@ApiModelProperty(value = "手机号是否关联验证,0没 有验证,1已验证(针对企业登录)")
	private Integer isRelated;

	@ApiModelProperty(value = "用户中心url，com:mpm2.5,info：通用积分制url")
	private String usercenterUrl;


	public String getBelongto() {
		return belongto;
	}

	public void setBelongto(String belongto) {
		this.belongto = belongto;
	}

	public DeployProfile getProfile() {
		return profile;
	}

	public void setProfile(DeployProfile profile) {
		this.profile = profile;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public MpmUser() {
		this("-", "", "", true, true, true, true, new ArrayList<>());
	}

	public MpmUser(String username, String password, String name, Collection<? extends GrantedAuthority> authorities) {
		this(username, password, name, true, true, true, true, authorities);
	}

	public MpmUser(String username, String password, String name, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String username) {
		this.name = username;
	}

	public String getLogintype() {
		return logintype;
	}

	public void setLogintype(String logintype) {
		this.logintype = logintype;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	public String getCompanycode() {
		return companycode;
	}

	public void setCompanycode(String companycode) {
		this.companycode = companycode;
	}

	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Integer getIsValidated() {
		return isValidated;
	}

	public void setIsValidated(Integer isValidated) {
		this.isValidated = isValidated;
	}

	public Integer getIsRelated() {
		return isRelated;
	}

	public void setIsRelated(Integer isRelated) {
		this.isRelated = isRelated;
	}

	public String getUsercenterUrl() {
		return usercenterUrl;
	}

	public void setUsercenterUrl(String usercenterUrl) {
		this.usercenterUrl = usercenterUrl;
	}
}
