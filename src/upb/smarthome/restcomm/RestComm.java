/*
 * Smart Home Project
 * Copyright (c) 2011 All rights reserved.
 * Polytehnic University of Bucharest
 * Developed by Daniel-Octavian Rizea 
 */

package upb.smarthome.restcomm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * The Class RestComm.
 * This class implements REST primitives
 */
public class RestComm {
	
    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
	/**
     * Convert stream to string.
     *
     * @param is the is
     * @return the string
     */
    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    // GET Method 
    /**
     * Rest get.
     *
     * @param url the url
     * @return the jSON object
     * @throws JSONException the jSON exception
     * @throws ClientProtocolException the client protocol exception
     * @throws ConnectTimeoutException the connect timeout exception
     */
    public static JSONObject restGet(String url) throws JSONException, ClientProtocolException, ConnectTimeoutException
    {
    	Log.i("Rest",url);
    	
        HttpClient httpclient = new DefaultHttpClient();
 
        // Prepare a request object
        HttpGet httpget = new HttpGet(url); 
        httpget.addHeader("Content-type", "text/json");
        // Execute the request
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            // Examine the response status
            Log.i("Rest",response.getStatusLine().toString());
 
            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release
            
            if (entity != null) {
 
                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                String result= convertStreamToString(instream);
                Log.i("Rest",result);
 
                JSONObject json=null;
                
                // A Simple JSONObject Creation
                json=new JSONObject(result);
                
                // Closing the input stream will trigger connection release
                instream.close();
                
                return json;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // PUT Method
    /**
     * Rest put.
     *
     * @param url the url
     * @return the jSON object
     * @throws JSONException the jSON exception
     * @throws ClientProtocolException the client protocol exception
     * @throws ConnectTimeoutException the connect timeout exception
     */
    public static JSONObject restPut(String url) throws JSONException, ClientProtocolException, ConnectTimeoutException
    {
    	Log.i("Rest",url);
    	
        HttpClient httpclient = new DefaultHttpClient();
 
        // Prepare a request object
        HttpPut httpput = new HttpPut(url);
        

        
        // Execute the request
        HttpResponse response;
        try {
            response = httpclient.execute(httpput);
            // Examine the response status
            Log.i("Rest",response.getStatusLine().toString());
 
            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release
            
            if (entity != null) {
 
                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                String result= convertStreamToString(instream);
                Log.i("Rest",result);
 
                JSONObject json=null;
                
                // A Simple JSONObject Creation
                json=new JSONObject(result);
                
                // Closing the input stream will trigger connection release
                instream.close();
                
                return json;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        return null;
    }
    
    // POST Method
    /**
     * Rest post.
     *
     * @param url the url
     * @param pairs the pairs
     * @return the jSON object
     * @throws JSONException the jSON exception
     * @throws ClientProtocolException the client protocol exception
     * @throws ConnectTimeoutException the connect timeout exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public static JSONObject restPost(String url,List<NameValuePair> pairs) throws JSONException, ClientProtocolException, ConnectTimeoutException, UnsupportedEncodingException
    {
    	Log.i("Rest",url);
    	
        HttpClient httpclient = new DefaultHttpClient();
 
        // Prepare a request object
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        
        //add POST body 
        httppost.setEntity(new UrlEncodedFormEntity(pairs));
        
        // Execute the request
        HttpResponse response;
        try {
            response = httpclient.execute(httppost);
            // Examine the response status
            Log.i("Rest",response.getStatusLine().toString());
 
            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release
            
            if (entity != null) {
 
                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                String result= convertStreamToString(instream);
                Log.i("Rest",result);
 
                JSONObject json=null;
                
                // A Simple JSONObject Creation
                json=new JSONObject(result);
                
                // Closing the input stream will trigger connection release
                instream.close();
                
                return json;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        return null;
    }
    
}