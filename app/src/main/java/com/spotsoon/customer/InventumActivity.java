package com.spotsoon.customer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.invoice.InvoiceManagementPackageList;
import com.squareup.picasso.Picasso;
import com.threembed.homebean.InvoiceManagementPackageListModel;
import com.threembed.spotasap_pojos.InvoiceDetailsHelper;
import com.threembed.spotasap_pojos.PlansCouponsDetails;
import com.threembed.spotasap_pojos.Subscriptions;
import com.threembed.spotasap_pojos.SubscriptionsDetailsHelper;
import com.utility.RoundTransform;
import com.utility.SpotasManager;
import com.utility.Utility;
import com.utility.VariableConstants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

/**
 * Created by VARUN on 2/21/2016.
 */
public class InventumActivity extends AppCompatActivity implements View.OnClickListener {

    private SpotasManager session;
    private String businessId, name, logo, subcriptionsUrl, type, bannerUrl, address, shop_latitude, shop_longitude, MemberFlag, showPlans;
    private String TAG = "InventumActivity";
    private EditText userId,ent_last4nmbr;
    public static ArrayList<SubscriptionsDetailsHelper> subscriptions = new ArrayList<SubscriptionsDetailsHelper>();
    ArrayList<InvoiceManagementPackageListModel> invoicepackageList = new ArrayList<InvoiceManagementPackageListModel>();
    private String purchase_new_pack = null, packageAmt = null,  response = null,integType=null,customerphone=null;
    private TextView  proceed_to_pay_4digitphone;
    private LinearLayout phonevalidation_Layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(InventumActivity.this, new Crashlytics());
        setContentView(R.layout.inventum_activity);

        businessId = getIntent().getStringExtra("BUSINESS_ID");
        name = getIntent().getStringExtra("Name");
        logo = getIntent().getStringExtra("Image");
        subcriptionsUrl = getIntent().getStringExtra("URL");
        type = getIntent().getStringExtra("TYPE");
        bannerUrl = getIntent().getStringExtra("BANNER");
        address = getIntent().getStringExtra("ADDRESS");
        shop_latitude = getIntent().getStringExtra("shoplatitude");
        shop_longitude = getIntent().getStringExtra("shoplongitude");
        MemberFlag = getIntent().getStringExtra("ConnectedFlag");
        showPlans = getIntent().getStringExtra("MorePlans");


        initializeViews();
    }


    private void initializeViews() {
        Utility.statusbar(InventumActivity.this);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_inventum);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        session = new SpotasManager(InventumActivity.this);
        userId = (EditText) findViewById(R.id.ent_userid);
        ent_last4nmbr = (EditText)findViewById(R.id.ent_last4nmbr);
        TextView proceed = (TextView) findViewById(R.id.proceed_to_pay);
         proceed_to_pay_4digitphone = (TextView)findViewById(R.id.proceed_to_pay_4digitphone);
        TextView connect_status = (TextView) findViewById(R.id.connect_status);
        TextView merchant_name = (TextView) findViewById(R.id.merchant_name);
        TextView businessAddress = (TextView) findViewById(R.id.merchant_address);
        ImageView businessBanner = (ImageView) findViewById(R.id.business_banner);
        ImageView businessPic = (ImageView) findViewById(R.id.business_pic);
        LinearLayout back = (LinearLayout) findViewById(R.id.back_layout);
        phonevalidation_Layout = (LinearLayout)findViewById(R.id.phonevalidation_Layout);


        proceed.setOnClickListener(this);
        proceed_to_pay_4digitphone.setOnClickListener(this);
        back.setOnClickListener(this);
        merchant_name.setText(name);
        businessAddress.setText(address);

        if (type.equalsIgnoreCase(getString(R.string.invoice_managment))) {
            userId.setHint("Enter your phone number or customer id");
        }
        else
        {
            userId.setHint("Enter Your Internet Username (Account ID)");
        }
        if (MemberFlag != null) {
            if (MemberFlag.equalsIgnoreCase("1")) {
                connect_status.setVisibility(View.VISIBLE);
            } else {
                connect_status.setVisibility(View.GONE);
            }
        }

        if (logo != null && logo.length() > 0) {
            Picasso.with(this)
                    .load(logo)
                    .transform(new RoundTransform(10, 0))
                    .fit()
                    .placeholder(R.drawable.customer_paper_magazine_image_frame)
                    .into(businessPic);
        } else {
            Picasso.with(this)
                    .load(R.drawable.launcher)
                    .transform(new RoundTransform(10, 0))
                    .fit()
                    .placeholder(R.drawable.customer_paper_magazine_image_frame)
                    .into(businessPic);
        }


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int banner_width = metrics.widthPixels;
        int banner_height = metrics.heightPixels;

        RelativeLayout.LayoutParams llparams = new RelativeLayout.LayoutParams(banner_width, banner_height / 3);
        businessBanner.setLayoutParams(llparams);
        businessBanner.setBackgroundResource(R.color.banner_background);

        if (bannerUrl != null && bannerUrl.length() > 0) {
            Picasso.with(this)
                    .load(bannerUrl)
                    .resize(banner_width, banner_height / 3)
                    .into(businessBanner);
        } else {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.anim_three, R.anim.anim_four);
            return true;
        } else if (item.getItemId() == R.id.location) {
            if (shop_latitude != null && shop_longitude != null) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", Double.parseDouble(shop_latitude), Double.parseDouble(shop_longitude), address);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            } else {
                Toast.makeText(InventumActivity.this, "Location not available", Toast.LENGTH_LONG).show();
            }

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.proceed_to_pay) {
            if (userId.getText().toString().trim().length() > 0) {

                if (type.equalsIgnoreCase(getString(R.string.invoice_managment))) {

                    new RequestFor_Invoice_Mgmt_Subscriptions().execute(VariableConstants.INVOICE_MGMT_SUBSCRIPTION, userId.getText().toString().trim());

                } else {
                    RequestForSubscriptions();
                }
            } else {
                Toast.makeText(InventumActivity.this, "Please enter user id", Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.back_layout) {
            finish();
            overridePendingTransition(R.anim.anim_three, R.anim.anim_four);
        }
        else if (v.getId() == R.id.proceed_to_pay_4digitphone){

            if (ent_last4nmbr.getText().toString().trim().equalsIgnoreCase(customerphone))
            {
                showSubscriptionsList(response, packageAmt);
            }
            else {
                displayifInvalid("2");
            }
        }
    }

    private void RequestForSubscriptions() {
        final ProgressDialog pDialog = new ProgressDialog(InventumActivity.this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(true);
        if (pDialog != null) {
            pDialog.show();
        }

        final JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("ent_dev_id", Utility.getDeviceId(this));
            jsonObj.put("ent_sess_token", session.getSession_token());
            jsonObj.put("ent_business_id", businessId);
            jsonObj.put("ent_actid", userId.getText().toString().trim());
            jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());
        } catch (Exception e) {
        }
        Utility.doJsonRequest(VariableConstants.host_url
                + subcriptionsUrl, jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                // TODO Auto-generated method stub

                 response = jsonResponse;
                if (pDialog != null) {
                    pDialog.dismiss();
                }
                Log.i(TAG, "onSuccess subs jsonResponse=" + response);


                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("subscriptions")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("subscriptions");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                            if (jsonObject1.has("purchase_new_pack")) {
                                purchase_new_pack = jsonObject1.getString("purchase_new_pack");
                            }
                            if (jsonObject1.has("packageAmt")) {
                                packageAmt = jsonObject1.getString("packageAmt");
                            }
                            if (jsonObject1.has("integType")) {
                                integType = jsonObject1.getString("integType");
                            }
                            if (jsonObject1.has("Customer")){
                                JSONObject customerobj = jsonObject1.getJSONObject("Customer");
                                 customerphone = customerobj.getString("phone");
                            }


                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                    if (integType!=null && integType.equalsIgnoreCase(getString(R.string.icenet))) {
                        phonevalidation_Layout.setVisibility(View.VISIBLE);
                        ent_last4nmbr.setText(customerphone.substring(0, Math.min(customerphone.length(), 6)));
                    }
                else {
                        phonevalidation_Layout.setVisibility(View.GONE);
                        showSubscriptionsList(response, packageAmt);

                }
            }

            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub
                Utility.printLog("HomeFrag JSON DATA Error" + error);

                if (pDialog != null) {
                    pDialog.dismiss();
                }
            }
        });
    }

    private void showSubscriptionsList(final String jsonResponse, String packageAmt) {
        try {

            Gson gson = new Gson();
            final Subscriptions subscriptionsResponse = gson.fromJson(jsonResponse, Subscriptions.class);

            if (subscriptionsResponse != null) {
                if (subscriptionsResponse.getErrFlag().equalsIgnoreCase("0")) {
                    if (subscriptionsResponse.getSubscriptions() != null && subscriptionsResponse.getSubscriptions().size() > 0) {
                        subscriptions.clear();
                        subscriptions = subscriptionsResponse.getSubscriptions();


                        if (type != null && type.equalsIgnoreCase(getString(R.string.tacitine))) {
                            if ((subscriptions.get(0).getAccount_state() != null && subscriptions.get(0).getAccount_state().length() > 0) &&
                                    (subscriptions.get(0).getCurrent_plan_type() != null && subscriptions.get(0).getCurrent_plan_type().length() > 0)) {
                                if (subscriptions.get(0).getAccount_state().equalsIgnoreCase("active")
                                        && subscriptions.get(0).getCurrent_plan_type().equalsIgnoreCase("extended")) {


                                    Intent intent = new Intent(InventumActivity.this, TacitineSummaryActivity.class);
                                    intent.putExtra("PROVIDER", name);
                                    intent.putExtra("TYPE", type);
                                    intent.putExtra("BUSINESS_ID", businessId);
                                    intent.putExtra("ENCODED", "");
                                    intent.putExtra("MorePlans", showPlans);
                                    intent.putExtra("DOM_ID", "");
                                    //intent.putExtra("DOM_ID",subscriptionsResponse.getSubscriptions().get(0).getDomid());

                                    SubscriptionsDetailsHelper detailsHelper = subscriptionsResponse.getSubscriptions().get(0);
                                    intent.putExtra("PACKAGE", detailsHelper.getPkgid());
                                    intent.putExtra("planType", detailsHelper.getPlanType());
                                    intent.putExtra("actid", detailsHelper.getActid());
                                    intent.putExtra("packageAmount", packageAmt);
                                    intent.putExtra("billCycle", detailsHelper.getBillCycle());
                                    intent.putExtra("nextRenewalDate", detailsHelper.getNextRenewalDate());
                                    intent.putExtra("convenienceRate", detailsHelper.getConvenienceRate());
                                    intent.putExtra("convenienceFee", detailsHelper.getConvenienceFee());
                                    intent.putExtra("Total", detailsHelper.getTotal());
                                    intent.putExtra("purchase_new_pack", purchase_new_pack);
                                    intent.putExtra("jsonResponse", jsonResponse);

                                    if (detailsHelper.getCustomer() != null) {
                                        intent.putExtra("name", detailsHelper.getCustomer().getName());
                                        intent.putExtra("phone", detailsHelper.getCustomer().getPhone());
                                        intent.putExtra("email", detailsHelper.getCustomer().getEmail());
                                    }

                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_two, R.anim.anim_one);


                                } else {

                                    String base64 = getBase64String(jsonResponse);

                                    Intent intent = new Intent(InventumActivity.this, TacitineSummaryActivity.class);
                                    intent.putExtra("PROVIDER", name);
                                    intent.putExtra("TYPE", type);
                                    intent.putExtra("BUSINESS_ID", businessId);
                                    intent.putExtra("ENCODED", base64);
                                    intent.putExtra("MorePlans", showPlans);
                                    intent.putExtra("DOM_ID", "");
                                    //intent.putExtra("DOM_ID",subscriptionsResponse.getSubscriptions().get(0).getDomid());

                                    SubscriptionsDetailsHelper detailsHelper = subscriptionsResponse.getSubscriptions().get(0);
                                    intent.putExtra("PACKAGE", detailsHelper.getPkgid());
                                    intent.putExtra("planType", detailsHelper.getPlanType());
                                    intent.putExtra("actid", detailsHelper.getActid());
                                    intent.putExtra("packageAmount", packageAmt);
                                    intent.putExtra("billCycle", detailsHelper.getBillCycle());
                                    intent.putExtra("nextRenewalDate", detailsHelper.getNextRenewalDate());
                                    intent.putExtra("convenienceRate", detailsHelper.getConvenienceRate());
                                    intent.putExtra("convenienceFee", detailsHelper.getConvenienceFee());
                                    intent.putExtra("Total", detailsHelper.getTotal());
                                    intent.putExtra("purchase_new_pack", purchase_new_pack);
                                    intent.putExtra("jsonResponse", jsonResponse);
                                    if (detailsHelper.getCustomer() != null) {
                                        intent.putExtra("name", detailsHelper.getCustomer().getName());
                                        intent.putExtra("phone", detailsHelper.getCustomer().getPhone());
                                        intent.putExtra("email", detailsHelper.getCustomer().getEmail());
                                    }

                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
                                }
                            }
                        } else {


                            String base64 = getBase64String(jsonResponse);

                            if (subscriptionsResponse.getSubscriptions().get(0).getInvoice() != null && subscriptionsResponse.getSubscriptions().get(0).getInvoice().size() > 0) {
                                Intent intent = new Intent(InventumActivity.this, PostpayInvoiceSummaryActivity.class);
                                intent.putExtra("PROVIDER", name);
                                intent.putExtra("TYPE", type);
                                intent.putExtra("BUSINESS_ID", businessId);
                                intent.putExtra("ENCODED", base64);
                                intent.putExtra("DOM_ID", subscriptionsResponse.getSubscriptions().get(0).getDomid());
                                intent.putExtra("PACKAGE", subscriptionsResponse.getSubscriptions().get(0).getPkgid());
                                intent.putExtra("MorePlans", showPlans);

                                InvoiceDetailsHelper helper = subscriptionsResponse.getSubscriptions().get(0).getInvoice().get(0);
                                intent.putExtra("invoiceDate", helper.getInvoiceDate());
                                intent.putExtra("dueDate", helper.getDueDate());
                                intent.putExtra("billStartDate", helper.getBillStartDate());
                                intent.putExtra("billEndDate", helper.getBillEndDate());
                                intent.putExtra("billAmount", helper.getBillAmount());
                                intent.putExtra("pendingAmount", helper.getPendingAmount());
                                intent.putExtra("payableAmount", helper.getPayableAmount());
                                intent.putExtra("convenienceRate", helper.getConvenienceRate());
                                intent.putExtra("convenienceFee", helper.getConvenienceFee());
                                intent.putExtra("Total", helper.getTotal());
                                intent.putExtra("amountEditable", helper.getAmountEditable());
                                intent.putExtra("invoiceHTML", helper.getInvoiceHTML());
                                intent.putExtra("daysToDue", helper.getDaysToDue());
                                intent.putExtra("invoiceNo", helper.getInvoiceNo());
                                intent.putExtra("paymentsDone", helper.getPaymentsDone());
                                intent.putExtra("openingBalance", helper.getOpeningBalance());

                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
                            } else {

                                Intent intent = null;
                                if (subscriptionsResponse.getSubscriptions().get(0).getPackages() != null && subscriptionsResponse.getSubscriptions().get(0).getPackages().size() > 0) {
                                    if (subscriptionsResponse.getSubscriptions().get(0).getPackages().get(0).getCoupons().size() > 1) {
                                        intent = new Intent(InventumActivity.this, SelectPackageActivity.class);
                                        intent.putExtra("JsonResponse", jsonResponse);
                                    } else if (subscriptionsResponse.getSubscriptions().get(0).getPackages().get(0).getCoupons().size() > 0) {

                                        intent = new Intent(InventumActivity.this, SubscriptionsSummaryAvtivity.class);

                                        JSONObject object1 = new JSONObject(jsonResponse);
                                        JSONArray subscriptionsArray = object1.getJSONArray("subscriptions");
                                        JSONObject subscriptionsObject = subscriptionsArray.getJSONObject(0);

                                        JSONArray packagesArray = subscriptionsObject.getJSONArray("Packages");
                                        JSONObject packagesObject = packagesArray.getJSONObject(0);

                                        JSONArray couponsArray = packagesObject.getJSONArray("Coupons");
                                        JSONObject couponsObject = couponsArray.getJSONObject(0);


                                        byte[] data_coupon = couponsObject.toString().getBytes("UTF-8");
                                        String base64_coupon = Base64.encodeToString(data_coupon, Base64.DEFAULT);

                                        PlansCouponsDetails plan = subscriptionsResponse.getSubscriptions().get(0).getPackages().get(0).getCoupons().get(0);

                                        intent.putExtra("CouponID", plan.getCouponID());
                                        intent.putExtra("Description", plan.getDescription());
                                        intent.putExtra("Price", plan.getPrice());
                                        intent.putExtra("Tax", plan.getTax());
                                        intent.putExtra("ParentRatePlan", plan.getParentRatePlan());
                                        intent.putExtra("TaxPercent", plan.getTaxPercent());
                                        intent.putExtra("SubTotal", plan.getSubTotal());
                                        intent.putExtra("convenienceFee", plan.getConvenienceFee());
                                        intent.putExtra("Total", plan.getTotal());
                                        intent.putExtra("CurrentPackage", base64_coupon);
                                    }
                                }

                                if (intent != null) {
                                    intent.putExtra("PROVIDER", name);
                                    intent.putExtra("TYPE", type);
                                    intent.putExtra("BUSINESS_ID", businessId);
                                    intent.putExtra("ENCODED", base64);
                                    intent.putExtra("DOM_ID", subscriptionsResponse.getSubscriptions().get(0).getDomid());
                                    intent.putExtra("PACKAGE", subscriptionsResponse.getSubscriptions().get(0).getPkgid());
                                    intent.putExtra("MorePlans", showPlans);

                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
                                } else {
                                    Toast.makeText(InventumActivity.this, "We're unable to find your package details", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                } else if (subscriptionsResponse.getErrFlag().equalsIgnoreCase("1")) {

                    displayifInvalid("1");

                } else {
                }
            }
        } catch (Exception e) {
            Log.i("exception ",e.getMessage());
        }
    }

    private void displayifInvalid(String number) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels - 150;

        final Dialog dialog = new Dialog(InventumActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (number.equalsIgnoreCase("1")) {
            dialog.setContentView(R.layout.invalid_username);
        }
        if (number.equalsIgnoreCase("2")){
            dialog.setContentView(R.layout.invalid_phonenumber);
        }
        RelativeLayout layout1 = (RelativeLayout) dialog.findViewById(R.id.layout1);
        RelativeLayout layout2 = (RelativeLayout) dialog.findViewById(R.id.layout2);

        LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height / 2);
        layout1.setLayoutParams(llparams);
        layout2.setLayoutParams(llparams);
        TextView gotit = (TextView) dialog.findViewById(R.id.tv_gotid);
        gotit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    private String getBase64String(String jsonObject) {
        try {

            JSONObject object1 = new JSONObject(jsonObject);
            JSONArray objectSub1 = object1.getJSONArray("subscriptions");
            JSONObject jsonObject1 = objectSub1.getJSONObject(0);
            byte[] data = jsonObject1.toString().getBytes("UTF-8");
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException e) {
        }

        return null;
    }
    private String getBase64Stringinvoicemgmt(String jsonObject) {
        try {

            JSONObject object1 = new JSONObject(jsonObject);
            JSONObject objectSub1 = object1.getJSONObject("data");
            byte[] data = objectSub1.toString().getBytes("UTF-8");
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException e) {
        }

        return null;
    }




    private class RequestFor_Invoice_Mgmt_Subscriptions extends AsyncTask<String, Void, String> {
        String result1;
        ArrayList<NameValuePair> namevaluepairs = new ArrayList<NameValuePair>();
        final ProgressDialog pDialog = new ProgressDialog(InventumActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(true);
            if (pDialog != null) {
                pDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {


            namevaluepairs.add(new BasicNameValuePair("deviceID", Utility.getDeviceId(InventumActivity.this)));
            namevaluepairs.add(new BasicNameValuePair("sessionToken", session.getSession_token()));
            namevaluepairs.add(new BasicNameValuePair("BusinessID", businessId));
            namevaluepairs.add(new BasicNameValuePair("searchKey", params[1]));
            try {
                result1 = Utility.executeHttpPost(params[0], namevaluepairs);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result1;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                if (pDialog != null) {
                    pDialog.dismiss();
                }
                try {

                    JSONObject jsonObject = new JSONObject(s);
                    String errNum = jsonObject.getString("errNum");
                    String errFlag = jsonObject.getString("errFlag");
                    String msg = jsonObject.getString("msg");
                    if (s.length()>0 && errFlag.equalsIgnoreCase("0")) {
                        String base64 = getBase64Stringinvoicemgmt(s);

                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        JSONArray jsonArray = jsonObject1.getJSONArray("invoices");
                        if (jsonArray.length() > 1) {
                            gotoPostpayInvoicePackageList(jsonArray.length(), jsonArray,base64);
                        } else {
                            gotoPostpayInvoiceSummaryActivity(jsonArray.length(), jsonArray,base64);
                        }
                    }
                    else {
                        Toast.makeText(InventumActivity.this, ""+msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                if (pDialog != null) {
                    pDialog.dismiss();
                }
            }
        }


    }

    private void gotoPostpayInvoicePackageList(int length, JSONArray jsonArray,String base64) {
        if (invoicepackageList.size()>0){
            invoicepackageList.clear();
        }
        for (int i = 0 ; i<length; i++){
            JSONObject invoiceonj = null;
            try {
                invoiceonj = jsonArray.getJSONObject(i);
                if (invoiceonj!=null && invoiceonj.length()>0) {

                    String BillingStartDate = invoiceonj.getString("BillingStartDate");
                    String BillingEndDate = invoiceonj.getString("BillingEndDate");
                    String InvoiceNum = invoiceonj.getString("InvoiceNum");
                    String PrevDue = invoiceonj.getString("PrevDue");
                    String BillAmount = invoiceonj.getString("BillAmount");
                    String Payments = invoiceonj.getString("Payments");
                    String TaxPercent = invoiceonj.getString("TaxPercent");
                    String TaxAmount = invoiceonj.getString("TaxAmount");
                    String ConvenienceFeeRate = invoiceonj.getString("ConvenienceFeeRate");
                    String PartialPayment = invoiceonj.getString("PartialPayment");
                    String OnlinePayments = invoiceonj.getString("OnlinePayments");
                    String InvoiceDate = invoiceonj.getString("InvoiceDate");
                    String DueDate = invoiceonj.getString("DueDate");
                    String PostDuePenalty = invoiceonj.getString("PostDuePenalty");
                    String DaysToDue = invoiceonj.getString("DaysToDue");
                    String ProductName = invoiceonj.getString("ProductName");
                    InvoiceManagementPackageListModel invoicepackagemodel = new InvoiceManagementPackageListModel(BillingStartDate,
                            BillingEndDate,InvoiceNum,PrevDue,BillAmount,Payments,TaxPercent,TaxAmount,ConvenienceFeeRate,PartialPayment,OnlinePayments,InvoiceDate,
                            DueDate,PostDuePenalty,DaysToDue,ProductName);

                    invoicepackageList.add(invoicepackagemodel);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Intent sendinvoicelist= new Intent(InventumActivity.this, InvoiceManagementPackageList.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("allinvoiceList", invoicepackageList);
        bundle.putString("PROVIDER", name);
        bundle.putString("TYPE", type);
        bundle.putString("BUSINESS_ID", businessId);
        bundle.putString("ENCODED", base64);
        bundle.putString("DOM_ID", "");
        bundle.putString("MorePlans", showPlans);
        sendinvoicelist.putExtras(bundle);
        startActivity(sendinvoicelist);
    }
    private void gotoPostpayInvoiceSummaryActivity(int length, JSONArray jsonArray, String base64) {

        for (int i = 0 ; i<length; i++){
            JSONObject invoiceonj = null;
            try {
                invoiceonj = jsonArray.getJSONObject(i);

            if (invoiceonj!=null && invoiceonj.length()>0) {

                Double total = getTotal(invoiceonj.getString("PrevDue"),invoiceonj.getString("BillAmount"),invoiceonj.getString("Payments"));
                Double confee = getConFee(invoiceonj.getString("ConvenienceFeeRate"),total);
                Double payableamnt = total + confee;

                Intent intent = new Intent(InventumActivity.this, PostpayInvoiceSummaryActivity.class);
                intent.putExtra("PROVIDER", name);
                intent.putExtra("TYPE", type);
                intent.putExtra("BUSINESS_ID", businessId);
                intent.putExtra("ENCODED", base64);
                intent.putExtra("DOM_ID", "");
                intent.putExtra("PACKAGE", invoiceonj.getString("ProductName"));
                intent.putExtra("MorePlans", showPlans);

                intent.putExtra("invoiceDate", invoiceonj.getString("InvoiceDate"));
                intent.putExtra("dueDate", invoiceonj.getString("DueDate"));
                intent.putExtra("billStartDate", invoiceonj.getString("BillingStartDate"));
                intent.putExtra("billEndDate", invoiceonj.getString("BillingEndDate"));
                intent.putExtra("billAmount", invoiceonj.getString("BillAmount"));
                intent.putExtra("pendingAmount",invoiceonj.getString("PrevDue"));
                intent.putExtra("payableAmount", ""+payableamnt);
                intent.putExtra("convenienceRate", invoiceonj.getString("ConvenienceFeeRate"));
                intent.putExtra("convenienceFee",""+confee);
                intent.putExtra("Total", ""+payableamnt);
                intent.putExtra("amountEditable",invoiceonj.getString("PartialPayment"));
                intent.putExtra("invoiceHTML", "");
                intent.putExtra("daysToDue", invoiceonj.getString("DaysToDue"));
                intent.putExtra("invoiceNo", invoiceonj.getString("InvoiceNum"));
                intent.putExtra("paymentsDone", invoiceonj.getString("Payments"));
                intent.putExtra("openingBalance", invoiceonj.getString("PrevDue"));
                //  intent.putExtra("TaxAmount", jsonObject1.getString("TaxAmount"));


                startActivity(intent);
                overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
            }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public static Double getConFee(String convenienceFeeRate, Double total) {
        double conveniencefeerate, totl,confee;
        conveniencefeerate =  Double.valueOf(convenienceFeeRate);
        totl = total;
        confee = ((conveniencefeerate * totl) /100);

        return confee;
    }
    public  Double getTotal(String prevDue, String billAmount, String payments) {

        double prevdue, billamount,payment,total;
        prevdue = Double.valueOf(prevDue);
        billamount = Double.valueOf(billAmount);
        payment = Double.valueOf(payments);
        total =((prevdue + billamount)-payment);
        return total;
    }

}