package com.main.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserCoordinates implements Serializable {

    private String uuid;
    private String timecollected;
    private double xPos;
    private double yPos;
}
