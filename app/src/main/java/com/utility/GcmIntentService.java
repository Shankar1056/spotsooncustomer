package com.utility;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.spotsoon.customer.GcmBroadcastReceiver;
import com.spotsoon.customer.NotificationHandler;
import com.spotsoon.customer.R;
import com.threembed.spotasap_pojos.Notification_list_pojo;
import com.threembed.spotasap_pojos.Notificationaleret;
import com.threembed.spotasap_pojos.Notificationdatapojo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

public class GcmIntentService extends IntentService
{
	public static final int NOTIFICATION_ID = 1;
	public static NotificationManager notificationManager;
	private Intent intent;
	private Notification_list_pojo notification_list_pojo;

	public GcmIntentService()
	{
		super(VariableConstants.SENDER_ID_PUSH);
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Bundle extras = intent.getExtras();
		Context context=this;
		SpotasManager spotasManager=new SpotasManager(context);
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);

		Log.i("Spotsoon","onHandleIntent intent="+intent);
		Log.i("Spotsoon","onHandleIntent extras="+extras.toString());

		String payload = extras.getString("payload");
		String action = extras.getString("action");
		String type = extras.getString("type");

		Log.i("Spotsoon","onHandleIntent payload="+payload);
		Log.i("Spotsoon","onHandleIntent action="+action);
		Log.i("Spotsoon","onHandleIntent type="+type);

		if(!extras.isEmpty())
		{
			if(type!=null && type.length()>0)
			{
				if(type.equalsIgnoreCase("text"))
				{
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

						Notification noti = new Notification();
						noti = setBigTextStyleNotification(payload);

						noti.defaults |= Notification.DEFAULT_LIGHTS;
						noti.defaults |= Notification.DEFAULT_VIBRATE;
						noti.defaults |= Notification.DEFAULT_SOUND;

						noti.flags |= Notification.FLAG_ONLY_ALERT_ONCE;

						NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
						mNotificationManager.notify(0, noti);
					}
					else {

						sendNotification(payload, "");
					}
				}
				else if(type.equalsIgnoreCase("image")) {

					String img_url = extras.getString("img_url");
					Log.i("Spotsoon","onHandleIntent img_url="+img_url);

					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

						Notification noti = new Notification();
						noti = setBigPictureStyleNotification(img_url,payload);

						noti.defaults |= Notification.DEFAULT_LIGHTS;
						noti.defaults |= Notification.DEFAULT_VIBRATE;
						noti.defaults |= Notification.DEFAULT_SOUND;

						noti.flags |= Notification.FLAG_ONLY_ALERT_ONCE;

						NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
						mNotificationManager.notify(0, noti);
					}
					else {
						Log.i("Spotsoon","onHandleIntent img_url not Jelly");
						sendNotification(payload, "");
					}
				}
			}
		}

		/*if(!extras.isEmpty())
		{
			*//*
			 * Filter messages based on message type. Since it is likely that GCM
			 * will be extended in the future with new message types, just ignore
			 * any message types you're not interested in, or that you don't
			 * recognize.
			 *//*
			Gson gson=new Gson();
			notification_list_pojo = gson.fromJson(spotasManager.getNotificationList(),Notification_list_pojo.class);

			if(notification_list_pojo!=null)
			{
				if(notification_list_pojo.getNotification_data().size()>=5)
				{

					notification_list_pojo.getNotification_data().remove(notification_list_pojo.getNotification_data().size()-1);
				}
			}else
			{
				notification_list_pojo=new Notification_list_pojo();
			}

			*//**
			 * Opening the splash screen on on click of the notification icon..*//*

			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
			{
				sendNotification("Send error: " + extras.toString(),"");
			}
			else if(GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
			{
				sendNotification("Deleted messages on server: " +extras.toString(),"");
			}
			else if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
			{
				String message=null;
				String extra=null;
				String status = null;
				String simplebusiness_ID = null;
				String BusinessID=null;
				String Businesstype=null;

				if (extras!=null&&extras.toString().length()>0)
				{
					message = extras.getString("payload");
					extra = extras.getString("action");
					BusinessID = extras.getString("bid");
					Businesstype = extras.getString("type");
					status = extras.getString("s");
					simplebusiness_ID = extras.getString("busId");
					Log.i("","the status code "+status+" "+simplebusiness_ID+" id "+BusinessID);
					Utility.printLog("Business type notification:" + Businesstype);
					Notificationdatapojo notificationdata=new Notificationdatapojo();
					notificationdata.setSimpleBusiness_Id(simplebusiness_ID);
					notificationdata.setStatus(status);
					notificationdata.setBusiness_id(BusinessID);
					notificationdata.setBusinesstype(Businesstype);
					notificationdata.setMessgae(message);

					notificationdata.setDatetime(getCurrentDateTimeString());
					notificationdata.setReadcheck(true);
					notification_list_pojo.getNotification_data().add(notificationdata);
					*//**
					 * Calling comparable method to check if the *//*
					Collections.sort(notification_list_pojo.getNotification_data());
					String notification_list=gson.toJson(notification_list_pojo, Notification_list_pojo.class);
					spotasManager.setNotificationList(notification_list);
				}

				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

						setBigTextStyleNotification(message);
				}
				else {

						sendNotification(message, extra);
				}

				*//*if(VariableConstants.isNotificationaleretpageOpened)
				{
					updateList();
				}else if(VariableConstants.isMenuActivityActivity&&VariableConstants.isAppalive)
				{
					//MainActivityNew.UpdateNotificationNumber();
				}*//*
			}
		}*/

		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg,String action)
	{
		VariableConstants.counterfornotifiation = notification_list_pojo.getNotification_data().size();
		//if(VariableConstants.isMenuActivityActivity && !VariableConstants.isNotificationaleretpageOpened)

		{
			VariableConstants.notificationNumber = VariableConstants.notificationNumber+1;
		}

		/**
		 * Creating the Notification Manager Object*/
		notificationManager= (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
		/**
		 * Creating intent to handel the on click*/
		if(!VariableConstants.isAppalive && action!=null)
		{
			intent = new Intent(this,NotificationHandler.class);
			intent.putExtra(VariableConstants.noitfy_id,"2");
		}
		else if(action!=null)
		{
			intent = new Intent(this,NotificationHandler.class);
			intent.putExtra(VariableConstants.noitfy_id,"1");
		}

		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pandinginten = PendingIntent.getActivity(this,NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		/**
		 * Creating Notification object and Define its properties..*/

		Notification notification=new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.launcher)
				.setTicker("SpotSoon Alert Message ")
				.setContentText(msg)
				.setAutoCancel(true)
				.setContentIntent(pandinginten)
				.setOngoing(true)
				.setContentTitle("SpotSoon")
				.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.FLAG_AUTO_CANCEL)
				.setWhen(System.currentTimeMillis())
				.setNumber(VariableConstants.counterfornotifiation)
				.build();

		/**
		 * Telling the MainActivity that coming from the Notification......  */
		notificationManager.notify(NOTIFICATION_ID, notification);
	}

	private void updateList()
	{
		Notificationaleret.updateArrayList();
	}

	/**
	 * Reading the current notification time in the formate
	 * yyyy-MM-dd HH:mm:ss from Data class */
	public String getCurrentDateTimeString()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String currentDateTimeString = dateFormat.format(date);
		return currentDateTimeString;
	}

	private Notification setBigTextStyleNotification(String message) {
		Bitmap remote_picture = null;

		// Create the style object with BigTextStyle subclass.
		NotificationCompat.BigTextStyle notiStyle = new NotificationCompat.BigTextStyle();
		notiStyle.setBigContentTitle(getString(R.string.app_name));
		//notiStyle.setSummaryText("Text when it expanded");

		try {

			//remote_picture = BitmapFactory.decodeStream((InputStream) new URL(sample_url).getContent());
			remote_picture = BitmapFactory.decodeResource(getResources(), R.drawable.launcher);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Add the big text to the style.
		CharSequence bigText = message;
		notiStyle.bigText(bigText);

		// Creates an explicit intent for an ResultActivity to receive.
		Intent resultIntent = new Intent(this, NotificationHandler.class);

		if(!VariableConstants.isAppalive)
		{
			resultIntent = new Intent(this,NotificationHandler.class);
			resultIntent.putExtra(VariableConstants.noitfy_id,"2");
		}
		else
		{
			resultIntent = new Intent(this,NotificationHandler.class);
			resultIntent.putExtra(VariableConstants.noitfy_id,"1");
		}

		// This ensures that the back button follows the recommended convention for the back key.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

		// Adds the back stack for the Intent (but not the Intent itself).
		stackBuilder.addParentStack(NotificationHandler.class);

		// Adds the Intent that starts the Activity to the top of the stack.
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		return new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.launcher)
				.setAutoCancel(true)
				.setLargeIcon(remote_picture)
				.setContentIntent(resultPendingIntent)
						//.addAction(R.drawable.launcher, "One", resultPendingIntent)
						//.addAction(R.drawable.launcher, "Two", resultPendingIntent)
						//.addAction(R.drawable.launcher, "Three", resultPendingIntent)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(message)
				.setStyle(notiStyle).build();
	}

	/**
	 * Big Picture Style Notification
	 *
	 * @return Notification
	 */
	private Notification setBigPictureStyleNotification(String image_url,String message) {
		Bitmap remote_picture = null;
		Bitmap large_icon = null;

		// Create the style object with BigPictureStyle subclass.
		NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
		notiStyle.setBigContentTitle(getString(R.string.app_name));
		notiStyle.setSummaryText(message);

		try {
			remote_picture = BitmapFactory.decodeStream((InputStream) new URL(image_url).getContent());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Add the big picture to the style.
		notiStyle.bigPicture(remote_picture);

		// Creates an explicit intent for an ResultActivity to receive.
		Intent resultIntent = new Intent(this, NotificationHandler.class);

		if(!VariableConstants.isAppalive)
		{
			resultIntent = new Intent(this,NotificationHandler.class);
			resultIntent.putExtra(VariableConstants.noitfy_id,"2");
		}
		else
		{
			resultIntent = new Intent(this,NotificationHandler.class);
			resultIntent.putExtra(VariableConstants.noitfy_id,"1");
		}
		// This ensures that the back button follows the recommended convention for the back key.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

		// Adds the back stack for the Intent (but not the Intent itself).
		stackBuilder.addParentStack(NotificationHandler.class);

		// Adds the Intent that starts the Activity to the top of the stack.
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		try {
			large_icon = BitmapFactory.decodeResource(getResources(), R.drawable.launcher);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.launcher)
				.setAutoCancel(true)
				.setLargeIcon(large_icon)
				.setContentIntent(resultPendingIntent)
				//.addAction(R.drawable.launcher, "One", resultPendingIntent)
				//.addAction(R.drawable.launcher, "Two", resultPendingIntent)
				//.addAction(R.drawable.launcher, "Three", resultPendingIntent)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(message)
				.setStyle(notiStyle).build();
	}
}