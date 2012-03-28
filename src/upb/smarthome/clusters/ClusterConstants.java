/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome.clusters;

/**
 * The Class ClusterConstants.
 */
public class ClusterConstants {

	//Cluster ID's used to get data from clusters 
	/** The Constant ID_BASIC_CLUSTER. */
	public static final int ID_BASIC_CLUSTER = 0;
	
	/** The Constant ID_POWER_CONFIGURATION_CLUSTER. */
	public static final int ID_POWER_CONFIGURATION_CLUSTER = 1;
	
	/** The Constant ID_TEMPERATURE_MEASUREMENT_CLUSTER. */
	public static final int ID_TEMPERATURE_MEASUREMENT_CLUSTER = 402;
	
	/** The Constant ID_ON_OFF_CLUSTER. */
	public static final int ID_ON_OFF_CLUSTER = 7;
	
	/** The Constant ID_FLOW_MESUREMENT_CLUSTER. */
	public static final int ID_FLOW_MESUREMENT_CLUSTER = 404;
	
	/** The Constant ID_THERMOSTAT_CLUSTER. */
	public static final int ID_THERMOSTAT_CLUSTER = 201;
	
	
	//Basic cluster ID attributes
	/** The Constant BASIC_ZCLVersion. */
	public static final String BASIC_ZCLVersion = "0000";
	
	/** The Constant BASIC_PowerSource. */
	public static final String BASIC_PowerSource = "0007";
	
	/** The Constant BASIC_LocationDescription. */
	public static final String BASIC_LocationDescription = "0010";
	
	/** The Constant BASIC_DeviceEnabled. */
	public static final String BASIC_DeviceEnabled = "0012";
	
	//Power Configuration Cluster
	/** The Constant POWER_MainsVoltage. */
	public static final String POWER_MainsVoltage = "0000";
	
	/** The Constant POWER_MainsFrequency. */
	public static final String POWER_MainsFrequency = "0001";
	
	/** The Constant POWER_BatteryVoltage. */
	public static final String POWER_BatteryVoltage = "0020";
	
	//Temperature measurement cluster
	/** The Constant TEMPERATURE_MeasuredValue. */
	public static final String TEMPERATURE_MeasuredValue = "0000";
	
	//On/Off cluster
	/** The Constant ON_OFF. */
	public static final String ON_OFF = "0000";
	
	//Flow measurement cluster
	/** The Constant FLOW_MeasuredValue. */
	public static final String FLOW_MeasuredValue = "0000";
	
	//Thermostat cluster
	/** The Constant THERMOSTAT_LocalTemperature. */
	public static final String THERMOSTAT_LocalTemperature = "0000";
	
	/** The Constant THERMOSTAT_MinHeatSetpointLimit. */
	public static final String THERMOSTAT_MinHeatSetpointLimit = "0015";
	
	/** The Constant THERMOSTAT_MaxHeatSetpointLimit. */
	public static final String THERMOSTAT_MaxHeatSetpointLimit = "0016";
	
}
