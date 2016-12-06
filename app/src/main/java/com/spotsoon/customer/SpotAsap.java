package com.spotsoon.customer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.threembed.MainActivityNew;
import com.threembed.WebDisplay;
import com.threembed.spotasap_pojos.HomeTypesHelper;
import com.threembed.spotasap_pojos.Mobile_Verification;
import com.utility.Readsms;
import com.utility.SpotasManager;
import com.utility.Utility;
import com.utility.VariableConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;

public class SpotAsap extends Activity  implements View.OnClickListener
{
    /**
     * spotasManager is a class which save the data temp
     * the variable for this class is spmanager
     * Button variable are signin,signup which is used to goto Login or Register Screen respectively
     * integer variable _splashTime is used here to show splash screen for defined intervel
     * Boolean variable _active is used to check session is true or not
     * Double variable latLng is used to get the current latitude and longitude
     */
    private SpotasManager spmanager;
    private LinearLayout enterNumberLayout,verifyNumberLayout,signupLayout;
    private Button enterNumberNext,signupDone;
    private EditText mCountryCode,mMobile,mActivationCode,mName,mEmail;
    private TextView emailOtp,acvtivation_title;
    private boolean isLogin;
    private String regId,serverActivationCode;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000 ;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private GoogleCloudMessaging gcm;
    private IntentFilter intentFilter;
    private Readsms readsms;
    private boolean doubleBackToExitPressedOnce = false;
    private int current_page = 1;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.splash_screen_new);
        Utility.statusbar(SpotAsap.this);
        Log.i("Splash Screen","Splash 4");

        spmanager = new SpotasManager(this);
        initializeViews();

        readsms = new Readsms() {
            @Override
            protected void onSmsReceived(String s)
            {
                if(s!=null && s.length()>0)
                {
                    if(s.contains("SpotSoon"))
                    {
                        String splitmsg[] = s.split(" ");
                        mActivationCode.setText(splitmsg[0]);
                    }
                }
            }
        };

        intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(1000);

        if (Utility.isNetworkAvailable(SpotAsap.this)) {
            getRegistrationId();
        }
    }


    private void initializeViews()
    {
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);

        enterNumberLayout = (LinearLayout)findViewById(R.id.ent_mobile_layout);
        verifyNumberLayout = (LinearLayout)findViewById(R.id.verify_number_layout);
        signupLayout = (LinearLayout)findViewById(R.id.signup_layout);

        enterNumberNext = (Button)findViewById(R.id.ent_num_next);
        Button verifyNumberNext = (Button)findViewById(R.id.verify_num_next);
        signupDone = (Button)findViewById(R.id.signup_done);

        mCountryCode = (EditText)findViewById(R.id.et_country_code);
        mMobile = (EditText)findViewById(R.id.et_mobile_code);
        mActivationCode = (EditText)findViewById(R.id.act_code);
        mName = (EditText)findViewById(R.id.et_enter_name);
        mEmail = (EditText)findViewById(R.id.et_enter_email);
        TextView resendOtp = (TextView)findViewById(R.id.resend_otp);
        emailOtp = (TextView)findViewById(R.id.resend_otp_email);
        acvtivation_title =(TextView)findViewById(R.id.tv_sent_conf);
        TextView terms_of_services = (TextView)findViewById(R.id.tv_pp2);
        TextView privacy_policy = (TextView)findViewById(R.id.tv_pp3);
        TextView content_policies = (TextView)findViewById(R.id.tv_pp4);

        mMobile.getBackground().mutate().setColorFilter(Utility.getColor(SpotAsap.this,R.color.grey_edittext), PorterDuff.Mode.SRC_ATOP);
        mCountryCode.getBackground().mutate().setColorFilter(Utility.getColor(SpotAsap.this,R.color.grey_edittext), PorterDuff.Mode.SRC_ATOP);

        enterNumberNext.setOnClickListener(this);
        verifyNumberNext.setOnClickListener(this);
        signupDone.setOnClickListener(this);
        resendOtp.setOnClickListener(this);
        emailOtp.setOnClickListener(this);
        terms_of_services.setOnClickListener(this);
        privacy_policy.setOnClickListener(this);
        content_policies.setOnClickListener(this);



        mMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (mMobile.getText().toString().length() == 10) {

                    enterNumberNext.setBackgroundResource(R.drawable.next_btn_blue);
                } else {
                    enterNumberNext.setBackgroundResource(R.drawable.next_btn_grey);
                }
            }
        });

        mActivationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (mActivationCode.getText().toString().length() == 5) {

                    if (serverActivationCode.equalsIgnoreCase(mActivationCode.getText().toString().trim())) {

                        hideSoftKeyboard();

                        Utility.printLog("isLogin="+isLogin);
                        if (isLogin) {
                            //loginUser();


                            if(Utility.isNetworkAvailable(SpotAsap.this))
                            {
                                if(snackbar!=null && snackbar.isShown())
                                {
                                    snackbar.dismiss();
                                }

                                loginUser();
                            }
                            else
                            {
                                Toast.makeText(SpotAsap.this, "No internet connection", Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                showSnackBar();
                                    }
                                }, 2000);
                            }

                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                            current_page = 3;
                            signupLayout.setVisibility(View.VISIBLE);

                            YoYo.with(Techniques.SlideOutLeft)
                                    .duration(700)
                                    .playOn(findViewById(R.id.verify_number_layout));

                            YoYo.with(Techniques.SlideInRight)
                                    .duration(700)
                                    .playOn(findViewById(R.id.signup_layout));
                        }
                    }, 2000);
                        }
                    } else {
                        Toast.makeText(SpotAsap.this, "Enter Correct OTP", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(mEmail.getText().toString().trim().length() >= 5) {

                    if(isValidEmail(mEmail.getText().toString().trim()))
                    {
                        if(mName.getText().toString().trim().length()>0)
                        {
                            signupDone.setBackgroundResource(R.drawable.next_btn_blue);
                        }
                        else
                        {
                            signupDone.setBackgroundResource(R.drawable.next_btn_grey);
                        }
                    }
                    else
                    {
                        signupDone.setBackgroundResource(R.drawable.next_btn_grey);
                    }
                }
                else
                {
                    signupDone.setBackgroundResource(R.drawable.next_btn_grey);
                }
            }
        });

        if(!spmanager.isLoggedIn())
        {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMobile.requestFocus();
                    enterNumberLayout.setVisibility(View.VISIBLE);

                    YoYo.with(Techniques.SlideInUp)
                            .duration(700)
                            .playOn(findViewById(R.id.ent_mobile_layout));
                }
            }, 2000);


        }

        }



    }



    @Override
    protected void onResume() {
        super.onResume();

        if(intentFilter!=null)
        {
            this.registerReceiver(readsms, intentFilter);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(spmanager.isLoggedIn())
                        {

                            if(Utility.isNetworkAvailable(SpotAsap.this)) {
                                getCategories();

                            }
                            else {
                                Toast.makeText(SpotAsap.this, "No internet connection", Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                    }
                                }, 2000);

                            }
                        }
                        else
                        {
                            if(!(Utility.isNetworkAvailable(SpotAsap.this))) {
                                Toast.makeText(SpotAsap.this, "No internet connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        }).start();

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(intentFilter!=null)
        {
            this.unregisterReceiver(readsms);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ent_num_next:

                hideSoftKeyboard();

                if(mCountryCode.getText().toString().trim().length()>0)
                {
                    if(mMobile.getText().toString().trim().length()==10)
                    {
                        if(Utility.isNetworkAvailable(SpotAsap.this))
                        {
                            if(snackbar!=null && snackbar.isShown())
                            {
                                snackbar.dismiss();
                            }
                            getVerificationCode();
                        }
                        else
                        {
                            showSnackBar();
                        }

                    }
                    else
                    {
                        Toast.makeText(SpotAsap.this,"Please enter 10-digit mobile number",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(SpotAsap.this,"Please enter country code",Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.verify_num_next:

                hideSoftKeyboard();

                if(mActivationCode.getText().toString().length()==5)
                {
                    if(serverActivationCode.equalsIgnoreCase(mActivationCode.getText().toString().trim()))
                    {
                        if(isLogin)
                        {
                            if(Utility.isNetworkAvailable(SpotAsap.this))
                            {
                                if(snackbar!=null && snackbar.isShown())
                                {
                                    snackbar.dismiss();
                                }

                                loginUser();
                            }
                            else
                            {
                                showSnackBar();
                            }
                        }
                        else {
                            current_page = 3;
                            signupLayout.setVisibility(View.VISIBLE);

                            YoYo.with(Techniques.SlideOutLeft)
                                    .duration(700)
                                    .playOn(findViewById(R.id.verify_number_layout));

                            YoYo.with(Techniques.SlideInRight)
                                    .duration(700)
                                    .playOn(findViewById(R.id.signup_layout));
                        }
                    }
                    else {

                        Toast.makeText(SpotAsap.this, "Enter Correct OTP", Toast.LENGTH_LONG).show();
                    }

                }else if(mActivationCode.getText().toString().equals("")){
                    Toast.makeText(SpotAsap.this, "Enter OTP", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(SpotAsap.this, "Enter Valid OTP", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.signup_done:

                hideSoftKeyboard();

                if(mName.getText().toString().trim().length()>0)
                {
                    if(mEmail.getText().toString().trim().length()>0)
                    {
                        if(isValidEmail(mEmail.getText().toString().trim()))
                        {

                            if(Utility.isNetworkAvailable(SpotAsap.this))
                            {
                                if(snackbar!=null && snackbar.isShown())
                                {
                                    snackbar.dismiss();
                                }
                                signupUser();
                            }
                            else
                            {
                                showSnackBar();
                            }
                        }
                        else
                        {
                            Toast.makeText(SpotAsap.this, "Please enter valid Email ID", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(SpotAsap.this,"Please enter Email ID",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(SpotAsap.this,"Please enter Full Name",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.resend_otp:

                if(Utility.isNetworkAvailable(SpotAsap.this))
                {
                    if(snackbar!=null && snackbar.isShown())
                    {
                        snackbar.dismiss();
                    }
                    resendVerificationCode("getVerificationCode");
                }
                else
                {
                    showSnackBar();
                }

                break;
            case R.id.resend_otp_email:

                if(Utility.isNetworkAvailable(SpotAsap.this))
                {
                    if(snackbar!=null && snackbar.isShown())
                    {
                        snackbar.dismiss();
                    }
                    resendVerificationCode("getVerificationCodeViaEmail");
                }
                else
                {
                    showSnackBar();
                }

                break;

            case R.id.tv_pp2:

                gonextActivity("http://www.spotsoon.com/termsconditions/","Terms of Service");

                break;
            case R.id.tv_pp3:

                gonextActivity("http://www.spotsoon.com/privacy-policy/","Privacy Policy");

                break;
            case R.id.tv_pp4:

                gonextActivity("http://www.spotsoon.com/termsconditions/","Content Policiesy");

                break;
        }
    }

    private void gonextActivity( String s, String s1) {

        Intent intent = new Intent(SpotAsap.this,WebDisplay.class);
        intent.putExtra("Link",s);
        intent.putExtra("Title",s1);
        startActivity(intent);
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    private void getVerificationCode()
    {
        final ProgressDialog pDialog = new ProgressDialog(SpotAsap.this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(true);
        if(pDialog!=null) {
            pDialog.show();
        }

        JSONObject jsonObj = new JSONObject();
        try
        {
            Utility.printLog("getVerificationCode url = "+VariableConstants.host_url + "getVerificationCode");
            jsonObj.put("ent_mobile",mMobile.getText().toString().trim());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        Utility.doJsonRequest(VariableConstants.host_url + "getVerificationCode", jsonObj, new Utility.JsonRequestCallback() {
                @Override
            public void onSuccess(String jsonResponse) {

                if(pDialog!=null) {
                    pDialog.dismiss();
                }

                if(jsonResponse!=null) {
                    try {

                        Gson gson = new Gson();
                        Mobile_Verification respo_getcode = gson.fromJson(jsonResponse, Mobile_Verification.class);

                        if(respo_getcode.getErrFlag().equals("0"))
                        {
                            String flag = respo_getcode.getFlag();

                            Utility.printLog("isLogin flag="+flag);
                            if(flag!=null)
                            {
                                acvtivation_title.setText("We've sent an SMS with an activation code to : +91"+mMobile.getText().toString().trim());
                                if(flag.equalsIgnoreCase("1"))//1-signup,0-login
                                {
                                    isLogin = true;
                                    emailOtp.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    isLogin = false;
                                    emailOtp.setVisibility(View.INVISIBLE);
                                }
                            }

                            Utility.printLog("isLogin 1="+isLogin);

                            serverActivationCode = respo_getcode.getCode();

                            verifyNumberLayout.setVisibility(View.VISIBLE);

                            current_page = 2;

                            YoYo.with(Techniques.SlideOutLeft)
                                    .duration(700)
                                    .playOn(findViewById(R.id.ent_mobile_layout));

                            YoYo.with(Techniques.SlideInRight)
                                    .duration(700)
                                    .playOn(findViewById(R.id.verify_number_layout));

                        }
                    } catch (Exception e) {
                        Utility.printLog("give me error" + e);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(String error) {

                if(pDialog!=null) {
                    pDialog.dismiss();
                }

                Utility.printLog("HomeFrag JSON DATA Error" + error);
            }
        });
    }//End of jsonCaller()

    private void resendVerificationCode(final String url)
    {
        final ProgressDialog pDialog = new ProgressDialog(SpotAsap.this);
        pDialog.setMessage("Resending OTP...");
        pDialog.setCancelable(true);
        if(pDialog!=null) {
            pDialog.show();
        }

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("ent_mobile", mMobile.getText().toString().trim());

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        Utility.doJsonRequest(VariableConstants.host_url + url, jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(String jsonResponse) {

                if(pDialog!=null) {
                    pDialog.dismiss();
                }

                if(jsonResponse!=null) {
                    try {
                        Gson gson = new Gson();
                        Mobile_Verification respo_getcode = gson.fromJson(jsonResponse, Mobile_Verification.class);
                        if(respo_getcode.getErrFlag().equals("0"))
                        {

                            serverActivationCode = respo_getcode.getCode();

                            if(url.equalsIgnoreCase("getVerificationCode"))
                            {
                                Toast.makeText(SpotAsap.this,"OTP resent to above phone number",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(SpotAsap.this,"OTP sent to registered Email address",Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(SpotAsap.this, respo_getcode.getErrMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Utility.printLog("give me error" + e);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(String error) {

                if(pDialog!=null) {
                    pDialog.dismiss();
                }

                Utility.printLog("HomeFrag JSON DATA Error" + error);
            }
        });
    }

    private void loginUser()
    {
        final ProgressDialog pDialog = new ProgressDialog(SpotAsap.this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(true);
        if(pDialog!=null) {
            pDialog.show();
        }

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("ent_phone",mMobile.getText().toString().trim());
            jsonObj.put("ent_device_type", "2");
            jsonObj.put("ent_dev_id", Utility.getDeviceId(this));
            if(regId!=null && regId.length()>0)
            {
                spmanager.setPushTokenUpdate(true);
                jsonObj.put("ent_push_token",regId);
            }
            else {
                spmanager.setPushTokenUpdate(false);
            }
            jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());
            jsonObj.put("ent_version","v1.2");

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        Utility.doJsonRequest(VariableConstants.host_url + "slaveSignup", jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(String jsonResponse) {

                if(pDialog!=null) {
                    pDialog.dismiss();
                }
                if(jsonResponse!=null) {
                    try {
                        Gson gson = new Gson();
                        Mobile_Verification respo_getcode = gson.fromJson(jsonResponse, Mobile_Verification.class);
                        if(respo_getcode.getErrFlag().equals("0"))
                        {
                            spmanager.setIsLogin(true);
                            spmanager.setFname(respo_getcode.getFname());
                            spmanager.setEmailId(respo_getcode.getEmail());
                            spmanager.setPhone(mMobile.getText().toString().trim());
                            spmanager.setUserId(respo_getcode.getToken());
                            spmanager.setSession_token(respo_getcode.getToken());
                            spmanager.setProfilePic(respo_getcode.getPic());

                            Intent intent = new Intent(SpotAsap.this, MainActivityNew.class);
                            startActivity(intent);
                            finish();

                        }
                        else {
                            Toast.makeText(SpotAsap.this, respo_getcode.getErrMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Utility.printLog("give me error" + e);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(String error) {

                if(pDialog!=null) {
                    pDialog.dismiss();
                }
                Utility.printLog("HomeFrag JSON DATA Error" + error);
            }
        });
    }//End of jsonCaller()

    private void signupUser()
    {
        final ProgressDialog pDialog = new ProgressDialog(SpotAsap.this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(true);
        if(pDialog!=null) {
            pDialog.show();
        }

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("ent_phone",mMobile.getText().toString().trim());
            jsonObj.put("ent_password",mActivationCode.getText().toString().trim());
            jsonObj.put("ent_first_name",mName.getText().toString().trim());
            jsonObj.put("ent_email", mEmail.getText().toString().trim());
            jsonObj.put("ent_device_type", "2");
            jsonObj.put("ent_dev_id", Utility.getDeviceId(SpotAsap.this));

            if(regId!=null && regId.length()>0)
            {
                spmanager.setPushTokenUpdate(true);
                jsonObj.put("ent_push_token",regId);
            }
            else {
                spmanager.setPushTokenUpdate(false);
            }

            jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());
            jsonObj.put("ent_version", "v1.2");

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        Utility.doJsonRequest(VariableConstants.host_url + "slaveSignup", jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(String jsonResponse) {

                /*if(pDialog!=null) {
                    pDialog.dismiss();
                }*/
                pDialog.dismiss();
                if(jsonResponse!=null) {
                    try {
                        Gson gson = new Gson();
                        Mobile_Verification respo_getcode = gson.fromJson(jsonResponse, Mobile_Verification.class);
                        if(respo_getcode.getErrFlag().equals("0"))
                        {

                            spmanager.setIsLogin(true);
                            spmanager.setFname(mName.getText().toString().trim());
                            spmanager.setEmailId(mEmail.getText().toString().trim());
                            spmanager.setPhone(mMobile.getText().toString().trim());
                            spmanager.setUserId(respo_getcode.getToken());
                            spmanager.setSession_token(respo_getcode.getToken());
                            spmanager.setProfilePic(respo_getcode.getPic());

                            Intent intent = new Intent(SpotAsap.this, MainActivityNew.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(SpotAsap.this, respo_getcode.getErrMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Utility.printLog("give me error" + e);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(String error) {

                if(pDialog!=null) {
                    pDialog.dismiss();
                }
                Utility.printLog("HomeFrag JSON DATA Error" + error);
            }
        });
    }//End of jsonCaller()

    private String getRegistrationId()
    {
        final SharedPreferences prefs = getGCMPreferences(this);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(this);

        if (registeredVersion != currentVersion)
        {
            new GCMRegistration().execute();
            return "";
        }
        else
        {
            if (registrationId.isEmpty())
            {
                new GCMRegistration().execute();
                return "";
            }
            else
            {
                regId = registrationId;

            }
        }
        return regId;
    }
    /************************************************************************/

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context)
    {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(SpotAsap.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /********************************************************************************/
    private class GCMRegistration extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... params)
        {
            try {
                if(gcm == null)
                {
                    gcm = GoogleCloudMessaging.getInstance(SpotAsap.this);
                //    Log.d("GCMRegistration  gcm " + gcm, "Success");
                }
                regId = gcm.register(VariableConstants.SENDER_ID_PUSH);


                // You should send the registration ID to your server over HTTP,
                // so it can use GCM/HTTP or CCS to send messages to your app.
                // The request to your server should be authenticated if your app
                // is using accounts

                // For this demo: we don't need to send it because the device
                // will send upstream messages to a server that echo back the
                // message using the 'from' address in the message.

                // Persist the regID - no need to register again.
                //storeRegistrationId(LoginActivity.this, regid);
                //this.params=params;
            }
            catch (IOException ex)
            {
                Utility.printLog("GCMRegistration doInBackground   msg "+ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Utility.printLog("REGISTRATION ID IS"+regId);
            if(regId==null||"".equals(regId))
            {
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        if(current_page == 1)
        {
            if(doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(SpotAsap.this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
        else if(current_page == 2)
        {

            current_page = 1;

            mActivationCode.setText("");

            YoYo.with(Techniques.SlideInLeft)
                    .duration(700)
                    .playOn(findViewById(R.id.ent_mobile_layout));

            YoYo.with(Techniques.SlideOutRight)
                    .duration(700)
                    .playOn(findViewById(R.id.verify_number_layout));
        }
        else if(current_page == 3)
        {

            current_page = 2;

            YoYo.with(Techniques.SlideInLeft)
                    .duration(700)
                    .playOn(findViewById(R.id.verify_number_layout));

            YoYo.with(Techniques.SlideOutRight)
                    .duration(700)
                    .playOn(findViewById(R.id.signup_layout));
        }
    }







    private void getCategories()
    {


        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("ent_dev_id", Utility.getDeviceId(SpotAsap.this));
            jsonObj.put("ent_sess_token", spmanager.getSession_token());
            jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());


        } catch (JSONException e) {
            e.printStackTrace();
            Utility.printLog("printStackTrace " + e);
        }

        //GetBusinessTypes
        Utility.doJsonRequest(VariableConstants.host_url + "getCategories", jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(String jsonResponse) {


                try {

                    Gson gson = new Gson();
                    HomeTypesHelper typesResponse = gson.fromJson(jsonResponse, HomeTypesHelper.class);
                    if(typesResponse != null) {
                        if (typesResponse.getErrFlag() != null && typesResponse.getErrFlag().equalsIgnoreCase("0")) {

                            Intent intent = new Intent(SpotAsap.this, MainActivityNew.class);
                            intent.putExtra("HAS_VALUE",true);
                            intent.putExtra("VALUE",jsonResponse);
                            startActivity(intent);
                            finish();

                        } else if (typesResponse.getErrNum() != null && typesResponse.getErrNum().equalsIgnoreCase("101")) {

                            spmanager.setIsLogin(false);

                            Toast.makeText(SpotAsap.this, typesResponse.getErrMsg(), Toast.LENGTH_LONG).show();
                            Intent intent1 = new Intent(SpotAsap.this, SpotAsap.class);
                            startActivity(intent1);
                            finish();
                        }
                        else
                        {
                            Intent intent = new Intent(SpotAsap.this, MainActivityNew.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else
                    {
                        Intent intent = new Intent(SpotAsap.this, MainActivityNew.class);
                        startActivity(intent);
                        finish();
                    }

                }
                catch (Exception e)
                {
                    Intent intent = new Intent(SpotAsap.this, MainActivityNew.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onError(String error) {

                Intent intent = new Intent(SpotAsap.this, MainActivityNew.class);
                startActivity(intent);
                finish();

                Utility.printLog("HomeFragmentNew JSON DATA Error" + error);
            }
        });
    }

    private void showSnackBar()
    {
        snackbar = Snackbar
                .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(Utility.isNetworkAvailable(SpotAsap.this))
                        {
                        }
                        else
                        {
                            snackbar.dismiss();
                            showSnackBar();
                        }
                    }
                });
        // Changing message text color
        snackbar.setActionTextColor(Color.RED);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}