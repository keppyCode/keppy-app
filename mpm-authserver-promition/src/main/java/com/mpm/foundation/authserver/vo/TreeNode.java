package com.mpm.foundation.authserver.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "树模型")
public class TreeNode<T> implements Comparable<TreeNode<T>> {
	@ApiModelProperty(value = "节点")
	private T node;
	@ApiModelProperty(value = "子节点")
	private List<TreeNode<T>> children = new ArrayList<>();
	@ApiModelProperty(value = "排序")
	private int order;

	public TreeNode(T node) {
		this.node = node;
	}

	public TreeNode(T node, int order) {
		this.node = node;
		this.order = order;
	}

	public T getNode() {
		return node;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public void setNode(T node) {
		this.node = node;
	}

	public List<TreeNode<T>> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNode<T>> children) {
		this.children = children;
	}

	@Override
	public int compareTo(TreeNode<T> o) {
		return this.order > o.getOrder() ? 1 : (this.order == o.getOrder() ? 0 : -1);
	}

}
