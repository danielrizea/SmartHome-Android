/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */

package upb.smarthome.restcomm;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import upb.smarthome.clusters.ClusterConstants;
import upb.smarthome.data.SmartHomeProvider;
import upb.smarthome.devices.DeviceConstants;
import upb.smarthome.devices.LogicalDevice;
import android.util.Log;

/**
 * The Class JSONParser.
 * Used to parse JSON content
 */
public class JSONParser {
	

	// extract a device ID from a JSON description
	/**
	 * Extract id.
	 *
	 * @param json the json
	 * @return the int
	 * @throws JSONException the jSON exception
	 */
	public static int extractId(JSONObject json) throws JSONException {
		return json.getInt("id");
	}
	// extract extAddress
	/**
	 * Extract ext address.
	 *
	 * @param json the json
	 * @return the string
	 * @throws JSONException the jSON exception
	 */
	public static String extractExtAddress(JSONObject json ) throws JSONException  {
		return json.getString("extAddress");
	}
	//extract endPoint
	/**
	 * Extract end point.
	 *
	 * @param json the json
	 * @return the int
	 * @throws JSONException the jSON exception
	 */
	public static int extractEndPoint(JSONObject json ) throws JSONException  {
		return json.getInt("endpoint");
	}
	//extract CLusterID
	/**
	 * Extract cluster id.
	 *
	 * @param json the json
	 * @return the int
	 * @throws JSONException the jSON exception
	 */
	public static int extractClusterID(JSONObject json ) throws JSONException  {
		return json.getInt("clusterID");
	}
	//extract Time stamp
	/**
	 * Extract timestamp.
	 *
	 * @param json the json
	 * @return the long
	 * @throws JSONException the jSON exception
	 */
	public static long extractTimestamp(JSONObject json ) throws JSONException  {
		return json.getLong("timestamp");
	}
	
	//get statusSet from a device
	/**
	 * Extract status set.
	 *
	 * @param json the json
	 * @return the jSON object
	 * @throws JSONException the jSON exception
	 */
	public static JSONObject extractStatusSet(JSONObject json ) throws JSONException {
		return json.getJSONObject("statusSet");
	}
	
	//extract a JSONArray attribute list
	/**
	 * Extract attributes.
	 *
	 * @param json the json
	 * @return the jSON array
	 * @throws JSONException the jSON exception
	 */
	public static JSONArray extractAttributes(JSONObject json)  throws JSONException {
				return json.getJSONArray("attributes");
	}
	
	
	// change an actuator setting and confirm
	/**
	 * Confirm setting.
	 *
	 * @param URI the uRI
	 * @return the string
	 * @throws JSONException the jSON exception
	 * @throws ClientProtocolException the client protocol exception
	 * @throws ConnectTimeoutException the connect timeout exception
	 */
	public static String confirmSetting(String URI) throws JSONException, ClientProtocolException, ConnectTimeoutException {
		JSONObject response = RestComm.restPut(URI);
		if (response != null) {
			return response.getJSONObject("actuator").getString("setting");
		}
		return null;
	}
	
	//get string to insert into database
	/**
	 * Gets the information for database initialization.
	 *
	 * @return the information for database initialization
	 * @throws JSONException the jSON exception
	 * @throws ClientProtocolException the client protocol exception
	 * @throws ConnectTimeoutException the connect timeout exception
	 */
	public static HashMap<String,String> getInformationForDatabaseInitialization()throws JSONException, ClientProtocolException, ConnectTimeoutException {
		
		HashMap<String, String> databaseInitialize = new HashMap<String, String>();
		
		String devicesS = "";
		String basic_clusters = "";
		String power_clusters = "";
		String on_off_clusters = "";
		String temperature_clusters = "";
		String flow_clusters = "";
		String thermostat_clusters = "";
		
		//devices so we can determine very fast if we have a device based on its extAddress and endpoint (they will act as a key for the HashMap)
		HashMap<String, LogicalDevice> devicesL  = new HashMap<String, LogicalDevice>();
		
		
		//this retrieves all statuses from all clusters on all servers, do it only once to initialize database
		//it is very expensive on CPU
		
		String uri = SmartHomeProvider.baseUri + "status/timestamp/latest.json";
		
		JSONObject responseJson = RestComm.restGet(uri);
		JSONArray result = responseJson.getJSONArray("statusSet");
		
		for(int i=0;i<result.length();i++){
			JSONObject status = result.getJSONObject(i);
			
			String extAddress = status.getString("extAddress"); 
			int endpoint = status.getInt("endpoint");
			String timestamp = status.getString("timestamp");
			int clusterID = status.getInt("clusterID");
			String deviceKey = extAddress+ "_" + endpoint;
			int id = status.getInt("id");
			
			//Log.d("debug_parser","attributes :" + status.getString("attributes") );
			//Log.d("debug_parser","cluster:" + clusterID );
			//Log.d("debug_parser","extAddress" + extAddress  );
			
			String att = status.getString("attributes");
			
			JSONObject attributes = new JSONObject();
			
			StringTokenizer tock = new StringTokenizer(att);
			
			if(att.equals("1")){
				//error in server ZigBee standard
				attributes.put(ClusterConstants.ON_OFF, "00");
			}else{ 
				//can't parseArray because key's are not String => Exception so use a tockenizer
				//this is not JSON standard => speak with professor and assistants for bug fix on server 
				while(tock.hasMoreElements()){
					
					String key = tock.nextToken(":[]{}, ");
					String value = tock.nextToken(":[]{}, ");
					
					attributes.put(key, value);
				}
			}
			//Log.d("debug_parser","New Attributes" + attributes.toString());
			switch(clusterID){
			
				case ClusterConstants.ID_BASIC_CLUSTER : {
			
					
					String location = attributes.getString(ClusterConstants.BASIC_LocationDescription);
					String zclVersion = attributes.getString(ClusterConstants.BASIC_ZCLVersion);
					String deviceEnabled = attributes.getString(ClusterConstants.BASIC_DeviceEnabled);
					String powerSource = attributes.getString(ClusterConstants.BASIC_PowerSource);
					
					
					
					
					if(basic_clusters == "")
						basic_clusters += "SELECT "+id+" AS '_id', '"
					+extAddress+ "' AS 'device_extAddress', "
								  +endpoint + " AS 'endpoint' ,"
		                          +timestamp+ " AS 'timestamp', '"
		                          +zclVersion+ "' AS 'ZCLVersion', '"
		                          +location+ "' AS 'LocationDescription', '"
								  +powerSource+ "' AS 'PowerSource', '"
								  +deviceEnabled+ "' AS 'DeviceEnabled' ";
					else
						basic_clusters += "UNION SELECT "+id+" AS '_id', '"
					+extAddress+ "' , "
								+endpoint + " AS 'endpoint' ,"
		                          +timestamp+ " , '"
		                          +zclVersion+ "' , '"
		                          +location+ "' , '"
								  +powerSource+ "' , '"
								  +deviceEnabled+ "'  ";
					
				}break;
				
				case ClusterConstants.ID_POWER_CONFIGURATION_CLUSTER : {
					
					String mainsVoltageH;
					String mainsFrequencyH;
					String batteryVoltageH;
					Double mainsVoltage=0d;
					Double mainsFrequency=0d;
					Double batteryVoltage=0d;
					
					if(attributes.opt(ClusterConstants.POWER_MainsVoltage) != null){
						mainsVoltageH = attributes.getString(ClusterConstants.POWER_MainsVoltage);
						mainsVoltage = Long.parseLong(mainsVoltageH, 16) * 100d;
					}else
						mainsVoltageH = "";	
					
					if(attributes.opt(ClusterConstants.POWER_MainsFrequency) != null){
						mainsFrequencyH = attributes.getString(ClusterConstants.POWER_MainsFrequency);
						mainsFrequency = Long.parseLong(mainsFrequencyH, 16) *1d;
					}else
						mainsFrequencyH = "";
					
					if(attributes.opt(ClusterConstants.POWER_BatteryVoltage) != null){
						batteryVoltageH = attributes.getString(ClusterConstants.POWER_BatteryVoltage);
						batteryVoltage = Long.parseLong(batteryVoltageH, 16) *100d;
					}else
						batteryVoltageH = "";
					
					
					
					if(power_clusters == "")
						power_clusters += "SELECT "+id+" AS '_id', '"
					+extAddress+ "' AS 'device_extAddress', "
								+endpoint + " AS 'endpoint' ,"
		                          +timestamp+ " AS 'timestamp', '"
		                          +mainsVoltage+ "' AS 'MainsVoltage', '"
		                          +mainsFrequency+ "' AS 'MainsFrequency', '"
								  +batteryVoltage+ "' AS 'BatteryVoltage' ";
					else
						power_clusters += "UNION SELECT "+id+" AS '_id', '"
					+extAddress+ "' , "
								+endpoint + " AS 'endpoint' ,"
		                          +timestamp+ " , '"
		                          +mainsVoltage+ "' , '"
		                          +mainsFrequency+ "' , '"
								  +batteryVoltage+ " '";
								  
					
				}break;
				
				case ClusterConstants.ID_ON_OFF_CLUSTER : {
					
					if(!devicesL.containsKey(deviceKey)){
						LogicalDevice dev = new LogicalDevice(extAddress,endpoint,DeviceConstants.TYPE_ON_OFF);
						devicesL.put(deviceKey, dev);
					}
					
					
					String st = attributes.getString(ClusterConstants.ON_OFF);
					
					String stat = Long.parseLong(st)+"";
					if(on_off_clusters == "")
						on_off_clusters +=  "SELECT "+id+" AS '_id', '"
					+extAddress+ "' AS 'device_extAddress', "
								+endpoint + " AS 'endpoint' ,"
		                          +timestamp+ " AS 'timestamp', '"
								  +stat+ "' AS 'status' ";
					else
						on_off_clusters +=  "UNION SELECT "+id+" AS '_id', '"
					+extAddress+ "', "
								+endpoint + " AS 'endpoint' ,"
		                          +timestamp+ ", '"
								  +stat+ "'";
					
				} break;
				
				case ClusterConstants.ID_TEMPERATURE_MEASUREMENT_CLUSTER : {
					
					if(!devicesL.containsKey(deviceKey)){
						LogicalDevice dev = new LogicalDevice(extAddress,endpoint,DeviceConstants.TYPE_TEMPERATURE_SENSOR);
						devicesL.put(deviceKey, dev);
					}
					
					
					String tempStringHex = attributes.getString(ClusterConstants.TEMPERATURE_MeasuredValue);
					Double temp = Long.parseLong(tempStringHex, 16)/100d;
					
					if(temperature_clusters == "")
						temperature_clusters += "SELECT "+id+" AS '_id', '"
					+extAddress+ "' AS 'device_extAddress', "
								+endpoint + " AS 'endpoint' ,"
		                          +timestamp+ " AS 'timestamp', '"
								  +temp+ "' AS 'MeasuredValue' ";
					else
						temperature_clusters += "UNION SELECT "+id+" AS '_id', '"
					+extAddress+ "' , "
								+endpoint + " AS 'endpoint' ,"
		                          +timestamp+ " , '"
								  +temp+ "'";	
					
					
				} break;
				
				case ClusterConstants.ID_FLOW_MESUREMENT_CLUSTER : {
					
					if(!devicesL.containsKey(deviceKey)){
						LogicalDevice dev = new LogicalDevice(extAddress,endpoint,DeviceConstants.TYPE_FLOW_METER);
						devicesL.put(deviceKey, dev);
					}
				
					String flowStringHex = attributes.getString(ClusterConstants.FLOW_MeasuredValue);
					Double flow = Long.parseLong(flowStringHex, 16)/100d;
					
					if(flow_clusters == "")
						flow_clusters += "SELECT "+id+" AS '_id', '"
					+extAddress+ "' AS 'device_extAddress', "
								+endpoint + " AS 'endpoint' ,"
		                          +timestamp+ " AS 'timestamp', '"
								  +flow+ "' AS 'MeasuredValue' ";
					else
						flow_clusters += "UNION SELECT "+id+" AS '_id', '"
					+extAddress+ "' , "
								+endpoint + "  ,"
		                          +timestamp+ " , '"
								  +flow+ "'";	
					
				} break;
				
				case ClusterConstants.ID_THERMOSTAT_CLUSTER : {
					if(!devicesL.containsKey(deviceKey)){
						LogicalDevice dev = new LogicalDevice(extAddress,endpoint,DeviceConstants.TYPE_THERMOSTAT);
						devicesL.put(deviceKey, dev);
					}
					
					Double localTemp;
					String localTempH;
					
					if(attributes.opt(ClusterConstants.THERMOSTAT_LocalTemperature) != null){
						localTempH = attributes.getString(ClusterConstants.THERMOSTAT_LocalTemperature);
					
						localTemp = Long.parseLong(localTempH, 16) / 100d;
					}
					else
						localTemp = -1D;
					
					String minH = attributes.getString(ClusterConstants.THERMOSTAT_MinHeatSetpointLimit);
					String maxH = attributes.getString(ClusterConstants.THERMOSTAT_MaxHeatSetpointLimit);
					
					
					Double min = Long.parseLong(minH, 16)/100d;
					Double max = Long.parseLong(maxH, 16)/100d;
					
					
					
					if(thermostat_clusters == "")
						thermostat_clusters += "SELECT "+id+" AS '_id', '"
					+extAddress+ "' AS 'device_extAddress', "
								+endpoint + " AS 'endpoint' ,"
		                          +timestamp+ " AS 'timestamp', '"
		                          +min+ "' AS 'MinHeatSetpointLimit', '"
		                          +max+ "' AS 'MaxHeatSetpointLimit', '"
								  +localTemp+ "' AS 'LocalTemperature' ";
					else
						thermostat_clusters += "UNION SELECT "+id+" AS '_id', '"
					+extAddress+ "', "
								+endpoint + " AS 'endpoint' ,"
		                          +timestamp+ " , '"
		                          +min+ "' , '"
		                          +max+ "' , '"
								  +localTemp+ "'  ";
					
				} break;
				
			}		
			//determine witch cluster the status corresponds to 	
		}
		//initialize string on devices
		Collection<LogicalDevice> col = devicesL.values();
		Iterator<LogicalDevice> it = col.iterator();
		int id=0;
		while(it.hasNext()){
			
			LogicalDevice dev = it.next();
			Log.d("debug_database",dev.extAddress + " id"+ id + dev.endPoint + " type" + dev.type );
			if(devicesS == "")
				devicesS += "SELECT "+id+" AS '_id', '"
						+dev.extAddress+ "' AS 'extAddress', "
                        +dev.endPoint+ " AS 'endpoint', "
						+dev.type+ " AS 'type' ";
			else
				devicesS += "UNION SELECT "+id+" AS '_id', '"
						+dev.extAddress+ "', "
                        +dev.endPoint+ " , "
						+dev.type+ " ";
			id++;
		}
		
		
		
		databaseInitialize.put("devices", devicesS);
		databaseInitialize.put("basic_clusters", basic_clusters);
		databaseInitialize.put("power_clusters",power_clusters);
		databaseInitialize.put("on_off_clusters",on_off_clusters);
		databaseInitialize.put("temperature_clusters",temperature_clusters);
		databaseInitialize.put("flow_clusters",flow_clusters);
		databaseInitialize.put("thermostat_clusters",thermostat_clusters);
		return databaseInitialize;
		
	}
	
}
