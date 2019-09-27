package com.mpm.common.utils;

import java.io.Serializable;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;

public class RedisUtils {
	private RedisConnectionFactory connectionFactory;
	private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();
	private String prefix = "";

	public RedisUtils(RedisConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	/**
	 * 获取redis connection
	 * 
	 * @return
	 */
	private RedisConnection getConnection() {
		return connectionFactory.getConnection();
	}

	/**
	 * 保存
	 * 
	 * @param key
	 * @param object
	 * @param seconds
	 */
	public void store(String key, Serializable object, int seconds) {
		byte[] serializedAccessToken = serialize(object);
		byte[] accessKey = serializeKey(key);
		RedisConnection conn = getConnection();
		try {
			conn.openPipeline();
			conn.set(accessKey, serializedAccessToken);
			conn.expire(accessKey, seconds);
			conn.closePipeline();
		} finally {
			conn.close();
		}
	}

	/**
	 * 获取
	 * 
	 * @param key
	 * @param requiredType
	 * @return
	 */
	public <T> T get(String key, Class<T> requiredType) {
		byte[] serializedKey = serializeKey(key);
		byte[] bytes = null;
		RedisConnection conn = getConnection();
		try {
			bytes = conn.get(serializedKey);
		} finally {
			conn.close();
		}
		T object = deserialize(bytes, requiredType);
		return object;
	}

	/**
	 * 序列化key
	 * 
	 * @param object
	 * @return
	 */
	private byte[] serializeKey(String object) {
		return serialize(prefix + object);
	}

	/**
	 * 序列化字符串
	 * 
	 * @param string
	 * @return
	 */
	private byte[] serialize(String string) {
		return serializationStrategy.serialize(string);
	}

	private String deserializeString(byte[] bytes) {
		return serializationStrategy.deserializeString(bytes);
	}

	private byte[] serialize(Object object) {
		return serializationStrategy.serialize(object);
	}

	private <T> T deserialize(byte[] bytes, Class<T> requiredType) {
		return serializationStrategy.deserialize(bytes, requiredType);
	}

}
