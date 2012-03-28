/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome.devices;

import upb.smarthome.clusters.BasicCluster;
import upb.smarthome.clusters.PowerCluster;

/**
 * The Class LogicalDevice.
 */
public class LogicalDevice {

	/** The ext address. */
	public String extAddress;
	
	/** The end point. */
	public int endPoint = -1;
	
	/** The type. */
	public int type;
	
	/** The unit. */
	public String unit;
	
	//basic clusters
	/** The basic cluster. */
	private BasicCluster basicCluster;
	
	/** The power cluster. */
	private PowerCluster powerCluster;
	
	/** The position. */
	public String position ;
	
	/**
	 * Instantiates a new logical device.
	 *
	 * @param extAddress the ext address
	 * @param endPoint the end point
	 * @param type the type
	 */
	public LogicalDevice(String extAddress, int endPoint, int type){
		
		this.extAddress = extAddress;
		this.endPoint = endPoint;
		this.type = type;
		
		basicCluster = new BasicCluster();
		powerCluster = new PowerCluster();
		
	}
	
	/**
	 * Gets the unit.
	 *
	 * @return the unit
	 */
	public String getUnit(){
		return unit;
	}
	
}
