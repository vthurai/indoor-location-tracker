package com.main.dofn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.vendor.guava.v26_0_jre.com.google.common.collect.Iterators;

import com.google.api.services.bigquery.model.TableRow;
import com.google.common.collect.Iterables;
import com.main.model.BeaconDetection;
import com.main.model.BeaconTimeKey;
import com.main.model.Source;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class MergeAdvertisements extends DoFn<KV<BeaconTimeKey, Iterable<Source>>, BeaconDetection>{
    
	private final double measuredPower;
	private final double environmentalFactorN;
	private final TupleTag<TableRow> radiusValidationTupleTag;
    
	@ProcessElement
    public void processElement(ProcessContext c) {
	
	    try {
    		Map<String, List<Integer>> rssiListPerRssi = new HashMap<>();
    		Map<String, Double> aggregatedRssi = new HashMap<>();
    		for (Source source : c.element().getValue()) {
    			if(!rssiListPerRssi.containsKey(source.getRpi()))
    				rssiListPerRssi.put(source.getRpi(), new ArrayList<>());
    			rssiListPerRssi.get(source.getRpi()).add(source.getRssi());
    		}
    
    		if (rssiListPerRssi.size() == 3) {
    		    for(Entry<String, List<Integer>> rssiList: rssiListPerRssi.entrySet()) {
        			Double avgRssi = null;
//        			int size = Iterables.size(c.element().getValue());
        			if(Iterables.size(c.element().getValue()) >= 3) {
        			    avgRssi = getCleanAverage(rssiList.getValue(), c.element().getKey());
        			} else {
        			    avgRssi = rssiList.getValue().stream()
        			        .mapToInt(Integer::intValue)
        			        .average()
        			        .getAsDouble();
        			}
        			aggregatedRssi.put(rssiList.getKey(), avgRssi);
        		}
        		Map<String, Double> rpiLocationMapping = this.calculateDistance(aggregatedRssi);
        		
        		BeaconDetection beaconLocation = BeaconDetection.builder()
        				.beaconTimeKey(c.element().getKey())
        				.rpiLocationMapping(rpiLocationMapping)
        				.build();
        		
        		log.info(String.format("Time: %s | UUID: %s\nCollected RSSI: %s -> Average RSSI: %s"
        				+ "\nCalculated Radius: %s", c.element().getKey().getTimecollected(), c.element().getKey().getUuid(),
        				rssiListPerRssi, aggregatedRssi, rpiLocationMapping));
        		
        		for (Entry<String, Double> radius: rpiLocationMapping.entrySet()) {
        		    String rpi = radius.getKey();
        		    double avgRssi = aggregatedRssi.get(rpi);
        		    TableRow row = new TableRow()
        		            .set("uuid", c.element().getKey().getUuid())
        		            .set("timecollected", c.element().getKey().getTimecollected())
        		            .set("avgRSSI", avgRssi)
        		            .set("radius", radius.getValue())
        		            .set("rpi", rpi);
        		    c.output(radiusValidationTupleTag, row);        
        		}
        		
        		c.output(beaconLocation);
    		} 
    		else
    			log.warn(String.format("[Time: %s | UUID: %s] - Not all datapoints are available\n%s",
    			        c.element().getKey().getTimecollected(), c.element().getKey().getUuid(), rssiListPerRssi));
	    } catch(Exception e) {
	        log.error("Failed Merged Advertisement", e);
	    }
	}
	
	private Map<String, Double> calculateDistance(Map<String, Double> aggregatedRssi) {
		Map<String, Double> rpiDistance = new HashMap<>();
		
		for(Entry<String, Double> rpiRssi : aggregatedRssi.entrySet()) {
			double radius = Math.pow(10, 
					(measuredPower - rpiRssi.getValue()) / (10 * environmentalFactorN)); 
			rpiDistance.put(rpiRssi.getKey(), radius);
		}
		return rpiDistance;
	}
	
	private double getCleanAverage(List<Integer> values, BeaconTimeKey beaconTimeKey) {
	    
	    Collections.sort(values);
	    List<Integer> highValues = new ArrayList<>();
        List<Integer> lowValues = new ArrayList<>();
        if (values.size() % 2 == 0) {
            lowValues = values.subList(0, values.size() / 2);
            highValues = values.subList(values.size() / 2, values.size());
        } else {
            lowValues = values.subList(0, values.size() / 2);
            highValues = values.subList(values.size() / 2 + 1, values.size());
        }
        int q1 = getMedian(lowValues);
        int q3 = getMedian(highValues);
        double iqr = q3 - q1;
        double lowerFence = q1 - 1.5 * iqr;
        double upperFence = q3 + 1.5 * iqr;
        
        double sum = 0;
        int count = 0;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) >= lowerFence || values.get(i) <= upperFence) {
                sum += values.get(i);
                count++;
            } else
                log.warn(String.format("[Time: %s | UUID: %s] - Dropping outlier RSSI %s",
                        beaconTimeKey.getTimecollected(), beaconTimeKey.getUuid(), values.get(i)));
        }
	    return sum/count;
	}
	
	private int getMedian(List<Integer> values) {
	    Collections.sort(values);
	    if (values.size() % 2 == 0)
            return (values.get(values.size() / 2) + values.get(values.size() / 2 - 1)) / 2;
        else
            return values.get(values.size() / 2);
	}

}
