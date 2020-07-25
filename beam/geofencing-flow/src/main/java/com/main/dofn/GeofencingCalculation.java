package com.main.dofn;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.DoFn.ProcessContext;
import org.apache.beam.sdk.transforms.DoFn.ProcessElement;
import org.apache.beam.sdk.values.KV;

import com.google.api.services.bigquery.model.TableRow;
import com.main.model.Station;
import com.main.model.UserCoordinates;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeofencingCalculation extends DoFn<KV<Station, Iterable<UserCoordinates>>, TableRow> {

    @ProcessElement
    public void processElement(ProcessContext c) {
        Station station = c.element().getKey();
        double xUpperLimit = station.getXPos() + station.getRadius();
        double xLowerLimit = station.getXPos() - station.getRadius();
        double yUpperLimit = station.getYPos() + station.getRadius();
        double yLowerLimit = station.getYPos() - station.getRadius();

        int occurrences = 0;
        for (UserCoordinates userPosition : c.element().getValue()) {
            if(userPosition.getXPos() <= xUpperLimit && userPosition.getXPos() >= xLowerLimit &&
                    userPosition.getYPos() <= yUpperLimit && userPosition.getYPos() >= yLowerLimit) {
                occurrences++;
                log.info(userPosition.toString());
            }
        }
        
        TableRow row = new TableRow()
                .set("uuid", c.element().getValue().iterator().next().getUuid())
                .set("station", c.element().getKey().getName())
                .set("occurrences", occurrences)
                .set("component", c.element().getKey().getComponent());
 
        c.output(row);
    }
}
