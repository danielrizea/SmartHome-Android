/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome.clusters;

/**
 * The Class BasicClusterStatus.
 * Class used in ZigBee standard
 */
public class BasicClusterStatus {

	/** The Power source value. */
	public int PowerSourceValue;
	
	/** The Location description. */
	public String LocationDescription;
	
	/** The Device enabled. */
	public Boolean DeviceEnabled;
	
	/** The ZCL version. */
	public int ZCLVersion;
	
	/** The timestamp. */
	public long timestamp;
	
}
