package com.main.dofn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;

import com.main.model.Station;
import com.main.model.UserCoordinates;

public class SortUserPerStation extends DoFn<KV<String, Iterable<UserCoordinates>>, KV<Station, Iterable<UserCoordinates>>> {

    private final List<Station> stations = new ArrayList<>(Arrays.asList(
            Station.builder().name("Booth").xPos(-0.25).yPos(1.75).radius(1).component("Amazing").build(),
            Station.builder().name("Reading Area").xPos(0).yPos(7).radius(1.5).component("Learning").build(),
            Station.builder().name("Toy Area").xPos(-0.5).yPos(8).radius(1.5).component("Fun").build(),
            Station.builder().name("Music Area").xPos(0).yPos(7.5).radius(2).component("Learning").build(),
            Station.builder().name("Build Blocks").xPos(0).yPos(-5).radius(1).component("Fun").build()));
    
    @ProcessElement
    public void processElement(ProcessContext c) {
        //Call firestore
        //Parse into a List of Stations
        //Output one at a time
        for (Station station : stations) {
            c.output(KV.of(station, c.element().getValue()));
        }
    }
    
}
