package com.spotsoon.customer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.threembed.MainActivityNew;
import com.threembed.closeandopenbean.SubscriptionsHelper;
import com.utility.SpotasManager;
import com.utility.Utility;
import com.utility.VariableConstants;

import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;


/**
 * Created by VARUN on 3/20/2016.
 */
public class InventumRechargeStatusActivity extends AppCompatActivity{

    private TextView status_msg,pending_msg,tv_payuId;
    private ImageView status_image,addView;
    private RelativeLayout statusLayout;
    private String TAG = "Spotsoon";
    private SpotasManager session;
    private String mPayuId;
    private boolean isInvoice;
    private LinearLayout progressLayout;
    boolean toshowdialog = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(InventumRechargeStatusActivity.this, new Crashlytics());
        setContentView(R.layout.inventum_recharge_status);
        mPayuId = getIntent().getStringExtra("PAYU_ID");
        String mType = getIntent().getStringExtra("TYPE");
        isInvoice = getIntent().getBooleanExtra("INVOICE",false);
        Utility.statusbar(InventumRechargeStatusActivity.this);

        initializeViews();

        if(mPayuId!=null)
        {
            tv_payuId.setText(mPayuId);
        }
        else {
            tv_payuId.setVisibility(View.GONE);
        }

        getInventumRechargeStatus();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeViews()
    {
        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_inventum_status);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(isInvoice)
        {
            getSupportActionBar().setTitle("Payment Status");
        }
        else {
            getSupportActionBar().setTitle("Recharge Status");
        }

        session = new SpotasManager(this);
        status_image = (ImageView)findViewById(R.id.status_image);
        status_msg = (TextView)findViewById(R.id.status_message);
        pending_msg = (TextView)findViewById(R.id.pending_status_message);
        tv_payuId = (TextView)findViewById(R.id.tv_payuId);
        Button gotIt = (Button)findViewById(R.id.got_it);
        statusLayout = (RelativeLayout)findViewById(R.id.status_layout);
        addView = (ImageView)findViewById(R.id.addView);
        progressLayout=(LinearLayout)findViewById(R.id.progressLayout);
        progressLayout.setVisibility(View.GONE);
        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
    }

    private void getInventumRechargeStatus()
    {
        final ProgressDialog pDialog = new ProgressDialog(InventumRechargeStatusActivity.this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(true);
        if(pDialog!=null) {
            pDialog.show();
        }

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("ent_dev_id", Utility.getDeviceId(InventumRechargeStatusActivity.this));
            jsonObj.put("ent_sess_token", session.getSession_token());
            jsonObj.put("ent_payu_id", mPayuId);
           // jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());

        }
        catch(Exception e)
        {
        }
        Utility.doJsonRequest(VariableConstants.GET_PAYMENTSTATUS + "getPaymentStatus", jsonObj, new Utility.JsonRequestCallback() {
            @SuppressLint("LongLogTag")
            @Override
            public void onSuccess(String jsonResponse) {
                // TODO Auto-generated method stub

                if (pDialog != null) {
                    pDialog.dismiss();
                }

                Log.i(TAG, "getInventumRechargeStatus jsonResponse=" + jsonResponse);

                try {

                    Gson gson = new Gson();
                    SubscriptionsHelper subscriptionsResponse = gson.fromJson(jsonResponse, SubscriptionsHelper.class);

                    if (subscriptionsResponse != null) {

                        if (subscriptionsResponse.getErrFlag().equalsIgnoreCase("0")) {

                            statusLayout.setVisibility(View.VISIBLE);

                            if (subscriptionsResponse.getAdv() != null && subscriptionsResponse.getAdv().length() > 0) {

                                Picasso.with(InventumRechargeStatusActivity.this)
                                        .load(subscriptionsResponse.getAdv())
                                        .placeholder(R.drawable.customer_paper_magazine_image_frame)
                                        .into(addView);
                            }
                               String errmsg = subscriptionsResponse.getErrMsg();
                            if (subscriptionsResponse.getErrMsg() != null && subscriptionsResponse.getErrMsg().length() > 0) {
                                status_msg.setText(subscriptionsResponse.getErrMsg());
                                if (subscriptionsResponse.getRateUs().equalsIgnoreCase("1")) {
                                    progressLayout.setVisibility(View.VISIBLE);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            if (toshowdialog)
                                            rateUsPopup();
                                            progressLayout.setVisibility(View.GONE);
                                        }
                                    }, 4000);

                                }
                            } else {
                                status_msg.setText("Payment Successful!");

                            }

                            if (subscriptionsResponse.getErrDesc() != null && subscriptionsResponse.getErrDesc().length() > 0) {
                                pending_msg.setText(subscriptionsResponse.getErrDesc());
                            } else {
                                pending_msg.setText("Please go to My Orders page for status update");
                            }



                        } else {

                            statusLayout.setVisibility(View.VISIBLE);

                            status_msg.setText("Recharge Pending!");
                            status_image.setBackgroundResource(R.drawable.pending);
                            pending_msg.setText("You will receive an SMS or Email, once your operator has completed this request");
                        }
                    } else {
                        showDialog();
                    }

                } catch (Exception e) {

                    showDialog();
                }
            }

            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub

                if (pDialog != null) {
                    pDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(InventumRechargeStatusActivity.this, MainActivityNew.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.anim_three, R.anim.anim_four);
        toshowdialog = false;
        progressLayout.setVisibility(View.GONE);

    }

    private void showDialog()
    {
                      AlertDialog.Builder builder = new AlertDialog.Builder(InventumRechargeStatusActivity.this);
                        builder.setTitle("Warning!");
                        builder.setMessage("Something went wrong!");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();

    }
    private void rateUsPopup() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View dialogView = inflater.inflate(R.layout.rate_us_popup, null);
        LinearLayout meh = (LinearLayout) dialogView.findViewById(R.id.ll_meh);
        LinearLayout lovedIt = (LinearLayout) dialogView.findViewById(R.id.ll_loved_it);
        builder.setView(dialogView);
        final android.support.v7.app.AlertDialog alertDialog = builder.create();

        meh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                openFeedBackPopup();
            }
        });

        lovedIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                openRateAppPopUp();
            }
        });

        alertDialog.show();
    }

    private void openRateAppPopUp() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View dialogView = inflater.inflate(R.layout.rate_app_pop_up, null);
        TextView nope = (TextView) dialogView.findViewById(R.id.tv_nope);
        TextView sure = (TextView) dialogView.findViewById(R.id.tv_sure);
        builder.setView(dialogView);
        final android.support.v7.app.AlertDialog alertDialog = builder.create();

        nope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                final String appPackage = getPackageName();

                try{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+appPackage)));
                }
                catch (android.content.ActivityNotFoundException e)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+appPackage)));
                }
            }
        });

        alertDialog.show();

    }



    private void openFeedBackPopup() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View dialogView = inflater.inflate(R.layout.feedback_popup, null);
        TextView nope = (TextView) dialogView.findViewById(R.id.tv_nope);
        TextView sure = (TextView) dialogView.findViewById(R.id.tv_sure);
        builder.setView(dialogView);
        final android.support.v7.app.AlertDialog alertDialog = builder.create();

        nope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                openMailBox();
            }
        });

        alertDialog.show();
    }
    private void openMailBox() {
        //
        Intent emailIntent =  new Intent (Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@spotsoon.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Customer Feedback");
        startActivity(emailIntent);
    }

}