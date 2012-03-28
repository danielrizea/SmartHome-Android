/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import upb.smarthome.clusters.ClusterConstants;
import upb.smarthome.clusters.FlowClusterStatus;
import upb.smarthome.clusters.OnOffClusterStatus;
import upb.smarthome.clusters.TemperatureClusterStatus;
import upb.smarthome.clusters.ThermostatClusterStatus;
import upb.smarthome.data.SmartHomeProvider;
import upb.smarthome.devices.DeviceConstants;
import upb.smarthome.devices.FlowSensor;
import upb.smarthome.devices.LogicalDevice;
import upb.smarthome.devices.OnOffActuator;
import upb.smarthome.devices.TemperatureSensor;
import upb.smarthome.devices.ThermostatActuator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Class SmartHomeActivity.
 */
public class SmartHomeActivity extends Activity {

 // Local data
 	/** The provider. */
 private String provider = "content://" + SmartHomeProvider.PROVIDER_NAME;
 	
 	/** The senzori. */
	 public ArrayList<LogicalDevice> senzori;
 	
	 /** The senzor types. */
	 public ArrayList<String> senzorTypes;
 	
 	//default value selected
 	/** The selected spinner value. */
	 public int selectedSpinnerValue = 0;
 	
	 /** The handler. */
	 private Handler handler = new Handler();
 	
 	 /** The spinner. */
 	 Spinner spinner;
 	
 	 //by default show temperature senzors
 	/** The senzor type listing. */
 	 private int senzorTypeListing = DeviceConstants.TYPE_TEMPERATURE_SENSOR;
 	
 	// UI elements
 	/** The lv. */
	 private ListView lv;
 	
 	// used to update UI from content provider
 	/** The ui update. */
	 private MyUiUpdate uiUpdate;

 	/** The la. */
	 ListWithImageAdapter la ;
 	
    /** The content provider. */
    SmartHomeProvider contentProvider = new SmartHomeProvider();

    /** The verify battery life. */
    private Runnable verifyBatteryLife;
    
    //minimal battery voltage value 2000 mV
    /** The min battery voltage alert value. */
    private int minBatteryVoltageAlertValue = 2000;
    
    
    /**
     * The Class MyUiUpdate.
     */
    class MyUiUpdate implements Runnable{
    	
	    /* (non-Javadoc)
	     * @see java.lang.Runnable#run()
	     */
	    @Override
    	public void run() {
    		//update UI when new data is available
    		
    		Log.d("senzori","Senzori" + senzori.size()+" "+ selectedSpinnerValue);
    	setSenzorSelection(selectedSpinnerValue);
    	Log.d("senzori","Senzori" + senzori.size());
    	
    	la.items.clear();
   	    Log.d("senzori","Aici adauga" + senzori.size());
   	    la.items.addAll(senzori);
   	      
   	    la.notifyDataSetChanged();
   	      
   	    Toast.makeText(getApplicationContext(), "New data update", Toast.LENGTH_LONG).show();
    	}
    }
 	
 	/**
	  * Called when the activity is first created.
	  *
	  * @param savedInstanceState the saved instance state
	  */
     @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         
         // UI initialisation
         setContentView(R.layout.sensors);
         
         String[] senzorTypesStrings = {"Temperature","Flow","Thermostat","Switch"}; 
         
         Spinner spinner = (Spinner) findViewById(R.id.chose_senzor_type);
         
         ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item,senzorTypesStrings);
         
         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
         spinner.setAdapter(adapter);

         //set regular updates from server
         
         if(contentProvider.isUpdatesEnabled() == false)
         contentProvider.updateRegularFromServer();
         
         uiUpdate = new MyUiUpdate();
         contentProvider.setUiUpdate(handler, uiUpdate);
                  
         lv = (ListView) findViewById(R.id.listView);
         
         //default value of list is temperature
         setSenzorSelection(DeviceConstants.TYPE_TEMPERATURE_SENSOR);
         
         // Configure UI for the sensor list
         la = new ListWithImageAdapter(this,R.layout.list_item, senzori);
         lv.setAdapter(la);
         // What happens when you click a button in the Sensor list
         lv.setOnItemClickListener(new OnItemClickListener() {
     	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
     	    	
     	    	LogicalDevice s = (LogicalDevice)senzori.get(position);
     	    	
         		//start new activity
     	    	if(s.type == DeviceConstants.TYPE_ON_OFF){
     	    		new ToggleOnOff().execute((OnOffActuator)s);
     	    	}else
     	    	if(s.type == DeviceConstants.TYPE_THERMOSTAT)
     	    	{
     	    	
	     	    	Intent intent = new Intent(getApplicationContext(), ThermostatControl.class);
	     	    	intent.putExtra("extAddress", s.extAddress);
	     	    	intent.putExtra("endpoint", s.endPoint);
	     	    	intent.putExtra("type", s.type);
	     	    	
	     	    	startActivity(intent);
     	    	}
     	    	else
     	    	{
     	    		Intent intent = new Intent(getApplicationContext(), SensorDetails.class);
	     	    	intent.putExtra("extAddress", s.extAddress);
	     	    	intent.putExtra("endpoint", s.endPoint);
	     	    	intent.putExtra("type", s.type);
     	    		
	     	    	startActivity(intent);
     	    	}
     	    }
         });
         
         
         verifyBatteryLife = new Runnable() {
			
			@Override
			public void run() {
				
				
				
				
				Uri  getSensorPowerInfo= Uri.parse(provider+"/cluster/"+ClusterConstants.ID_POWER_CONFIGURATION_CLUSTER);
				Cursor values = managedQuery(getSensorPowerInfo, null, null, null, null);
				
				
				if(values.moveToFirst()){
					do{
						int batteryVoltage = values.getInt(values.getColumnIndex(SmartHomeProvider.BatteryVoltage));
						
						Log.d("Battery","voltage : " + batteryVoltage);
						
						final String extAddressLowPower = values.getString(values.getColumnIndex(SmartHomeProvider.Device_extAddress));
						
						//if battery voltage under a value and positive (it has a battery)
						if( batteryVoltage < minBatteryVoltageAlertValue && batteryVoltage > 0)
							handler.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								//show an alert box
								 new AlertDialog.Builder(SmartHomeActivity.this)
							      .setMessage("It has been detected that sensor with id :" + extAddressLowPower + " has a low voltage and may malfanction.Please change batteries!")
							      .setTitle("Low voltage ")
							      .setCancelable(true)
							      .setNeutralButton(android.R.string.cancel,
							         new DialogInterface.OnClickListener() {
							         public void onClick(DialogInterface dialog, int whichButton){}
							         })
							      .show();
							}
						});
								
							   
					
						
					}while(values.moveToNext());
				}
				
			}
		};
		
		
		//check to see if all sensors are ok, they have energy over a value
		new Thread(verifyBatteryLife).start();
     }
     
     /**
      * Sets the senzor selection.
      *
      * @param senzorType the new senzor selection
      */
     public void setSenzorSelection(int senzorType){
    	 
         Uri allSenzors = Uri.parse(provider+"/devices/type/"+senzorType);
        
         senzori = new ArrayList<LogicalDevice>();

         selectedSpinnerValue = senzorType;
         
         Log.d("senzori","Get senzori din baza de date ");
    	 Cursor c = managedQuery(allSenzors, null, null, null, null);
         if (c.moveToFirst()) {
         	do{       
         		Log.d("senzori","Senzors");
         		String extAddress = c.getString(c.getColumnIndex(SmartHomeProvider.ExtAddress));
 				int endpoint = c.getInt(c.getColumnIndex(SmartHomeProvider.Endpoint));
 				int type = c.getInt(c.getColumnIndex(SmartHomeProvider.TYPE));
         		switch(senzorType){
         		
         				case DeviceConstants.TYPE_TEMPERATURE_SENSOR : {
         					
             				TemperatureSensor device = new TemperatureSensor(extAddress, endpoint, type);
             				
             				Uri getSenzorPosition= Uri.parse(provider + "/device/"+extAddress+"/"+endpoint+"/cluster/"+ClusterConstants.ID_BASIC_CLUSTER+"/last");
             				Cursor val = managedQuery(getSenzorPosition, null, null, null, null);
             				//add senzor special cluster value
             				if(val.getCount()>0){
             					
             					val.moveToFirst();
                 				device.position = val.getString(val.getColumnIndex(SmartHomeProvider.LocationDescription));
                 				Log.d("senzor_info","Location : " + device.position);
             				}
         
             				Uri getSenzorLastDataCluster = Uri.parse(provider + "/device/"+extAddress+"/"+endpoint+"/cluster/"+ClusterConstants.ID_TEMPERATURE_MEASUREMENT_CLUSTER+"/last");
             				val = managedQuery(getSenzorLastDataCluster, null, null, null, null);
             				//add senzor special cluster value
             				if(val.getCount()>0){
             					val.moveToFirst();
                 				device.tempCluster.currentStatus = new TemperatureClusterStatus(val.getFloat(val.getColumnIndex(SmartHomeProvider.MeasuredValue)),val.getInt(val.getColumnIndex(SmartHomeProvider.Timestamp)));
                 				Log.d("senzor_info","Temeprature: " + device.tempCluster.currentStatus.measuredValue);
             				}
             				else
             					Log.d("senzor_info","Temperature: nu e setata");
             				senzori.add(device);
         				}
         				break;
         				case DeviceConstants.TYPE_FLOW_METER : {
         					
         					FlowSensor device = new FlowSensor(extAddress, endpoint, type);
             				
             				Uri getSenzorPosition= Uri.parse(provider + "/device/"+extAddress+"/"+endpoint+"/cluster/"+ClusterConstants.ID_BASIC_CLUSTER+"/last");
             				Cursor val = managedQuery(getSenzorPosition, null, null, null, null);
             				//add senzor special cluster value
             				if(val.getCount()>0){
             					val.moveToFirst();
                 				device.position = val.getString(val.getColumnIndex(SmartHomeProvider.LocationDescription));
             				}
         
             				Uri getSenzorLastDataCluster = Uri.parse(provider + "/device/"+extAddress+"/"+endpoint+"/cluster/"+ClusterConstants.ID_FLOW_MESUREMENT_CLUSTER+"/last");
             				val = managedQuery(getSenzorLastDataCluster, null, null, null, null);
             				//add senzor special cluster value
             				if(val.getCount()>0){
             					val.moveToFirst();
                 				device.flowCluster.currentStatus = new FlowClusterStatus(val.getFloat(val.getColumnIndex(SmartHomeProvider.MeasuredValue)),val.getInt(val.getColumnIndex(SmartHomeProvider.Timestamp)));
             				}
             				senzori.add(device);
         				}
         				break;
         				
         				case DeviceConstants.TYPE_ON_OFF : {
         					
         					OnOffActuator device = new OnOffActuator(extAddress, endpoint, type);
             				
             				Uri getSenzorPosition= Uri.parse(provider + "/device/"+extAddress+"/"+endpoint+"/cluster/"+ClusterConstants.ID_BASIC_CLUSTER+"/last");
             				Cursor val = managedQuery(getSenzorPosition, null, null, null, null);
             				//add senzor special cluster value
             				if(val.getCount()>0){
             					
             					val.moveToFirst();
                 				device.position = val.getString(val.getColumnIndex(SmartHomeProvider.LocationDescription));
             				}
             				
             				
             				Uri getSenzorLastDataCluster = Uri.parse(provider + "/device/"+extAddress+"/"+endpoint+"/cluster/"+ClusterConstants.ID_ON_OFF_CLUSTER+"/last");
             				val = managedQuery(getSenzorLastDataCluster, null, null, null, null);
             				//add senzor special cluster value
             				
             				Log.d("debug","Intrari " + val.getCount());
             				if(val.getCount()>0){
             					val.moveToFirst();
                 				device.onOffcluster.currentStatus = new OnOffClusterStatus(val.getInt(val.getColumnIndex(SmartHomeProvider.On_Off_Status)),val.getInt(val.getColumnIndex(SmartHomeProvider.Timestamp)));
                 				do{
                 					Log.d("valori_on_off","Ext Timestamp status "+device.onOffcluster.currentStatus.status+" val" + val.getString(val.getColumnIndex("device_extAddress")) + " " + val.getInt(val.getColumnIndex(SmartHomeProvider.Timestamp)) +" " + val.getInt(val.getColumnIndex(SmartHomeProvider.On_Off_Status)) + " " +val.getCount());
                 				}while(val.moveToNext());
             				}
             				
         
             				
             				Log.d("timestamp","On off switch" + device.onOffcluster.currentStatus.timestamp + " " + device.onOffcluster.currentStatus.status);
             				senzori.add(device);
         				}
         				break;
         				
         				case DeviceConstants.TYPE_THERMOSTAT : {
         					
         					ThermostatActuator device = new ThermostatActuator(extAddress, endpoint, type);
             				
             				Uri getSenzorPosition= Uri.parse(provider + "/device/"+extAddress+"/"+endpoint+"/cluster/"+ClusterConstants.ID_BASIC_CLUSTER+"/last");
             				Cursor val = managedQuery(getSenzorPosition, null, null, null, null);
             				//add senzor special cluster value
             				if(val.getCount()>0){
             					val.moveToFirst();
                 				device.position = val.getString(val.getColumnIndex(SmartHomeProvider.LocationDescription));
             				}
         
             				Uri getSenzorLastDataCluster = Uri.parse(provider + "/device/"+extAddress+"/"+endpoint+"/cluster/"+ClusterConstants.ID_THERMOSTAT_CLUSTER+"/last");
             				val = managedQuery(getSenzorLastDataCluster, null, null, null, null);
             				//add senzor special cluster value
             				if(val.getCount()>0){
             					val.moveToFirst();
                 				device.thermostatCluster.currentStatus = new ThermostatClusterStatus(val.getFloat(val.getColumnIndex(SmartHomeProvider.LocalTemperature)),val.getFloat(val.getColumnIndex(SmartHomeProvider.MinHeat)),val.getFloat(val.getColumnIndex(SmartHomeProvider.MaxHeat)),val.getInt(val.getColumnIndex(SmartHomeProvider.Timestamp)));
             				}
             				senzori.add(device);
         				}
         				break;
         		}
         	}while (c.moveToNext());
         }
     }

     
     // What happens when you press the menu button on your phone
     /* (non-Javadoc)
      * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
      */
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         //MenuInflater inflater = getMenuInflater();
         //inflater.inflate(R.menu, menu);
         return true;
     }
     // What happens when you press a button in the Options menu
     /* (non-Javadoc)
      * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
      */
     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
  
         return true;
     }
     
     // Custom adapter to populate the sensors list
      /**
      * The Class ListWithImageAdapter.
      */
     private class ListWithImageAdapter extends ArrayAdapter<LogicalDevice> {

     	/** The items. */
	     private ArrayList<LogicalDevice> items;

     	/**
	      * Instantiates a new list with image adapter.
	      *
	      * @param context the context
	      * @param textViewResourceId the text view resource id
	      * @param items the items
	      */
	     public ListWithImageAdapter(Context context, int textViewResourceId, ArrayList<LogicalDevice> items) {
                 super(context, textViewResourceId, items);
                 this.items = items;
         }

         /* (non-Javadoc)
          * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
          */
         @Override
         public View getView(int position, View convertView, ViewGroup parent) {
                 View v = convertView;
                 if (v == null) {
                     LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                     v = vi.inflate(R.layout.list_item, null);
                 }
                 
                 LogicalDevice s = items.get(position);
                 if (s != null) {
                         TextView t = (TextView) v.findViewById(R.id.txtName);
                         if (t != null) {
                               t.setText(s.position);                            
                         }
                         t = (TextView) v.findViewById(R.id.txtLocation);
                         if (t != null) {
                        	 
                               t.setText(""+s.extAddress);
                        	 
                         }
                         t = (TextView) v.findViewById(R.id.txtValue);
                         if (t != null) {
                         	//TODO	
                        	 
                        	 switch(s.type){
                        	 
                        	 	case DeviceConstants.TYPE_TEMPERATURE_SENSOR: {t.setText("" + ((TemperatureSensor)s).tempCluster.currentStatus.measuredValue );} break;
                        	 	
                        	 	case DeviceConstants.TYPE_FLOW_METER: {t.setText("" + ((FlowSensor)s).flowCluster.currentStatus.measuredValue);} break;
                        	 	
                        	 	case DeviceConstants.TYPE_ON_OFF: {
                        	 		if(((OnOffActuator)(s)).onOffcluster.currentStatus.status == 1)
                        	 			t.setText("On");
                        	 		else
                        	 			t.setText("Off");
                        	 	} break;
                        	 	
                        	 	case DeviceConstants.TYPE_THERMOSTAT: {t.setText("" + ((ThermostatActuator)s).thermostatCluster.currentStatus.MinHeatSetpointLimit + " :" +
                        	 	+((ThermostatActuator)s).thermostatCluster.currentStatus.MaxHeatSetpointLimit );} break;
                        	 	
                        	 
                        	 }
                        	 
                         }
                         ImageView i = (ImageView) v.findViewById(R.id.img);
                         if (i != null) {
                         	if (s.type==DeviceConstants.TYPE_TEMPERATURE_SENSOR) {
                         		i.setImageResource(R.drawable.temperature);
                         	}
                         	if (s.type==DeviceConstants.TYPE_FLOW_METER) {
                         		i.setImageResource(R.drawable.meter);
                         	}
                         	if(s.type==DeviceConstants.TYPE_ON_OFF){
                         		if(((OnOffActuator)(s)).onOffcluster.currentStatus.status == 1)
                         			i.setImageResource(R.drawable.light_switch_on);
                         		else
                         			i.setImageResource(R.drawable.light_switch_off);
                         	}
                         	if(s.type==DeviceConstants.TYPE_THERMOSTAT){
                         		i.setImageResource(R.drawable.thermostat);
                         	}
                         }
                 }
                 return v;
         }
     }
      
      /**
       * The listener interface for receiving myOnItemSelected events.
       * The class that is interested in processing a myOnItemSelected
       * event implements this interface, and the object created
       * with that class is registered with a component using the
       * component's <code>addMyOnItemSelectedListener<code> method. When
       * the myOnItemSelected event occurs, that object's appropriate
       * method is invoked.
       *
       * @see MyOnItemSelectedEvent
       */
      class MyOnItemSelectedListener implements OnItemSelectedListener {

    	    /* (non-Javadoc)
    	     * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long)
    	     */
    	    public void onItemSelected(AdapterView<?> parent,
    	        View view, int pos, long id) {
    	     
    	      Log.d("senzori","Selctat" + pos);
    	      switch(pos){
    	      
    	      case 0 : setSenzorSelection(DeviceConstants.TYPE_TEMPERATURE_SENSOR); break;
    	      case 1 : setSenzorSelection(DeviceConstants.TYPE_FLOW_METER);break;
    	      case 2 : setSenzorSelection(DeviceConstants.TYPE_THERMOSTAT);break;
    	      case 3 : setSenzorSelection(DeviceConstants.TYPE_ON_OFF);break;
    	      }
    	      
    	      la.items.clear();
    	      Log.d("senzori","Aici adauga" + senzori.size());
    	      la.items.addAll(senzori);
    	      
    	      la.notifyDataSetChanged();
    	      
    	      Toast.makeText(parent.getContext(), "The senzor displayed is " +
        	          parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
    	    }

    	    /* (non-Javadoc)
    	     * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
    	     */
    	    public void onNothingSelected(AdapterView parent) {
    	      // Do nothing.
    	    }
    	}
      
      
      
      
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    	protected void onResume() {
    		// TODO Auto-generated method stub
    		super.onResume();
    		if(contentProvider.isUpdatesEnabled() == false)
    			contentProvider.updateRegularFromServer();
    		
    		contentProvider.setUiUpdate(handler, uiUpdate);
    	}  
      
      
      /* (non-Javadoc)
       * @see android.app.Activity#onStop()
       */
      @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	
    	
    	//contentProvider.stopRegularUpdates();
    	
      }
      
      /* (non-Javadoc)
       * @see android.app.Activity#onDestroy()
       */
      @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	contentProvider.stopRegularUpdates();
    	
      }
      
      
      // custom AsyncTask that is called when an actuator is tapped in order to toggle its setting
      /**
       * The Class ToggleOnOff.
       */
      private class ToggleOnOff extends AsyncTask<OnOffActuator, Integer, Boolean> {
      	
      	/** The o. */
	      private OnOffActuator o;

          /* (non-Javadoc)
           * @see android.os.AsyncTask#doInBackground(Params[])
           */
          protected Boolean doInBackground(OnOffActuator... os) {
          	
  	    	
  	    	ContentValues editedValues = new ContentValues();
  	    	o = os[0];
  	    	
  	    	if(o.onOffcluster.currentStatus.status == 1)
  	    		editedValues.put("status", 0);
  	    	else
  	    		editedValues.put("status", 1);
  	    	
  	    	Uri actuatorChange = Uri.parse(provider+"/device/"+o.extAddress+"/"+o.endPoint+"/switch/"+0);
  	    	
  	    	
  	        if (getContentResolver().update(actuatorChange,editedValues,null,null)!=0)
  	        	return true;
  	        return false;
          }

          /* (non-Javadoc)
           * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
           */
          protected void onPostExecute(Boolean result) {
  	        if (result){
  	        	
  	        	//((ListWithImageAdapter) lv.getAdapter()).notifyDataSetChanged();
  	        } else {
  	        	Toast.makeText(getApplicationContext(), "Actuator setting change failed", Toast.LENGTH_SHORT);
  	        }
          }
      }  
      
}

