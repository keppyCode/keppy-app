package com.mpm.foundation.authserver.security.crypto.password;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密类
 * 
 * @author zzm
 *
 */
public class MpmPasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		return encodeMD5String(rawPassword.toString()).toUpperCase();
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return encode(rawPassword).equals(encodedPassword);
	}

	/**
	 * 用MD5算法进行加密
	 * 
	 * @param str 需要加密的字符串
	 * @return MD5加密后的结果
	 */
	public static String encodeMD5String(String str) {
		return encode(str, "MD5");
	}

	private static String encode(String str, String method) {
		MessageDigest md = null;
		String dstr = null;
		try {
			md = MessageDigest.getInstance(method);
			md.update(str.getBytes());
			byte[] digest = md.digest();
			return getFormattedText(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return dstr;
	}

	private static String getFormattedText(byte[] bytes) {
		StringBuilder buf = new StringBuilder(bytes.length * 2);

		for (int j = 0; j < bytes.length; ++j) {
			buf.append(HEX_DIGITS[(bytes[j] >> 4 & 0xF)]);
			buf.append(HEX_DIGITS[(bytes[j] & 0xF)]);
		}
		return buf.toString();
	}

	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f' };

	public static void main(String[] args) {
		// 670B14728AD9902AECBA32E22FA4F6BD
		System.out.println(MpmPasswordEncoder.encodeMD5String("000000"));
	}

}
