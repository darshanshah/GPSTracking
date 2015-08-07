package com.android.gps_status;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootHelper extends BroadcastReceiver {
	private final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub

		if (arg1.getAction().equals(BOOT_COMPLETED_ACTION)) {
			Intent myIntent = new Intent(arg0, GPSService.class);
			arg0.startService(myIntent);
		}

	}
}