/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome.devices;

import upb.smarthome.clusters.FlowCluster;

/**
 * The Class FlowSensor.
 */
public class FlowSensor extends LogicalDevice{

	
	/** The flow cluster. */
	public FlowCluster flowCluster;
	
	/**
	 * Instantiates a new flow sensor.
	 *
	 * @param extAddString the ext add string
	 * @param endPoint the end point
	 * @param type the type
	 */
	public FlowSensor(String extAddString, int endPoint, int type) {
		super(extAddString, endPoint, type);
		// TODO Auto-generated constructor stub
		
		flowCluster = new FlowCluster();
	}

}
