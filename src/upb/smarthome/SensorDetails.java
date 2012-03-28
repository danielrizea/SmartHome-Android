/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */
package upb.smarthome;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import upb.smarthome.clusters.ClusterConstants;
import upb.smarthome.clusters.FlowClusterStatus;
import upb.smarthome.clusters.TemperatureClusterStatus;
import upb.smarthome.data.SmartHomeProvider;
import upb.smarthome.devices.DeviceConstants;
import upb.smarthome.devices.FlowSensor;
import upb.smarthome.devices.LogicalDevice;
import upb.smarthome.devices.TemperatureSensor;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.androidplot.Plot;
import com.androidplot.series.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;


/**
 * The Class SensorDetails.
 */
public class SensorDetails extends Activity{

	 // Local data
 	/** The provider. */
 	private String provider = "content://" + SmartHomeProvider.PROVIDER_NAME;
 	
	/** The ext address. */
	private String extAddress;
	
	/** The endpoint. */
	private int endpoint;
	
	/** The type. */
	private int type;
	
	/** The my simple xy plot. */
	private XYPlot mySimpleXYPlot;
	
	/** The flipper. */
	private ViewFlipper flipper;

	/** The pd. */
	private ProgressDialog pd;
	
	/** The img. */
	private ImageView img; 
	
	/** The location. */
	private TextView location;
	
	/** The sensor value. */
	private TextView sensorValue;
	
	/** The timestamp. */
	private TextView timestamp;
	
	/** The position val. */
	private String positionVal;
	
	/** The show value. */
	private String showValue;
	
	/** The timestamp val. */
	private int timestampVal;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.sensor_details);
		
		pd = ProgressDialog.show(SensorDetails.this, "Download date", "Se descarca datele necesare istoriei");
		
		
		Bundle bundle = getIntent().getExtras();
		mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		
		//get sensor information
		extAddress = bundle.getString("extAddress");
		endpoint = bundle.getInt("endpoint");
		type = bundle.getInt("type");
		
		img = (ImageView)findViewById(R.id.sensor_image);
	
		location = (TextView)findViewById(R.id.sensor_Location);
		sensorValue = (TextView)findViewById(R.id.sensor_value);
		timestamp = (TextView)findViewById(R.id.sensor_last_update);
				
		//get sensor falues in new thread
		new Thread() 
		{
		  public void run() 
		  { 	
			  
		     try
		       {
		    	LogicalDevice device = null;
		 		int clusterID =402;
		 		
		    	 Log.d("details", ""+type+" "+extAddress + " " +endpoint);
		 		switch(type){
		  		
		 			case DeviceConstants.TYPE_TEMPERATURE_SENSOR : {

		 				clusterID = ClusterConstants.ID_TEMPERATURE_MEASUREMENT_CLUSTER;
		 				device = new TemperatureSensor(extAddress, endpoint, type);
		 				
		 				Uri getSenzorPosition= Uri.parse(provider + "/device/"+extAddress+"/"+endpoint+"/cluster/"+ClusterConstants.ID_BASIC_CLUSTER+"/last");
		 				Cursor val = managedQuery(getSenzorPosition, null, null, null, null);
		 				//add senzor special cluster value
		 				if(val.getCount()>0){
		 					
		 					val.moveToFirst();
		  				device.position = val.getString(val.getColumnIndex(SmartHomeProvider.LocationDescription));
		  				Log.d("senzor_info","Location : " + device.position);
		  				positionVal = device.position;
		 				}

		 				Uri getSenzorLastDataCluster = Uri.parse(provider + "/device/"+extAddress+"/"+endpoint+"/cluster/"+ClusterConstants.ID_TEMPERATURE_MEASUREMENT_CLUSTER+"/last");
		 				val = managedQuery(getSenzorLastDataCluster, null, null, null, null);
		 				//add senzor special cluster value
		 				if(val.getCount()>0){
		 					val.moveToFirst();
		 					TemperatureClusterStatus status = new TemperatureClusterStatus(val.getFloat(val.getColumnIndex(SmartHomeProvider.MeasuredValue)),val.getInt(val.getColumnIndex(SmartHomeProvider.Timestamp)));
		 					showValue = status.measuredValue + " " + status.unit;
		 					timestampVal = status.timestamp;
		 				}
		 				else
		 					Log.d("senzor_info","Temperature: nu e setata");

		 			}
		 			break;
		 			case DeviceConstants.TYPE_FLOW_METER : {
		 				
		 				
		 				
		 				clusterID = ClusterConstants.ID_FLOW_MESUREMENT_CLUSTER;
		 				device = new FlowSensor(extAddress, endpoint, type);
		 				
		 				Uri getSenzorPosition= Uri.parse(provider + "/device/"+extAddress+"/"+endpoint+"/cluster/"+ClusterConstants.ID_BASIC_CLUSTER+"/last");
		 				Cursor val = managedQuery(getSenzorPosition, null, null, null, null);
		 				//add senzor special cluster value
		 				if(val.getCount()>0){
		 					val.moveToFirst();
		  				device.position = val.getString(val.getColumnIndex(SmartHomeProvider.LocationDescription));
		 				positionVal = device.position;
		 				}

		 				Uri getSenzorLastDataCluster = Uri.parse(provider + "/device/"+extAddress+"/"+endpoint+"/cluster/"+ClusterConstants.ID_FLOW_MESUREMENT_CLUSTER+"/last");
		 				val = managedQuery(getSenzorLastDataCluster, null, null, null, null);
		 				//add senzor special cluster value
		 				if(val.getCount()>0){
		 					val.moveToFirst();
		 					FlowClusterStatus status = new FlowClusterStatus(val.getFloat(val.getColumnIndex(SmartHomeProvider.MeasuredValue)),val.getInt(val.getColumnIndex(SmartHomeProvider.Timestamp)));
		  					showValue = status.measuredValue + " " + status.unit;
		  					timestampVal = status.timestamp;
		 				}
		
		 				
		 			}
		 			break;
		 			
		 	}
		 		
		 		//get data for chart creation 
		 		
		 		
		 		Number[] timestamps = new Number[5];
		 		Number[] values = new Number[5];
		 		
		     	Uri allValues = Uri.parse(provider+ "/device/"+device.extAddress+"/"+device.endPoint+"/cluster/"+clusterID+"/last_10_values");
		         Cursor c = managedQuery(allValues, null, null, null, null);
		         
		         Log.d("chart","make chart");
		         int i=0;
		         if (c.moveToFirst()) {
		         	do{
		         		// Difference between SQL Timestamp and Java Timestamp, got to *1000
		         		timestamps[i] = new Long(c.getLong(c.getColumnIndex(SmartHomeProvider.Timestamp))*1000);
		         		values[i] = new Double(c.getFloat(c.getColumnIndex(SmartHomeProvider.MeasuredValue)));
		         	Log.d("values","Values" + values[i] + " " + timestamps[i] +" value:"+ c.getString(c.getColumnIndex(SmartHomeProvider.MeasuredValue)));
		         	} while (c.moveToNext() && ++i<5);
		         }
		     	createChart(timestamps, values, device);
		 	
		      }
		    catch (Exception e)
		    {
		        Log.e("tag",e.getMessage());
		    }
		// dismiss the progress dialog   
		 
		     runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
	  				location.setText(positionVal);
	 				timestamp.setText(DateFormat.getDateFormat(SensorDetails.this).format(new Date(timestampVal * 1000)) + "");
	 				sensorValue.setText(showValue);

	 				pd.dismiss();
				}
			});
		    
		 }
		}.start();

		
		if(type == DeviceConstants.TYPE_FLOW_METER)
			img.setImageResource(R.drawable.meter);
		else
			img.setImageResource(R.drawable.temperature);
	}
	
	 /**
 	 * Creates the chart.
 	 *
 	 * @param timestamps the timestamps
 	 * @param values the values
 	 * @param s the s
 	 */
 	private void createChart(Number[] timestamps, Number[] values, LogicalDevice s){
	    	mySimpleXYPlot.clear();

	    	// create our series from our array of nums:
	        XYSeries series = new SimpleXYSeries(
	                Arrays.asList(timestamps),
	                Arrays.asList(values),
	                "Temperatura de la senzor");
	 
	        mySimpleXYPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
	        mySimpleXYPlot.getGraphWidget().getGridLinePaint().setColor(Color.BLACK);
	        mySimpleXYPlot.getGraphWidget().getGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1,1}, 1));
	        mySimpleXYPlot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
	        mySimpleXYPlot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
	        mySimpleXYPlot.getGraphWidget().getDomainLabelPaint().setTextSize(16);
	        mySimpleXYPlot.getGraphWidget().getRangeLabelPaint().setTextSize(16);
	        mySimpleXYPlot.getGraphWidget().getCursorLabelPaint().setTextSize(16);
	 
	        mySimpleXYPlot.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
	        mySimpleXYPlot.getBorderPaint().setStrokeWidth(1);
	        mySimpleXYPlot.getBorderPaint().setAntiAlias(false);
	        mySimpleXYPlot.getBorderPaint().setColor(Color.WHITE);
	 
	        // setup our line fill paint to be a slightly transparent gradient:
	        Paint lineFill = new Paint();
	        lineFill.setAlpha(200);
	        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR));
	 
	        LineAndPointFormatter formatter  = new LineAndPointFormatter(Color.rgb(0, 0,0), Color.BLUE, Color.RED);
	        formatter.setFillPaint(lineFill);
	        mySimpleXYPlot.getGraphWidget().setPadding(2, 2, 15, 10);
	        mySimpleXYPlot.addSeries(series, formatter);
	       
	        
	        //mySimpleXYPlot.setRangeBoundaries(-10, 50, BoundaryMode.SHRINNK);
	        // draw a domain tick for each entry:
	        mySimpleXYPlot.setDomainStep(XYStepMode.SUBDIVIDE, timestamps.length);
	 
	        // customize our domain/range labels
	        mySimpleXYPlot.setDomainLabel("Ora");
	        
	        if(s.type == DeviceConstants.TYPE_TEMPERATURE_SENSOR)
	        
	        	mySimpleXYPlot.setRangeLabel("temperatura" +" ("+ TemperatureClusterStatus.unit+")");
	        
	        if(s.type == DeviceConstants.TYPE_FLOW_METER)
	            
	        	mySimpleXYPlot.setRangeLabel("volumul"+" ("+FlowClusterStatus.unit+")");
	        
	 
	        // get rid of decimal points in our range labels:
	       // mySimpleXYPlot.setRangeValueFormat(new DecimalFormat("0"));
	 
	        mySimpleXYPlot.setDomainValueFormat(new SimpleDateFormat("k:mm"));
	 
	        // by default, AndroidPlot displays developer guides to aid in laying out your plot.
	        // To get rid of them call disableAllMarkup():
	        mySimpleXYPlot.disableAllMarkup();
	    }
	    

}
