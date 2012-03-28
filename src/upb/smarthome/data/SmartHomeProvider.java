/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */

package upb.smarthome.data;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import upb.smarthome.clusters.ClusterConstants;
import upb.smarthome.restcomm.JSONParser;
import upb.smarthome.restcomm.RestComm;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

/**
 * The Class SmartHomeProvider.
 * ContentProvider for resources on the smart home server. Also has an SQLite local cache.
 */

public class SmartHomeProvider extends ContentProvider {
	
	
	
	/** The updates enabled. */
	private static boolean updatesEnabled = false;
	
	/** The ui handler. */
	private static Handler uiHandler = null;
	
	/** The ui runnable. */
	private static Runnable uiRunnable = null;
	
	/** The exec. */
	public static ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(2);
	
	/** The smart home provicer. */
	public static SmartHomeProvider smartHomeProvicer;
	// Remove server initialization
    /** The Constant protocol. */
	public static final String protocol = "http://";
    
    /** The Constant host. */
    public static final String host = "embedded.cs.pub.ro/";
    
    /** The Constant serverURL. */
    public static final String serverURL = "si/zigbee/";
    
    /** The Constant status. */
    public static final String status = "status/";
    
    /** The Constant cluster. */
    public static final String cluster = "cluster/";
    		
    /** The content resolver. */
    public static ContentResolver contentResolver;
    
    /** The scheduled task. */
    public static ScheduledFuture scheduledTask = null;
    //5 seconds update latest data
    /** The Constant SCHEDULE_LATEST_DATA_REFRESH_RATE. */
    public static final int SCHEDULE_LATEST_DATA_REFRESH_RATE = 5;
    
    /** The Constant baseUri. */
    public static final String baseUri = "http://embedded.cs.pub.ro/si/zigbee/";
    
	   
	/** The Constant PROVIDER_NAME. */
	public static final String PROVIDER_NAME = "upb.smarthome.data.SmartHomeProvider";
	
	/** The Constant CONTENT_URI_DEVICES. */
	public static final Uri CONTENT_URI_DEVICES = Uri.parse("content://"+ PROVIDER_NAME + "/devices");
	
	/** The Constant CONTENT_URI_DEVICE_CLUSTERS. */
	public static final Uri CONTENT_URI_DEVICE_CLUSTERS = Uri.parse("content://"+ PROVIDER_NAME + "/device");
	
	/** The Constant CONTENT_URI_DEVICE_CLUSTERS_LAST. */
	public static final Uri CONTENT_URI_DEVICE_CLUSTERS_LAST = Uri.parse("content://"+ PROVIDER_NAME + "/device");
	
	/** The Constant CONTENT_URI_ALL. */
	public static final Uri CONTENT_URI_ALL = Uri.parse("content://"+ PROVIDER_NAME );
	
	/** The Constant DEBUG. */
	public static final String DEBUG = "debug_database";
	// column names
	/** The Constant _ID1. */
	public static final String _ID1 = "_id";
	
	/** The Constant NAME. */
	public static final String NAME = "name";
    
    /** The Constant LOCATION. */
    public static final String LOCATION = "location";
 
    /** The Constant _ID2. */
    public static final String _ID2 = "_id";
    
    /** The Constant IDSENSOR. */
    public static final String IDSENSOR = "idSenzor";

    /** The Constant VALUE. */
    public static final String VALUE = "value";
	
	/** The Constant _ID3. */
	public static final String _ID3 = "_id";
    
    /** The Constant SETTING. */
    public static final String SETTING = "setting";
 
    /** The Constant BatteryVoltage. */
    public static final String BatteryVoltage = "BatteryVoltage";
    
    /** The Constant TYPE. */
    public static final String TYPE = "type";
    
    /** The Constant ZCLVersion. */
    public static final String ZCLVersion = "ZCLVersion";
    
    /** The Constant PowerSource. */
    public static final String PowerSource = "PowerSource";
    
    /** The Constant LocationDescription. */
    public static final String LocationDescription = "LocationDescription";
    
    /** The Constant DeviceEnabled. */
    public static final String DeviceEnabled = "DeviceEnabled";
    
    /** The Constant Timestamp. */
    public static final String Timestamp = "timestamp";
    
    /** The Constant ExtAddress. */
    public static final String ExtAddress = "extAddress";
    
    /** The Constant Device_extAddress. */
    public static final String Device_extAddress = "device_extAddress";
    
    /** The Constant Endpoint. */
    public static final String Endpoint = "endpoint";
    
    /** The Constant MeasuredValue. */
    public static final String MeasuredValue = "MeasuredValue";
    
    /** The Constant LocalTemperature. */
    public static final String LocalTemperature = "LocalTemperature";
    
    /** The Constant MinHeat. */
    public static final String MinHeat = "MinHeatSetpointLimit";
    
    /** The Constant MaxHeat. */
    public static final String MaxHeat = "MaxHeatSetpointLimit";
    
    /** The Constant On_Off_Status. */
    public static final String On_Off_Status = "status";
    
    /** The Constant CLUSTER_ID. */
    public static final String CLUSTER_ID="clusterID";
    // routes
    
    
    
    /** The Constant DEVICES. */
    private static final int DEVICES = 0;
    
    /** The Constant DEVICE_CLUSTER_VALUES. */
    private static final int DEVICE_CLUSTER_VALUES = 1;
    
    /** The Constant DEVICE_CLUSTER_LAST_VALUE. */
    private static final int DEVICE_CLUSTER_LAST_VALUE =2;
    
    /** The Constant UPDATE_ON_OFF_SWITCH. */
    private static final int UPDATE_ON_OFF_SWITCH = 3;
    
    /** The Constant UPDATE_DEVICE_CLUSTER. */
    private static final int UPDATE_DEVICE_CLUSTER = 4;
    
    /** The Constant UPDATE_THERMOSTAT. */
    private static final int UPDATE_THERMOSTAT = 5; 
    
    /** The Constant DEVICE_TYPE_ALL. */
    private static final int DEVICE_TYPE_ALL = 6;
    
    /** The Constant DEVICE_GET_LAST_10_VALUES. */
    private static final int DEVICE_GET_LAST_10_VALUES = 7;
    
    /** The Constant DEVICES_GET_CLUSTER_LAST. */
    private static final int DEVICES_GET_CLUSTER_LAST = 8;

    // UriMatcher
    /** The Constant uriMatcher. */
    private static final UriMatcher uriMatcher;
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		
		uriMatcher.addURI(PROVIDER_NAME, "devices", DEVICES);
		uriMatcher.addURI(PROVIDER_NAME, "device/*/#/cluster/#", DEVICE_CLUSTER_VALUES);
		uriMatcher.addURI(PROVIDER_NAME,"device/*/#/cluster/#/last",DEVICE_CLUSTER_LAST_VALUE);
		uriMatcher.addURI(PROVIDER_NAME, "devices/type/#", DEVICE_TYPE_ALL);
		uriMatcher.addURI(PROVIDER_NAME,"device/*/#/switch/#",UPDATE_ON_OFF_SWITCH);
		uriMatcher.addURI(PROVIDER_NAME,"device/*/#/cluster/#/last_10_values",DEVICE_GET_LAST_10_VALUES);
		uriMatcher.addURI(PROVIDER_NAME,"device/*/#/thermostat",UPDATE_THERMOSTAT);
		uriMatcher.addURI(PROVIDER_NAME,"cluster/#",DEVICES_GET_CLUSTER_LAST);
	}
   
	//---for database use---
	/** The smarthome db. */
	public static SQLiteDatabase smarthomeDB = null;
	
	/** The Constant DATABASE_NAME. */
	private static final String DATABASE_NAME = "SmartHomeZigBee";
	
	/** The Constant DATABASE_TABLE_DEVICES. */
	private static final String DATABASE_TABLE_DEVICES = "devices";
	
	/** The Constant DATABASE_TABLE_BASIC_CLUSTER_STATUSES. */
	private static final String DATABASE_TABLE_BASIC_CLUSTER_STATUSES = "basic_clusters";
	
	/** The Constant DATABASE_TABLE_POWER_CLUSTER_STATUSES. */
	private static final String DATABASE_TABLE_POWER_CLUSTER_STATUSES = "power_clusters";
	
	/** The Constant DATABASE_TABLE_TEMPERATURE_CLUSTER_STATUSES. */
	private static final String DATABASE_TABLE_TEMPERATURE_CLUSTER_STATUSES = "temperature_clusters";
	
	/** The Constant DATABASE_TABLE_ON_OFF_CLUSTERS_STATUSES. */
	private static final String DATABASE_TABLE_ON_OFF_CLUSTERS_STATUSES = "on_off_clusters";
	
	/** The Constant DATABASE_TABLE_FLOW_CLUSTERS_STATUSES. */
	private static final String DATABASE_TABLE_FLOW_CLUSTERS_STATUSES = "flow_clusters";
	
	/** The Constant DATABASE_TABLE_THERMOSTAT_CLUSTER_STATUSES. */
	private static final String DATABASE_TABLE_THERMOSTAT_CLUSTER_STATUSES = "thermostat_clusters";
	

	/** The Constant DATABASE_VERSION. */
	private static final int DATABASE_VERSION = 1;
		   
		   /**
   		 * The Class DatabaseHelper.
   		 */
   		private static class DatabaseHelper extends SQLiteOpenHelper 
		   {
		      
      		/**
      		 * Instantiates a new database helper.
      		 *
      		 * @param context the context
      		 */
      		DatabaseHelper(Context context) {
		         super(context, DATABASE_NAME, null, DATABASE_VERSION);
		      }

		      /* (non-Javadoc)
      		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
      		 */
      		@Override
		      public void onCreate(SQLiteDatabase db)
		      {
		      }
		      
		      /* (non-Javadoc)
      		 * @see android.database.sqlite.SQLiteOpenHelper#onOpen(android.database.sqlite.SQLiteDatabase)
      		 */
      		@Override
		      public void onOpen(SQLiteDatabase db)
		      {
		    	  // Hack-ish initialisation. In a real case a smarter initialization is needed.
		    	  
		    	  
		    	  //create tables 
		    	  db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_DEVICES );
		    	  db.execSQL("CREATE TABLE " + DATABASE_TABLE_DEVICES + " (_id" +
		    	  		" INTEGER PRIMARY KEY AUTOINCREMENT," + "extAddress text, endpoint INTEGER, type INTEGER);");
		    	  
		    	  db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_BASIC_CLUSTER_STATUSES );
		    	  db.execSQL("CREATE TABLE " + DATABASE_TABLE_BASIC_CLUSTER_STATUSES + " (_id" +
			    	  		" INTEGER PRIMARY KEY AUTOINCREMENT," + " device_extAddress text,endpoint INTEGER,timestamp INTEGER, ZCLVersion text, LocationDescription text, PowerSource text,  DeviceEnabled text);");
			    	  
		    	  
		    	  db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_FLOW_CLUSTERS_STATUSES );
		    	  db.execSQL("CREATE TABLE " + DATABASE_TABLE_FLOW_CLUSTERS_STATUSES + " (_id" +
			    	  		" INTEGER PRIMARY KEY AUTOINCREMENT," + "device_extAddress text, endpoint INTEGER,timestamp INTEGER,MeasuredValue text );");
			    	  
		    	  
		    	  db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_POWER_CLUSTER_STATUSES );
		    	  db.execSQL("CREATE TABLE " + DATABASE_TABLE_POWER_CLUSTER_STATUSES + " (_id" +
			    	  		" INTEGER PRIMARY KEY AUTOINCREMENT," + "device_extAddress text, endpoint INTEGER,timestamp INTEGER,MainsVoltage text, MainsFrequency text, BatteryVoltage text );");
			    	
		    	  
		    	  db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ON_OFF_CLUSTERS_STATUSES );
		    	  db.execSQL("CREATE TABLE " + DATABASE_TABLE_ON_OFF_CLUSTERS_STATUSES + " (_id" +
			    	  		" INTEGER PRIMARY KEY AUTOINCREMENT," + "device_extAddress text, endpoint INTEGER,timestamp INTEGER,status text );");
			    	
		    	  
		    	  db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_TEMPERATURE_CLUSTER_STATUSES );
		    	  db.execSQL("CREATE TABLE " + DATABASE_TABLE_TEMPERATURE_CLUSTER_STATUSES + " (_id" +
			    	  		" INTEGER PRIMARY KEY AUTOINCREMENT," + "device_extAddress text, endpoint INTEGER,timestamp INTEGER ,MeasuredValue text );");
			    	
		    	  
		    	  db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_THERMOSTAT_CLUSTER_STATUSES );
		    	  db.execSQL("CREATE TABLE " + DATABASE_TABLE_THERMOSTAT_CLUSTER_STATUSES + " (_id" +
			    	  		" INTEGER PRIMARY KEY AUTOINCREMENT," + "device_extAddress text, endpoint INTEGER, timestamp INTEGER, MinHeatSetpointLimit text, MaxHeatSetpointLimit text, LocalTemperature text);");

		    	  Log.d(DEBUG,"Tables created");	
		    	  
				  Log.d(DEBUG,"Start parsing for initial data ");

						
						HashMap<String,String> info = null;
				    	  try{
				    		  
					          info = JSONParser.getInformationForDatabaseInitialization();
				    	  }catch(Exception e){
				    		  Log.d("debug_database","Could not parse all info " + e.getMessage());
				    	  }
				    	  
				    	  Log.d("database","initial data from server");
				    	 
				    	  try {		  
				    		  
				    		  
				    		  db.execSQL("INSERT INTO "+DATABASE_TABLE_DEVICES+" "+info.get("devices"));
				    		  db.execSQL("INSERT INTO "+DATABASE_TABLE_BASIC_CLUSTER_STATUSES+" "+info.get("basic_clusters"));
				    		  db.execSQL("INSERT INTO "+DATABASE_TABLE_POWER_CLUSTER_STATUSES+" "+info.get("power_clusters"));
				    		  db.execSQL("INSERT INTO "+DATABASE_TABLE_ON_OFF_CLUSTERS_STATUSES+" "+info.get("on_off_clusters"));
				    		  db.execSQL("INSERT INTO "+DATABASE_TABLE_FLOW_CLUSTERS_STATUSES+" "+info.get("flow_clusters"));
				    		  db.execSQL("INSERT INTO "+DATABASE_TABLE_TEMPERATURE_CLUSTER_STATUSES+" "+info.get("temperature_clusters"));
				    		  db.execSQL("INSERT INTO "+DATABASE_TABLE_THERMOSTAT_CLUSTER_STATUSES+" "+info.get("thermostat_clusters"));
				    		  
				    		  
				          } catch (Exception e) {
				        	  Log.d("debug_database","Could not create database " + e.getMessage());
				              e.printStackTrace();
				          }

		    	  
		      }
		      
		      /* (non-Javadoc)
      		 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
      		 */
      		@Override
		      public void onUpgrade(SQLiteDatabase db, int oldVersion, 
		      int newVersion) {
		         Log.w("Content provider database", 
		              "Upgrading database from version " + 
		              oldVersion + " to " + newVersion + 
		              ", which will destroy all old data");
		         db.execSQL("DROP TABLE IF EXISTS " +DATABASE_TABLE_BASIC_CLUSTER_STATUSES);
		         db.execSQL("DROP TABLE IF EXISTS " +DATABASE_TABLE_DEVICES);
		         db.execSQL("DROP TABLE IF EXISTS " +DATABASE_TABLE_FLOW_CLUSTERS_STATUSES);
		         db.execSQL("DROP TABLE IF EXISTS " +DATABASE_TABLE_ON_OFF_CLUSTERS_STATUSES);
		         db.execSQL("DROP TABLE IF EXISTS " +DATABASE_TABLE_POWER_CLUSTER_STATUSES);
		         db.execSQL("DROP TABLE IF EXISTS " +DATABASE_TABLE_TEMPERATURE_CLUSTER_STATUSES);
		         db.execSQL("DROP TABLE IF EXISTS " +DATABASE_TABLE_THERMOSTAT_CLUSTER_STATUSES);
		         onCreate(db);
		      }
		   }   
		   
		   
		   
		   
		   /* (non-Javadoc)
   		 * @see android.content.ContentProvider#getType(android.net.Uri)
   		 */
   		@Override
		   public String getType(Uri uri) {
		      switch (uriMatcher.match(uri)){
		      	//--get all devices --
		      	case DEVICES:
		      		 return PROVIDER_NAME + "/devices";
		      	case DEVICE_CLUSTER_VALUES:
		      		 return PROVIDER_NAME + "/device/*/#/cluster/#";
		      	case DEVICE_CLUSTER_LAST_VALUE:
		      		return PROVIDER_NAME + "/device/*/#/cluster/#/last";
		      	case DEVICE_TYPE_ALL:
		      		return PROVIDER_NAME + "/devices/type/#";
		      	case DEVICE_GET_LAST_10_VALUES :
		      		return PROVIDER_NAME + "/device/*/#/cluster/#/last_10_values";
		      	case UPDATE_ON_OFF_SWITCH :
		      		return PROVIDER_NAME + "/device/*/#/switch/#";
		      	case UPDATE_THERMOSTAT :
		      		return PROVIDER_NAME + "/device/*/#/thermostat";
		      	case DEVICES_GET_CLUSTER_LAST :
		      		return PROVIDER_NAME + "/cluster/#";
		      
		        
		        default:
		            throw new IllegalArgumentException("Unsupported URI: " + uri);        
		      }   
		   }
		   
		   /* (non-Javadoc)
   		 * @see android.content.ContentProvider#onCreate()
   		 */
   		@Override
		   public boolean onCreate() {
		      Context context = getContext();
		      DatabaseHelper dbHelper = new DatabaseHelper(context);
		      smarthomeDB = dbHelper.getWritableDatabase();
		      smartHomeProvicer = this;
		      return (smarthomeDB == null)? false:true;
		   }
		   
		   /* (non-Javadoc)
   		 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
   		 */
   		@Override
		   public Cursor query(Uri uri, String[] projection, String selection,
		      String[] selectionArgs, String sortOrder) {
		           
		      SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		      
		      switch (uriMatcher.match(uri)){
			      case DEVICES:
			    	  sqlBuilder.setTables(DATABASE_TABLE_DEVICES);
			    	  if (sortOrder==null || sortOrder=="") sortOrder = null;
			    	  break;
			      case DEVICE_CLUSTER_VALUES:
			    	  
			    	  switch(Integer.parseInt(uri.getPathSegments().get(4))){
			    	  
				    	  case ClusterConstants.ID_BASIC_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_BASIC_CLUSTER_STATUSES);} break;
				    	  
				    	  case ClusterConstants.ID_FLOW_MESUREMENT_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_FLOW_CLUSTERS_STATUSES);} break;
				    	  
				    	  case ClusterConstants.ID_ON_OFF_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_ON_OFF_CLUSTERS_STATUSES);} break;
				    	  
				    	  case ClusterConstants.ID_POWER_CONFIGURATION_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_POWER_CLUSTER_STATUSES);} break;
				    	  
				    	  case ClusterConstants.ID_TEMPERATURE_MEASUREMENT_CLUSTER  : {sqlBuilder.setTables(DATABASE_TABLE_TEMPERATURE_CLUSTER_STATUSES);} break;
				    	  
				    	  case ClusterConstants.ID_THERMOSTAT_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_THERMOSTAT_CLUSTER_STATUSES);} break;
				    	  
			    	  }

			    	  sqlBuilder.appendWhere(" device_extAddress" + " = '" + uri.getPathSegments().get(1)+"'");
			    	  sqlBuilder.appendWhere(" AND endpoint" + " = " + uri.getPathSegments().get(2));
			    	  if (sortOrder==null || sortOrder=="") sortOrder = "timestamp";
			    	  break;
			      case DEVICE_CLUSTER_LAST_VALUE: {
			    	  
			    	  String table = "";
			    	  
			    	  switch(Integer.parseInt(uri.getPathSegments().get(4))){
			    	  
			    	  case ClusterConstants.ID_BASIC_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_BASIC_CLUSTER_STATUSES);
			    	  table = DATABASE_TABLE_BASIC_CLUSTER_STATUSES;
			    	  } break;
			    	  
			    	  case ClusterConstants.ID_FLOW_MESUREMENT_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_FLOW_CLUSTERS_STATUSES);
			    	  table = DATABASE_TABLE_FLOW_CLUSTERS_STATUSES;
			    	  } break;
			    	  
			    	  case ClusterConstants.ID_ON_OFF_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_ON_OFF_CLUSTERS_STATUSES);
			    	  table = DATABASE_TABLE_ON_OFF_CLUSTERS_STATUSES;
			    	  } break;
			    	  
			    	  case ClusterConstants.ID_POWER_CONFIGURATION_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_POWER_CLUSTER_STATUSES);
			    	  table = DATABASE_TABLE_POWER_CLUSTER_STATUSES;
			    	  } break;
			    	  
			    	  case ClusterConstants.ID_TEMPERATURE_MEASUREMENT_CLUSTER  : {sqlBuilder.setTables(DATABASE_TABLE_TEMPERATURE_CLUSTER_STATUSES);
			    	  table = DATABASE_TABLE_TEMPERATURE_CLUSTER_STATUSES;
			    	  } break;
			    	  
			    	  case ClusterConstants.ID_THERMOSTAT_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_THERMOSTAT_CLUSTER_STATUSES);
			    	  table = DATABASE_TABLE_THERMOSTAT_CLUSTER_STATUSES;
			    	  } break;
			    	  
			    	  }
			    	  
			    	  sqlBuilder.appendWhere("device_extAddress" + " = '" + uri.getPathSegments().get(1)+"'");
			    	 sqlBuilder.appendWhere(" AND endpoint" + " = '" + uri.getPathSegments().get(2)+"'");
			    	  
			    	  Log.d("debug_databse","SQL " +uri.getPathSegments().get(1)+" "+ uri.getPathSegments().get(2) + " " + table) ;
			    	  
			    	  //sort and return latest limit
			    	  Cursor c = sqlBuilder.query(smarthomeDB, projection, selection, selectionArgs, null,null, "timestamp DESC");
			    	  
			    	  
			    	  
				      //---register to watch a content URI for changes---
				      c.setNotificationUri(getContext().getContentResolver(), uri);
				      return c;
			      }
			      case DEVICE_GET_LAST_10_VALUES : {
			    	  
			    	  	String table = "";
			    	  
			    	  String uriGet = baseUri + status + "device/"	+ uri.getPathSegments().get(1) + "/"
			    	  + uri.getPathSegments().get(2)+"/cluster/"+uri.getPathSegments().get(4)+".json";
			    	  
			    	  //recuperare si inserare daca nu exista in baza de date a ultimelor valori de pe server
			    	  
			    	  try{
			    		  processJSONSqliteResponse(uriGet);	
			    	  }
			    	  catch(Exception e){
			    		  Log.d("latest_values","Exception in retrieving latest values :" + e.getMessage() );
			    	  }
			    	  switch(Integer.parseInt(uri.getPathSegments().get(4))){
			    	  
			    	  case ClusterConstants.ID_BASIC_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_BASIC_CLUSTER_STATUSES);
			    	  table = DATABASE_TABLE_BASIC_CLUSTER_STATUSES;
			    	  } break;
			    	  case ClusterConstants.ID_FLOW_MESUREMENT_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_FLOW_CLUSTERS_STATUSES);
			    	  table = DATABASE_TABLE_FLOW_CLUSTERS_STATUSES;
			    	  } break;
			    	  case ClusterConstants.ID_ON_OFF_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_ON_OFF_CLUSTERS_STATUSES);
			    	  table = DATABASE_TABLE_ON_OFF_CLUSTERS_STATUSES;
			    	  } break;
			    	  case ClusterConstants.ID_POWER_CONFIGURATION_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_POWER_CLUSTER_STATUSES);
			    	  table = DATABASE_TABLE_POWER_CLUSTER_STATUSES;
			    	  } break;
			    	  case ClusterConstants.ID_TEMPERATURE_MEASUREMENT_CLUSTER  : {sqlBuilder.setTables(DATABASE_TABLE_TEMPERATURE_CLUSTER_STATUSES);
			    	  table = DATABASE_TABLE_TEMPERATURE_CLUSTER_STATUSES;
			    	  } break;
			    	  case ClusterConstants.ID_THERMOSTAT_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_THERMOSTAT_CLUSTER_STATUSES);
			    	  table = DATABASE_TABLE_THERMOSTAT_CLUSTER_STATUSES;
			    	  } break;
			    	  
			    	  }
			    	  
			    	 sqlBuilder.appendWhere("device_extAddress" + " = '" + uri.getPathSegments().get(1)+"'");
			    	 sqlBuilder.appendWhere(" AND endpoint" + " = '" + uri.getPathSegments().get(2)+"'");
			    	  
			    	  Log.d("debug_databse","SQL " +uri.getPathSegments().get(1)+" "+ uri.getPathSegments().get(2) + " " + table) ;
			    	  
			    	  //sort and return latest limit
			    	  Cursor c = sqlBuilder.query(smarthomeDB, projection, selection, selectionArgs, null,null, "timestamp","10");
			    	  
				      //---register to watch a content URI for changes---
				      c.setNotificationUri(getContext().getContentResolver(), uri);
				      return c;
			      }
			    	  
			      case DEVICE_TYPE_ALL : {
			    	  sqlBuilder.setTables(DATABASE_TABLE_DEVICES);
			    	  sqlBuilder.appendWhere("type" +" = "+ uri.getPathSegments().get(2));
			      }
			      break;
			      case DEVICES_GET_CLUSTER_LAST : {
			    	  
			    	  String table;
			    	  
			    	  switch(Integer.parseInt(uri.getPathSegments().get(1))){
			    	  
			    	  case ClusterConstants.ID_BASIC_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_BASIC_CLUSTER_STATUSES);
			    	  table = DATABASE_TABLE_BASIC_CLUSTER_STATUSES;
			    	  } break;
			    	  case ClusterConstants.ID_FLOW_MESUREMENT_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_FLOW_CLUSTERS_STATUSES);
			    	  table = DATABASE_TABLE_FLOW_CLUSTERS_STATUSES;
			    	  } break;
			    	  case ClusterConstants.ID_ON_OFF_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_ON_OFF_CLUSTERS_STATUSES);
			    	  table = DATABASE_TABLE_ON_OFF_CLUSTERS_STATUSES;
			    	  } break;
			    	  case ClusterConstants.ID_POWER_CONFIGURATION_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_POWER_CLUSTER_STATUSES);
			    	  table = DATABASE_TABLE_POWER_CLUSTER_STATUSES;
			    	  } break;
			    	  case ClusterConstants.ID_TEMPERATURE_MEASUREMENT_CLUSTER  : {sqlBuilder.setTables(DATABASE_TABLE_TEMPERATURE_CLUSTER_STATUSES);
			    	  table = DATABASE_TABLE_TEMPERATURE_CLUSTER_STATUSES;
			    	  } break;
			    	  case ClusterConstants.ID_THERMOSTAT_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_THERMOSTAT_CLUSTER_STATUSES);
			    	  table = DATABASE_TABLE_THERMOSTAT_CLUSTER_STATUSES;
			    	  } break;
			    	  
			    	  }
			    	  
			      }
			      break;
			      
			      default: throw new SQLException("Failed to process " + uri);
		      }
		      
		      Cursor c = sqlBuilder.query(smarthomeDB, projection, selection, selectionArgs, null, null, sortOrder);
		   
		      //---register to watch a content URI for changes---
		      c.setNotificationUri(getContext().getContentResolver(), uri);
		      return c;
		   }
		   
		   /* (non-Javadoc)
   		 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
   		 */
   		@Override
		   public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) 
		   {
		      int changed = 0;
		      
		      switch (uriMatcher.match(uri)){
		      case UPDATE_DEVICE_CLUSTER:{
		    	  String newValue = values.getAsString(SETTING); 
		    	  String serverUri = baseUri + status + "device/" + uri.getPathSegments().get(1)+ "/" + uri.getPathSegments().get(2)+"/cluster/"+uri.getPathSegments().get(4);
		    	  
		    	  try {
		    		  String response = JSONParser.confirmSetting(serverUri);
		    		  if (response.equalsIgnoreCase(newValue)) {
		    			  // setarea a fost schimbata pe server - o schimbam si local
		    			//  changed = smarthomeDB.update(DATABASE_TABLE_ACTUATORS, values, "_id="+uri.getPathSegments().get(1), selectionArgs);
		    		  
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		    	  break;  
		      
		       case UPDATE_ON_OFF_SWITCH:
		       {
		    	   int value = values.getAsInteger("status"); 
		    	   String  serverUri = baseUri + "cmd.json";
			    	  Log.d("data","Update switch " + value);
			    	  
			    	  ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			    	  
			    	  pairs.add(new BasicNameValuePair("id", ""));
			    	  pairs.add(new BasicNameValuePair("cmd[extAddress]", uri.getPathSegments().get(1) ));
			    	  pairs.add(new BasicNameValuePair("cmd[endpoint]", uri.getPathSegments().get(2)));
			    	  pairs.add(new BasicNameValuePair("cmd[clusterID]", ClusterConstants.ID_ON_OFF_CLUSTER + ""));
			    	  
			    	  if(value == 0){
			    		  pairs.add(new BasicNameValuePair("cmd[attributes]", "{0000:00}"));
			    		  Log.d("debug","OFF cmd");
			    	  }else{
			    		  Log.d("debug","ON cmd");
			    		  pairs.add(new BasicNameValuePair("cmd[attributes]", "{0000:01}"));
			    	  
			    	  }
			    	  pairs.add(new BasicNameValuePair("cmd[timestamp]", System.currentTimeMillis()/1000 +""));
			    	  
			    	  try {
			    		  
			    		  JSONObject resp = RestComm.restPost(serverUri, pairs);
			    		  
			    	  } catch (Exception e) {
			    		  e.printStackTrace();
			    	  }
		       }
			   break; 	
		      
		    	  
		       case UPDATE_THERMOSTAT  : {
		    	   int min = (int)(values.getAsFloat("minValue") * 100f);
		    	   int max = (int)(values.getAsFloat("maxValue") * 100f);
		    	   
		    	   String serverUri = baseUri + "cmd.json";
			    	 Log.d("thermostat","Send data : " + min + " max :" + max );
			    	  
			    	  ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			    	  
			    	  pairs.add(new BasicNameValuePair("id", ""));
			    	  pairs.add(new BasicNameValuePair("cmd[extAddress]", uri.getPathSegments().get(1) ));
			    	  pairs.add(new BasicNameValuePair("cmd[endpoint]", uri.getPathSegments().get(2)));
			    	  pairs.add(new BasicNameValuePair("cmd[clusterID]", ClusterConstants.ID_THERMOSTAT_CLUSTER + ""));
			    	  
			    	  
			    	  
			    	  pairs.add(new BasicNameValuePair("cmd[attributes]", "[{0015:"+Integer.toHexString(min)+"},{0016:"+Integer.toHexString(max)+"}]"));
			    	  Log.d("data","Update switch [{0015:0"+Integer.toHexString(min)+"},{0016:"+Integer.toHexString(max)+"}]");
			    	  

			    	  pairs.add(new BasicNameValuePair("cmd[timestamp]", System.currentTimeMillis()/1000 +""));
			    	  
			    	  try {
			    		  
			    		  JSONObject resp = RestComm.restPost(serverUri, pairs);
			    		  
			    	  } catch (Exception e) {
			    		  e.printStackTrace();
			    	  }
		    	   	
		       }
		       break;
		    	  
			      default: throw new SQLException("Failed to process " + uri);
		      }
		      return changed;
		   }
		   
		   /* (non-Javadoc)
   		 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
   		 */
   		@Override
		   public Uri insert(Uri uri, ContentValues values) {
			   // we will not insert new sensors or actuators
			   return null;
		   }
		   
		   /* (non-Javadoc)
   		 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
   		 */
   		@Override
		   public int delete(Uri arg0, String arg1, String[] arg2) {
			   // we will not delete sensors or actuators
			   return 0;
		   }
		   
		   /**
   		 * Update regular from server.
   		 *
   		 * @return true, if successful
   		 */
   		public boolean updateRegularFromServer(){
			   
			   
			  if(smarthomeDB == null){
				  Log.d("database","database is null");
			  }
			
			   scheduledTask = exec.scheduleAtFixedRate(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.d(DEBUG,"Start parsing");
			    	  //fetch data for tables
					

			    	  if(smarthomeDB == null)
			    		  Log.d("debug","SmarthomeDB is null");
			    	  boolean isUpdate = false;
			    	  
			    	  try{
			    		  String uri = SmartHomeProvider.baseUri + "status/timestamp/latest.json";
			    		  isUpdate = processJSONSqliteResponse(uri);
			    	  }
			    	  catch(Exception e){
			    		  Log.d("debug","Exception in parsing data for update " + e.getMessage());
			    	  }

			    	  if(uiHandler != null && uiRunnable != null){
			    		  if(isUpdate){
			    			  uiHandler.post(uiRunnable);
			    			  Log.d("database","Post refresh callback");
			    		  }
			    		  else
			    		  	Log.d("updater","Nothing to update");
			    	  }
			    	  else
			    		  Log.d("database","Can't post refresh callbacks");
			    	  
				}
			}, SCHEDULE_LATEST_DATA_REFRESH_RATE, SCHEDULE_LATEST_DATA_REFRESH_RATE, TimeUnit.SECONDS);
			
			   //set updates enabled
			   updatesEnabled = true;
			   
			   
			   return true;
			   
		   
		   }
		   
		   /**
   		 * Checks if is updates enabled.
   		 *
   		 * @return true, if is updates enabled
   		 */
   		public boolean isUpdatesEnabled(){
			   
			   if(scheduledTask != null)
				   return true;
			   
			   return false;
		   }
		   
		   
		   /**
   		 * Stop regular updates.
   		 */
   		public void stopRegularUpdates(){
			   if(!scheduledTask.isCancelled())
				   scheduledTask.cancel(false);
			   
			   updatesEnabled = false;
			   scheduledTask = null;
			  
		   }

		   /**
   		 * Sets the ui update.
   		 *
   		 * @param handler the handler
   		 * @param run the run
   		 */
   		public void setUiUpdate(Handler handler, Runnable run){
			   
			   this.uiHandler = handler;
			   this.uiRunnable = run;
			   
		   }
		   
   		/**
   		 * Clear ui update.
   		 */
   		public void clearUiUpdate(){
			   
			   this.uiHandler = null;
			   this.uiRunnable = null;
		   }
		   
		   /**
   		 * Checks if is ui update enabled.
   		 *
   		 * @return true, if is ui update enabled
   		 */
   		public boolean isUiUpdateEnabled(){
			 
			   if(this.uiHandler == null || this.uiRunnable == null)
				   return false;
			   
			   return true;
		   }
		   
		   //for an update parse the answer and verify if it is in database, if not , add it to the database
		   /**
   		 * Process json sqlite response.
   		 *
   		 * @param uri the uri
   		 * @return true, if successful
   		 * @throws JSONException the jSON exception
   		 * @throws ConnectTimeoutException the connect timeout exception
   		 * @throws ClientProtocolException the client protocol exception
   		 */
   		private boolean processJSONSqliteResponse(String uri) throws JSONException, ConnectTimeoutException,ClientProtocolException{
			   
			   boolean isUpdate = false;
			   
			   
				
				JSONObject responseJson = RestComm.restGet(uri);
				JSONArray result = responseJson.getJSONArray("statusSet");
				
				for(int i=0;i<result.length();i++){
					JSONObject status = result.getJSONObject(i);
					
					String extAddress = status.getString("extAddress"); 
					int endpoint = status.getInt("endpoint");
					String timestamp = status.getString("timestamp");
					int clusterID = status.getInt("clusterID");
					int id = status.getInt("id");
					
					Log.d("debug_parser","attributes :" + status.getString("attributes") );
					Log.d("debug_parser","cluster:" + clusterID );
					Log.d("debug_parser","extAddress" + extAddress  );
					SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
					
					String table ="";
					switch(clusterID){
			    	  
			    	  case ClusterConstants.ID_BASIC_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_BASIC_CLUSTER_STATUSES);
			    	  table = DATABASE_TABLE_BASIC_CLUSTER_STATUSES;
			    	  } break;
			    	  
			    	  case ClusterConstants.ID_FLOW_MESUREMENT_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_FLOW_CLUSTERS_STATUSES);
			    	  table = DATABASE_TABLE_FLOW_CLUSTERS_STATUSES;
			    	  } break;
			    	  
			    	  case ClusterConstants.ID_ON_OFF_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_ON_OFF_CLUSTERS_STATUSES);
			    	  table = DATABASE_TABLE_ON_OFF_CLUSTERS_STATUSES;
			    	  } break;
			    	  
			    	  case ClusterConstants.ID_POWER_CONFIGURATION_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_POWER_CLUSTER_STATUSES);
			    	  table = DATABASE_TABLE_POWER_CLUSTER_STATUSES;
			    	  } break;
			    	  
			    	  case ClusterConstants.ID_TEMPERATURE_MEASUREMENT_CLUSTER  : {sqlBuilder.setTables(DATABASE_TABLE_TEMPERATURE_CLUSTER_STATUSES);
			    	  table = DATABASE_TABLE_TEMPERATURE_CLUSTER_STATUSES;
			    	  } break;
			    	  
			    	  case ClusterConstants.ID_THERMOSTAT_CLUSTER : {sqlBuilder.setTables(DATABASE_TABLE_THERMOSTAT_CLUSTER_STATUSES);
			    	  table = DATABASE_TABLE_THERMOSTAT_CLUSTER_STATUSES;
			    	  } break;
			    	  
			    	  }
					
					sqlBuilder.appendWhere("_id " +"="+ id);
					Cursor cur = sqlBuilder.query(smarthomeDB, null, null, null, null, null, null);
					
					if(cur.getCount() == 0){
					cur.close();
						//at least one update
						isUpdate = true;
					//if new row then add 
						
					String att = status.getString("attributes");
					
					JSONObject attributes = new JSONObject();
					StringTokenizer tock = new StringTokenizer(att);
					//can't parseArray because key's are not String => Exception
					
					if(att.equals("1")){
					//server error , not complaint with ZigBee standard
						attributes.put(ClusterConstants.ON_OFF,"00" ); 
						Log.d("attribute_error","attibute 1");
					}else
						{
						Log.d("attribute_error","attibute 2");
					while(tock.hasMoreElements()){
						
						String key = tock.nextToken(":[]{}, ");
						String value = tock.nextToken(":[]{}, ");	
						attributes.put(key, value);
					}
						}
					//Log.d("debug_parser","New Attributes" + attributes.toString());
					ContentValues a = new ContentValues();
					a.put(Device_extAddress, extAddress);
					a.put(Endpoint, endpoint);
					a.put("_id", id);
					a.put(Timestamp, timestamp);
					
					Log.d("database","UPDATE timestamp "+timestamp);
					
					switch(clusterID){
					
						case ClusterConstants.ID_BASIC_CLUSTER : {
					

							String location = attributes.getString(ClusterConstants.BASIC_LocationDescription);
							String zclVersion = attributes.getString(ClusterConstants.BASIC_ZCLVersion);
							String deviceEnabled = attributes.getString(ClusterConstants.BASIC_DeviceEnabled);
							String powerSource = attributes.getString(ClusterConstants.BASIC_PowerSource);
							a.put(LocationDescription,location);
							a.put(ZCLVersion, zclVersion);
							a.put(DeviceEnabled, deviceEnabled);
							a.put(PowerSource,powerSource);
							
							
							smarthomeDB.insert(table, null, a);
							
							
							
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
							
							a.put("mainsVoltage",mainsVoltage);
							a.put("mainsFrequency", mainsFrequency);
							a.put("batteryVoltage", batteryVoltage);
							smarthomeDB.insert(table, null, a);
										
						}break;
						
						case ClusterConstants.ID_ON_OFF_CLUSTER : {
							

							
							String st = attributes.getString(ClusterConstants.ON_OFF);
							
							String stat = Long.parseLong(st)+"";
							a.put(On_Off_Status, stat);
							long res = smarthomeDB.insertOrThrow(table, null, a);
							
							Log.d("insert","Inserted " + res);
							
						} break;
						
						case ClusterConstants.ID_TEMPERATURE_MEASUREMENT_CLUSTER : {

							String tempStringHex = attributes.getString(ClusterConstants.TEMPERATURE_MeasuredValue);
							Double temp = Long.parseLong(tempStringHex, 16)/100d;
							
							a.put(MeasuredValue, temp);
							Log.d("alues","Measured value " + temp);
							smarthomeDB.insert(table, null, a);
							
							
						} break;
						
						case ClusterConstants.ID_FLOW_MESUREMENT_CLUSTER : {
							

							String flowStringHex = attributes.getString(ClusterConstants.FLOW_MeasuredValue);
							Double flow = Long.parseLong(flowStringHex, 16)/100d;
							a.put(MeasuredValue, flow);
							smarthomeDB.insert(table, null, a);
							
							
							
						} break;
						
						case ClusterConstants.ID_THERMOSTAT_CLUSTER : {

							
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
							a.put(MinHeat, min);
							a.put(MaxHeat, max);
							a.put(LocalTemperature, localTemp);
							smarthomeDB.insert(table, null, a);
							Log.d("thermostat","Temperature " + minH +" "+ min + " " + maxH + " " + max );
							
							
						} break;
						
					}
					}
					else
						cur.close();
				}
				
				return isUpdate;
		   }
		   
		   
}
