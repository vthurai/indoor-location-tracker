package com.main;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.main.config.MyDayPipelineOptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {
	
    public static void main(String[] args) {
    	
    	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyDayPipeline.class);
    	MyDayPipeline app = context.getBean(MyDayPipeline.class);

    	PipelineOptionsFactory.register(MyDayPipelineOptions.class);

    	Pipeline p = app.buildPipeline();
    	p.run();
      
	}
}
