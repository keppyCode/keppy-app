package com.mpm.foundation.authserver.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;

import java.util.List;

public class Zkclient {
	private static CuratorFramework zkclient;
	private static PathChildrenCache cache;

	public Zkclient(String namespace, String connectString) {
		zkclient = CuratorFrameworkFactory.builder().connectString(connectString).sessionTimeoutMs(30000)
				.connectionTimeoutMs(30000).canBeReadOnly(false)
				.retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE)).defaultData(null)
				.namespace(namespace).build();
		zkclient.start();
		// zkclient.newNamespaceAwareEnsurePath(namespace);
	}

	public void createrEphemeral(String path, String content) throws Exception {
		zkclient.create().withMode(CreateMode.EPHEMERAL).forPath(path, content.getBytes("utf-8"));
	}

	/**
	 * 创建或更新一个节点
	 * 
	 * @param path    路径
	 * @param content 内容
	 **/
	public void createrOrUpdateEphemeral(String path, String content) throws Exception {
		if (zkclient.checkExists().forPath(path) != null) {
			delete(path);
		}
		zkclient.create().withMode(CreateMode.EPHEMERAL).forPath(path, content.getBytes("utf-8"));
	}

	/**
	 * 创建或更新一个节点
	 * 
	 * @param path    路径
	 * @param content 内容
	 **/
	public void createrOrUpdatePersistent(String path, String content) throws Exception {
		if (zkclient.checkExists().forPath(path) != null) {
			delete(path);
		}
		zkclient.create().withMode(CreateMode.PERSISTENT).forPath(path, content.getBytes("utf-8"));
	}

	/**
	 * 删除zk节点
	 * 
	 * @param path 删除节点的路径
	 * 
	 **/
	public void delete(String path) throws Exception {
		zkclient.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
	}

	/**
	 * 判断路径是否存在
	 * 
	 * @param path
	 **/
	public boolean isExist(String path) throws Exception {
		if (zkclient.checkExists().forPath(path) == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 读取的路径
	 * 
	 * @param path
	 **/
	public String read(String path) throws Exception {
		String data = new String(zkclient.getData().forPath(path), "utf-8");
		return data;
	}

	public List<String> getChildren(String path) throws Exception {
		List<String> paths = zkclient.getChildren().forPath(path);
		return paths;
	}

	public void watch(String path, PathChildrenCacheListener listener) throws Exception {
		cache = new PathChildrenCache(zkclient, path, true);
		cache.start();
		cache.getListenable().addListener(listener);
	}

	public void destory() {
		if (cache != null) {
			CloseableUtils.closeQuietly(cache);
		}
		if (zkclient != null) {
			CloseableUtils.closeQuietly(zkclient);
		}
	}

	public static void main(String[] args) throws Exception {
		Zkclient t = new Zkclient("mpm/zuul", "192.168.1.81:2181");

		t.createrOrUpdatePersistent("/ggg", "gdsagdsa");
		System.out.println("ok");
	}
}
