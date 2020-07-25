package com.main.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Source {
	
	private String uuid;
	private int rssi;
	private String timecollected;
	private String rpi;
	
}
