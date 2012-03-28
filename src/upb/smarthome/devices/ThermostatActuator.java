/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome.devices;

import upb.smarthome.clusters.ThermostatCluster;

/**
 * The Class ThermostatActuator.
 */
public class ThermostatActuator extends LogicalDevice{

	
	/** The thermostat cluster. */
	public ThermostatCluster thermostatCluster;
	
	/**
	 * Instantiates a new thermostat actuator.
	 *
	 * @param extAddString the ext add string
	 * @param endPoint the end point
	 * @param type the type
	 */
	public ThermostatActuator(String extAddString, int endPoint, int type) {
		super(extAddString, endPoint, type);
		// TODO Auto-generated constructor stub
		
		thermostatCluster = new ThermostatCluster();
	}

}
