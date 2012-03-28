/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome.clusters;

import java.util.ArrayList;

/**
 * The Class OnOffCluster.
 */
public class OnOffCluster {

	/** The statuses. */
	public ArrayList<OnOffClusterStatus> statuses;
	
	/** The current status. */
	public OnOffClusterStatus currentStatus;
	
	/**
	 * Instantiates a new on off cluster.
	 */
	public OnOffCluster(){
		
		statuses = new ArrayList<OnOffClusterStatus>();
	}
}
