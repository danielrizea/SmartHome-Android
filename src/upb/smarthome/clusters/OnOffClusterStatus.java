/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome.clusters;

/**
 * The Class OnOffClusterStatus.
 */
public class OnOffClusterStatus {

	/** The status. */
	public int status;
	
	/** The timestamp. */
	public int timestamp;

	/**
	 * Instantiates a new on off cluster status.
	 *
	 * @param status the status
	 * @param timestamp the timestamp
	 */
	public OnOffClusterStatus(int status,int timestamp){
		
		this.status = status;
		this.timestamp = timestamp;
	}
}
