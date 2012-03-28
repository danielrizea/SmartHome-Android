/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome.devices;

import upb.smarthome.clusters.TemperatureCluster;

/**
 * The Class TemperatureSensor.
 */
public class TemperatureSensor extends LogicalDevice{

	
	/** The temp cluster. */
	public TemperatureCluster tempCluster;
	
	/**
	 * Instantiates a new temperature sensor.
	 *
	 * @param extAddString the ext add string
	 * @param endPoint the end point
	 * @param type the type
	 */
	public TemperatureSensor(String extAddString, int endPoint, int type) {
		super(extAddString, endPoint, type);
		// TODO Auto-generated constructor stub
		
		tempCluster = new TemperatureCluster();
	}

	
}
