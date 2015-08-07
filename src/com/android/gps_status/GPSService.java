package com.android.gps_status;

import java.io.IOException;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSService extends Service 
{
    
    public static gps_status MAIN_ACTIVITY;    
    
    
	LocationListener locationListener;
	Location currentLocation;
	LocationManager locationManager;
	Location location;
	Criteria crit = new Criteria();
	
	final HttpClient client = new DefaultHttpClient();
	HttpPost post;

	double lat,lng;
	String provider,latLongString,latitude_str,longitude_str,Id,IP;
	
	//Database
	final String MY_DATABASE_NAME = "GPSDB";
	final String MY_TABLE_NAME = "tblids";
	String[] columns = {"IDs","default_id"};
    SQLiteDatabase myDB = null;
    
    // hooks main activity here    
    public static void setMainActivity(gps_status gpsStatus) 
    {
      MAIN_ACTIVITY = gpsStatus;      
    }
    
    /* 
     * not using ipc...but if we use in future
     */
    public IBinder onBind(Intent intent) {
      return null;
    }

    @Override 
    public void onCreate() 
    {
      super.onCreate();     

      _startService();

      if (MAIN_ACTIVITY != null)  Log.d(getClass().getSimpleName(), "GPSService started");
    }

    @Override 
    public void onDestroy() 
    {
      super.onDestroy();

      _shutdownService();

      if (MAIN_ACTIVITY != null)  Log.d(getClass().getSimpleName(), "GPSService stopped");
    }

    
    /*
     * starting the service
     */
    private void _startService()
    {    
       doServiceWork();
    }
    
    /*
     * start the processing, the actual work, getting config params, get data from network etc
     */
    private void doServiceWork()
    {
    	locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	crit.setAccuracy(Criteria.ACCURACY_FINE);
    	provider = locationManager.getBestProvider(crit, true);  
    	
    	
    	locationListener = new LocationListener() 
    	{
	  		public void onLocationChanged(Location location) 
	  		{
	  		  updateWithNewLocation(location);
	  		}
	  		public void onProviderDisabled(String provider){}
	  		public void onProviderEnabled(String provider) {}
	  		public void onStatusChanged(String provider, int status, Bundle extras) {}
  		};
		// Request for the live location
  		locationManager.requestLocationUpdates(provider,60000,1,locationListener);

    }
    private String get_defaultid()
    {
    	String id="";
    	try 
        {
        	myDB =  this.openOrCreateDatabase(MY_DATABASE_NAME, 0, null);
            Cursor c = myDB.query(MY_TABLE_NAME, columns, null,null,null, null,null);
            
            if (c != null) 
            {
            	c.moveToFirst();
            	for(int i =0;i<c.getCount();i++)
            	{
            		if(c.getString(1).equals("T"))
            		{
            			id  = c.getString(0);
            			break;
            		}
            		c.moveToNext();
            	}

            }
        }
        catch(Exception ex)
        {
        }
    	return id;
    }
    public void send_data() throws ClientProtocolException, IOException
    {
    	HttpResponse response = client.execute(post);
    }
    public void updateWithNewLocation(Location location) 
    {
    	  if (location != null) 
    	  {
    	     lat = location.getLatitude();
    	     lng = location.getLongitude();
    	     latitude_str = ""+lat;
    	     longitude_str = ""+lng;
    	    latLongString = "Latitude:" + lat + "\nLongitude:" + lng;
    	    Id = get_defaultid();
        	post = new HttpPost("http://darshan74.dyndns.org/ASP.NET/GPS/Location/default.aspx?latitude="+lat+"&longitude="+lng+"&id="+Id);
    	  } 
    	  else 
    	  {
    	    latLongString = "No location found";
    	  }
    	 
    	  try {
			send_data();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    /*
     * shutting down the service
     */
    private void _shutdownService()
    {
    	locationManager.removeUpdates(locationListener);
    }
}
