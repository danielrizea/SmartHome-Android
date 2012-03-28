/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome.clusters;

import java.util.ArrayList;

public class ThermostatCluster {

	public ArrayList<ThermostatClusterStatus> statuses ;
	
	public ThermostatClusterStatus currentStatus;
	
	/**
	 * Instantiates a new thermostat cluster.
	 */
	public ThermostatCluster(){
		
		statuses = new ArrayList<ThermostatClusterStatus>();
	}
}
