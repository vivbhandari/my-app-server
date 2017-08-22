package com.dekses.jersey.docker.demo;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

	/**
	 * Method handling HTTP GET requests. The returned object will be sent to
	 * the client as "text/plain" media type.
	 *
	 * @return String that will be returned as a text/plain response.
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("test")
	public String test() {
		return "test";
	}

	@GET
	@Secured
	@Produces(MediaType.TEXT_PLAIN)
	@Path("securedtest")
	public String securedtest() {
		return "securedtest";
	}

	@GET
	@Secured
	@Produces(MediaType.TEXT_PLAIN)
	@Path("counter")
	public String getCounter() {
		String returnStr = "Served by %s, from %s, counter=%s \n";
		Integer counter = Main.myKafkaConsumer.counter;
		String source = "kafka";
		if (counter == null) {
			counter = QueryEngine.getCounter();
			source = "db";
			Main.myKafkaProducer.sendCounter(counter);
		}

		return String.format(returnStr, Main.CONTAINER, source, counter);
	}

	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Path("providers")
	public Response getProviders() throws JSONException {
		System.out.println(String.format("Served by %s \n", Main.CONTAINER));

		JSONObject jsonObject = new JSONObject();
		JSONArray jsonProviders = new JSONArray();
		for (List<String> provider : UserUtil.getInstance().providersData) {
			JSONObject jsonProvider = new JSONObject();
			jsonProvider.put("title", provider.get(0));
			jsonProvider.put("address", provider.get(1));
			jsonProvider.put("image", provider.get(2));
			jsonProviders.put(jsonProvider);
		}
		jsonObject.put("providers", jsonProviders);
		return Response.status(200).entity(jsonObject.toString()).build();
	}

	@POST
	@Secured
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("users")
	public Response registerProvider(String input) throws JSONException {
		System.out.println(String.format("Served by %s \n", Main.CONTAINER));

		JSONObject jsonObject = new JSONObject(input);
		List<String> provider = new ArrayList<String>();
		provider.add(jsonObject.getString("title"));
		provider.add(jsonObject.getString("address"));
		provider.add(jsonObject.getString("image"));
		UserUtil.getInstance().providersData.add(provider);
		return Response.status(201).entity("success \n").build();
	}

	@GET
	// @Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Path("image")
	public Response getImage() throws JSONException {
		System.out.println(String.format("Served by %s \n", Main.CONTAINER));

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("data", ImageManipulation.covertToString("/Users/vivb/Ali.png"));
		return Response.status(200).entity(jsonObject.toString()).build();
	}

	@POST
	@Secured
	@Produces(MediaType.TEXT_PLAIN)
	@Path("counter")
	public Response incrementCounter() {
		int counter = QueryEngine.incrementCounter();
		Main.myKafkaProducer.sendCounter(counter);
		return Response.status(201)
				.entity("Served by " + Main.CONTAINER + ", new value=" + counter + "\n").build();
	}

	@POST
	// @Secured
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("image")
	public Response saveImage(String input) throws JSONException {
		JSONObject inputJsonObject = new JSONObject(input);
		System.out.println(String.format("saveImage Served by %s \n", Main.CONTAINER));
		ImageManipulation.covertToImage(inputJsonObject.getString("data"),
				"/Users/vivb/Ali_output.png");
		return Response.status(201).entity("Served by " + Main.CONTAINER + "\n").build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("testPost")
	public Response testPost(String input) throws JSONException {
		JSONObject inputJsonObject = new JSONObject(input);
		System.out.println(String.format("testPost Served by %s \n", Main.CONTAINER));
		System.out.println(
				String.format("testPost Served by %s \n", inputJsonObject.getString("data")));
		return Response.status(201).entity("Served by " + Main.CONTAINER + "\n").build();
	}
}
