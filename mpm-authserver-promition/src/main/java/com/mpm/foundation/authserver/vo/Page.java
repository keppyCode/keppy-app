package com.mpm.foundation.authserver.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "分页模型")
public class Page<T> {
	@ApiModelProperty(value = "当前页")
	private int curpage;
	@ApiModelProperty(value = "页大小")
	private int pagesize;
	@ApiModelProperty(value = "总数")
	private int total;
	@ApiModelProperty(value = "列表")
	private List<T> list = new ArrayList<>();

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public Page() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getCurpage() {
		return curpage;
	}

	public void setCurpage(int curpage) {
		this.curpage = curpage;
	}

	public Page(int curpage, int pagesize, int total, List<T> list) {
		super();
		this.curpage = curpage;
		this.pagesize = pagesize;
		this.total = total;
		this.list = list;
	}

	

}
