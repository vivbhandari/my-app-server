package com.dekses.jersey.docker.demo;

import java.util.HashMap;

public class UserUtil {
	HashMap<String, String> users = new HashMap<String, String>();
	HashMap<String, String> tokens = new HashMap<String, String>();
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
