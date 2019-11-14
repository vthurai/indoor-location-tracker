package com.main;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import com.main.config.MyDayPipelineOptions;

public class Application {
	public static void main(String[] args) {
		MyDayPipelineOptions pipelineOptions = PipelineOptionsFactory
				.fromArgs(args)
				.as(MyDayPipelineOptions.class);
		
		Pipeline p = Pipeline.create(pipelineOptions);
		
		MyDayPipeline.build(p).run();
	}
}
