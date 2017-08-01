package com.dekses.jersey.docker.demo;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.codehaus.jettison.json.JSONException;

public class MyKafkaProducer {
	KafkaProducer<String, String> producer = null;
	Properties props = new Properties();

	public MyKafkaProducer() {
		props = new Properties();
		props.put("bootstrap.servers", "kafka1:9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		producer = new KafkaProducer<String, String>(props);
	}

	public void sendCounter(int counter) {
		System.out.println("Sending counter");
		producer.send(new ProducerRecord<String, String>("test", "counter", Integer.toString(counter)));
	}

	public void sendToken(String token) throws JSONException {
		System.out.println("Sending token");
		producer.send(new ProducerRecord<String, String>("test", "token", token));
	}

	public void close() {
		producer.close();
	}
}