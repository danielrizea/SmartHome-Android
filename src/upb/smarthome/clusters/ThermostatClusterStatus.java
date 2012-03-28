/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome.clusters;

/**
 * The Class ThermostatClusterStatus.
 */
public class ThermostatClusterStatus {

	/** The unit. */
	public static String unit = "°C";
	
	/** The Local temperature. */
	public float LocalTemperature;
	
	/** The Min heat setpoint limit. */
	public float MinHeatSetpointLimit;
	
	/** The Max heat setpoint limit. */
	public float MaxHeatSetpointLimit;
	
	/** The timestamp. */
	public int timestamp;
	
	/**
	 * Instantiates a new thermostat cluster status.
	 *
	 * @param localTemperature the local temperature
	 * @param minHeatSetpointLimit the min heat setpoint limit
	 * @param maxHeatSetpointLimit the max heat setpoint limit
	 * @param timestamp the timestamp
	 */
	public ThermostatClusterStatus(float localTemperature,
			float minHeatSetpointLimit, float maxHeatSetpointLimit,
			int timestamp) {
		super();
		LocalTemperature = localTemperature;
		MinHeatSetpointLimit = minHeatSetpointLimit;
		MaxHeatSetpointLimit = maxHeatSetpointLimit;
		this.timestamp = timestamp;
	}
	
}
