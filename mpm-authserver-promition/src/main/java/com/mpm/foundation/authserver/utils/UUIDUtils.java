package com.mpm.foundation.authserver.utils;

import java.util.UUID;

public class UUIDUtils {
	public static String shortuuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static void main(String[] args) {
		System.out.println(UUIDUtils.shortuuid());
	}

}
