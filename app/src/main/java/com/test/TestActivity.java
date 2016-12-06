package com.test;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.spotsoon.customer.R;

import org.apache.http.NameValuePair;

import java.util.ArrayList;

/**
 * Created by Shankar on 8/25/2016.
 */
public class TestActivity extends AppCompatActivity {
    public static ArrayList<TestItemModels> subscriptions = new ArrayList<TestItemModels>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        new RequestFor_Invoice_Mgmt_Subscriptions().execute();
    }
    private class RequestFor_Invoice_Mgmt_Subscriptions extends AsyncTask<String, Void, String> {
        String result1;
        ArrayList<NameValuePair> namevaluepairs = new ArrayList<NameValuePair>();
        final ProgressDialog pDialog = new ProgressDialog(TestActivity.this);

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



            try {
                result1 ="{ \"Employee\" :[{\"name\":\"Shankar\",\"email\":\"shankar@gmail.com\",\"contact\":\"1234567890\"},{\"name\":\"Shankar\",\"email\":\"shankar@gmail.com\",\"contact\":\"1234567890\"},{\"name\":\"Shankar\",\"email\":\"shankar@gmail.com\",\"contact\":\"1234567890\"}] }";

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
                    Gson gson = new Gson();
                    final TestModel testModel = gson.fromJson(s, TestModel.class);

                    subscriptions = testModel.getTestModels();
                        String name = subscriptions.get(0).getName();
                        String email = subscriptions.get(0).getEmail();
                        String contact =subscriptions.get(0).getContact();

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
}
