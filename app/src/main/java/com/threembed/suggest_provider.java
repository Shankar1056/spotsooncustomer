package com.threembed;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.Category_adapter;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.spotsoon.customer.R;
import com.spotsoon.customer.Validators;
import com.threembed.homebean.Data;
import com.threembed.homebean.HomeClass;
import com.threembed.spotasap_pojos.Suggestprovider_pojo;
import com.utility.SpotasManager;
import com.utility.Utility;
import com.utility.VariableConstants;

import org.json.JSONObject;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

/**
 * Created by embed on 19/6/15.
 */
public class suggest_provider extends Fragment {


    CharSequence[] options;
    ArrayList<String> cat=new ArrayList<String>();
    boolean mvalidte=true;
    Button recommend;
    ImageButton Categarydropdown;
    ProgressDialog pDialog,catDialog;
    HomeClass respo_singin;
    ArrayList<Data> dataList;
    EditText business_name,area,phone_number,comments;
    TextView drop_cat;
    private boolean checkbusiness_name,carea=false;
    ListView cat_list;
    Category_adapter category_adapter;
    Spinner spinner;
    Suggestprovider_pojo suggestprovider_pojo;
    RelativeLayout edit_profile;
    SpotasManager SpotasManager;
   // RelativeLayout  comment_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        SpotasManager = new SpotasManager(getActivity());
        Fabric.with(getActivity(), new Crashlytics());
        View view = inflater.inflate(R.layout.suggest_provider,container, false);
        recommend= (Button) view.findViewById(R.id.recommend);
        business_name= (EditText) view.findViewById(R.id.business_name);
        area= (EditText) view.findViewById(R.id.area);
        phone_number= (EditText) view.findViewById(R.id.phone_number);
        comments= (EditText) view.findViewById(R.id.comments);
        comments.setGravity(Gravity.LEFT | Gravity.TOP);
        drop_cat= (TextView) view.findViewById(R.id.drop_cat);
        TextView textRecommend= (TextView) view.findViewById(R.id.textRecommend);

        dataList=new ArrayList<Data>();
        pDialog = new ProgressDialog(view.getContext());
        pDialog.setMessage("Submitting...");
        pDialog.setCancelable(false);

        catDialog=new ProgressDialog(view.getContext());
        catDialog.setMessage(getResources().getString(R.string.loading));
        catDialog.setCancelable(false);

        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        phone_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mvalidte=true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mvalidte=true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                Validators validators = new Validators();
                if(!validators.contac_Status(phone_number.getText().toString())){
                    mvalidte=false;
                }
            }
        });



//
//
//
        drop_cat.setOnClickListener(new  View.OnClickListener(){

            @Override
            public void onClick(View v) {
              //  categoryDialog();
                catDialog.show();
                GetBusinessTypesService();

            }
        });

        recommend.setOnClickListener(new View.OnClickListener(){

            Validators validators = new Validators();


            @Override
            public void onClick(View v) {

                if(drop_cat.getText().toString().equals("")){
                    checkbusiness_name=false;
                }

            if(business_name.getText().toString().equals("")){
                 checkbusiness_name=false;

              }else {
                checkbusiness_name=true;
             }

                if(area.getText().toString().equals("")){
                    carea=false;

                }else {
                    carea=true;
                }

                if(!phone_number.getText().toString().equals("")){

                    if(!validators.contac_Status(phone_number.getText().toString())){
                        mvalidte=false;
                    }

                }

                if(checkbusiness_name && carea){

                        pDialog.show();
                        Suggestprovider();
                }else{
                    Toast.makeText(getActivity(),"Please Fill the Details",Toast.LENGTH_SHORT).show();
                }



            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


    }






    public void Suggestprovider(){

        JSONObject jsonObj = new JSONObject();
        SpotasManager SpotasManager = new SpotasManager(getActivity());

        if (Utility.isNetworkAvailable(getActivity()))
        {


            try {
                jsonObj.put("SessionToken",SpotasManager.getSession_token());
                jsonObj.put("DateTime",Utility.getCurrentDateTimeString());
                jsonObj.put("DeviceId",Utility.getDeviceId(getActivity()));
              //  jsonObj.put("Category",spinner.getSelectedItem().toString());
                jsonObj.put("Category",drop_cat.getText().toString());
                jsonObj.put("BusinessName",business_name.getText().toString());
                jsonObj.put("Area", area.getText().toString());
                jsonObj.put("Contact",phone_number.getText().toString());
                jsonObj.put("Comments",comments.getText().toString());




                Utility.printLog("the sugestprovider catgory param is " + jsonObj);

            } catch (Exception e) {

            }
            //calling doJsonRequest of Utility class

            Utility.doJsonRequest(VariableConstants.host_url1+"SuggestProvider", jsonObj, new Utility.JsonRequestCallback() {
                @Override
                public void onSuccess(String jsonResponse) {
                    // TODO Auto-generated method stub
                    Utility.printLog("provider JSON DATA" + jsonResponse);

                    callproviderResponse(jsonResponse);


                }

                @Override
                public void onError(String error) {
                    // TODO Auto-generated method stub
                    Utility.printLog("HomeFrag JSON DATA Error" + error);

                }

            });
        }
        else
        {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    private void callproviderResponse(String respo){
        pDialog.dismiss();
        Utility.printLog("respo in recommend" + respo);
       // if(Message)
        if(respo!=null){
            try {


                Gson gson = new Gson();
                suggestprovider_pojo = gson.fromJson(respo, Suggestprovider_pojo.class);

                if (suggestprovider_pojo.getMessage().equals("Successful")) {
                    Toast.makeText(getActivity(), "Thanks for sharing, that's very kind of you", Toast.LENGTH_LONG).show();
                    business_name.setText("");
                    area.setText("");
                    drop_cat.setText("");
                    phone_number.setText("");
                    comments.setText("");

                }else if(suggestprovider_pojo.getMessage().equals("Mandatory Field Missing")){

                }
                else {
                    Toast.makeText(getActivity(), "Invalid token, please login or register", Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                Utility.printLog(e+"");

            }
        }


    }

    public void GetBusinessTypesService () {

        /********************************************************/
        JSONObject jsonObj = new JSONObject();
        //SpotasManager SpotasManager = new SpotasManager(getActivity());

        if (Utility.isNetworkAvailable(getActivity()))
        {
            //GPSTracker gpsTracker = new GPSTracker(getActivity());

            try {
                //jsonObj.put("Latitude", gpsTracker.getLatitude()+"");
                //jsonObj.put("Longitude",gpsTracker.getLongitude()+"");

                /*SessionToken
                        DeviceId
                DateTime*/

                jsonObj.put("SessionToken",SpotasManager.getSession_token());
                jsonObj.put("DateTime",Utility.getCurrentDateTimeString());
                jsonObj.put("DeviceId",Utility.getDeviceId(getActivity()));
                jsonObj.put("Latitude", SpotasManager.getCurrentlatitude());
                jsonObj.put("Longitude",SpotasManager.getCurrentlongitude());


            } catch (Exception e) {

            }
            //calling doJsonRequest of Utility class

            Utility.doJsonRequest(VariableConstants.host_url1+"GetBusinessTypes", jsonObj, new Utility.JsonRequestCallback() {
                @Override
                public void onSuccess(String jsonResponse) {
                    // TODO Auto-generated method stub

                    callbusinessResponse(jsonResponse);


                }

                @Override
                public void onError(String error) {
                    // TODO Auto-generated method stub
                    Utility.printLog("HomeFrag JSON DATA Error" + error);

                }

            });
        }
        else
        {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }

    }

    private void callbusinessResponse(String strgServerResponce) {

        Utility.printLog("strgServerResponce" + strgServerResponce);

        final Dialog deleteDialog = new Dialog(getActivity());
        deleteDialog.setTitle("Choose Category");
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.setContentView(R.layout.single_category);
        ListView listView = ((ListView) (deleteDialog.findViewById(R.id.category_list)));

        if(strgServerResponce!=null);
        {
            //JSONObject jsonresponseObject;
                dataList.clear();
                    Gson gson = new Gson();
                    respo_singin = gson.fromJson(strgServerResponce, HomeClass.class);

                      dataList.addAll(respo_singin.getData());


                       Utility.printLog("sizeof"+dataList.size());
                        ArrayList<String> categories=new ArrayList<String>();
//
                          for (int i=0;i<dataList.size();i++){
                              categories.add(respo_singin.getData().get(i).getCategory());


                              Utility.printLog("the params in catname " + respo_singin.getData().get(i).getCategory());
                            }
//
//                     ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,categories);
                    // spinner.setAdapter(adapter);
                if (strgServerResponce != null && dataList.size()>0) {

                    category_adapter = new Category_adapter(getActivity(), categories);
                    listView.setAdapter(category_adapter);

                }

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                                {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                        String cat = parent.getItemAtPosition(position).toString();
                                                        drop_cat.setText(cat);
                                                        Utility.printLog("position"+position+"id"+id+"data");
                                                        deleteDialog.dismiss();
                                                    }
                                                }
                );

            catDialog.dismiss();
                deleteDialog.show();



                //}

        }

    }
}
