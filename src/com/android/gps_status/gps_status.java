package com.android.gps_status;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class gps_status extends Activity {
	// General
	Button b1, b2, b3, b4, b5, b6, b7, b8, b9;
	Boolean flag = false;
	Spinner s1;
	View v1;
	double lat, lng;
	String provider, latLongString, latitude_str, longitude_str, Id, IP, id;
	ArrayAdapter<CharSequence> AdapterCountries;
	// Database
	final String MY_DATABASE_NAME = "GPSDB";
	final String MY_TABLE_NAME = "tblids";
	String[] columns = { "IDs", "default_id" };
	SQLiteDatabase myDB = null;

	LocationManager locationManager;
	Criteria crit = new Criteria();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Initialization
		v1 = findViewById(R.id.widget38);
		v1.setBackgroundDrawable(getWallpaper());
		// v1.setBackgroundResource(R.drawable.bg_img);
		b1 = (Button) findViewById(R.id.widget40);
		b2 = (Button) findViewById(R.id.widget41);
		b3 = (Button) findViewById(R.id.widget42);
		b4 = (Button) findViewById(R.id.widget43);
		b5 = (Button) findViewById(R.id.Button01);
		b6 = (Button) findViewById(R.id.Button02);
		b9 = (Button) findViewById(R.id.update);
		b7 = (Button) findViewById(R.id.add);
		b8 = (Button) findViewById(R.id.remove);
		s1 = (Spinner) findViewById(R.id.spinner1);

		// For Spinner Update
		AdapterCountries = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item);
		AdapterCountries
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s1.setAdapter(AdapterCountries);
		setspinner();

		// For Service..
		GPSService.setMainActivity(this);
		final Intent gpsService = new Intent(this, GPSService.class);

		// Listeners
		b1.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				showGPSDisabledAlertToUser();
			}
		});
		b2.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				startService(gpsService);
				flag = true;
				updateButtons();
			}
		});
		b6.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				stopService(gpsService);
				finish();
			}
		});
		b5.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				stopService(gpsService);
				flag = false;
				updateButtons();
			}
		});
		b3.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), act.class);
				startActivityForResult(myIntent, 0);
			}
		});
		b4.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), act2.class);
				startActivityForResult(myIntent, 0);
			}
		});
		b7.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), act3.class);
				startActivityForResult(myIntent, 0);
			}
		});
		b8.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				removeEntry(s1.getSelectedItem().toString());
				setspinner();
				updateButtons();
			}
		});
		s1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				update_defaultid(s1.getSelectedItem().toString());
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		b9.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				updateLocation();
			}
		});
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		updateButtons();

	}

	@Override
	public void onResume() {
		super.onResume();
		setspinner();
		updateButtons();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void updateLocation() {
		try {
			final HttpClient client = new DefaultHttpClient();
			HttpPost post;
			Location location;
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			crit.setAccuracy(Criteria.ACCURACY_FINE);
			provider = locationManager.getBestProvider(crit, true);
			location = locationManager.getLastKnownLocation(provider);
			lat = location.getLatitude();
			lng = location.getLongitude();
			latitude_str = "" + lat;
			longitude_str = "" + lng;
			latLongString = "Lat:" + lat + "\nLong:" + lng;
			Id = get_defaultid();
			post = new HttpPost(
					"http://maunik318.dyndns.org/ASP.NET/GPS/Location/default.aspx?latitude="
							+ lat + "&longitude=" + lng + "&id=" + Id);
			HttpResponse response = client.execute(post);
			MessageBox(latLongString);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String get_defaultid() {
		try {
			myDB = this.openOrCreateDatabase(MY_DATABASE_NAME, 0, null);
			Cursor c = myDB.query(MY_TABLE_NAME, columns, null, null, null,
					null, null);

			if (c != null) {
				c.moveToFirst();
				for (int i = 0; i < c.getCount(); i++) {
					if (c.getString(1).equals("T")) {
						id = c.getString(0).toString();
						break;
					}
					c.moveToNext();
				}

			}
		} catch (Exception ex) {
		}
		return id;
	}

	private void update_defaultid(String data) {
		try {
			myDB = this.openOrCreateDatabase(MY_DATABASE_NAME, 0, null);
			myDB.execSQL("UPDATE tblids SET default_id = 'F'");
			myDB.execSQL("UPDATE tblids SET default_id = 'T' where IDs='"
					+ data + "'");
			setspinner();
		} catch (Exception ex) {
			MessageBox(ex.toString());
		}
	}

	private void updateButtons() {
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			b1.setEnabled(false);
			// Toast.makeText(this, "GPS is Enabled in your devide",
			// Toast.LENGTH_SHORT).show();
		}
		if (s1.getCount() == 0 || (flag == true)) {
			b2.setEnabled(false);
			b5.setEnabled(true);
		} else {
			b2.setEnabled(true);
			b5.setEnabled(false);
		}
		if (s1.getCount() == 0) {
			b8.setEnabled(false);
			b9.setEnabled(false);
		} else {
			b8.setEnabled(true);
			b9.setEnabled(true);
		}
	}

	private void removeEntry(String data) {
		try {
			myDB = this.openOrCreateDatabase(MY_DATABASE_NAME, 0, null);
			myDB.execSQL("DELETE FROM tblids Where IDs = '" + data + "'");
		} catch (Exception ex) {
			MessageBox(ex.toString());
		}
	}

	private void setspinner() {
		try {
			myDB = this.openOrCreateDatabase(MY_DATABASE_NAME, 0, null);
			myDB.execSQL("CREATE TABLE IF NOT EXISTS tblids (IDs VARCHAR(40) PRIMARY KEY,default_id VARCHAR(1));");
			Cursor c = myDB.query(MY_TABLE_NAME, columns, null, null, null,
					null, null);

			if (c != null) {
				c.moveToFirst();
				AdapterCountries.clear();
				for (int i = 0; i < c.getCount(); i++) {
					AdapterCountries.add(c.getString(0));
					c.moveToNext();
				}
			}
		} catch (Exception ex) {
			MessageBox(ex.toString());
		}
	}

	public void stop() {
		b5.setEnabled(false);
		b2.setEnabled(true);
		flag = false;
	}

	public void showGPSDisabledAlertToUser() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage(
						"GPS is disabled in your device. Would you like to enable it?")
				.setCancelable(false)
				.setPositiveButton("Goto Settings Page To Enable GPS",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent callGPSSettingIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(callGPSSettingIntent);

							}
						});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	private void MessageBox(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

}