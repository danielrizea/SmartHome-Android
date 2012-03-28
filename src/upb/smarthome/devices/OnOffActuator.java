/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome.devices;

import upb.smarthome.clusters.OnOffCluster;

public class OnOffActuator extends LogicalDevice{

	public OnOffCluster onOffcluster;
	
	/**
	 * Instantiates a new on off actuator.
	 *
	 * @param extAddString the ext add string
	 * @param endPoint the end point
	 * @param type the type
	 */
	public OnOffActuator(String extAddString, int endPoint, int type) {
		super(extAddString, endPoint, type);
		// TODO Auto-generated constructor stub
		
		onOffcluster = new OnOffCluster();
	}

}
