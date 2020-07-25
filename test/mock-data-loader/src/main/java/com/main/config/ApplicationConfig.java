package com.main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gson.Gson;

@Configuration
@PropertySource("classpath:application.properties")
public class ApplicationConfig {
	
	private final String resouceFolder = "src/main/resources/";
	
	@Bean
	public String topicName(@Value("${gcp.dataflow.projectId}") String projectId,
			@Value("${gcp.pubsub.topic.newevent}") String topicId) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(projectId));
		Preconditions.checkArgument(!Strings.isNullOrEmpty(topicId));
		return String.format("projects/%s/topics/%s",projectId, topicId);
	}
	
	
//	@Bean(name = "jsonMessage")
//	public NewUserRequest jsonMessage(ObjectMapper objectMapper,
//			@Value("${nbcu.newuser.sample}") String sampleMessage) throws JsonParseException, JsonMappingException, IOException{
//		Preconditions.checkArgument(!Strings.isNullOrEmpty(sampleMessage));
//		byte[] jsonContent = FileUtils.readFileToByteArray(new File(resouceFolder + sampleMessage));
//		return objectMapper.readValue(jsonContent, NewUserRequest.class);
//	}
	
	@Bean
	public String inputPath(@Value("${local.path.mockdata}") String localPath) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(localPath));
		return resouceFolder + localPath;
	}
	
	@Bean
	public Gson gson() {
		return new Gson();
	}

}
