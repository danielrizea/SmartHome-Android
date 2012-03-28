/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome;

import upb.smarthome.clusters.ClusterConstants;
import upb.smarthome.clusters.ThermostatClusterStatus;
import upb.smarthome.data.SmartHomeProvider;
import upb.smarthome.devices.OnOffActuator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Class ThermostatControl.
 * You can modify the temperature using a flip gesture on the Oy Axis
 * If you make a flip from buttom to up on the right size of the thermostat, you will increase the high value, else 
 * you will increase the minimum value.
 */
public class ThermostatControl extends Activity{

	 // Local data
	/** The provider. */
 	private String provider = "content://" + SmartHomeProvider.PROVIDER_NAME;
	
	
	/** The thermostat min. */
	private TextView thermostatMin;
	
	/** The thermostat max. */
	private TextView thermostatMax;
	
	/** The position. */
	private TextView position;
	
	/** The progress dialog. */
	private ProgressDialog progressDialog;
	
	/** The save settings button. */
	private Button saveSettingsButton;
	
	/** The measured min. */
	private float measuredMin;
	
	/** The measured max. */
	private float measuredMax;
	
	/** The img. */
	private ImageView img;
	
	/** The Constant MIN_DISTANCE. */
	static final int MIN_DISTANCE = 30;
    
    /** The up y. */
    private float downX, downY, upX, upY;
	
    /** The display width. */
    private float displayWidth;
    
    //modified based on the touched event
    /** The STE p_ value. */
    private float STEP_VALUE = 0.01f;
    
    /** The ext address. */
    private String extAddress;
    
    /** The end point. */
    private int endPoint;
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.thermostat);
		Bundle bundle = getIntent().getExtras();
		thermostatMin = (TextView) findViewById(R.id.thermostat_value_min);
		thermostatMax = (TextView) findViewById(R.id.thermostat_value_max);
		position = (TextView) findViewById(R.id.thermostat_position);
		saveSettingsButton = (Button) findViewById(R.id.thermostat_save);
		
		
		img = (ImageView) findViewById(R.id.thermostat_image);
		
		img.setImageResource(R.drawable.thermostat);
		
		extAddress = bundle.getString("extAddress");
		endPoint = bundle.getInt("endpoint");
		int type = bundle.getInt("type");
		int clusterID = ClusterConstants.ID_THERMOSTAT_CLUSTER;

			
			Uri getSenzorPosition= Uri.parse(provider + "/device/"+extAddress+"/"+endPoint+"/cluster/"+ClusterConstants.ID_BASIC_CLUSTER+"/last");
			Cursor val = managedQuery(getSenzorPosition, null, null, null, null);
			//add senzor special cluster value
			if(val.getCount()>0){
				val.moveToFirst();
			position.setText(val.getString(val.getColumnIndex(SmartHomeProvider.LocationDescription)));

			}

			Uri getSenzorLastDataCluster = Uri.parse(provider + "/device/"+extAddress+"/"+endPoint+"/cluster/"+ClusterConstants.ID_THERMOSTAT_CLUSTER+"/last");
			val = managedQuery(getSenzorLastDataCluster, null, null, null, null);
			//add senzor special cluster value
			if(val.getCount()>0){
				val.moveToFirst();
				measuredMin = val.getFloat(val.getColumnIndex(SmartHomeProvider.MinHeat));
				measuredMax = val.getFloat(val.getColumnIndex(SmartHomeProvider.MaxHeat));
				
				
				
			}
		Log.d("thermostat",""+ measuredMin + " " + measuredMax);	
			
		thermostatMin.setText(measuredMin + " ");
		thermostatMax.setText(": "+measuredMax + " °C" );
		
		saveSettingsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new SaveThermostatValues().execute(new ThermostatClusterStatus(0,measuredMin,measuredMax,0));
			}
		});
		
		Display display = getWindowManager().getDefaultDisplay(); 
		displayWidth = display.getWidth();
		
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	
		String logTag = "command log";
		

		switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN: {
            downX = event.getX();
            downY = event.getY();
            return true;
        }
        case MotionEvent.ACTION_UP: {
            upX = event.getX();
            upY = event.getY();

            float deltaX = downX - upX;
            float deltaY = downY - upY;
            
            
           
            // swipe vertical
            if (Math.abs(deltaY) > MIN_DISTANCE) {
            	
            	STEP_VALUE = (int)(Math.abs(deltaY)%(MIN_DISTANCE))/100f;
                 
            	Log.d("thermostat","Step " + STEP_VALUE);
                // up to down
                if (deltaY < 0) {
                   
                	Log.d(logTag,"up to down");
                	
                	Log.d(logTag,"Width x up " + displayWidth + ":" + upX );
                	 
                	if(upX>displayWidth/2){
                     	// right value
                		 measuredMax -= STEP_VALUE;
                		 thermostatMax.setText(String.format("%.2g",measuredMax)+" °C");
                		 
                     }
                     else
                     {	// left value
                    	 measuredMin -= STEP_VALUE;
                    	 thermostatMin.setText(String.format("%.2g",measuredMin)+" ");
                     	
                     }
                    return true;
                }
                // down to up
                if (deltaY > 0) {
                	
                	 if(upX>displayWidth/2){
                      	// right value
                 		 measuredMax += STEP_VALUE;
                 		 thermostatMax.setText(String.format("%.2g",measuredMax)+" °C");
                      }
                      else
                      {	// left value
                     	 measuredMin += STEP_VALUE;
                     	 thermostatMin.setText(String.format("%.2g",measuredMin)+" ");
                      	
                      }
                	Log.d(logTag,"down to up");
                    return true;
                }
            } else {
                if(Math.abs(deltaX)<15){
                    //onClickEvent();
                }
                Log.i("swipe", "Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
            }
            // swipe vertical?
            if (Math.abs(deltaY) > MIN_DISTANCE) {
             
            } else {
                Log.i(logTag, "Swipe was only " + Math.abs(deltaX)
                    + " long, need at least " + MIN_DISTANCE);
            }

            return true;
        }
    }
    return false;
		
	}
	
	  // custom AsyncTask that is called when an actuator is tapped in order to toggle its setting
    /**
  	 * The Class SaveThermostatValues.
  	 */
  	private class SaveThermostatValues extends AsyncTask<ThermostatClusterStatus, Integer, Boolean> {
    	
    	/** The o. */
	    private OnOffActuator o;

        /* (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        protected Boolean doInBackground(ThermostatClusterStatus... os) {
        	
	    	
        	ThermostatClusterStatus status;
	    	status = os[0];
	    	
	    	ContentValues editedValues = new ContentValues();
	    	
	    
	    		editedValues.put("minValue", status.MinHeatSetpointLimit);
	    		editedValues.put("maxValue", status.MaxHeatSetpointLimit);
	    	
	    	Uri actuatorChange = Uri.parse(provider+"/device/"+extAddress+"/"+endPoint+"/thermostat/");
	    	
	    	
	        if (getContentResolver().update(actuatorChange,editedValues,null,null)!=0)
	        	return true;
	        return false;
        }

        /* (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        protected void onPostExecute(Boolean result) {
	        if (result){
	        	
	        	Toast.makeText(getApplicationContext(), "Thermostat temperature changed", Toast.LENGTH_SHORT);
	        	
	        	//((ListWithImageAdapter) lv.getAdapter()).notifyDataSetChanged();
	        } else {
	        	Toast.makeText(getApplicationContext(), "Actuator setting change failed", Toast.LENGTH_SHORT);
	        }
        }
    }
}
