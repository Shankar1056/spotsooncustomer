package com.spotsoon.customer;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.utility.GcmIntentService;
import com.utility.SpotasManager;
import com.utility.Utility;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver
{
	private String action;
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";


	@Override
	public void onReceive(Context context, Intent intent)
	{
		SpotasManager spotasManager = new SpotasManager(context);
		spotasManager.setIsPendingScreenNotificationcame(true);
		Bundle extras = intent.getExtras();

		if (extras!=null)
		{
			String str="";
			for(String key:intent.getExtras().keySet())
			{
				str=str+""+key+"=>"+intent.getExtras().get(key) + ";";
			}
			Utility.printLog("Data is" + str);
		}

		if (extras!=null&&extras.toString().length()>0)
		{
		String message = extras.getString("payload");
			action = extras.getString("action");
			spotasManager.setACTION(action);
			spotasManager.setPayload(message);
		}


		if (action==null||"com.google.android.c2dm.intent.REGISTRATION".equals(action))
		{
			Utility.printLog("C2DM", "Received registration ID");

			final String registrationId=intent.getStringExtra("registration_id");
			String error = intent.getStringExtra("error");
			Utility.printLog("C2DM", "dmControl: registrationId = " + registrationId+ ", error = "+ error);
			storeRegistrationId(context,registrationId) ;
		}
		ComponentName comp = new ComponentName(context.getPackageName(),GcmIntentService.class.getName());
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}

	private void storeRegistrationId(Context context, String regId)
	{
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	private SharedPreferences getGCMPreferences(Context context)
	{
		return context.getSharedPreferences(SpotAsap.class.getSimpleName(),Context.MODE_PRIVATE);
	}


	public static int getAppVersion(Context context)
	{
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
			return packageInfo.versionCode;

		} catch (NameNotFoundException e)
		{
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

}