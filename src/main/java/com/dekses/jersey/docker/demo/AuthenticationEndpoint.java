package com.dekses.jersey.docker.demo;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONObject;

@Path("/authentication")
public class AuthenticationEndpoint {
	private SecureRandom secureRandom = new SecureRandom();

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response authenticateUser(@QueryParam("username") String username, @QueryParam("password") String password) {

		try {
			// Authenticate the user using the credentials provided
			authenticate(username, password);

			// Issue a token for the user
			String token = issueToken(username);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("token", token);
			jsonObject.put("username", username);

			if (Main.myKafkaProducer != null) {
				Main.myKafkaProducer.sendToken(jsonObject.toString());
			}

			// Return the token on the response
			return Response.ok(jsonObject.toString()).build();

		} catch (Exception e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	private void authenticate(String username, String password) throws Exception {
		// Authenticate against a database, LDAP, file or whatever
		// Throw an Exception if the credentials are invalid
		if (UserUtil.getInstance().users.containsKey(username)
				&& UserUtil.getInstance().users.get(username).equals(password)) {
			return;
		}
		throw new Exception("authentication failed");
	}

	private String issueToken(String username) {
		// Issue a token (can be a random String persisted to a database or a
		// JWT token)
		// The issued token must be associated to a user
		// Return the issued token
		String token = new BigInteger(130, secureRandom).toString(32);
		UserUtil.getInstance().tokens.put(token, username);
		return token;
	}
}