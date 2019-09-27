package com.mpm.foundation.authserver.security.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "企业用户")
public class MpmEmdmUser extends MpmUser {
	@ApiModelProperty(value = "公司id")
	private String companyId;
	@ApiModelProperty(value = "企业员工名字")
	private String emname;
	@ApiModelProperty(value = "部门id")
	private String departmentId;
	@ApiModelProperty(value = "部门名称")
	private String departmentName;
	@ApiModelProperty(value = "工号")
	private String empNum;
	@ApiModelProperty(value = "员工id,慎用")
	private String employeeId;

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getEmpNum() {
		return empNum;
	}

	public void setEmpNum(String empNum) {
		this.empNum = empNum;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmname() {
		return emname;
	}

	public void setEmname(String emname) {
		this.emname = emname;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

}
