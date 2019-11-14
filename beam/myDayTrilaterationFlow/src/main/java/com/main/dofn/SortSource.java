package com.main.dofn;

import java.io.IOException;

import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.DoFn.ProcessContext;
import org.apache.beam.sdk.transforms.DoFn.ProcessElement;
import org.apache.beam.sdk.values.KV;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.model.BeaconTimeKey;
import com.main.model.Source;

public class SortSource extends DoFn<PubsubMessage, KV<String, Source>>{

	private ObjectMapper objectMapper;
	
	@Setup
	public void setup() {
		this.objectMapper = new ObjectMapper();
	}
	
	@ProcessElement
    public void processElement(ProcessContext c) throws JsonParseException, JsonMappingException, IOException {
		Source source = objectMapper.readValue(c.element().getPayload(), Source.class);
//		BeaconTimeKey instance = BeaconTimeKey.builder()
//				.uuid(source.getUuid())
//				.timecollected(source.getTimecollected())
//				.build();
		c.output(KV.of(source.getUuid(), source));
	}
}
