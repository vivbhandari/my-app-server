package com.dekses.jersey.docker.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	String[][] providersData = { { "Home", "1050 columbus ave,san francisco,ca 94133" },
			{ "Work", "333 Bryant Street,san francisco,ca" }, { "Demandbase", "Demandbase" } };

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
	@Produces(MediaType.APPLICATION_JSON)
	@Path("providers")
	public Response getProviders() throws JSONException {

		JSONObject jsonObject = new JSONObject();
		JSONArray jsonProviders = new JSONArray();
		for (String[] provider : providersData) {
			JSONObject jsonProvider = new JSONObject();
			jsonProvider.put("title", provider[0]);
			jsonProvider.put("address", provider[1]);
			jsonProviders.put(jsonProvider);
		}
		jsonObject.put("providers", jsonProviders);
		return Response.status(200).entity(jsonObject.toString()).build();
	}

	@POST
	public Response incrementCounter() {
		int counter = QueryEngine.incrementCounter();
		Main.myKafkaProducer.sendCounter(counter);
		return Response.status(201).entity("Served by " + Main.CONTAINER + ", new value=" + counter + "\n").build();
	}
}
