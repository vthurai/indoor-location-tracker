package com.main.dofn;

import org.apache.beam.sdk.transforms.DoFn;

import com.google.api.services.bigquery.model.TableRow;
import com.main.config.TrilaterationCoefficients;
import com.main.model.BeaconDetection;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class TrialterationCalculation extends DoFn<BeaconDetection, TableRow>{
	
	private final TrilaterationCoefficients coefficients;
	
	@ProcessElement
    public void processElement(ProcessContext c) {
	
		double radius1 = c.element().getRpiLocationMapping().get("rpiA");
		double radius2 = c.element().getRpiLocationMapping().get("rpiB");
		double radius3 = c.element().getRpiLocationMapping().get("rpiC");
		
		/*
		 * x(t) = B1 * R3(t)^2 - B2 * R2(t)^2 + B3 * R1(t)^2 + B4
		 */
		double x = coefficients.getB1() * Math.pow(radius3, 2)
				- coefficients.getB2() *  Math.pow(radius2, 2)
				+ coefficients.getB3() * Math.pow(radius1, 2)
				+ coefficients.getB4();
		
		/*
		 * y(t) = A4 * x(t) + A5 * [R3(t)^2 - R2(t)^2] + A6
		 */
		double y = coefficients.getA4() * x + coefficients.getA5()
				* (Math.pow(radius3, 2) - Math.pow(radius2, 2))
				+ coefficients.getA6();

		log.info(String.format("[Time %s] ID %s;\nx = %s; y = %s", 
				c.element().getBeaconTimeKey().getTimecollected(),
				c.element().getBeaconTimeKey().getUuid(), x, y));
		
		TableRow row = new TableRow()
				.set("uuid", c.element().getBeaconTimeKey().getUuid())
				.set("timecollected", c.element().getBeaconTimeKey().getTimecollected())
				.set("x_axis", x)
				.set("y_axis", y);
		
		c.output(row);
	}

}
