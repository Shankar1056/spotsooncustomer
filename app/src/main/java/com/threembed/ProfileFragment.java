package com.threembed;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.spotsoon.customer.R;
import com.spotsoon.customer.SpotAsap;
import com.spotsoon.customer.Validators;
import com.threembed.spotasap_pojos.Mobile_Verification;
import com.threembed.spotasap_pojos.MyProfile_pojo;
import com.utility.SpotasManager;
import com.utility.Utility;
import com.utility.VariableConstants;

import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

/**
 * Created by embed on 28/5/15.
 * for profile
 */

public class ProfileFragment extends Fragment
{   private ProgressDialog pDialog;
    private Button profile_button;
    private TextView profilenametv;
    private SpotasManager SpotasManager;
    private EditText profile_name,profile_email,profile_number;
    private MyProfile_pojo MyProfile_pojo;
    private Validators validators= new Validators();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("");
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_profile);

        RelativeLayout edit_profile= (RelativeLayout) getActivity().findViewById(R.id.edit_profile);
        edit_profile.setVisibility(View.VISIBLE);
        TextView headerpagetv = (TextView) getActivity().findViewById(R.id.headerpagetv);
        headerpagetv.setText(R.string.myprofile);

        SpotasManager = new SpotasManager(getActivity());
        LayoutInflater inflator = LayoutInflater.from(getActivity());

        Fabric.with(getActivity(), new Crashlytics()); //Shankar Kumar


        View view = inflator.inflate(R.layout.profilefragment, null);

        profile_name= (EditText) view.findViewById(R.id.profile_name);
       // profilenametv=(TextView) getActivity().findViewById(R.id.profilenametv);
        profile_email= (EditText) view.findViewById(R.id.profile_email);
        profile_number= (EditText) view.findViewById(R.id.profile_number);
        profile_button= (Button) view.findViewById(R.id.profile_button);
        profile_button.setText("LOG OUT");

        pDialog = new ProgressDialog(view.getContext());
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);

        pDialog.show();
        profileService();
        edit_profile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                profile_button.setVisibility(View.VISIBLE);
                profile_button.setText("UPDATE");

                profile_email.setEnabled(true);
                profile_email.setInputType(InputType.TYPE_CLASS_TEXT);
                profile_email.setFocusable(true);
                profile_name.setEnabled(true);
                profile_name.setInputType(InputType.TYPE_CLASS_TEXT);
                profile_name.setFocusable(true);
                profile_number.setEnabled(true);
                profile_number.setInputType(InputType.TYPE_CLASS_TEXT);
                profile_number.setFocusable(true);

            }
        });


        profile_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if ("UPDATE".equals(profile_button.getText().toString())) {

                    boolean flag = true;
                    if (profile_name.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Enter Name", Toast.LENGTH_LONG).show();
                        flag = false;
                    } else if (!validators.lastName_status(profile_name.getText().toString())) {
                        flag = false;
                        Toast.makeText(getActivity(), "Invalid Name", Toast.LENGTH_LONG).show();
                    } else if (profile_email.getText().toString().equals("")) {
                        flag = false;
                        Toast.makeText(getActivity(), "Enter Email", Toast.LENGTH_LONG).show();
                    } else if (!validators.emailId_status(profile_email.getText().toString())) {
                        flag = false;
                        Toast.makeText(getActivity(), "Invalid Email", Toast.LENGTH_LONG).show();
                    } else if (profile_number.getText().toString().equals("")) {
                        flag = false;
                        Toast.makeText(getActivity(), "Enter Phone No", Toast.LENGTH_LONG).show();
                    } else if (!validators.contac_Status(profile_number.getText().toString())) {
                        flag = false;
                        Toast.makeText(getActivity(), "Invalid Phone No", Toast.LENGTH_LONG).show();
                    }

                    if (flag) {
                        pDialog.setMessage("Updating...");
                        pDialog.show();

                        updateprofile();

                        profile_button.setVisibility(View.GONE);
                        profile_button.setText("LOG OUT");

                    }
                } else if ("LOG OUT".equals(profile_button.getText().toString())) {

                    logoutservice();
                }
            }

        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();

    }

    ////////////////////////
    public void updateprofile(){

        JSONObject jsonObj = new JSONObject();
        SpotasManager SpotasManager = new SpotasManager(getActivity());

        if (Utility.isNetworkAvailable(getActivity()))
        {
            try {

                if(profile_name.getText().toString().trim().contains(" ")){
                    String[]  name= profile_name.getText().toString().split(" ");
                    jsonObj.put("ent_first_name", name[0]);
                    jsonObj.put("ent_last_name", name[1]);
                }else {
                    String  fullname=profile_name.getText().toString();
                    jsonObj.put("ent_first_name",fullname);
                    jsonObj.put("ent_last_name","");

                }
                jsonObj.put("ent_dev_id", Utility.getDeviceId(getActivity()));
                jsonObj.put("ent_email",profile_email.getText().toString());
                jsonObj.put("ent_phone", profile_number.getText().toString());
                jsonObj.put("ent_sess_token", SpotasManager.getSession_token());
                jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());

                Utility.printLog("the profparam is " + jsonObj);

            } catch (Exception e) {
                e.printStackTrace();

            }
            //calling doJsonRequest of Utility class

            Utility.doJsonRequest(VariableConstants.host_url+"updateProfile", jsonObj, new Utility.JsonRequestCallback() {
                @Override
                public void onSuccess(String jsonResponse) {

                    callProfileupdateResponse(jsonResponse);
                    Utility.printLog("updateprof JSON DATA" + jsonResponse);



                }

                @Override
                public void onError(String error) {
                    Utility.printLog("updateprof JSON DATA Error" + error);

                }

            });
        }
        else
        {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }

    }

    private void callProfileupdateResponse(String jsonResponse) {

        pDialog.dismiss();
        try
        {
            Gson gson = new Gson();
            MyProfile_pojo = gson.fromJson(jsonResponse, MyProfile_pojo.class);
            if(MyProfile_pojo.getErrNum().equals("54")) {

                profile_name.setText(profile_name.getText().toString());
                profile_email.setText(profile_email.getText().toString());
                profile_number.setText(profile_number.getText().toString());
                profile_name.setEnabled(false);
                profile_email.setEnabled(false);
                profile_number.setEnabled(false);

                //String img_url = VariableConstants.image_path+spManager.getProfilePic();
                profilenametv.setText(profile_name.getText().toString());



                Toast.makeText(getActivity(), "Profile Updated", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getActivity(), "Invalid token, please login or register", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            Utility.printLog(e+"");
        }

    }



    public void profileService () {

        /********************************************************/

        JSONObject jsonObj = new JSONObject();


        if (Utility.isNetworkAvailable(getActivity()))
        {
            try {


                jsonObj.put("ent_dev_id", Utility.getDeviceId(getActivity()));

                jsonObj.put("ent_sess_token", SpotasManager.getSession_token());
                jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());




            } catch (Exception e) {

            }
            //calling doJsonRequest of Utility class

            Utility.doJsonRequest(VariableConstants.host_url+"getProfile", jsonObj, new Utility.JsonRequestCallback() {
                @Override
                public void onSuccess(String jsonResponse) {
                    Utility.printLog("Profile JSON DATA" + jsonResponse);

                    try {
                        callProfileResponse(jsonResponse);
                    }
                    catch (Exception e)

                    {
                        e.printStackTrace();
                    }
                    Utility.printLog("the json object response is in get Profile  ::" + jsonResponse);

                }

                @Override
                public void onError(String error) {
                    Utility.printLog("HomeFrag JSON DATA Error" + error);

                }

            });
        }
        else
        {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }

    }



    private void callProfileResponse(String jsonResponse) {


        pDialog.dismiss();

        Gson gson = new Gson();
        MyProfile_pojo = gson.fromJson(jsonResponse, MyProfile_pojo.class);
        if(MyProfile_pojo.getErrNum().equals("33")) {

            // Toast.makeText(getActivity(), "Response" + response, Toast.LENGTH_LONG).show();
            profile_name.setText(MyProfile_pojo.getfName() + ' ' + MyProfile_pojo.getlName());
            profile_email.setText(MyProfile_pojo.getEmail());
            profile_number.setText(MyProfile_pojo.getPhone());
            //profile_image.setb



        }
        else {
            Toast.makeText(getActivity(), "Invalid token, please login or register", Toast.LENGTH_LONG).show();
        }


    }

    ///////////////Logout service//////////////
    private void logoutservice(){
     if(pDialog!=null)
     {
       pDialog.show();
     }
        JSONObject jsonObj= new JSONObject();

        if(Utility.isNetworkAvailable(getActivity()))
        {
            try {
                jsonObj.put("ent_sess_token", SpotasManager.getSession_token());
                jsonObj.put("ent_dev_id",Utility.getDeviceId(getActivity()));
                jsonObj.put("ent_user_type","2");
                jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());


            }catch (Exception e){

            }
            Utility.doJsonRequest(VariableConstants.host_url+"logout",jsonObj,new Utility.JsonRequestCallback(){

                @Override
                public void onSuccess(String result) {
                    Utility.printLog("logout responce"+result);

                    logoutreponse(result);

                }

                @Override
                public void onError(String error) {
                    Utility.printLog("HomeFrag JSON DATA Error" + error);
                }
            });

        }else {
            Toast.makeText(getActivity(),"No Internet Connection ",Toast.LENGTH_LONG).show();
        }

    }


    private void logoutreponse(String reponse){
        if(pDialog!=null) {
            pDialog.dismiss();
        }


        Gson gson=new Gson();
        Mobile_Verification log_out_pojo=gson.fromJson(reponse,Mobile_Verification.class);
        if (log_out_pojo.getErrFlag().equals("0")){

            /**
             * Notifying Notification manager as App is alive*/
            VariableConstants.isAppalive=false;

            Intent intent1=new Intent(getActivity(), SpotAsap.class);
            SpotasManager manager=new SpotasManager(getActivity());
            manager.clearSession();
            manager.setIsLogin(false);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent1);
            getActivity().finish();
        }
        else {
            Toast.makeText(getActivity(),"Oops! Something went wrong Please try again.",Toast.LENGTH_LONG).show();
        }
    }
}