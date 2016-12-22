package com.spotsoon.customer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
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
import com.threembed.spotasap_pojos.PayU_Hashpojo;
import com.utility.OkHttpRequest;
import com.utility.SpotasManager;
import com.utility.Utility;
import com.utility.VariableConstants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.fabric.sdk.android.Fabric;

/**
 * Created by VARUN on 7/3/2016.
 */
public class PostpayInvoiceSummaryActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mBillStartDate,mBillEndDate,mInvoiceNumber,mPreviousDue,mBillAmount,mPaidAmount,mInvoiceDay,mInvoiceMonthYear,mDueDay,mDueMonthYear;
    private TextView mDaysToDueDate,mDueDateStatus,mCurrentPlan,mConvenienceFee;
    private EditText mEnterdAmount;
    private Button mProceedToPay;
    private ImageView mArrowView,poweredby;
    private LinearLayout mDetailsLayout;
    private String type,businessId,encodedString,packageId;
    private String invoiceDate,daysToDue,invoiceNo,paymentsDone,dueDate,billStartDate,billEndDate,billAmount,openingBalance,payableAmount,convenienceRate,convenienceFee,Total,amountEditable,invoiceHTML;
    private ProgressDialog payuDialog;
    private SpotasManager session;
    private String mPayuId,planActDate;
    private PaymentParams mPaymentParams;
    private PayuConfig payuConfig;
    private PayuHashes payuHashes;
    private PayU_Hashpojo ServerResponse;
    private Intent intent;
    private boolean isAmountEdited;
    private Double editedTotalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(PostpayInvoiceSummaryActivity.this, new Crashlytics());
        setContentView(R.layout.postpay_invoice);
        initializeViews();

        Intent intent = getIntent();
        if(intent!=null) {

            String providerName = intent.getStringExtra("PROVIDER");
            type = intent.getStringExtra("TYPE");
            businessId = intent.getStringExtra("BUSINESS_ID");
            encodedString = intent.getStringExtra("ENCODED");
            String domId = intent.getStringExtra("DOM_ID");
            packageId = intent.getStringExtra("PACKAGE");
            String morePlans = intent.getStringExtra("MorePlans");
            invoiceDate = intent.getStringExtra("invoiceDate");
            dueDate = intent.getStringExtra("dueDate");
            billStartDate = intent.getStringExtra("billStartDate");
            billEndDate = intent.getStringExtra("billEndDate");
            billAmount = intent.getStringExtra("billAmount");
            String pendingAmount = intent.getStringExtra("pendingAmount");
            openingBalance = intent.getStringExtra("openingBalance");
            payableAmount = intent.getStringExtra("payableAmount");
            convenienceRate = intent.getStringExtra("convenienceRate");
            convenienceFee = Utility.round(Double.parseDouble(intent.getStringExtra("convenienceFee")));
            Total = Utility.round(Double.parseDouble(intent.getStringExtra("Total")));
            amountEditable = intent.getStringExtra("amountEditable");
            invoiceHTML = intent.getStringExtra("invoiceHTML");
            daysToDue = intent.getStringExtra("daysToDue");
            invoiceNo = intent.getStringExtra("invoiceNo");
            paymentsDone = intent.getStringExtra("paymentsDone");

        }

        setValues();

        mEnterdAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!mEnterdAmount.getText().toString().trim().equals("")) {
                    double res = 0.0;
                    try {


                        if(convenienceRate!=null && convenienceRate.length()>0)
                        {
                            double convRate = Double.valueOf(convenienceRate);
                            if(convRate>0)
                            {
                                double amount = Double.parseDouble(mEnterdAmount.getText().toString().trim());
                                res = (amount / 100.0f) * Double.parseDouble(convenienceRate);
                            }
                        }

                        editedTotalAmount = 0.0;
                        if (!(mEnterdAmount.getText().toString().trim().equalsIgnoreCase("-"))) {
                            editedTotalAmount = res + Double.parseDouble(mEnterdAmount.getText().toString().trim());//+ Double.parseDouble(pendingAmount);
                            convenienceFee = Utility.round(res);
                            mConvenienceFee.setText("Convenience Fee : " + Utility.round(res));
                            if ((Double.parseDouble(mEnterdAmount.getText().toString().trim())) < ( Double.parseDouble(Total)) || (Double.parseDouble(mEnterdAmount.getText().toString().trim())) > ( Double.parseDouble(Total))) {
                                mProceedToPay.setText("Proceed to pay " + getString(R.string.Rs) + " " + Utility.round(editedTotalAmount));
                            }
                        }
                        else {
                            mConvenienceFee.setText("Convenience Fee : 0.0");
                            mProceedToPay.setText("Proceed to pay " + getString(R.string.Rs) + " 0.0");
                        }
                        isAmountEdited = true;
                    } catch (NumberFormatException e) {
//                        mConvenienceFee.setText("Convenience Fee : " + convenienceFee);
//                        mEnterdAmount.setText(Total);
                        mConvenienceFee.setText("Convenience Fee : 0.0");
                        mProceedToPay.setText("Proceed to pay " + getString(R.string.Rs) + " 0.0");
                    }

                } else {
                    mConvenienceFee.setText("Convenience Fee : 00");
                    mProceedToPay.setText("Proceed to pay " + getString(R.string.Rs) + " 00");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initializeViews()
    {
        Utility.statusbar(PostpayInvoiceSummaryActivity.this);

        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_summary);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Billing Summary");

        session = new SpotasManager(PostpayInvoiceSummaryActivity.this);

        Calendar currentCalendar = Calendar.getInstance();
        int toYear = currentCalendar.get(Calendar.YEAR);
        int toMonth = currentCalendar.get(Calendar.MONTH);
        int toDay = currentCalendar.get(Calendar.DAY_OF_MONTH);

        planActDate = toYear+"-"+makeTwoDigits(toMonth+1)+"-"+makeTwoDigits(toDay);

        mBillStartDate = (TextView)findViewById(R.id.bill_start_date);
        mBillEndDate = (TextView)findViewById(R.id.bill_end_date);
        mInvoiceNumber = (TextView)findViewById(R.id.invoice_number);
        mPreviousDue = (TextView)findViewById(R.id.previous_due_amount);
        mBillAmount = (TextView)findViewById(R.id.bill_amount);
        mPaidAmount = (TextView)findViewById(R.id.paid_amount);
        mInvoiceDay = (TextView)findViewById(R.id.invoice_day);
        mInvoiceMonthYear = (TextView)findViewById(R.id.invoice_month_year);
        mDueDay = (TextView)findViewById(R.id.invoice_due_day);
        mDueMonthYear = (TextView)findViewById(R.id.invoice_due_month_year);
        mDaysToDueDate = (TextView)findViewById(R.id.due_days_remaining);
        mDueDateStatus = (TextView)findViewById(R.id.due_dates_status);
        mCurrentPlan = (TextView)findViewById(R.id.current_plan);
        mConvenienceFee = (TextView)findViewById(R.id.convenience_fee);
        mEnterdAmount = (EditText)findViewById(R.id.enter_amount);
        mProceedToPay = (Button)findViewById(R.id.proceed_to_pay);
        TextView mViewInvoice = (TextView)findViewById(R.id.view_invoice);
        mArrowView = (ImageView)findViewById(R.id.arrow);
        poweredby = (ImageView)findViewById(R.id.poweredby);
        RelativeLayout mViewDetailsLayout = (RelativeLayout)findViewById(R.id.view_details_layout);
        mDetailsLayout = (LinearLayout)findViewById(R.id.details_layout);

        mEnterdAmount.getBackground().mutate().setColorFilter(Utility.getColor(PostpayInvoiceSummaryActivity.this,R.color.grey_edittext), PorterDuff.Mode.SRC_ATOP);
        mProceedToPay.setOnClickListener(this);
        mViewInvoice.setOnClickListener(this);
        mViewDetailsLayout.setOnClickListener(this);
    }

    private void setValues() {

        if(invoiceNo!=null && invoiceNo.length()>0)
        {
            mInvoiceNumber.setText("Invoice No : "+invoiceNo);
        }

        if(convenienceRate!=null && convenienceRate.length()>0)
        {
            try {

                Double rate = Double.parseDouble(convenienceRate);
                if(rate>0)
                {}
                else
                {
                    mArrowView.setVisibility(View.INVISIBLE);
                }
            }
            catch (NumberFormatException e)
            {
                mArrowView.setVisibility(View.INVISIBLE);
            }
        }

        if(amountEditable!=null && amountEditable.length()>0)
        {
            if(amountEditable.equalsIgnoreCase("YES") || amountEditable.equalsIgnoreCase("1"))
            {
                mEnterdAmount.setEnabled(true);
            }
            else {
                mEnterdAmount.setEnabled(false);
            }
        }

        if (billStartDate != null && billStartDate.length() > 0) {
            mBillStartDate.setText("Your bill from " + billStartDate);
        }

        if (billEndDate != null && billEndDate.length() > 0) {
            mBillEndDate.setText(" to " + billEndDate);
        }

        if (openingBalance != null && openingBalance.length() > 0) {
            mPreviousDue.setText(openingBalance);
        }

        if (billAmount != null && billAmount.length() > 0) {
            mBillAmount.setText(billAmount);
        }

        if(paymentsDone!=null && paymentsDone.length()>0)
        {
            mPaidAmount.setText(paymentsDone);
        }

        if (invoiceDate != null && invoiceDate.length() > 0) {
            String[] date = invoiceDate.split(" ");
            if (date.length == 3) {
                mInvoiceDay.setText(date[0]);
                mInvoiceMonthYear.setText(" " + date[1] + " " + date[2]);
            } else {
                mInvoiceDay.setText(invoiceDate);
            }
        }

        if (dueDate != null && dueDate.length() > 0) {
            String[] date = dueDate.split(" ");
            if (date.length == 3) {
                mDueDay.setText(date[0]);
                mDueMonthYear.setText(" " + date[1] + " " + date[2]);
            } else {
                mDueDay.setText(dueDate);
            }


        }

        if(daysToDue!=null && daysToDue.length()>0)
        {
            int days = Integer.parseInt(daysToDue);
            if (days > 0) {

                mDaysToDueDate.setText(days+" Days");
                mDueDateStatus.setText(" to Due Date");
            }
            else {
                days *= -1;
                mDaysToDueDate.setText(days+" Days");
                mDueDateStatus.setText(" Overdue");
            }
        }

        if(packageId!=null && packageId.length()>0)
        {
            mCurrentPlan.setText(packageId);
        }

        if(convenienceFee!=null && convenienceFee.length()>0)
        {
            mConvenienceFee.setText("Convenience Fee : "+convenienceFee);
        }

        if(Total!=null && Total.length()>0)
        {
            mEnterdAmount.setText(Total);
            mEnterdAmount.setSelection(mEnterdAmount.getText().toString().trim().length());
            mProceedToPay.setText("Proceed to pay "+getString(R.string.Rs)+Total);
        }
    }



    public void getDateDiffString(Date dateOne)
    {
        long timeOne = dateOne.getTime();
        long timeTwo = System.currentTimeMillis();

        long oneDay = 1000 * 60 * 60 * 24;
        long delta = (timeTwo - timeOne) / oneDay;

        if (delta > 0) {

            mDaysToDueDate.setText(delta+" Days");
            mDueDateStatus.setText(" over Due Date");
        }
        else {
            delta *= -1;
            mDaysToDueDate.setText(delta+" Days");
            mDueDateStatus.setText(" to Due Date");
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id)
        {
            case R.id.proceed_to_pay:

                if (mEnterdAmount.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(PostpayInvoiceSummaryActivity.this, " please enter Payable amount.", Toast.LENGTH_LONG).show();
                    return;
                }

                if ((mEnterdAmount.getText().toString().trim().equalsIgnoreCase("0")) || (mEnterdAmount.getText().toString().trim().equalsIgnoreCase("00"))  ) {
                    Toast.makeText(PostpayInvoiceSummaryActivity.this, "Payable amount cannot be zero, please enter correct amount.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(mEnterdAmount.getText().toString().trim().length()>0)
                {

                    try {
                        Double amount = Double.parseDouble(mEnterdAmount.getText().toString().trim());

                        if(amount>0)
                        {
                            if (type.equalsIgnoreCase(getResources().getString(R.string.invoice_managment))){
                                new GetPayUTxnIdInvoiceMgnt().execute(VariableConstants.PayUTxnId_INVOICE_MGMT);
                            }
                            else {
                                getPayUTxnId();
                            }
                        }
                        else {
                            Toast.makeText(PostpayInvoiceSummaryActivity.this,"Payable amount cannot be less than zero, please enter correct amount.",Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (NumberFormatException e1)
                    {
                        Toast.makeText(PostpayInvoiceSummaryActivity.this,"Please enter correct amount.",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(PostpayInvoiceSummaryActivity.this,"Payable amount cannot be less than zero, please enter correct amount.",Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.view_invoice:
                Intent intent = new Intent(PostpayInvoiceSummaryActivity.this,PostPayInvoice.class);
                intent.putExtra("HTML", invoiceHTML);
                startActivity(intent);
                break;
            case R.id.view_details_layout:

                if(convenienceRate!=null && convenienceRate.length()>0)
                {
                    try {

                        Double rate = Double.parseDouble(convenienceRate);
                        if(rate>0)
                        {
                            if(mDetailsLayout.getVisibility() == View.VISIBLE)
                            {
                                rotate(0, mArrowView);
                                collapse(mDetailsLayout);
                            }
                            else {
                                rotate(180, mArrowView);
                                expand(mDetailsLayout);
                            }
                        }
                        {
                            mArrowView.setVisibility(View.INVISIBLE);
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        mArrowView.setVisibility(View.INVISIBLE);
                    }
                }


                break;

            default:
                break;
        }
    }

    private String makeTwoDigits(int value)
    {
        String data = ""+value;
        if(data.length()==1)
        {
            data = "0"+data;
        }
        return data;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.anim_three, R.anim.anim_four);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_three, R.anim.anim_four);
    }

    private void getPayUTxnId()
    {
        payuDialog = new ProgressDialog(PostpayInvoiceSummaryActivity.this);
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
            jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());
            jsonObj.put("rc_scheduled_date", planActDate);
            jsonObj.put("ent_package", "");

            //replacing the amount edited by user
            if(isAmountEdited)
            {
                try {
                    byte[] data = Base64.decode(encodedString,Base64.DEFAULT);
                    String text = new String(data,"UTF-8");

                    text = text.replace("\"payableAmount\":"+"\""+payableAmount+"\"", "\"payableAmount\":"+"\""+Utility.round(Double.valueOf(mEnterdAmount.getText().toString().trim()))+"\"");

                    double editedConvenienceFee = 0;
                    if(convenienceRate!=null && convenienceRate.length()>0)
                    {
                        double convRate = Double.valueOf(convenienceRate);
                        if(convRate>0)
                        {
                            double amount = Double.parseDouble(mEnterdAmount.getText().toString().trim());
                            editedConvenienceFee = (amount / 100.0f) * Double.parseDouble(convenienceRate);
                            text = text.replace("\"convenienceFee\":"+"\""+convenienceFee+"\"", "\"convenienceFee\":"+"\""+editedConvenienceFee+"\"");
                        }
                    }

                    text = text.replace("\"Total\":"+"\""+Total+"\"", "\"Total\":"+"\""+editedTotalAmount+"\"");

                    byte[] dataEncode = text.getBytes("UTF-8");
                    String base64Encoded = Base64.encodeToString(dataEncode, Base64.DEFAULT);

                    jsonObj.put("ent_act_id", base64Encoded);
                  //  Log.i("Spotsoon", "invGetPayuId edited jsonObj = " + text);
                }
                catch (UnsupportedEncodingException e){
                    jsonObj.put("ent_act_id", encodedString);
                }
            }
            else {
                jsonObj.put("ent_act_id", encodedString);
            }

        }
        catch(Exception e)
        {
        }
        Utility.doJsonRequest(VariableConstants.host_url + "invGetPayuId", jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                // TODO Auto-generated method stub



                try {
                    String PaymentGateway = null;
                    if (jsonResponse != null && jsonResponse.length() > 0) {
                        JSONObject object = new JSONObject(jsonResponse);
                        String errFlag = object.getString("errFlag");
                        if (errFlag != null && errFlag.equalsIgnoreCase("0")) {
                            if (object.has("payuId")) {
                                mPayuId = object.getString("payuId");
                            }
                            if (object.has("PaymentGateway")){
                                 PaymentGateway = object.getString("PaymentGateway");
                            }

                            if(isAmountEdited)
                            {
                                if (PaymentGateway.equalsIgnoreCase("RAZORPAY")){
                                    startRazorPayment("" + Utility.round(editedTotalAmount * 100));
                                }else {
                                    payu(mPayuId, "" + Utility.round(editedTotalAmount), packageId);
                                }


                            }
                            else {
                                if (PaymentGateway.equalsIgnoreCase("RAZORPAY")){
                                    startRazorPayment("" + Utility.round((Double.parseDouble(Total)) * 100));
                                }else {
                                    payu(mPayuId, "" + Utility.round(Double.parseDouble(Total)), packageId);
                                }

                            }
                        } else {

                            if (payuDialog != null) {

                                payuDialog.dismiss();
                                payuDialog = null;
                            }

                            Toast.makeText(PostpayInvoiceSummaryActivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(PostpayInvoiceSummaryActivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                    if (payuDialog != null) {

                        payuDialog.dismiss();
                        payuDialog = null;
                    }
                }

            }

            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub
                Utility.printLog("HomeFrag JSON DATA Error" + error);

                if (payuDialog != null && payuDialog.isShowing()) {

                    payuDialog.dismiss();
                    payuDialog = null;
                }
            }
        });
    }



    private class GetPayUTxnIdInvoiceMgnt extends AsyncTask<String, Void, String> {
        String result1;
        Double invoiceConFee,invoiceAmount;
        ArrayList<NameValuePair> namevaluepairs = new ArrayList<NameValuePair>();



        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            payuDialog = new ProgressDialog(PostpayInvoiceSummaryActivity.this);
            payuDialog.setMessage("Please wait, Redirecting to payment screen...");
            payuDialog.setCancelable(true);
            if(payuDialog!=null) {
                payuDialog.show();
            }
            if(isAmountEdited)
            {
                invoiceConFee = Double.parseDouble(convenienceFee);
                invoiceAmount = Double.parseDouble(mEnterdAmount.getText().toString().trim());
            }
            else {
                invoiceConFee = Double.parseDouble(convenienceFee);
                invoiceAmount = (Double.parseDouble(payableAmount) - Double.parseDouble(convenienceFee));
            }
        }

        @Override
        protected String doInBackground(String... params) {



            namevaluepairs.add(new BasicNameValuePair("deviceID", Utility.getDeviceId(PostpayInvoiceSummaryActivity.this)));
            namevaluepairs.add(new BasicNameValuePair("sessionToken", session.getSession_token()));
            namevaluepairs.add(new BasicNameValuePair("InvoiceNum", invoiceNo));
            namevaluepairs.add(new BasicNameValuePair("Source", "CUSTOMER-APP"));
            namevaluepairs.add(new BasicNameValuePair("Amount", ""+invoiceAmount));
            namevaluepairs.add(new BasicNameValuePair("ConvenienceFee", ""+invoiceConFee));
            namevaluepairs.add(new BasicNameValuePair("PaymentMethod", "ONLINE"));
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

            if (payuDialog != null) {

                payuDialog.dismiss();
                payuDialog = null;
            }


            if (s != null) {
                String PaymentGateway = null;

                try {

                    JSONObject jsonObject = new JSONObject(s);
                    String errNum = jsonObject.getString("errNum");
                    String errFlag = jsonObject.getString("errFlag");
                    String msg = jsonObject.getString("msg");

                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    if (s.length()>0 && errFlag.equalsIgnoreCase("0") && jsonObject1!=null && jsonObject1.length()>0) {
                        if (jsonObject1.has("TransactionId")) {
                            mPayuId = jsonObject1.getString("TransactionId");
                        }
                        if (jsonObject1.has("PaymentGateway")){
                            PaymentGateway = jsonObject1.getString("PaymentGateway");
                        }

                        if(isAmountEdited)
                        {
                            if (PaymentGateway.equalsIgnoreCase("RAZORPAY")){
                                startRazorPayment("" + Utility.round(editedTotalAmount * 100));
                            }else {
                                payu(mPayuId, "" + Utility.round(editedTotalAmount), packageId);
                            }

                        }
                        else {
                            if (PaymentGateway.equalsIgnoreCase("RAZORPAY")){
                                startRazorPayment("" + Utility.round((Double.parseDouble(Total)) * 100 ));
                            }else {
                                payu(mPayuId, "" + Utility.round(Double.parseDouble(Total)), packageId);
                            }

                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PostpayInvoiceSummaryActivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                    if (payuDialog != null) {

                        payuDialog.dismiss();
                        payuDialog = null;
                    }
                }

            } else {
                if (payuDialog != null) {

                    payuDialog.dismiss();
                    payuDialog = null;
                }
            }
        }
    }

    private void payu(String payUId, String totalAmount,String packageId)
    {
        if(payuDialog == null)
        {
            payuDialog = new ProgressDialog(PostpayInvoiceSummaryActivity.this);
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
        payuConfig.setEnvironment(0);

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

        if(null != mPaymentParams.getOfferKey())
            postParamsBuffer.append(concatParams(PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey()));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();
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
                payuHashes.setVasForMobileSdkHash(ServerResponse.getVas_for_mobile_sdk_hash());
                payuHashes.setPaymentRelatedDetailsForMobileSdkHash(ServerResponse.getPayment_related_details_for_mobile_sdk_hash());
                payuHashes.setDeleteCardHash(ServerResponse.getDelete_user_card_hash());
                payuHashes.setStoredCardsHash(ServerResponse.getSave_user_card_hash());
                payuHashes.setEditCardHash(ServerResponse.getEdit_user_card_hash());
                payuHashes.setSaveCardHash(ServerResponse.getSave_user_card_hash());
                payuHashes.setCheckOfferStatusHash(ServerResponse.getCheck_offer_status_hash());
                payuHashes.setCheckIsDomesticHash(ServerResponse.getCheck_isDomestic_hash());

                if (payuDialog != null && payuDialog.isShowing()) {

                    payuDialog.dismiss();
                    payuDialog = null;
                }

                launchSdkUI();
            }

            @Override
            public void onError(String error) {
                System.out.print("onError error = " + error);

                if (payuDialog != null && payuDialog.isShowing()) {

                    payuDialog.dismiss();
                    payuDialog = null;
                }
            }
        });
    }

    private void launchSdkUI()
    {
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
        intent.putExtra(PayuConstants.STORE_ONE_CLICK_HASH, 1);

        System.out.println("ali " + payuHashes.getPaymentHash());
        intent.putExtra(PayuConstants.SALT, "6QJvrdvp");

        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PayuConstants.PAYU_REQUEST_CODE)
        {
            if(data != null ) {

                String resultedstring = data.getStringExtra(PayuConstants.PAYU_RESPONSE);

                if(resultedstring != null && resultedstring.contains("success")) {

                    Intent inventumIntent = new Intent(PostpayInvoiceSummaryActivity.this, InventumRechargeStatusActivity.class);
                    inventumIntent.putExtra("PAYU_ID", mPayuId);
                    inventumIntent.putExtra("TYPE", type);
                    inventumIntent.putExtra("INVOICE", true);
                    startActivity(inventumIntent);
                    overridePendingTransition(R.anim.anim_two, R.anim.anim_one);

                }
                else
                {
                    if (type.equalsIgnoreCase(getResources().getString(R.string.invoice_managment))){
                        Toast.makeText(this,"We are sorry, your transaction failed. Please contact our customer care for any assistance.",Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Toast.makeText(this, "We are sorry, your recharge failed. Please contact our customer care for any assistance.", Toast.LENGTH_SHORT).show();
                    }
                }
            }else {
                if (type.equalsIgnoreCase(getResources().getString(R.string.invoice_managment))) {
                    Toast.makeText(this, "We are sorry, your transaction failed. Please contact our customer care for any assistance.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "We are sorry, your recharge failed. Please contact our customer care for any assistance.", Toast.LENGTH_LONG).show();
                }
            }
        }
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
        if (payuDialog != null) {

            payuDialog.dismiss();
            payuDialog = null;
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

            Toast.makeText(this, "Payment failed:" , Toast.LENGTH_SHORT).show();
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

            payuDialog = new ProgressDialog(PostpayInvoiceSummaryActivity.this);
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
                            Intent inventumIntent = new Intent(PostpayInvoiceSummaryActivity.this, InventumRechargeStatusActivity.class);
                            inventumIntent.putExtra("PAYU_ID", mPayuId);
                            inventumIntent.putExtra("TYPE", type);
                            inventumIntent.putExtra("INVOICE", true);
                            startActivity(inventumIntent);
                            overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
            }
            else {
                Toast.makeText(PostpayInvoiceSummaryActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
}