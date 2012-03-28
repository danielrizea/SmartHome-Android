/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome.clusters;

import java.util.ArrayList;

/**
 * The Class TemperatureCluster.
 */
public class TemperatureCluster {

	/** The statuses. */
	public ArrayList<TemperatureClusterStatus> statuses;
	
	/** The current status. */
	public TemperatureClusterStatus currentStatus;
	
	/**
	 * Instantiates a new temperature cluster.
	 */
	public TemperatureCluster(){
	
		statuses = new ArrayList<TemperatureClusterStatus>();
	}
}
