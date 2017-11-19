package com.dekses.jersey.docker.demo;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Main class.
 *
 */
public class Main {
	// Base URI the Grizzly HTTP server will listen on
	public static String BASE_URI = "http://localhost:8080/myapp/";
	public static String LOCALHOST = "localhost";
	public static String CONTAINER = LOCALHOST;
	public static MyKafkaConsumer myKafkaConsumer = null;
	public static MyKafkaProducer myKafkaProducer = null;

	/**
	 * Starts Grizzly HTTP server exposing JAX-RS resources defined in this
	 * application.
	 * 
	 * @return Grizzly HTTP server.
	 */

	public static HttpServer startServer() {
		// create a resource config that scans for JAX-RS resources and
		// providers
		// in com.dekses.jersey.docker.demo package
		final ResourceConfig rc = new ResourceConfig().packages("com.dekses.jersey.docker.demo");

		Map<String, String> env = System.getenv();
		String hostname = env.get("HOSTNAME");
		System.out.println("HOSTNAME=" + hostname);
		if (hostname != null) {
			BASE_URI = "http://0.0.0.0:8080/myapp/";
			CONTAINER = hostname;
		}

		// create and start a new instance of grizzly http server
		// exposing the Jersey application at BASE_URI
		return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
	}

	/**
	 * Main method.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		final HttpServer server = startServer();

		// initialize kafka resources
		if (!CONTAINER.equals("localhost")) {
			myKafkaProducer = new MyKafkaProducer();
			myKafkaConsumer = new MyKafkaConsumer();
		}

		System.out.println(String.format("Jersey app started with WADL available at "
				+ "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
		System.in.read();
		server.stop();
	}
}
