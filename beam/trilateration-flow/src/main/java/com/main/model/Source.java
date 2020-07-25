package com.main.model;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Source implements Serializable {

	private static final long serialVersionUID = 1L;
	private String uuid;
	private int rssi;
	private String timecollected;
	private String rpi;
	
}
