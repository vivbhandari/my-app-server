package com.dekses.jersey.docker.demo;

import java.util.HashMap;

import org.apache.commons.collections4.map.PassiveExpiringMap;

public class UserUtil {
	int tokenExpiryTime = 60000;
	PassiveExpiringMap<String, String> accessTokens = new PassiveExpiringMap<String, String>(tokenExpiryTime);
	HashMap<String, String> refreshTokens = new HashMap<String, String>();
	HashMap<String, String> users = new HashMap<String, String>();
	private static UserUtil userUtil = null;

	private UserUtil() {
		users.put("user1", "password1");
		users.put("admin", "admin");
	}

	public static UserUtil getInstance() {
		if (userUtil == null) {
			userUtil = new UserUtil();
		}
		return userUtil;
	}
}
