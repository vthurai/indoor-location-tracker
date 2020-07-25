package com.main.dofn;

import java.util.ArrayList;
import java.util.List;

import org.apache.beam.sdk.coders.ListCoder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.state.StateSpec;
import org.apache.beam.sdk.state.StateSpecs;
import org.apache.beam.sdk.state.TimeDomain;
import org.apache.beam.sdk.state.Timer;
import org.apache.beam.sdk.state.TimerSpec;
import org.apache.beam.sdk.state.TimerSpecs;
import org.apache.beam.sdk.state.ValueState;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.main.model.BeaconTimeKey;
import com.main.model.Source;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatefulTimeBatching extends DoFn<KV<String, Source>, KV<BeaconTimeKey, Iterable<Source>>> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss").withZoneUTC();

    private final long DURATION;
    
    @StateId("payload")
    private final StateSpec<ValueState<List<Source>>> bufferedEvents = StateSpecs.value(ListCoder.of(SerializableCoder.of(Source.class)));

    @StateId("key")
    private final StateSpec<ValueState<BeaconTimeKey>> keyState = StateSpecs.value(SerializableCoder.of(BeaconTimeKey.class));
  
    @TimerId("stale")
    private final TimerSpec staleSpec = TimerSpecs.timer(TimeDomain.PROCESSING_TIME);
    
    public StatefulTimeBatching(long duration) {
        this.DURATION = duration;
    }
    
    @ProcessElement
    public void process(ProcessContext c,
        @StateId("payload") ValueState<List<Source>> bufferState,
        @StateId("key") ValueState<BeaconTimeKey> keyState,
        @TimerId("stale") Timer staleTimer) {
        
        if (bufferState.read() == null) {
            bufferState.write(new ArrayList<>());
            staleTimer.offset(Duration.standardSeconds(DURATION)).setRelative();
            keyState.write(new BeaconTimeKey(c.element().getKey(), FORMATTER.print(System.currentTimeMillis())));
            log.info(String.format("Initializing [Time: %s | UUID: %s]!", keyState.read().getTimecollected(), keyState.read().getUuid()));
        }
        
        bufferState.read().add(c.element().getValue());

    }
    
     @OnTimer("stale")
     public void onStale(OnTimerContext context,
             @StateId("payload") ValueState<List<Source>> bufferState,
             @StateId("key") ValueState<BeaconTimeKey> keyState) {
         
             if (bufferState.read().size() > 0) {
             log.info(String.format("[Time: %s | UUID: %s] Pushing %s records...",
                     keyState.read().getTimecollected(), keyState.read().getUuid(), bufferState.read().size()));
             context.output(KV.of(keyState.read(), bufferState.read()));
             }
             else {
                 log.error(String.format("[Time: %s | UUID: %s] No files were batched",
                     keyState.read().getTimecollected(), keyState.read().getUuid(), bufferState.read().size()));
             }
             bufferState.clear();
             keyState.clear();
        }
}