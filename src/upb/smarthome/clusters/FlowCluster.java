/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome.clusters;

import java.util.ArrayList;

/**
 * The Class FlowCluster.
 */
public class FlowCluster {

	/** The statuses. */
	public ArrayList<FlowClusterStatus> statuses;
	
	/** The current status. */
	public FlowClusterStatus currentStatus;
	
	/**
	 * Instantiates a new flow cluster.
	 */
	public FlowCluster(){
		
		statuses = new ArrayList<FlowClusterStatus>();
	}
}
