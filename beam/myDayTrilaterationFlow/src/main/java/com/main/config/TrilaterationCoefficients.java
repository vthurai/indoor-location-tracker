package com.main.config;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrilaterationCoefficients implements Serializable{

	private double a1;
	private double a2;
	private double a3;
	private double a4;
	private double a5;
	private double a6;
	private double b1;
	private double b2;
	private double b3;
	private double b4;
	
	@Builder
	public static TrilaterationCoefficients calculateCoefficients(double x1, double y1, double x2,
			double y2, double x3, double y3) {
		
		double a1 = (x2 - x1)/(y1 - y2);
		double a2 = 1 / (2 * (y1 - y2));
		double a3 = (Math.pow(x1, 2) - Math.pow(x2, 2) + Math.pow(y1, 2)
		- Math.pow(y2, 2)) / (2 * (y1 - y2));
		double a4 = (x3 - x2)/(y2 - y3);
		double a5 = 1 / (2 * (y2 - y3));
		double a6 = (Math.pow(x2, 2) - Math.pow(x3, 2) + Math.pow(y2, 2)
			- Math.pow(y3, 2)) / (2 * (y2 - y3));
		
		double b1 = a5 / (a1 - a4);
		double b2 = (a5 - a2) / (a1 - a4);
		double b3 = a2 / (a1 - a4);
		double b4 = (a6 - a3) / (a1 - a4);
		
		return new TrilaterationCoefficients(a1, a2, a3, a4, a5, a6, b1, b2, b3, b4);
	}
}
