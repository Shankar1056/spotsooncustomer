package com.adapter;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.spotsoon.customer.R;
import com.threembed.spotasap_pojos.Notificationdatapojo;
import com.utility.Utility;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by embed-pc on 14/10/15.
 */
public class NotificationList_adapter extends ArrayAdapter<Notificationdatapojo>
{
    private ArrayList<Notificationdatapojo> notificationdata;
    Context mcontext;
    Typeface robot_regular;

 public NotificationList_adapter(Context context,ArrayList<Notificationdatapojo> list)
    {
        super(context, R.layout.notification_iteam_list, list);
        notificationdata=list;
        Collections.sort(notificationdata);
        mcontext=context;
        robot_regular=Typeface.createFromAsset(mcontext.getAssets(),"font/ProximaNova-Regular_0.otf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.notification_iteam_list, parent, false);
        TextView timebefore=(TextView)convertView.findViewById(R.id.timebefore);
        timebefore.setTypeface(robot_regular);
        timebefore.setText(getTimeDifference(notificationdata.get(position).getDatetime()));
        TextView textView=(TextView)convertView.findViewById(R.id.details);
        textView.setTypeface(robot_regular);
        textView.setText(notificationdata.get(position).getMessgae());
        TextView newItem=(TextView)convertView.findViewById(R.id.newtext);
        if(!notificationdata.get(position).isReadcheck())
        {
            newItem.setVisibility(View.INVISIBLE);
        }else
        {
            newItem.setVisibility(View.VISIBLE);
        }

        return convertView;
    }


    private String getTimeDifference(String receivedTime)
    {
        Date timedate_now=null,getTimedate_received=null;
        String delay_time="";
      String currentTime=Utility.getCurrentDateTimeString();
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            timedate_now=dateFormat.parse(currentTime);
            getTimedate_received=dateFormat.parse(receivedTime);

            long difference=timedate_now.getTime()-getTimedate_received.getTime();
            int day= (int)difference/(24*60*60*1000)%60;
            int hr=(int)difference/(60*60*1000)%60;
            int minute=(int)difference/(60*1000)%60;
            int sec=(int)difference/1000%60;
            if(day!=0)
            {
                delay_time=""+day+" "+" day";
            }else if(hr!=0)
            {
                delay_time=delay_time+" "+hr+" hour";
            } else if(minute!=0)
            {
                delay_time=delay_time+" "+minute+" min";
            }
            else if(sec!=0)
            {
                delay_time=delay_time+" "+sec+" sec";
            }

        }catch (ParseException e)
        {
            e.printStackTrace();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        if(delay_time==null||delay_time.equals(""))
        {
            return "Now";
        }else
        {
            return delay_time+" "+"ago";
        }
    }

}
