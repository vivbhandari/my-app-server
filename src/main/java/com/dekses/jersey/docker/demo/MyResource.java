package com.dekses.jersey.docker.demo;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
	@Produces(MediaType.TEXT_PLAIN)
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

	@POST
	public Response incrementCounter() {
		int counter = QueryEngine.incrementCounter();
		Main.myKafkaProducer.sendCounter(counter);
		return Response.status(200).entity("Served by " + Main.CONTAINER + ", new value=" + counter + "\n").build();
	}
}
