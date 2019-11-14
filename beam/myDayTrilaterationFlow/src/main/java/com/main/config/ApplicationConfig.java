package com.main.config;

import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.google.api.client.util.Preconditions;

@Configuration
@PropertySource("${classpath:application.properties}")
public class ApplicationConfig {

	private String projectId;
	private String stagingLocation;
	private String subscription;
	private TrilaterationCoefficients trilaterationCoefficients;
	private double measuredPower;
	private double environmentalFactorN;
	private String datasetId;
	private String cartesianTableId;
	
	public ApplicationConfig(@Value("${gcp.dataflow.projectId}") String projectId,
			@Value("${gcp.dataflow.stagingLocation}") String stagingLocation,
			@Value("${gcp.pubsub.subscription.newevent}") String subscription,		
			@Value("${gcp.bigquery.datasetId}") String datasetId,
			@Value("${gcp.bigquery.tableId.cartesian}") String cartesianTableId,
			@Value("${rpi.position.x1}") double x1,
			@Value("${rpi.position.y1}") double y1,
			@Value("${rpi.position.x2}") double x2,
			@Value("${rpi.position.y2}") double y2,
			@Value("${rpi.position.x3}") double x3,
			@Value("${rpi.position.y3}") double y3,
			@Value("${beacon.mPower}") double measuredPower,
			@Value("${beacon.envFactor}") double environmentalFactorN) {
	
		this.projectId = Preconditions.checkNotNull(projectId);
		this.stagingLocation = Preconditions.checkNotNull(stagingLocation);
		this.subscription = Preconditions.checkNotNull(subscription);
		this.datasetId =  Preconditions.checkNotNull(datasetId);
		this.cartesianTableId =  Preconditions.checkNotNull(cartesianTableId);
		
		this.trilaterationCoefficients = TrilaterationCoefficients
				.calculateCoefficients(x1, y1, x2, y2, x3, y3);

		this.measuredPower = measuredPower;
		this.environmentalFactorN = environmentalFactorN;
	}
	
	public MyDayPipelineOptions getOptions() {
		
		MyDayPipelineOptions options = PipelineOptionsFactory.as(MyDayPipelineOptions.class);
		
		options.setProject(projectId);
		options.setStagingLocation(stagingLocation);
		options.setSubscription(subscription);
		options.setDatasetId(datasetId);
		options.setCartesianTableId(cartesianTableId);
		
		options.setTrilaterationCoefficients(trilaterationCoefficients);
		options.setMeasuredPower(measuredPower);
		options.setEnvironmentalFactorN(environmentalFactorN);
		
		options.setJobName("myDay-Dataflow-Trilateration");
        options.setNumWorkers(1);
        options.setMaxNumWorkers(4);
        options.setRunner(DataflowRunner.class);

		return options;
	}
}
