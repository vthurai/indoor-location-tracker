package com.main;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.client.util.Charsets;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.main.model.Source;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoadData {

	private String topicName;
	private String inputPath;
	private Gson gson;
	
	@Autowired
	public LoadData(String topicName,
			String inputPath,
			Gson gson) {
		this.topicName = topicName;
		this.inputPath = inputPath;
		this.gson = gson;
	}
	
	public void run() throws IOException {
		String jsonContent = FileUtils.readFileToString(new File(inputPath), Charsets.UTF_8);
		Type listType = new TypeToken<List<Source>>(){}.getType();

		List<Source> sources = gson.fromJson(jsonContent, listType);
		Publisher publisher = null;
        List<ApiFuture<String>> futures = new ArrayList<>();
		try {
			publisher = Publisher.newBuilder(topicName).build();
			for(Source source : sources) {
				String message = gson.toJson(source);
				log.info(message);
				PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
	                    .setData(ByteString.copyFromUtf8(message))
	                    .build();

				futures.add(publisher.publish(pubsubMessage));
//				futures.clear();
			}
		} catch (Exception e) {
			log.error("Unable to push messages to pubsub", e);
		} finally {
			try {
				List<String> messageIds = ApiFutures.allAsList(futures).get();
	            for (String messageId : messageIds) {
	               log.info(messageId);
	            }
				if (publisher != null) {
					publisher.shutdown();
	                publisher.awaitTermination(1, TimeUnit.MINUTES);
				}
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				log.error("Unable to push messages to pubsub", e);
			}
		}
	}
}
