package com.main;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.bigquery.TableRowJsonCoder;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Sessions;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionTuple;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.TupleTagList;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.google.api.services.bigquery.model.TableRow;
import com.main.config.ApplicationConfig;
import com.main.config.LocationTrackingPipelineOptions;
import com.main.dofn.MergeAdvertisements;
import com.main.dofn.SortSource;
import com.main.dofn.StatefulTimeBatching;
import com.main.dofn.TrialterationCalculation;
import com.main.model.BeaconDetection;
import com.main.model.BeaconTimeKey;
import com.main.model.Source;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@ComponentScan
@Configuration
//@PropertySource("${classpath:application.properties}")
public class LocationTrackingPipeline {

	@Autowired
	private ApplicationConfig applicationConfig;
	
	public Pipeline buildPipeline() {
		
	    TupleTag<BeaconDetection> beaconDetectionTupleTag = new TupleTag<>();
	    TupleTag<TableRow> radiusValidationTupleTag = new TupleTag<>();
	    
		LocationTrackingPipelineOptions pipelineOptions = applicationConfig.getOptions();
		
		Pipeline p = Pipeline.create(pipelineOptions);
		
//		PCollection<BeaconLocation> rawLocation = 
		PCollectionTuple radiusCollections = p
			.apply(PubsubIO.readMessagesWithAttributes().fromSubscription(pipelineOptions.getSubscription()))
			.apply(ParDo.of(new SortSource()))
////				.setCoder(KvCoder.of(AvroCoder.of(BeaconTimeKey.class), SerializableCoder.of(Source.class)))
//            .setCoder(KvCoder.of(StringUtf8Coder.of(), SerializableCoder.of(Source.class)))
//            .apply(Window.into(FixedWindows.of(Duration.standardSeconds(5))))
////			.apply(Window.<KV<BeaconTimeKey, Source>>into(Sessions.withGapDuration(Duration.standardSeconds(15))))
////              .apply( Window.<KV<BeaconTimeKey, Source>>into(FixedWindows.of(Duration.standardMinutes(1)))
////              .triggering(Repeatedly.forever(AfterFirst.of(
////                      AfterPane.elementCountAtLeast(customOptions.getAwsS3NumElements()),
////                      AfterProcessingTime.pastFirstElementInPane().plusDelayOf(Duration.standardMinutes(customOptions.getAwsS3WindowMinutes())))))
////              .discardingFiredPanes()
////              .withAllowedLateness(Duration.ZERO))
//			.apply(GroupByKey.create())
			.apply(ParDo.of(new StatefulTimeBatching(5)))
			.apply(ParDo.of(MergeAdvertisements.builder()
					.measuredPower(pipelineOptions.getMeasuredPower())
					.environmentalFactorN(pipelineOptions.getEnvironmentalFactorN())
					.radiusValidationTupleTag(radiusValidationTupleTag)
					.build())
			        .withOutputTags(beaconDetectionTupleTag, TupleTagList.of(radiusValidationTupleTag)));
		
		radiusCollections.get(beaconDetectionTupleTag)
		    .setCoder(SerializableCoder.of(BeaconDetection.class))
			.apply(ParDo.of(TrialterationCalculation.builder()
					.coefficients(pipelineOptions.getTrilaterationCoefficients())
					.build()))
			.apply(BigQueryIO.writeTableRows()
				.to(String.format("%s:%s.%s", pipelineOptions.getProject(), pipelineOptions.getDatasetId(), pipelineOptions.getCartesianTableId()))
				.withLoadJobProjectId(pipelineOptions.getProject())
			    .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND)
				.withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_NEVER));
		
		radiusCollections.get(radiusValidationTupleTag)
		    .setCoder(TableRowJsonCoder.of())
            .apply(BigQueryIO.writeTableRows()
                    .to(String.format("%s:%s.%s", pipelineOptions.getProject(), pipelineOptions.getDatasetId(), "rssiRadiusValidation"))
                    .withLoadJobProjectId(pipelineOptions.getProject())
                    .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND)
                    .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_NEVER));
  
		return p;
	}
}
