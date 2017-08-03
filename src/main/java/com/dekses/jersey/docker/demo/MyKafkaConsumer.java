package com.dekses.jersey.docker.demo;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class MyKafkaConsumer implements Runnable {
	public boolean stopListening = false;
	public Integer counter = null;

	public MyKafkaConsumer() {
		new Thread(this).start();
		System.out.println("Started Kafka consumer");
	}

	public void run() {
		Properties props = new Properties();
		props.put("bootstrap.servers", "kafka1:9092");
		props.put("group.id", Main.CONTAINER);
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
		consumer.subscribe(Arrays.asList("test"));
		while (!stopListening) {
			ConsumerRecords<String, String> records = consumer.poll(100);
			for (ConsumerRecord<String, String> record : records) {
				System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
				if (record.key().equals("counter")) {
					counter = Integer.parseInt(record.value());
				} else if (record.key().equals("token")) {
					try {
						JSONObject jsonObject = new JSONObject(record.value());
						UserUtil.getInstance().accessTokens.put(jsonObject.getString("access_token"),
								jsonObject.getString("username"));
						UserUtil.getInstance().refreshTokens.put(jsonObject.getString("refresh_token"),
								jsonObject.getString("username"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		consumer.close();
	}

}
