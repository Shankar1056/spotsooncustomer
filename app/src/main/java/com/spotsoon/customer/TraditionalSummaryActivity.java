package com.spotsoon.customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.Button;
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
import com.threembed.homebean.CategoryProduct;
import com.threembed.homebean.Confirm_summitbean;
import com.threembed.spotasap_pojos.PayU_Hashpojo;
import com.utility.OkHttpRequest;
import com.utility.SpotasManager;
import com.utility.Utility;
import com.utility.VariableConstants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;


/**
 * Created by VARUN on 4/20/2016.
 */
public class TraditionalSummaryActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout packageLayout,packageLayoutMain;
    private TextView total_amt;
    private TextView tax,tax_amount,convinience_amount;
    private RelativeLayout tax_layout,convinience_fee_layout;
    private LinearLayout detailsLayout;
    private ImageView arrowView;
    private SpotasManager session;
    private String userId,houseNumber,lankMark,city,pincode,payuId,PaymentGateway = null;
    private double packgaeAmount=0,totalAmount=0,taxTotal=0,convinienceFee=0;
    private ProgressDialog payuDialog;
    private PaymentParams mPaymentParams;
    private PayuConfig payuConfig;
    private Intent intent;
    private PayuHashes payuHashes;
    private PayU_Hashpojo ServerResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(TraditionalSummaryActivity.this, new Crashlytics());
        setContentView(R.layout.traditional_summary);

        Intent intent = getIntent();
        if(intent!=null)
        {
            userId = intent.getStringExtra("USERNAME");
            houseNumber = intent.getStringExtra("HOUSE_NUMBER");
            lankMark = intent.getStringExtra("LANDMARK");
            city = intent.getStringExtra("CITY");
            pincode = intent.getStringExtra("PINCODE");

        }
        initializeViews();
        addSelectedPackages();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.anim_three, R.anim.anim_four);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeViews()
    {
        Utility.statusbar(TraditionalSummaryActivity.this);

        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_subcriptions);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Summary");

        session = new SpotasManager(TraditionalSummaryActivity.this);

        packageLayout = (LinearLayout)findViewById(R.id.selected_packages_layout);
        packageLayoutMain= (LinearLayout)findViewById(R.id.selected_packages_layout_main);
        RelativeLayout prev_due_layout = (RelativeLayout)findViewById(R.id.pre_due_layout);

        RelativeLayout showDetailsPackageLayout = (RelativeLayout)findViewById(R.id.package_layout);
        tax_layout = (RelativeLayout)findViewById(R.id.tax_layout);
        convinience_fee_layout = (RelativeLayout)findViewById(R.id.convinience_layout);
        RelativeLayout prev_renewal_layout = (RelativeLayout)findViewById(R.id.previous_renewal_layout);
        RelativeLayout next_renewal_layout = (RelativeLayout)findViewById(R.id.next_renewal_layout);

       // RelativeLayout  user_details_layout = (RelativeLayout)findViewById(R.id.user_details_layout);

        TextView package_name_main = (TextView)findViewById(R.id.package_name_top);
        TextView package_amount_main = (TextView)findViewById(R.id.package_price_top);

        ((TextView)findViewById(R.id.user_name)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.user_id)).setVisibility(View.GONE);


        Button proceed = (Button)findViewById(R.id.proceed);
        LinearLayout showDetailsLayout = (LinearLayout)findViewById(R.id.details_layout);
        detailsLayout = (LinearLayout)findViewById(R.id.summary_payment_layout);
        arrowView = (ImageView)findViewById(R.id.arrow_image);

        TextView proName = (TextView)findViewById(R.id.merchant_name);
        TextView view_plans = (TextView)findViewById(R.id.view_plans);
        total_amt = (TextView)findViewById(R.id.totalamount_price);
        TextView prev_ren = (TextView)findViewById(R.id.previous_renewal_date);
        TextView next_ren = (TextView)findViewById(R.id.next_renewal_date);

        TextView prev_due_amount = (TextView)findViewById(R.id.pre_due_amount);

        tax = (TextView)findViewById(R.id.tv_tax);
        tax_amount = (TextView)findViewById(R.id.tax_amount);

        convinience_amount = (TextView)findViewById(R.id.convinience_amount);

        proceed.setOnClickListener(this);
        showDetailsLayout.setOnClickListener(this);

        prev_due_layout.setVisibility(View.GONE);
        prev_due_layout.setVisibility(View.GONE);
        next_renewal_layout.setVisibility(View.GONE);
        prev_renewal_layout.setVisibility(View.GONE);
        showDetailsPackageLayout.setVisibility(View.GONE);
       // user_details_layout.setVisibility(View.GONE);
        view_plans.setVisibility(View.GONE);

        proName.setText(PackagesActivity.businessName);
    }

    private void addSelectedPackages()
    {
        String taxPerc = null;

        for(int i=0;i<PackagesActivity.response.getData().get(0).getCategories().size();i++)
        {
            for(int j=0;j<PackagesActivity.response.getData().get(0).getCategories().get(i).getProducts().size();j++)
            {
                if(PackagesActivity.response.getData().get(0).getCategories().get(i).getProducts().get(j).getPackageCount()>0)
                {

                    //adding selected packages to main details
                    addPackage(packageLayoutMain, PackagesActivity.response.getData().get(0).getCategories().get(i).getProducts().get(j).getProductName(),
                            PackagesActivity.response.getData().get(0).getCategories().get(i).getProducts().get(j).getPrice(),
                            PackagesActivity.response.getData().get(0).getCategories().get(i).getProducts().get(j).getPackageCount());

                    //adding selected packages to hidden details
                    addPackage(packageLayout, PackagesActivity.response.getData().get(0).getCategories().get(i).getProducts().get(j).getProductName(),
                            PackagesActivity.response.getData().get(0).getCategories().get(i).getProducts().get(j).getPrice(),
                            PackagesActivity.response.getData().get(0).getCategories().get(i).getProducts().get(j).getPackageCount());

                    packgaeAmount = packgaeAmount + Double.parseDouble(PackagesActivity.response.getData().get(0).getCategories().get(i).getProducts().get(j).getPrice());

                    totalAmount = totalAmount +(PackagesActivity.response.getData().get(0).getCategories().get(i).getProducts().get(j).getPackageCount() *
                            Double.parseDouble(PackagesActivity.response.getData().get(0).getCategories().get(i).getProducts().get(j).getPrice()));

                    taxTotal = taxTotal+PackagesActivity.response.getData().get(0).getCategories().get(i).getProducts().get(j).getCalculatedTax();
                    taxPerc = PackagesActivity.response.getData().get(0).getTaxPercent();
                }
            }
        }

        if(taxPerc!=null)
        {
            tax.setText("Tax("+taxPerc+"%)");
        }
        else {
            tax.setText("Tax");
        }

        if(taxTotal!=0)
        {
            tax_amount.setText(Utility.round(taxTotal));
        }
        else {
            tax_layout.setVisibility(View.GONE);
        }

        totalAmount = totalAmount + taxTotal;

        if(PackagesActivity.response.getData().get(0).getConvenienceCharge()!=null && PackagesActivity.response.getData().get(0).getConvenienceCharge().length()>0)
        {
            try {
                if(Double.parseDouble(PackagesActivity.response.getData().get(0).getConvenienceCharge()) > 0)
                {
                    double convPercentage = Double.parseDouble(PackagesActivity.response.getData().get(0).getConvenienceCharge());
                    convinienceFee =  (totalAmount / 100.0f) * convPercentage;
                    convinience_amount.setText(Utility.round(convinienceFee));

                    totalAmount = totalAmount + convinienceFee;
                }
                else {
                    convinience_fee_layout.setVisibility(View.GONE);
                }
            }
            catch (NumberFormatException e)
            {
            }
        }
        else {
            convinience_fee_layout.setVisibility(View.GONE);
        }
        total_amt.setText(Utility.round(totalAmount));
    }

    private void addPackage(LinearLayout layout,String name,String price,int count)
    {
        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View subCatRow = mInflater.inflate(R.layout.package_row_layout, null);

        final TextView packageName = (TextView) subCatRow.findViewById(R.id.package_name_top);
        final TextView packageAmount = (TextView) subCatRow.findViewById(R.id.package_price_top);

        if(count>1)
        {
            packageName.setText(name+"(x"+count+")");
            packageAmount.setText(Utility.round(count * Double.parseDouble(price)));
        }
        else {
            packageName.setText(name);
            packageAmount.setText(Utility.round(Double.parseDouble(price)));
        }
        //packageAmount.setText("₹ " +Utility.round(Double.parseDouble(price)));
        layout.addView(subCatRow);
        //packageLayoutMain.addView(subCatRow);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.details_layout)
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
        else if(v.getId() == R.id.proceed)
        {
            livebooking();
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

    private void livebooking()
    {
        payuDialog = new ProgressDialog(TraditionalSummaryActivity.this);
        payuDialog.setMessage("Please wait, Redirecting to payment screen...");
        payuDialog.setCancelable(true);
        if(payuDialog!=null) {
            payuDialog.show();
        }

        JSONArray jsonArray = new JSONArray();

        for(int i=0;i<PackagesActivity.response.getData().get(0).getCategories().size();i++)
        {
            for(int j=0;j<PackagesActivity.response.getData().get(0).getCategories().get(i).getProducts().size();j++)
            {
                if(PackagesActivity.response.getData().get(0).getCategories().get(i).getProducts().get(j).getPackageCount()>0)
                {
                    CategoryProduct product = PackagesActivity.response.getData().get(0).getCategories().get(i).getProducts().get(j);

                    JSONObject obj = new JSONObject();

                    double amt = (product.getPackageCount() *
                            Double.parseDouble(product.getPrice()));

                    //amt = amt + product.getCalculatedTax();
                    try
                    {
                        obj.put("ItemId", product.getId());
                        obj.put("ItemName", product.getProductName());
                        obj.put("Price", product.getPrice());
                        obj.put("Quantity", product.getPackageCount());
                        obj.put("ItemTotal", ""+amt);
                        obj.put("Status","1");
                        Utility.printLog("the jsonobject "+obj);

                        jsonArray.put(obj);
                    }
                    catch (JSONException e)
                    {
                        Utility.printLog("DefaultListItem.toString JSONException: " + e.getMessage());
                    }
                }
            }
        }

        JSONObject jsonObj = new JSONObject();
        try {


            jsonObj.put("ent_sess_token", session.getSession_token());
            jsonObj.put("ent_dev_id", Utility.getDeviceId(TraditionalSummaryActivity.this));
            //jsonObj.put("ent_amount", Utility.round(totalAmount));
            jsonObj.put("ent_amount", Utility.round(packgaeAmount));
            jsonObj.put("ent_addr_line2", city+", "+pincode);
            jsonObj.put("ent_addr_line1",houseNumber);
            jsonObj.put("ent_house_no",houseNumber);
            jsonObj.put("ent_landmark", lankMark);
            jsonObj.put("ent_items", jsonArray);
            jsonObj.put("ent_business_id", PackagesActivity.businessId);
            jsonObj.put("ent_convenience_fee", Utility.round(convinienceFee));//conviniencecharge
            jsonObj.put("ent_appnt_dt", Utility.getCurrentDateTimeString());
            jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());//

            jsonObj.put("ent_lat", "13.058618077372");
            jsonObj.put("ent_long", "77.563465437712");
            jsonObj.put("ent_delivery_fee", "0");
            jsonObj.put("ent_extra_notes", userId);
            jsonObj.put("ent_customer_type","");
            jsonObj.put("ent_identity_note","");
            jsonObj.put("ent_payment_type", "1");
            jsonObj.put("ent_delivery_type", "1");

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        Utility.doJsonRequest(VariableConstants.host_url + "liveBooking", jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                payuDialog.dismiss();

                livebookingResponse(jsonResponse);
            }

            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub
                Utility.printLog("HomeFrag JSON DATA Error" + error);

                if(payuDialog!=null) {

                    payuDialog.dismiss();
                    payuDialog = null;
                }
            }
        });

        /****************************************************************************************************/
    }

    private void livebookingResponse(String jsonResponse)
    {
        try
        {
            Gson gson = new Gson();
            Confirm_summitbean respo_singin = gson.fromJson(jsonResponse, Confirm_summitbean.class);

            if(respo_singin.getErrFlag()!=null && respo_singin.getErrFlag().equals("0"))
            {
                payuId = respo_singin.getPayUId();
                PaymentGateway = respo_singin.getPaymentGateway();
                if (PaymentGateway.equalsIgnoreCase("RAZORPAY")){
                    String a= Utility.round(totalAmount);
                  startRazorPayment("" + Utility.round((Double.parseDouble(a)) * 100 ));
                  /*  String b =Utility.round(totalAmount * 100);
                    String a =Utility.round((Double.parseDouble(String.valueOf(totalAmount))) * 100);
                    startRazorPayment("" + Utility.round((Double.parseDouble(String.valueOf(totalAmount))) * 100));*/

                }
                else {
                    payu();
                }


            }
            else if(respo_singin.getErrFlag()!=null && respo_singin.getErrFlag().equalsIgnoreCase("1"))
            {
                if(payuDialog!=null) {

                    payuDialog.dismiss();
                    payuDialog = null;
                }
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage(respo_singin.getErrMsg())
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            else {
                if(payuDialog!=null) {

                    payuDialog.dismiss();
                    payuDialog = null;
                }
            }
        }
        catch (Exception e)
        {
            Utility.printLog(e+"");
        }
    }

    private void payu()
    {
        intent = new Intent(this, PayUBaseActivity.class);
        mPaymentParams = new PaymentParams();
        payuConfig = new PayuConfig();
        mPaymentParams.setKey("rrC4St");
        mPaymentParams.setAmount(Utility.round(totalAmount));
        mPaymentParams.setProductInfo(session.getFname()+" - "+totalAmount);//need to add
        mPaymentParams.setFirstName(session.getFname());
        mPaymentParams.setEmail(session.getEmailId());
        mPaymentParams.setTxnId(payuId);
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
        // for check_isDomestic
        // if(null != cardBin)
        //postParamsBuffer.append(concatParams("card_bin", cardBin));

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
      //  Log.i("ali in ", "try block");

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
                payuHashes.setVasForMobileSdkHash(ServerResponse.getVas_for_mobile_sdk_hash());
                payuHashes.setPaymentRelatedDetailsForMobileSdkHash(ServerResponse.getPayment_related_details_for_mobile_sdk_hash());
                payuHashes.setDeleteCardHash(ServerResponse.getDelete_user_card_hash());
                payuHashes.setStoredCardsHash(ServerResponse.getSave_user_card_hash());
                payuHashes.setEditCardHash(ServerResponse.getEdit_user_card_hash());
                payuHashes.setSaveCardHash(ServerResponse.getSave_user_card_hash());
                payuHashes.setCheckOfferStatusHash(ServerResponse.getCheck_offer_status_hash());
                payuHashes.setCheckIsDomesticHash(ServerResponse.getCheck_isDomestic_hash());
                launchSdkUI();
            }

            @Override
            public void onError(String error) {
                System.out.print("Roshani into error" + error);

                if(payuDialog!=null) {

                    payuDialog.dismiss();
                    payuDialog = null;
                }
            }
        });
    }

    private void launchSdkUI()
    {
        if(payuDialog!=null) {

            payuDialog.dismiss();
            payuDialog = null;
        }

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
                //String resultedstring = data.getStringExtra("result");
                String resultedstring = data.getStringExtra(PayuConstants.PAYU_RESPONSE);

                if(resultedstring != null && resultedstring.contains("success")) {

                    Toast.makeText(this, "order placed successfully", Toast.LENGTH_SHORT).show();

                    VariableConstants.fromConformOrder = true;
                    Intent inventumIntent = new Intent(TraditionalSummaryActivity.this, InventumRechargeStatusActivity.class);
                    inventumIntent.putExtra("PAYU_ID", payuId);
                    inventumIntent.putExtra("TYPE", getString(R.string.traditional));
                    startActivity(inventumIntent);
                    overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
                }
                else
                {
                    Toast.makeText(this,"We are sorry, your recharge failed. Please contact our customer care for any assistance",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "We are sorry, your recharge failed. Please contact our customer care for any assistance", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_three, R.anim.anim_four);
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
            options.put("notes", new JSONObject("{order_id: "+payuId+"}"));


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
            Toast.makeText(this, "Payment failed: ", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Log.e("com.merchant", e.getMessage(), e);
        }
    }
    private class RazorPayment extends AsyncTask<String,Void,String> {
        String result1;
        ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            payuDialog = new ProgressDialog(TraditionalSummaryActivity.this);
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
if (s!=null) {

    VariableConstants.fromConformOrder = true;
    Intent inventumIntent = new Intent(TraditionalSummaryActivity.this, InventumRechargeStatusActivity.class);
    inventumIntent.putExtra("PAYU_ID", payuId);
    inventumIntent.putExtra("TYPE", getString(R.string.traditional));
    startActivity(inventumIntent);
    overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
}

    else {
        Toast.makeText(TraditionalSummaryActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

}


        }
    }
}

