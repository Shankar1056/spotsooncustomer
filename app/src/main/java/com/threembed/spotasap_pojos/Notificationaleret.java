package com.threembed.spotasap_pojos;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adapter.NotificationList_adapter;
import com.google.gson.Gson;
import com.spotsoon.customer.R;
import com.utility.GcmIntentService;
import com.utility.SpotasManager;
import com.utility.Utility;
import com.utility.VariableConstants;

import org.json.JSONObject;

public class Notificationaleret extends Fragment implements AdapterView.OnItemClickListener
{
    /**
     * Defining the all variables....*/

    private static ListView notification_list;
    private static RelativeLayout blankscreen;
    public static NotificationList_adapter notificationList_adapter;
    private static Activity myactivty=null;
    private SpotasManager spmanager;
    private static Gson gson=new Gson();
    JSONObject jsonObj = new JSONObject();
    ProgressDialog pDialog;
    private static Notification_list_pojo notification_list_pojo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        myactivty=getActivity();
        VariableConstants.fromRecuringbusiness=false;
        //VariableConstants.isNotificationaleretpageOpened=true;
        VariableConstants.notificationNumber=0;
        spmanager = new SpotasManager(getActivity());
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);

/**
 * Hiding the keypad of phone if open before opening the Fragment..
 * */
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        View view =inflater.inflate(R.layout.fragment_notification, container, false);

        notification_list=(ListView)view.findViewById(R.id.notificationlist);
        notification_list.setOnItemClickListener(this);

        TextView notiicationtext1=(TextView)view.findViewById(R.id.text1);

        TextView notificatiotext2=(TextView)view.findViewById(R.id.text2);

        TextView notificationtext3=(TextView)view.findViewById(R.id.text3);
        blankscreen=(RelativeLayout)view.findViewById(R.id.blancscreennotification);

        // notificationservice();

        return view;
    }

    private void notificationservice()
    {
        if(pDialog!=null)
        {
            pDialog.show();
        }
       /* JSONObject jsonObj = new JSONObject();*/

        try {


            jsonObj.put("ent_dev_id", Utility.getDeviceId(getActivity()));
            jsonObj.put("ent_sess_token", spmanager.getSession_token());
            jsonObj.put("ent_user_type", "2");
            jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());
            Utility.printLog("the params in notification is " + jsonObj);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        //calling doJsonRequest of Utility class

        Utility.doJsonRequest(VariableConstants.host_url + "getProfile", jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(String jsonResponse) {

                notificationResponse(jsonResponse);
                Utility.printLog("the json object response is in notification ::" + jsonResponse);
            }

            @Override
            public void onError(String error) {
                Utility.printLog("Notification JSON DATA Error" + error);

            }

        });

    }

    private void notificationResponse(String jsonResponse)
    {

    }

    @Override
    public void onResume()
    {
        super.onResume();
        ActionBar ab = getActivity().getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        getActivity().getActionBar().setDisplayShowCustomEnabled(true);
        getActivity().getActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = LayoutInflater.from(getActivity());
        View v = inflator.inflate(R.layout.notification_actionbar, null);
        TextView textView=(TextView)v.findViewById(R.id.notificationdata);
        getActivity().getActionBar().setCustomView(v);

        setAdapter();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //VariableConstants.isNotificationaleretpageOpened=false;
        Utility.printLog("Ondestroy of fragment");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        VariableConstants.isFromnotification=true;
        Notificationdatapojo notificationitem=(Notificationdatapojo)parent.getAdapter().getItem(position);
        notificationitem.setReadcheck(false);
        if(notification_list_pojo!=null)
        {
            notification_list_pojo.getNotification_data().set(position, notificationitem);
            notificationList_adapter.notifyDataSetChanged();
            String notification_list=gson.toJson(notification_list_pojo, Notification_list_pojo.class);
            spmanager.setNotificationList(notification_list);
        }
        /**
         * Storing the current business type in Session manager...*/
        spmanager.setBusinesstype(notificationitem.getBusinesstype());
        Utility.printLog("Business type:" + notificationitem.getBusinesstype());
        Log.i("Business recur", "" + notificationitem.getBusinesstype());
       /* if (notificationitem.getBusinesstype().equals("0")) {
            Intent intent=new Intent(getActivity(),Recuring_business.class);
            intent.putExtra("appointment_Id",notificationitem.getSimpleBusiness_Id());//notificationitem.getBusiness_id()
            intent.putExtra("bid",notificationitem.getBusiness_id());
            intent.putExtra("StatCode",notificationitem.getStatus());
            Log.i("", "the status is " +notificationitem.getStatus()+" "+notificationitem.getBusiness_id()+" "+notificationitem.getSimpleBusiness_Id());
            //Log.i("","the id's are "+business_id+" "+bid+" "+statecode);
            startActivity(intent);

        }
         if (!notificationitem.getBusinesstype().equals("0"))
        {
            Intent intent=new Intent(getActivity(), Last_order_summary.class);
            intent.putExtra("appointment_Id", notificationitem.getBusiness_id());
            Log.i("notification ","class "+ notificationitem.getBusiness_id()+" "+notificationitem.getSimpleBusiness_Id());
            startActivity(intent);


        }*/

    }

    public static void updateArrayList()
    {
        if(myactivty!=null)
        {
            myactivty.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (blankscreen.getVisibility() == View.VISIBLE) {
                        blankscreen.setVisibility(View.GONE);
                        notification_list.setVisibility(View.VISIBLE);
                    }
                    String notification_list_data = new SpotasManager(myactivty.getApplicationContext()).getNotificationList();
                    notification_list_pojo = gson.fromJson(notification_list_data, Notification_list_pojo.class);
                    notificationList_adapter = new NotificationList_adapter(myactivty, notification_list_pojo.getNotification_data());
                    notification_list.setAdapter(notificationList_adapter);
                    notificationList_adapter.notifyDataSetChanged();

                    /**
                     * Checking notification manager is created or not..*/
                    if (GcmIntentService.notificationManager != null) {
                        GcmIntentService.notificationManager.cancelAll();
                    }
                }
            });

        }

    }

    public void setAdapter()
    {
        String notification_list_data=new SpotasManager(myactivty.getApplicationContext()).getNotificationList();
        notification_list_pojo=gson.fromJson(notification_list_data,Notification_list_pojo.class);

        if(notification_list_pojo!=null&&notification_list_pojo.getNotification_data().size()>0)
        {
            /**
             * Checking notification manager is created or not..*/
            if(GcmIntentService.notificationManager!=null)
            {
                GcmIntentService.notificationManager.cancelAll();
            }
            notificationList_adapter=new NotificationList_adapter(myactivty,notification_list_pojo.getNotification_data());
            notification_list.setAdapter(notificationList_adapter);
            notificationList_adapter.notifyDataSetChanged();
        }else
        {
            notification_list.setVisibility(View.GONE);
            blankscreen.setVisibility(View.VISIBLE);
        }
    }
}
