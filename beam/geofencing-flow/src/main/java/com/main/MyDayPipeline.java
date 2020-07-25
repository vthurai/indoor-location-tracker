package com.main;

import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.IterableCoder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Reshuffle;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.common.collect.ImmutableList;
import com.main.config.MyDayPipelineOptions;
import com.main.dofn.GeofencingCalculation;
import com.main.dofn.ReadFromBigQuery;
import com.main.dofn.SortUserPerStation;
import com.main.model.Station;
import com.main.model.UserCoordinates;

//@Slf4j
public class MyDayPipeline {

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC();

	public static Pipeline build(Pipeline p) {
		
		MyDayPipelineOptions pipelineOptions = (MyDayPipelineOptions) p.getOptions();
		
//		pipelineOptions.setStagingLocation("gs://myday_dataflow/geofencing/staging");
		pipelineOptions.setTemplateLocation("gs://myday_dataflow/geofencing/template");
		pipelineOptions.setRunner(DataflowRunner.class);
		pipelineOptions.setProject("iot-myday");
//		String cartesanTableSpec = String.format("%s:%s.%s", pipelineOptions.getProject(), pipelineOptions.getDatasetId().get(), pipelineOptions.getCartesianTableId().get());
//		
//		String queryDate = Strings.isNullOrEmpty(pipelineOptions.getDate().get()) ? FORMATTER.print(System.currentTimeMillis()) : pipelineOptions.getDate().get();
//		FORMATTER.parseDateTime(queryDate);
		
		
		//Replace with the BigQuery SDK
		//Parse the tables into 
//		p.apply(BigQueryIO.readTableRows()
//		        
////                .from("iot-myday.ibeaconDataset.rssiRadiusValidation")
//                .fromQuery("SELECT * FROM `iot-myday.ibeaconDataset.rssiRadiusValidation`")
//                .usingStandardSql()
//                
//                );

		
		
		
//		        .from(cartesanTableSpec)
//				.fromQuery(String.format("SELECT * FROM [%s] WHERE DATE(timecollected) = '%s'", cartesanTableSpec, queryDate)));
//		.apply(ParDo.of(new DoFn<TableRow, Void>(){
//			
//			@ProcessElement
//		    public void processElement(ProcessContext c) throws IOException {
//				System.out.println(c.element().toPrettyString());
//			}
//		}));
//		String jsonSchema =  "[\r\n" + 
//		        "  {\r\n" + 
//		        "    \"description\": \"User\",\r\n" + 
//		        "    \"mode\": \"REQUIRED\",\r\n" + 
//		        "    \"name\": \"uuid\",\r\n" + 
//		        "    \"type\": \"STRING\"\r\n" + 
//		        "  },\r\n" + 
//		        "  {\r\n" + 
//		        "    \"description\": \"Station\",\r\n" + 
//		        "    \"mode\": \"REQUIRED\",\r\n" + 
//		        "    \"name\": \"station\",\r\n" + 
//		        "    \"type\": \"STRING\"\r\n" + 
//		        "  },\r\n" + 
//		        "  {\r\n" + 
//		        "    \"description\": \"number of times visted\",\r\n" + 
//		        "    \"mode\": \"REQUIRED\",\r\n" + 
//		        "    \"name\": \"occurrences\",\r\n" + 
//		        "    \"type\": \"INTEGER\"\r\n" + 
//		        "  }\r\n" + 
//		        "]";
		TableSchema tableSchema = new TableSchema().setFields(
                ImmutableList.of(
                   new TableFieldSchema().setName("uuid").setType("STRING").setMode("REQUIRED"),//REQUIRED
                   new TableFieldSchema().setName("station").setType("STRING").setMode("REQUIRED"),
                   new TableFieldSchema().setName("occurrences").setType("INTEGER").setMode("REQUIRED"),
                   new TableFieldSchema().setName("component").setType("STRING").setMode("NULLABLE")));
//		        "uuid:STRING,station:STRING,occurrences:INTEGER";
		String finalTable = "iot-myday:ibeaconDataset.geofencingResults";
		p
		    .apply(Create.of(1))
		    .apply(ParDo.of(new ReadFromBigQuery(pipelineOptions.getProject(),
		            pipelineOptions.getDatasetId(), pipelineOptions.getCartesianTableId(),
		            pipelineOptions.getStartDate(), pipelineOptions.getFinishDate())))
		    .setCoder(KvCoder.of(StringUtf8Coder.of(), SerializableCoder.of(UserCoordinates.class)))
		    .apply(GroupByKey.create())
            .setCoder(KvCoder.of(StringUtf8Coder.of(), IterableCoder.of(SerializableCoder.of(UserCoordinates.class))))
		    .apply(ParDo.of(new SortUserPerStation()))
            .setCoder(KvCoder.of(SerializableCoder.of(Station.class), IterableCoder.of(SerializableCoder.of(UserCoordinates.class))))
            .apply(Reshuffle.viaRandomKey())
            .apply(ParDo.of(new GeofencingCalculation()))
            .apply(BigQueryIO.writeTableRows()
                    .to(finalTable)
                    .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                    .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE)
//                    .withCustomGcsTempLocation(
                    .withSchema(tableSchema));
            
//		.apply(BigQueryIO.writeTableRows()
//                .to(pipelineOptions.getTableSpec())
//                .withCreateDisposition(CreateDisposition.CREATE_IF_NEEDED)
//                .withWriteDisposition(WriteDisposition.WRITE_TRUNCATE)
//                .withCustomGcsTempLocation(pipelineOptions.getCustomGcsTempLocation())
//                .withSchema(TransitDelay.getTransitDelaySchema()));
		
       return p;
		
	}
}
