package com.main;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.main.config.LocationTrackingPipelineOptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {
	
    public static void main(String[] args) {
    	
    	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(LocationTrackingPipeline.class);
    	LocationTrackingPipeline app = context.getBean(LocationTrackingPipeline.class);

    	PipelineOptionsFactory.register(LocationTrackingPipelineOptions.class);

    	Pipeline p = app.buildPipeline();
    	p.run();
      
	}
}
