package com.dekses.jersey.docker.demo;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Path("/user")
public class AuthenticationEndpoint {
	private SecureRandom secureRandom = new SecureRandom();

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("authentication")
	public Response authenticateUser(@QueryParam("username") String username,
			@QueryParam("password") String password) {

		try {
			// Authenticate the user using the credentials provided
			authenticate(username, password);

			// Issue a token for the user
			JSONObject jsonObject = getAuthenticationPayload(username);

			if (Main.myKafkaProducer != null) {
				Main.myKafkaProducer.sendRecord("token", jsonObject.toString());
			}

			// Return the token on the response
			return Response.status(201).entity(jsonObject.toString()).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED).entity("{}").build();
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("register")
	public Response registerUser(String input) {
		System.out.println("registerUser=" + input);

		try {
			JSONObject jsonInput = new JSONObject(input);

			String username = jsonInput.getString("emailId");

			if (UserUtil.getInstance().users.containsKey(username)) {
				return Response.status(Response.Status.CONFLICT)
						.entity("{\"reason\": \"user already exist\"}").build();
			}

			// Issue a token for the user
			JSONObject jsonObject = getAuthenticationPayload(username);

			if (Main.myKafkaProducer != null) {
				Main.myKafkaProducer.sendRecord("provider", jsonInput.toString());
				// this is only required till user db is not there
				jsonObject.put("password", jsonInput.getString("password"));
				Main.myKafkaProducer.sendRecord("token", jsonObject.toString());
				jsonObject.remove("password");
			} else {
				// Dev mode
				UserUtil.getInstance().users.put(username, jsonInput.getString("password"));

				String image = null;
				if (jsonInput.has("image")) {
					image = jsonInput.getString("image");
				}

				UserUtil.getInstance().providersData.add(Arrays.asList(new String[] {
						jsonInput.getString("name"), jsonInput.getString("address"), image }));
			}

			// Return the token on the response
			return Response.status(201).entity(jsonObject.toString()).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("authentication/refresh")
	public Response refreshToken(@QueryParam("username") String username,
			@QueryParam("token") String refreshToken) {

		try {
			String expectedRefreshToken = UserUtil.getInstance().refreshTokens.remove(username);

			if (expectedRefreshToken.equals(refreshToken)) {
				// Issue a new token for the user
				JSONObject jsonObject = getAuthenticationPayload(username);

				if (Main.myKafkaProducer != null) {
					Main.myKafkaProducer.sendRecord("token", jsonObject.toString());
				}

				// Return the token on the response
				return Response.status(201).entity(jsonObject.toString()).build();
			} else {
				return Response.status(Response.Status.UNAUTHORIZED).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	private JSONObject getAuthenticationPayload(String username) throws JSONException {
		// Issue a token for the user
		String accessToken = issueAccessToken(username);
		String refreshToken = issueRefreshToken(username);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("access_token", accessToken);
		jsonObject.put("refresh_token", refreshToken);
		jsonObject.put("username", username);
		jsonObject.put("token_type", "Bearer");
		jsonObject.put("expires_in", UserUtil.getInstance().tokenExpiryTime);
		jsonObject.put("image", UserUtil.getInstance().getImageData("Vivek_profile.jpg"));
		return jsonObject;
	}

	private void authenticate(String username, String password) throws Exception {
		if (UserUtil.getInstance().users.containsKey(username)
				&& UserUtil.getInstance().users.get(username).equals(password)) {
			return;
		}
		throw new Exception("authentication failed");
	}

	private String issueAccessToken(String username) {
		String token = getToken();
		UserUtil.getInstance().accessTokens.put(token, username);
		return token;
	}

	private String issueRefreshToken(String username) {
		String token = getToken();
		UserUtil.getInstance().refreshTokens.put(username, token);
		return token;
	}

	private String getToken() {
		return new BigInteger(130, secureRandom).toString(32);
	}
}