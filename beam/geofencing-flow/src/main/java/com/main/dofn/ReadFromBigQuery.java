package com.main.dofn;

import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.JobException;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.main.config.SelectStatement;
import com.main.model.UserCoordinates;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReadFromBigQuery extends DoFn<Integer, KV<String, UserCoordinates>> {

    BigQuery bigQuery;
    private SelectStatement selectStatement;
    private String projectId;
    private ValueProvider<String> datasetId;
    private ValueProvider<String> tableId;
    private ValueProvider<String> startDate;
    private ValueProvider<String> finishDate;
    
    public ReadFromBigQuery(String projectId,
            ValueProvider<String> datasetId,
            ValueProvider<String> tableId,
            ValueProvider<String> startDate,
            ValueProvider<String> finishDate) {
        this.projectId = projectId;
        this.datasetId = datasetId;
        this.tableId = tableId;
        this.startDate = startDate;
        this.finishDate = finishDate;
    }
    
    @Setup
    public void setup() {
        bigQuery = BigQueryOptions.getDefaultInstance().getService();
    }
    
    @ProcessElement
    public void processElement (ProcessContext c) throws JobException, InterruptedException {
//        String query = "SELECT corpus FROM `bigquery-public-data.samples.shakespeare` GROUP BY corpus;";
//        String query = "SELECT * FROM `iot-myday.ibeaconDataset.rssiRadiusValidation` WHERE uuid = 'f7826da64fa24e988024bc5b71e0abcd' AND timecollected > '2019-11-08T17:40:00' LIMIT 1000";
        SelectStatement selectStatement = new SelectStatement(projectId, datasetId.get(), tableId.get());
        String query = selectStatement.select(startDate.get(), finishDate.get());
        log.info(query);
        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();

        for (FieldValueList rows : bigQuery.query(queryConfig).iterateAll()) {
            UserCoordinates user = UserCoordinates.builder()
                    .uuid(rows.get(0).getStringValue())
                    .timecollected(rows.get(1).getStringValue())
                    .xPos(rows.get(2).getDoubleValue())
                    .yPos(rows.get(3).getDoubleValue())
                    .build();
            log.info(user.toString());
            c.output(KV.of(user.getUuid(), user));
        }
    }
}
