/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome.clusters;

public class TemperatureClusterStatus {

	public static String unit = "°C";
	
	public float measuredValue;
	public int timestamp;
	
	/**
	 * Instantiates a new temperature cluster status.
	 *
	 * @param measuredValue the measured value
	 * @param timestamp the timestamp
	 */
	public TemperatureClusterStatus(float measuredValue,int timestamp) {
		super();
		this.measuredValue = measuredValue;
		this.timestamp = timestamp;
	}
	
}
