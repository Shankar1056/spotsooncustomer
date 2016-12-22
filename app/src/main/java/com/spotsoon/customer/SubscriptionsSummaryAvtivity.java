package com.spotsoon.customer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.PayuConstants;
import com.payu.payuui.PayUBaseActivity;
import com.razorpay.Checkout;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;
import com.threembed.HomeFragmentNew;
import com.threembed.spotasap_pojos.PayU_Hashpojo;
import com.threembed.spotasap_pojos.Plans;
import com.threembed.spotasap_pojos.PlansCouponsDetails;
import com.threembed.spotasap_pojos.PlansDetailsHelper;
import com.threembed.spotasap_pojos.SubscriptionsDetailsHelper;
import com.utility.OkHttpRequest;
import com.utility.SpotasManager;
import com.utility.Utility;
import com.utility.VariableConstants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by VARUN on 2/21/2016.
 */
public class SubscriptionsSummaryAvtivity extends AppCompatActivity implements View.OnClickListener{

    private String TAG = "Spotsoon";
    private String providerName,type,businessId,currentPackageEncoded,encodedString,domId,packageId,morePlans;
    private String mCouponID,mDescription,mPrice,mTax,mParentRatePlan,mTaxPercent,mSubTotal,mconvenienceFee,mTotal;
    private ArrayList<SubscriptionsDetailsHelper> subscriptions = new ArrayList<SubscriptionsDetailsHelper>();
    private RelativeLayout package_layout,prev_due_layout,tax_layout,convinience_fee_layout,next_renewal_layout;
    private LinearLayout detailsLayout, mRechargeExpiryLayout;
    private PaymentParams mPaymentParams;
    private PayuConfig payuConfig;
    private PayuHashes payuHashes;
    private PayU_Hashpojo ServerResponse;
    private Intent intent;
    private ImageView arrowView,poweredby;
    private TextView userName,userId,total_amt,next_ren,view_plans;
    private TextView package_name,package_name_main,package_amount,package_amount_main,tax,tax_amount,convinience_amount;
    private SpotasManager session;
    private ProgressDialog payuDialog;
    private String mPayuId,newPackageAmount,planActDate,todayDate_ForExpiry, PaymentGateway = null;;
    private boolean isPackageChanged = false;
    private ImageView mRechargeNow,mRechargeLater,mRechargeExpiry;
    private LinearLayout mRechargesLayout;
    private TextView mRechargeExpiryDate,mRechargeLaterDate,postExpirationText;
    private Calendar currentCalendar;
    private boolean isPlanChanged;

    private List<PlansDetailsHelper> listDataHeaders = new ArrayList<PlansDetailsHelper>();
    private HashMap<PlansDetailsHelper, List<PlansCouponsDetails>> listDataChild = new HashMap<PlansDetailsHelper, List<PlansCouponsDetails>>();
    private final int REQUEST_CHANGE_PLAN = 11;

    SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("yyyy-M-dd hh:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(SubscriptionsSummaryAvtivity.this, new Crashlytics());
        setContentView(R.layout.subscriptions_summary);
        Intent intent = getIntent();
        if(intent!=null)
        {
            providerName = intent.getStringExtra("PROVIDER");
            type = intent.getStringExtra("TYPE");
            businessId = intent.getStringExtra("BUSINESS_ID");
            encodedString = intent.getStringExtra("ENCODED");
            domId = intent.getStringExtra("DOM_ID");
            packageId = intent.getStringExtra("PACKAGE");
            morePlans = intent.getStringExtra("MorePlans");
            currentPackageEncoded = intent.getStringExtra("CurrentPackage");

            mCouponID = intent.getStringExtra("CouponID");
            mDescription = intent.getStringExtra("Description");
            mPrice = intent.getStringExtra("Price");
            mTax = intent.getStringExtra("Tax");
            mParentRatePlan = intent.getStringExtra("ParentRatePlan");
            mTaxPercent = intent.getStringExtra("TaxPercent");
            mSubTotal = intent.getStringExtra("SubTotal");
            mconvenienceFee = intent.getStringExtra("convenienceFee");
            mTotal = intent.getStringExtra("Total");

             isPlanChanged = intent.getBooleanExtra("PLAN_CHANGED",false);
            if(isPlanChanged)
            {
                String payuId = intent.getStringExtra("");
                mPayuId = payuId;
                isPackageChanged = true;
                newPackageAmount = mTotal;
            }

            boolean from_home = intent.getBooleanExtra("FROM_HOME",false);
            if(from_home)
            {
                subscriptions = HomeFragmentNew.subscriptions;
            }
            else {
                subscriptions = InventumActivity.subscriptions;
            }
        }

        currentCalendar = Calendar.getInstance();
        int toYear = currentCalendar.get(Calendar.YEAR);
        int toMonth = currentCalendar.get(Calendar.MONTH);
        int toDay = currentCalendar.get(Calendar.DAY_OF_MONTH);

        planActDate = toYear+"-"+makeTwoDigits(toMonth+1)+"-"+makeTwoDigits(toDay);
        todayDate_ForExpiry = makeTwoDigits(toDay)+"-"+makeTwoDigits(toMonth+1)+"-"+toYear;

        Utility.statusbar(SubscriptionsSummaryAvtivity.this);

        initializeViews();

        try
        {
            setValues();

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        if(type!=null && type.equalsIgnoreCase(getString(R.string.inventum)))
        {
            if(morePlans!=null && morePlans.length()>0)
            {
                if(morePlans.equalsIgnoreCase("YES"))
                {
                    view_plans.setVisibility(View.VISIBLE);
                }
                else {
                    view_plans.setVisibility(View.GONE);
                }
            }
        }
        else {//dont show viewplans for non-inventum bussiness
            view_plans.setVisibility(View.GONE);
        }
    }


    private void initializeViews()
    {
        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_subcriptions);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Summary");

        session = new SpotasManager(SubscriptionsSummaryAvtivity.this);

        package_layout = (RelativeLayout)findViewById(R.id.package_layout);
        prev_due_layout = (RelativeLayout)findViewById(R.id.pre_due_layout);
        tax_layout = (RelativeLayout)findViewById(R.id.tax_layout);
        convinience_fee_layout = (RelativeLayout)findViewById(R.id.convinience_layout);
        RelativeLayout prev_renewal_layout = (RelativeLayout)findViewById(R.id.previous_renewal_layout);
        next_renewal_layout = (RelativeLayout)findViewById(R.id.next_renewal_layout);
        LinearLayout showDetailsLayout = (LinearLayout)findViewById(R.id.details_layout);
         detailsLayout = (LinearLayout)findViewById(R.id.summary_payment_layout);
        arrowView = (ImageView)findViewById(R.id.arrow_image);
        poweredby = (ImageView) findViewById(R.id.poweredby);
        Button proceed = (Button)findViewById(R.id.proceed);
        proceed.setOnClickListener(this);

        mRechargeExpiryDate = (TextView)findViewById(R.id.post_recharge_date);
        mRechargeLaterDate = (TextView)findViewById(R.id.rechare_later_date);
        mRechargeNow = (ImageView)findViewById(R.id.rechare_now_btn);
        mRechargeLater = (ImageView)findViewById(R.id.rechare_later_btn);
        mRechargeExpiry = (ImageView)findViewById(R.id.rechare_post_expire_btn);
        postExpirationText = (TextView)findViewById(R.id.postExpirationText);
        mRechargesLayout = (LinearLayout)findViewById(R.id.recharges_layout);
        LinearLayout mRechargeNowLayout = (LinearLayout)findViewById(R.id.rechare_now_layout);
        LinearLayout mRechargeLaterLayout = (LinearLayout)findViewById(R.id.rechare_later_layout);
         mRechargeExpiryLayout = (LinearLayout)findViewById(R.id.rechare_post_expire_layout);
        userName = (TextView)findViewById(R.id.user_name);
        userId = (TextView)findViewById(R.id.user_id);
        TextView proName = (TextView)findViewById(R.id.merchant_name);
        view_plans = (TextView)findViewById(R.id.view_plans);

        TextView tv_total_amt = (TextView)findViewById(R.id.totalamount_name);
        total_amt = (TextView)findViewById(R.id.totalamount_price);
        TextView tv_prev_ren = (TextView)findViewById(R.id.previous_renewal_date_tv);
        TextView prev_ren = (TextView)findViewById(R.id.previous_renewal_date);
        TextView tv_next_ren = (TextView)findViewById(R.id.next_renewal_date_tv);
        next_ren = (TextView)findViewById(R.id.next_renewal_date);


        package_name = (TextView)findViewById(R.id.package_name);
        package_name_main = (TextView)findViewById(R.id.package_name_top);
        package_amount = (TextView)findViewById(R.id.package_price);
        package_amount_main = (TextView)findViewById(R.id.package_price_top);

        TextView prev_due = (TextView)findViewById(R.id.tv_pre_due);
        TextView prev_due_amount = (TextView)findViewById(R.id.pre_due_amount);

        tax = (TextView)findViewById(R.id.tv_tax);
        tax_amount = (TextView)findViewById(R.id.tax_amount);

        TextView convinience_fee = (TextView)findViewById(R.id.tv_convinience_fee);
        convinience_amount = (TextView)findViewById(R.id.convinience_amount);

        proName.setText(providerName);
        view_plans.setOnClickListener(this);
        showDetailsLayout.setOnClickListener(this);
        mRechargeNowLayout.setOnClickListener(this);
        mRechargeLaterLayout.setOnClickListener(this);
        mRechargeExpiryLayout.setOnClickListener(this);

        mRechargeNow.setSelected(true);
        mRechargeLater.setSelected(false);
        mRechargeExpiry.setSelected(false);
        mRechargesLayout.setVisibility(View.VISIBLE);
    }

    private void setValues() throws Exception {
        if(subscriptions.size()>0) {
            if(type!=null)
            {
                if(type.equalsIgnoreCase(getString(R.string.inventum))) {
                    if(mTotal!=null && mTotal.length()>0)
                    {
                        total_amt.setText(""+Utility.round(Double.valueOf(mTotal)));
                    }
                    else {
                    }

                    if(subscriptions.get(0).getActname()!=null && subscriptions.get(0).getActname().length() > 0) {
                        userName.setText(subscriptions.get(0).getActname());
                    }
                    else {
                        userName.setVisibility(View.INVISIBLE);
                    }

                    if(subscriptions.get(0).getActid()!=null && subscriptions.get(0).getActid().length()>0)
                    {
                        userId.setText(subscriptions.get(0).getActid());
                    }
                    else {
                        userId.setVisibility(View.GONE);
                    }

                    if(mPrice!=null && mPrice.length()>0)
                    {
                        package_name_main.setText(mDescription);
                        package_name.setText(packageId);
                        //package_name.setText(subscriptions.get(0).getDescription());
                        package_amount.setText(""+Utility.round(Double.valueOf(mPrice)));
                        package_amount_main.setText(""+Utility.round(Double.valueOf(mPrice)));
                    }
                    else {
                        package_layout.setVisibility(View.GONE);
                    }

                    if(mTax!=null && mTax.length()>0)
                    {
                        if(mTax.equalsIgnoreCase("0"))
                        {
                            tax_layout.setVisibility(View.GONE);
                        }
                        else {

                            if(mTaxPercent!=null)
                            {
                                tax.setText("Tax("+mTaxPercent+"%)");
                            }

                            tax_amount.setText(""+Utility.round(Double.valueOf(mTax)));
                        }
                    }
                    else {
                        tax_layout.setVisibility(View.GONE);
                    }

                    if(mconvenienceFee!=null && mconvenienceFee.length()>0)
                    {
                        try {
                            if(Double.parseDouble(mconvenienceFee) > 0)
                            {
                                convinience_amount.setText(""+Utility.round(Double.valueOf(mconvenienceFee)));
                            }
                            else
                            {
                                convinience_fee_layout.setVisibility(View.GONE);
                            }
                        }
                        catch (NumberFormatException e) {
                        }
                    }
                    else {
                        convinience_fee_layout.setVisibility(View.GONE);
                    }

                    prev_due_layout.setVisibility(View.GONE);

                    if(subscriptions.get(0).getNextRenewalDate() !=null && subscriptions.get(0).getNextRenewalDate().length()>0)
                    {
                        next_ren.setText(subscriptions.get(0).getNextRenewalDate());
                    } else
                    {
                        next_renewal_layout.setVisibility(View.GONE);
                    }


                    if(subscriptions.get(0).getExpirydt()!=null && subscriptions.get(0).getExpirydt().length()>0)
                    {
                        //2016-10-06 19:40:00.0     ff     2016-09-07 14:29:30
                        try {
                            String[] expdate = subscriptions.get(0).getExpirydt().split(" ");
                            if(expdate.length > 0)
                            {
                                String[] exp = expdate[0].split("-");
                                if(exp.length > 2)
                                {
                                    currentCalendar.set(Calendar.YEAR, Integer.valueOf(exp[0]));
                                    currentCalendar.set(Calendar.MONTH, Integer.valueOf(exp[1])-1);
                                    currentCalendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(exp[2]));

                                    final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                    String formattedDate = df.format(currentCalendar.getTime());

                                    mRechargeExpiryDate.setText(formattedDate);
                                    try {
                                        Date date1 = simpleDateFormat.parse(Utility.GetTodaysDate());
                                        Date date2 = simpleDateFormat.parse(subscriptions.get(0).getExpirydt());
                                        if (date1.getTime() > date2.getTime())
                                        {
                                            postExpirationText.setTextColor(Color.parseColor("#9d9d9d"));
                                            mRechargeExpiryLayout.setClickable(false);
                                        }
                                        else
                                        {
                                            mRechargeExpiryLayout.setClickable(true);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                        catch (Exception e)
                        {
                        }
                    }

                }
                else if(type.equalsIgnoreCase(getString(R.string.invoice_managment)))
                {
                    total_amt.setText(mTotal);
                    //userName.setText(subscriptions.get(0).getCustomerId());

                    if(subscriptions.get(0).getActname()!=null && subscriptions.get(0).getActname().length()>0)
                    {
                        userName.setText(subscriptions.get(0).getActname());
                    }
                    else {
                        userName.setVisibility(View.GONE);
                    }

                    if(subscriptions.get(0).getActid()!=null && subscriptions.get(0).getActid().length()>0)
                    {
                        userId.setText(subscriptions.get(0).getActid());
                    }
                    else {
                        userId.setVisibility(View.GONE);
                    }

                    if(mPrice!=null && mPrice.length()>0)
                    {
                        package_name.setText(packageId);
                        package_amount.setText(""+Utility.round(Double.valueOf(mPrice)));

                        package_name_main.setText(mDescription);
                        package_amount_main.setText(""+Utility.round(Double.valueOf(mPrice)));
                    }
                    else {
                        package_layout.setVisibility(View.GONE);
                    }

                    if(mTax!=null && mTax.length()>0)
                    {
                        if(mTax.equalsIgnoreCase("0"))
                        {
                            tax_layout.setVisibility(View.GONE);
                        }
                        else {
                            tax_amount.setText(""+Utility.round(Double.valueOf(mTax)));
                        }
                    }
                    else {
                        tax_layout.setVisibility(View.GONE);
                    }

                    if(mconvenienceFee!=null && mconvenienceFee.length()>0)
                    {
                        if(mconvenienceFee.equalsIgnoreCase("0"))
                        {
                            convinience_fee_layout.setVisibility(View.GONE);
                        }
                        else
                        {
                            convinience_amount.setText(""+Utility.round(Double.valueOf(mconvenienceFee)));
                        }
                    }
                    else {
                        convinience_fee_layout.setVisibility(View.GONE);
                    }

                    prev_due_layout.setVisibility(View.GONE);


                    if(subscriptions.get(0).getRenewalDate()!=null && subscriptions.get(0).getRenewalDate().length()>0)
                    {
                        next_ren.setText(subscriptions.get(0).getRenewalDate());
                    }
                    else
                    {
                        next_renewal_layout.setVisibility(View.GONE);
                    }


                    if(subscriptions.get(0).getActcat()!=null && subscriptions.get(0).getActcat().length()>0)
                    {
                        if(subscriptions.get(0).getActcat().equalsIgnoreCase("1"))//pre paid
                        {
                        }
                        else if(subscriptions.get(0).getActcat().equalsIgnoreCase("2"))//post paid
                        {
                            mRechargeNow.setSelected(true);
                            mRechargeLater.setSelected(false);
                            mRechargeExpiry.setSelected(false);
                            mRechargesLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            overridePendingTransition(R.anim.anim_three, R.anim.anim_four);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method will check whether user selected recharge later date or not
     * @return
     **/
    private boolean chaeckRechargeLaterDate()
    {
        if(mRechargeLater.isSelected())
        {
            if(mRechargeLaterDate.getText().toString().trim().length() == 0)
            {
                Toast.makeText(SubscriptionsSummaryAvtivity.this,"Please select Recharge Later date",Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.proceed)
        {
            if(type!=null && type.equalsIgnoreCase(getString(R.string.inventum)))
            {
                if(chaeckRechargeLaterDate())
                {
                    if((isPackageChanged) &&(mPayuId!=null && mPayuId.length()>0))//if user changed to new package(payu id will generate while updating package)
                    {
                        if (PaymentGateway.equalsIgnoreCase("RAZORPAY")){

                            startRazorPayment("" + Utility.round((Double.parseDouble(newPackageAmount)) * 100));
                        }else {
                            payu(mPayuId, ""+Utility.round(Double.parseDouble(newPackageAmount)),
                                    package_name.getText().toString().trim());
                        }

                    }
                    else//default package(need to generate payu id)
                    {
                        getPayUTxnId();
                    }
                }
            }
            else
            {
                if (PaymentGateway.equalsIgnoreCase("RAZORPAY")){
                    startRazorPayment("" + Utility.round((Double.parseDouble(mTotal)) * 100 ));
                }
                else {
                    payu("", ""+Utility.round(Double.parseDouble(mTotal)), mCouponID);
                }

            }
        }
        else if(v.getId() == R.id.view_plans)
        {
            //getPlans();

            Intent intent = new Intent(SubscriptionsSummaryAvtivity.this,SelectPlanActivity.class);
            intent.putExtra("businessId",businessId);
            intent.putExtra("domId",domId);
            intent.putExtra("PLAN",mDescription);
            startActivityForResult(intent, REQUEST_CHANGE_PLAN);
            overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
        }
        else if(v.getId() == R.id.details_layout)
        {
            if(detailsLayout.getVisibility() == View.VISIBLE)
            {
                rotate(0, arrowView);
                collapse(detailsLayout);
            }
            else {
                rotate(180, arrowView);
                expand(detailsLayout);
            }
        }
        else if(v.getId() == R.id.rechare_now_layout)
        {
            mRechargeNow.setSelected(true);
            mRechargeLater.setSelected(false);
            mRechargeExpiry.setSelected(false);
            mRechargeLaterDate.setText("");

            currentCalendar = Calendar.getInstance();
            int toYear = currentCalendar.get(Calendar.YEAR);
            int toMonth = currentCalendar.get(Calendar.MONTH);
            int toDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
            //currentCalendar.add(Calendar.DATE, 1);

            planActDate = toYear+"-"+makeTwoDigits(toMonth + 1)+"-"+makeTwoDigits(toDay);
        }
        else if(v.getId() == R.id.rechare_later_layout)
        {
            mRechargeNow.setSelected(false);
            mRechargeLater.setSelected(true);
            mRechargeExpiry.setSelected(false);

            currentCalendar = Calendar.getInstance();
            currentCalendar.add(Calendar.DAY_OF_YEAR, 1);
            DatePickerDialog datePickerDialog = new DatePickerDialog(SubscriptionsSummaryAvtivity.this, mFromDateListener, currentCalendar.get(Calendar.YEAR),  currentCalendar.get(Calendar.MONTH),  currentCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setCancelable(false);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        }
        else if(v.getId() == R.id.rechare_post_expire_layout)
        {
            mRechargeNow.setSelected(false);
            mRechargeLater.setSelected(false);
            mRechargeExpiry.setSelected(true);
            mRechargeLaterDate.setText("");

            String[] exp = subscriptions.get(0).getExpirydt().split(" ");
            if(exp.length > 0)
            {
                planActDate = exp[0];
            }
        }
    }

    private void getPlans()
    {
        final ProgressDialog pDialog = new ProgressDialog(SubscriptionsSummaryAvtivity.this);
        pDialog.setMessage("Loading plans...");
        pDialog.setCancelable(true);
        if(pDialog!=null) {
            pDialog.show();
        }

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("ent_dev_id",Utility.getDeviceId(this));
            jsonObj.put("ent_sess_token", session.getSession_token());
            jsonObj.put("ent_business_id", businessId);
            jsonObj.put("ent_dom_id", domId);
            jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());
        }
        catch(Exception e)
        {
        }

        Utility.doJsonRequest(VariableConstants.host_url + "invGetPlans", jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(final String jsonResponse) {
                // TODO Auto-generated method stub

                try {

                    Gson gson = new Gson();
                    Plans plans = gson.fromJson(jsonResponse, Plans.class);

                    if (plans != null) {
                        if (plans.getErrFlag() != null && plans.getErrFlag().equalsIgnoreCase("0")) {

                            if (plans.getPlans() != null && plans.getPlans().size() > 0) {

                                listDataHeaders = plans.getPlans();
                                listDataChild = new HashMap<PlansDetailsHelper, List<PlansCouponsDetails>>();
                                for (int i = 0; i < listDataHeaders.size(); i++) {
                                    listDataChild.put(listDataHeaders.get(i),
                                            listDataHeaders.get(i).getCoupons());
                                }

                                final Dialog dialog = new Dialog(SubscriptionsSummaryAvtivity.this);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.plans_popup_exp_listview);
                                dialog.show();

                                TextView text = (TextView) dialog.findViewById(R.id.business_name);
                                text.setText(providerName);

                                ImageButton cancel = (ImageButton) dialog.findViewById(R.id.cancel_popup);
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                                ExpandableListView expListView = (ExpandableListView) dialog.findViewById(R.id.plans_list);
                                ExpandableListAdapter listAdapter = new ExpandableListAdapter(SubscriptionsSummaryAvtivity.this, listDataHeaders, listDataChild);
                                // setting list adapter
                                expListView.setAdapter(listAdapter);

                                // Listview on child click listener
                                expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                                    @Override
                                    public boolean onChildClick(ExpandableListView parent, View v,
                                                                int groupPosition, int childPosition, long id) {

                                        dialog.dismiss();

                                        try {
                                            mRechargeNow.setSelected(true);
                                            mRechargeLater.setSelected(false);
                                            mRechargeExpiry.setSelected(false);
                                            mRechargesLayout.setVisibility(View.VISIBLE);

                                            JSONObject object = new JSONObject(jsonResponse);
                                            JSONArray plansArray = object.getJSONArray("plans");
                                            JSONObject plansObject = plansArray.getJSONObject(groupPosition);

                                            JSONArray couponsArray = plansObject.getJSONArray("Coupons");
                                            JSONObject couponObject = couponsArray.getJSONObject(childPosition);


                                            byte[] data = couponObject.toString().getBytes("UTF-8");
                                            String base64 = Base64.encodeToString(data, Base64.DEFAULT);

                                            updateUserPlan(base64, groupPosition, childPosition);
                                        } catch (JSONException e) {
                                            cancelDialog(pDialog);
                                            Toast.makeText(SubscriptionsSummaryAvtivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                                        } catch (UnsupportedEncodingException e1) {
                                            cancelDialog(pDialog);
                                            Toast.makeText(SubscriptionsSummaryAvtivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                                        }

                                        return false;
                                    }
                                });

                            }
                        }
                    }


                } catch (Exception e) {
                    Toast.makeText(SubscriptionsSummaryAvtivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                    cancelDialog(pDialog);
                }
                cancelDialog(pDialog);
            }

            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub
                Utility.printLog("HomeFrag JSON DATA Error" + error);
                cancelDialog(pDialog);

                if (payuDialog != null && payuDialog.isShowing()) {

                    payuDialog.dismiss();
                    payuDialog = null;
                }
            }
        });
    }

    private void cancelDialog(final ProgressDialog pDialog)
    {
        /*if(pDialog!=null && pDialog.isShowing()) {
            pDialog.dismiss();
        }*/

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }
        }, 1500);
    }

    private void updateUserPlan(String newPackage,final int groupPos, final int childPos)
    {
       final ProgressDialog pDialog = new ProgressDialog(SubscriptionsSummaryAvtivity.this);
        pDialog.setMessage("Please wait, Updating...");
        pDialog.setCancelable(true);
        if(pDialog!=null) {
            pDialog.show();
        }

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("ent_dev_id",Utility.getDeviceId(this));
            jsonObj.put("ent_sess_token", session.getSession_token());
            jsonObj.put("ent_business_id", businessId);
            jsonObj.put("ent_act_id", encodedString);
            jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());
            //jsonObj.put("ent_new_package", newPackage);
            jsonObj.put("ent_package", newPackage);
            jsonObj.put("rc_scheduled_date", planActDate);

        }
        catch(Exception e)
        {
        }
        //calling doJsonRequest of Utility class
        Utility.doJsonRequest(VariableConstants.host_url + "invGetPayuId", jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                // TODO Auto-generated method stub

                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }

                try {

                    if (jsonResponse != null && jsonResponse.length() > 0) {
                        JSONObject object = new JSONObject(jsonResponse);
                        String errFlag = object.getString("errFlag");
                        if (errFlag != null && errFlag.equalsIgnoreCase("0")) {
                            if (object.has("payuId")) {
                                mPayuId = object.getString("payuId");
                               // mPayuId = payuId;
                            }

                            if (object.has("PaymentGateway"))
                            {
                                PaymentGateway = object.getString("PaymentGateway");

                            }



                            isPackageChanged = true;

                            PlansCouponsDetails plan = listDataChild.get(listDataHeaders.get(groupPos)).get(childPos);

                            newPackageAmount = plan.getTotal();

                            packageId = plan.getParentRatePlan();
                            mCouponID = plan.getCouponID();
                            mDescription = plan.getDescription();
                            mPrice = plan.getPrice();
                            mTax = plan.getTax();
                            mParentRatePlan = plan.getParentRatePlan();
                            mTaxPercent = plan.getTaxPercent();
                            mSubTotal = plan.getSubTotal();
                            mconvenienceFee = plan.getConvenienceFee();
                            mTotal = plan.getTotal();


                            try {
                                setValues();
                            } catch (Exception e) {
                            }

                        } else {
                            Toast.makeText(SubscriptionsSummaryAvtivity.this, object.getString("errMsg"), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(SubscriptionsSummaryAvtivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub
                Utility.printLog("HomeFrag JSON DATA Error" + error);

                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }
        });
    }

    private void getPayUTxnId()
    {
        payuDialog = new ProgressDialog(SubscriptionsSummaryAvtivity.this);
        payuDialog.setMessage("Please wait, Redirecting to payment screen...");
        payuDialog.setCancelable(true);
        if(payuDialog!=null) {
            payuDialog.show();
        }

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("ent_dev_id",Utility.getDeviceId(this));
            jsonObj.put("ent_sess_token", session.getSession_token());
            jsonObj.put("ent_business_id", businessId);
            jsonObj.put("ent_act_id", encodedString);
            if(currentPackageEncoded!=null)
            {
                jsonObj.put("ent_package", currentPackageEncoded);
            }
            jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());
            jsonObj.put("rc_scheduled_date", planActDate);

        }
        catch(Exception e)
        {
        }
        //calling doJsonRequest of Utility class
        Utility.doJsonRequest(VariableConstants.host_url + "invGetPayuId", jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                // TODO Auto-generated method stub


                try {


                    if(jsonResponse!=null && jsonResponse.length()>0)
                    {
                        JSONObject object = new JSONObject(jsonResponse);
                        String errFlag = object.getString("errFlag");
                        if(errFlag!=null && errFlag.equalsIgnoreCase("0"))
                        {
                            if (object.has("PaymentGateway")) {
                                mPayuId = object.getString("payuId");
                               // mPayuId = payuId;
                            }
                            if (object.has("PaymentGateway")){
                                PaymentGateway = object.getString("PaymentGateway");
                            }
                            if (PaymentGateway.equalsIgnoreCase("RAZORPAY")){
                                startRazorPayment("" + Utility.round((Double.parseDouble(mTotal)) * 100 ));
                            }else {
                                payu(mPayuId, ""+Utility.round(Double.parseDouble(mTotal)),
                                         mCouponID);
                            }



                        }
                        else {

                            if(payuDialog!=null) {

                                payuDialog.dismiss();
                                payuDialog = null;
                            }

                            Toast.makeText(SubscriptionsSummaryAvtivity.this,"Something went wrong, please try again",Toast.LENGTH_LONG).show();
                        }
                    }
                }
                catch (JSONException e)
                {
                    Toast.makeText(SubscriptionsSummaryAvtivity.this,"Something went wrong, please try again",Toast.LENGTH_LONG).show();
                    if(payuDialog!=null) {

                        payuDialog.dismiss();
                        payuDialog = null;
                    }
                }


            }

            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub
                Utility.printLog("HomeFrag JSON DATA Error" + error);

                if(payuDialog!=null && payuDialog.isShowing()) {

                    payuDialog.dismiss();
                    payuDialog = null;
                }
            }
        });
    }

    private void startRazorPayment(String amount) {

        /**
         * Replace with your public key
         */
        final String public_key = getString(R.string.razorpay_livekey);   // rzp_live_ILgsfZCZoFIKMb

        /**
         * You need to pass current activity in order to let razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();
        co.setPublicKey(public_key);

        try {
            JSONObject options = new JSONObject("{" +
                    "description: 'Demoing Charges'," +
                    "image: 'https://rzp-mobile.s3.amazonaws.com/images/rzp.png'," +
                    "currency: 'INR'}"
            );

            options.put("amount", amount);
            options.put("name", session.getFname());
            options.put("prefill", new JSONObject("{email: "+session.getEmailId()+" , contact: "+session.getPhone()+"}"));
            options.put("notes", new JSONObject("{order_id: "+mPayuId+"}"));


            co.open(activity, options);

        } catch (Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * The name of the function has to be
     *   onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    public void onPaymentSuccess(String razropayPaymentID){
        if (payuDialog != null) {

            payuDialog.dismiss();
            payuDialog = null;
        }
        try {
            new RazorPayment().execute("http://admin.spotsoon.com/RPCapture.php",razropayPaymentID);
        }
        catch (Exception e){
            Log.e("com.merchant", e.getMessage(), e);
        }
    }

    /**
     * The name of the function has to be
     *   onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    public void onPaymentError(int code, String response){
        if (payuDialog != null) {
            payuDialog.dismiss();
            payuDialog = null;
        }
        try {
            Toast.makeText(this, "Payment Cancelled, please try again!: ", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Log.e("com.merchant", e.getMessage(), e);
        }
    }

    private void payu(String payUId, String totalAmount,String packageId)
    {
        if(payuDialog == null)
        {
            payuDialog = new ProgressDialog(SubscriptionsSummaryAvtivity.this);
            payuDialog.setMessage("Please wait, Redirecting to payment screen...");
            payuDialog.setCancelable(true);
            if(payuDialog!=null) {
                payuDialog.show();
            }
        }

        intent = new Intent(this, PayUBaseActivity.class);
        mPaymentParams = new PaymentParams();
        payuConfig = new PayuConfig();
        mPaymentParams.setKey("rrC4St");

        mPaymentParams.setAmount(totalAmount);//String.valueOf(totaltopay)
        mPaymentParams.setProductInfo(packageId);
        mPaymentParams.setFirstName(session.getFname());
        mPaymentParams.setEmail(session.getEmailId());

        mPaymentParams.setTxnId(payUId);

        mPaymentParams.setUserCredentials("rrC4St:"+session.getEmailId());
        mPaymentParams.setSurl(VariableConstants.payU_Success_Url);
        mPaymentParams.setFurl(VariableConstants.payU_Failure_Url);
        intent.putExtra(PayuConstants.SALT, "6QJvrdvp");//6QJvrdvp  eCwWELxi
        mPaymentParams.setPhone(session.getPhone());
        mPaymentParams.setUdf1("");
        mPaymentParams.setUdf2("");
        mPaymentParams.setUdf3("");
        mPaymentParams.setUdf4("");
        mPaymentParams.setUdf5("");
        // mPaymentParams.setHash(payuHashes.getPaymentHash());
        payuConfig.setEnvironment(0);

        //generateHashFromSDK(mPaymentParams, "cg5Wirt2");
        generateHashFromServer(mPaymentParams);
    }

    private void generateHashFromServer(PaymentParams mPaymentParams)
    {
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayuConstants.KEY, mPaymentParams.getKey()));
        postParamsBuffer.append(concatParams(PayuConstants.AMOUNT, mPaymentParams.getAmount()));
        postParamsBuffer.append(concatParams(PayuConstants.TXNID, mPaymentParams.getTxnId()));
        postParamsBuffer.append(concatParams(PayuConstants.EMAIL, null == mPaymentParams.getEmail() ? "" : mPaymentParams.getEmail()));
        postParamsBuffer.append(concatParams(PayuConstants.PRODUCT_INFO, mPaymentParams.getProductInfo()));
        postParamsBuffer.append(concatParams(PayuConstants.FIRST_NAME, null == mPaymentParams.getFirstName() ? "" : mPaymentParams.getFirstName()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF1, mPaymentParams.getUdf1() == null ? "" : mPaymentParams.getUdf1()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF2, mPaymentParams.getUdf2() == null ? "" : mPaymentParams.getUdf2()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF3, mPaymentParams.getUdf3() == null ? "" : mPaymentParams.getUdf3()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF4, mPaymentParams.getUdf4() == null ? "" : mPaymentParams.getUdf4()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF5, mPaymentParams.getUdf5() == null ? "" : mPaymentParams.getUdf5()));
        postParamsBuffer.append(concatParams(PayuConstants.USER_CREDENTIALS, mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials()));

        // for offer_key
        if(null != mPaymentParams.getOfferKey())
            postParamsBuffer.append(concatParams(PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey()));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();
        // make api call
        HashesFromServerTask();
    }
    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    private void HashesFromServerTask()
    {
        payuHashes = new PayuHashes();

        System.out.print("ali into try Block");

        RequestBody requestBody = new FormEncodingBuilder()
                .add("amount", ""+mPaymentParams.getAmount())
                .add("tranctionId", "" + mPaymentParams.getTxnId())
                .add("KEY", "" + mPaymentParams.getKey())
                .add("EMAIL", "" + mPaymentParams.getEmail())
                .add("PRODUCT_INFO", "" + mPaymentParams.getProductInfo())
                .add("USER_CREDENTIALS", "" + mPaymentParams.getUserCredentials())
                .add("Phone", "" + mPaymentParams.getPhone())
                .add("firstNAme", "" + mPaymentParams.getFirstName())
                .add("furl", "" + mPaymentParams.getFurl())
                .add("surl", "" + mPaymentParams.getSurl())
                .build();


        OkHttpRequest.doJsonRequest("http://admin.spotsoon.com/spotsoon/payU/genHash.php", requestBody, new OkHttpRequest.JsonRequestCallback() {
            @Override
            public void onSuccess(String result) {

                ServerResponse = new PayU_Hashpojo();
                Gson gson = new Gson();
                ServerResponse = gson.fromJson(result, PayU_Hashpojo.class);
                payuHashes.setPaymentHash(ServerResponse.getPayment_hash());
                payuHashes.setMerchantIbiboCodesHash(ServerResponse.getGet_merchant_ibibo_codes_hash());
                Log.d("Ali", "" + ServerResponse.getGet_merchant_ibibo_codes_hash());
                payuHashes.setVasForMobileSdkHash(ServerResponse.getVas_for_mobile_sdk_hash());
                payuHashes.setPaymentRelatedDetailsForMobileSdkHash(ServerResponse.getPayment_related_details_for_mobile_sdk_hash());
                payuHashes.setDeleteCardHash(ServerResponse.getDelete_user_card_hash());
                payuHashes.setStoredCardsHash(ServerResponse.getSave_user_card_hash());
                payuHashes.setEditCardHash(ServerResponse.getEdit_user_card_hash());
                payuHashes.setSaveCardHash(ServerResponse.getSave_user_card_hash());
                payuHashes.setCheckOfferStatusHash(ServerResponse.getCheck_offer_status_hash());
                payuHashes.setCheckIsDomesticHash(ServerResponse.getCheck_isDomestic_hash());

                if(payuDialog!=null && payuDialog.isShowing()) {

                    payuDialog.dismiss();
                    payuDialog = null;
                }

                launchSdkUI();
            }

            @Override
            public void onError(String error) {
                System.out.print("onError error = " + error);

                if(payuDialog!=null && payuDialog.isShowing()) {

                    payuDialog.dismiss();
                    payuDialog = null;
                }
            }
        });
    }

    private void launchSdkUI()
    {
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        //intent.putExtra(PayuConstants.PAYMENT_DEFAULT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
        intent.putExtra(PayuConstants.STORE_ONE_CLICK_HASH, 1);

        System.out.println("ali " + payuHashes.getPaymentHash());

        /**
         *  just for testing, dont do this in production.
         *  i need to generate hash for {@link com.payu.india.Tasks.GetTransactionInfoTask} since it requires transaction id, i don't generate hash from my server
         *  merchant should generate the hash from his server.
         *
         */
        intent.putExtra(PayuConstants.SALT, "6QJvrdvp");

        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode == PayuConstants.PAYU_REQUEST_CODE)
        {
            if(data != null ) {

                String resultedstring = data.getStringExtra(PayuConstants.PAYU_RESPONSE);

                if(resultedstring != null && resultedstring.contains("success")) {

                    Intent inventumIntent = new Intent(SubscriptionsSummaryAvtivity.this, InventumRechargeStatusActivity.class);
                    inventumIntent.putExtra("PAYU_ID", mPayuId);
                    inventumIntent.putExtra("TYPE", type);
                    startActivity(inventumIntent);
                    overridePendingTransition(R.anim.anim_two, R.anim.anim_one);


                }
                else
                {
                    Toast.makeText(this,"We are sorry, your recharge failed. Please contact our customer care for any assistance.",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "We are sorry, your recharge failed. Please contact our customer care for any assistance.", Toast.LENGTH_LONG).show();
            }
        }
        else if(requestCode == REQUEST_CHANGE_PLAN)
        {
            if(data!=null)
            {
                String jsonResponse = data.getStringExtra("RESPONSE");
                int groupPosition = data.getIntExtra("GROUP_POS", 0);
                int childPosition = data.getIntExtra("CHILD_POS",0);

                try {

                    Gson gson = new Gson();
                    Plans plans = gson.fromJson(jsonResponse, Plans.class);

                    if (plans != null) {
                        if (plans.getErrFlag() != null && plans.getErrFlag().equalsIgnoreCase("0")) {

                            if (plans.getPlans() != null && plans.getPlans().size() > 0) {

                                listDataHeaders = plans.getPlans();
                                listDataChild = new HashMap<PlansDetailsHelper, List<PlansCouponsDetails>>();

                                for(int i = 0; i < listDataHeaders.size(); i++) {
                                    listDataChild.put(listDataHeaders.get(i),
                                            listDataHeaders.get(i).getCoupons());
                                }
                            }
                        }
                    }

                    mRechargeNow.setSelected(true);
                    mRechargeLater.setSelected(false);
                    mRechargeExpiry.setSelected(false);
                    mRechargesLayout.setVisibility(View.VISIBLE);

                    JSONObject object = new JSONObject(jsonResponse);
                    JSONArray plansArray = object.getJSONArray("plans");
                    JSONObject plansObject = plansArray.getJSONObject(groupPosition);

                    JSONArray couponsArray = plansObject.getJSONArray("Coupons");
                    JSONObject couponObject = couponsArray.getJSONObject(childPosition);


                    byte[] dataBytes = couponObject.toString().getBytes("UTF-8");
                    String base64 = Base64.encodeToString(dataBytes, Base64.DEFAULT);

                    updateUserPlan(base64, groupPosition, childPosition);
                } catch (JSONException e) {
                    Toast.makeText(SubscriptionsSummaryAvtivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                } catch (UnsupportedEncodingException e1) {
                    Toast.makeText(SubscriptionsSummaryAvtivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                }
            }
        }
    }



    private void rotate(float degree,View view) {
        final RotateAnimation rotateAnim = new RotateAnimation(0.0f, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(0);
        rotateAnim.setFillAfter(true);
        view.startAnimation(rotateAnim);
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private DatePickerDialog.OnDateSetListener mFromDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            // TODO Auto-generated method stub

            planActDate = year+"-"+makeTwoDigits(month+1)+"-"+makeTwoDigits(day);
            Utility.printLog("onDateSet fromDate=" + planActDate);

            currentCalendar.set(Calendar.YEAR, year);
            currentCalendar.set(Calendar.MONTH, month);
            currentCalendar.set(Calendar.DAY_OF_MONTH, day);

            //currentCalendar.add(Calendar.DATE, 1);
            final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(currentCalendar.getTime());

            mRechargeLaterDate.setText(formattedDate);
        }
    };

    private String makeTwoDigits(int value)
    {
        String data = ""+value;
        if(data.length()==1)
        {
            data = "0"+data;
        }
        return data;
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<PlansDetailsHelper> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<PlansDetailsHelper, List<PlansCouponsDetails>> _listDataChild;

        public ExpandableListAdapter(Context context, List<PlansDetailsHelper> listDataHeader,
                                     HashMap<PlansDetailsHelper, List<PlansCouponsDetails>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public PlansCouponsDetails getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final PlansCouponsDetails childItem = (PlansCouponsDetails) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.plans_rowitem, null);
            }

            TextView plan_name = (TextView) convertView.findViewById(R.id.plan_name);
            TextView plan_type= (TextView) convertView.findViewById(R.id.plan_type);
            TextView plan_price= (TextView) convertView.findViewById(R.id.plan_price);
            ImageView radioButton=(ImageView)convertView.findViewById(R.id.radio_btn);

            plan_type.setVisibility(View.GONE);
            plan_name.setText(childItem.getDescription());
            plan_price.setText(childItem.getTotal());

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .size();
        }

        @Override
        public PlansDetailsHelper getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            PlansDetailsHelper headerItem = (PlansDetailsHelper) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.plans_list_group, null);
            }

            TextView lblListHeader = (TextView) convertView.findViewById(R.id.plan_name);
            //lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerItem.getRatePlan());

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private class RazorPayment extends AsyncTask<String,Void,String> {
        String result1;
        ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
       
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            payuDialog = new ProgressDialog(SubscriptionsSummaryAvtivity.this);
            payuDialog.setMessage("Please do not press back button...");
            payuDialog.setCancelable(true);
            if(payuDialog!=null) {
                payuDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            nameValuePairs.add(new BasicNameValuePair("razorpay_payment_id" ,params[1]));
            try {
                result1= Utility.executeHttpPost(params[0], nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return result1;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            
            if (payuDialog != null) {
                payuDialog.dismiss();
                payuDialog = null;
            }

            if (s!=null){
                
                Intent inventumIntent = new Intent(SubscriptionsSummaryAvtivity.this, InventumRechargeStatusActivity.class);
                inventumIntent.putExtra("PAYU_ID", mPayuId);
                inventumIntent.putExtra("TYPE", type);
                startActivity(inventumIntent);
                overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
            }
            else {
                Toast.makeText(SubscriptionsSummaryAvtivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
}