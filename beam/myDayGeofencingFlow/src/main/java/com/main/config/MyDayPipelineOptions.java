package com.main.config;

import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.ValueProvider;

public interface MyDayPipelineOptions extends DataflowPipelineOptions {

	@Default.String("ibeaconDataset")
	ValueProvider<String> getDatasetId();
	void setDatasetId(ValueProvider<String> datasetId);
	
	@Default.String("CartesianCoordinates")
	ValueProvider<String> getCartesianTableId();
	void setCartesianTableId(ValueProvider<String> cartesianTableId);

	@Default.String("")
	ValueProvider<String> getProximityTableId();
	void setProximityTableId(ValueProvider<String> proximityTableId);
	
	@Default.String("")
	ValueProvider<String> getStartDate();
	void setStartDate(ValueProvider<String> startDate);
	
	@Default.String("")
    ValueProvider<String> getFinishDate();
    void setFinishDate(ValueProvider<String> finishDate);
    
}
