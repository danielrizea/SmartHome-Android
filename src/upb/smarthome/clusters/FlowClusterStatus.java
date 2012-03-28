/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome.clusters;

/**
 * The Class FlowClusterStatus.
 */
public class FlowClusterStatus {

	/** The unit. */
	public static String unit = "l";
	
	/** The measured value. */
	public float measuredValue;
	
	/** The timestamp. */
	public int timestamp;
	
	/**
	 * Instantiates a new flow cluster status.
	 *
	 * @param measuredValue the measured value
	 * @param timestamp the timestamp
	 */
	public FlowClusterStatus(float measuredValue, int timestamp) {
		super();
		this.measuredValue = measuredValue;
		this.timestamp = timestamp;
	}
	
	
}
