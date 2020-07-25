package com.main.config;

import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;

public interface LocationTrackingPipelineOptions extends DataflowPipelineOptions {

	String getSubscription();
	void setSubscription(String subscription);
	
	String getDatasetId();
	void setDatasetId(String datasetId);
	
	String getCartesianTableId();
	void setCartesianTableId(String cartesianTableId);
	
	TrilaterationCoefficients getTrilaterationCoefficients();
	void setTrilaterationCoefficients (TrilaterationCoefficients trilaterationCoefficients);
	
	double getMeasuredPower();
	void setMeasuredPower(double measuredPower);
	
	double getEnvironmentalFactorN();
	void setEnvironmentalFactorN(double environmentalFactorN);
}
