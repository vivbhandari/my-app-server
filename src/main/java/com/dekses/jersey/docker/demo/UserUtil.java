package com.dekses.jersey.docker.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.map.PassiveExpiringMap;

public class UserUtil {
	List<List<String>> providersData = new ArrayList<List<String>>();
	int tokenExpiryTime = 600000;
	PassiveExpiringMap<String, String> accessTokens = new PassiveExpiringMap<String, String>(
			tokenExpiryTime);
	HashMap<String, String> refreshTokens = new HashMap<String, String>();
	HashMap<String, String> users = new HashMap<String, String>();
	private static UserUtil userUtil = null;

	private UserUtil() {
		users.put("user1", "password1");
		users.put("admin", "admin");
		providersData.add(Arrays.asList(new String[] { "Vivek",
				"1050 columbus ave,san francisco,ca 94133", getImageData("Vivek_profile.jpg") }));
		providersData.add(Arrays.asList(new String[] { "Ali", "333 Bryant Street,san francisco,ca",
				getImageData("Ali_profile.png") }));
		providersData.add(Arrays.asList(new String[] { "Demandbase", "Demandbase", null }));
	}

	public String getImageData(String imageName) {
		String image = null;
		ClassLoader classLoader = getClass().getClassLoader();

		if (imageName != null) {
			String fileName = classLoader.getResource(imageName).getFile();
			System.out.println("path=" + fileName);
			image = ImageManipulation.covertToString(fileName);
		}
		return image;
	}

	public static UserUtil getInstance() {
		if (userUtil == null) {
			userUtil = new UserUtil();
		}
		return userUtil;
	}
}
