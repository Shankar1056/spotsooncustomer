package com.spotsoon.customer;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.threembed.MainActivityNew;
import com.utility.VariableConstants;


public class NotificationHandler extends Activity
{
private Intent intent=null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intentdata=getIntent();
        String msg=intentdata.getStringExtra(VariableConstants.noitfy_id);
        if(msg.equals("1"))
        {
          VariableConstants.isFromnotification=true;
          intent=new Intent(this,MainActivityNew.class);
        }else if(msg.equals("2"))
        {
            VariableConstants.isFromnotification=true;
            intent=new Intent(this,SpotAsap.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
