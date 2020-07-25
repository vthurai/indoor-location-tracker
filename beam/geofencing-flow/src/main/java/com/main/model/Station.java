package com.main.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Station implements Serializable{

    private String name;
    private double xPos;
    private double yPos;
    private double radius;
    private String component;
}
