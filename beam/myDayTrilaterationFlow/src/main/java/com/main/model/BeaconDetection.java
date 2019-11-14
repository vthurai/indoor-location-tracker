package com.main.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@Data
public class BeaconDetection implements Serializable {

	private BeaconTimeKey beaconTimeKey;
	private Map<String, Double> rpiLocationMapping;
//	private double xPos;
//	private double yPos;
	
	public BeaconDetection(BeaconTimeKey beaconTimeKey, Map<String, Double> rpiLocationMapping,
			double xPos, double yPos) {
		this.beaconTimeKey = beaconTimeKey;
		this.rpiLocationMapping = rpiLocationMapping;
//		this.xPos = xPos;
//		this.yPos = yPos;
	}

	public BeaconDetection(BeaconTimeKey beaconTimeKey, Map<String, Double> rpiLocationMapping) {
		this.beaconTimeKey = beaconTimeKey;
		this.rpiLocationMapping = rpiLocationMapping;
//		this.xPos = 0;
//		this.yPos = 0;
	}
}
