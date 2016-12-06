package com.spotsoon.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.threembed.homebean.CatagoriData;
import com.utility.RoundTransform;
import com.utility.SpotasManager;
import com.utility.Utility;
import com.utility.VariableConstants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by VARUN on 4/16/2016.
 */
public class PackagesActivity extends AppCompatActivity{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static String bannerUrl,logo,businessId,businessName,businessImage,businessAddress,getCustomerNotesCheck,customerNotes,connectedFlag,showAddress,userName;
    private SpotasManager session;
    public static CatagoriData response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(PackagesActivity.this, new Crashlytics());
        setContentView(R.layout.package_details);

        Toolbar mToolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar_package);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        Intent intent = getIntent();
        if(intent!=null)
        {
            bannerUrl = intent.getStringExtra("BANNER");
            logo = intent.getStringExtra("Image");
            businessId = intent.getStringExtra("businessId");
            businessName = intent.getStringExtra("BusinessName");
            businessImage = intent.getStringExtra("BusinessImage");
            businessAddress = intent.getStringExtra("BusinessAddress");
            customerNotes = intent.getStringExtra("CustomerNotes");
            connectedFlag = intent.getStringExtra("ConnectedFlag");
            getCustomerNotesCheck = getIntent().getStringExtra("getCustomerNotesCheck");
            showAddress = getIntent().getStringExtra("SHOW_ADDRESS");
            userName = getIntent().getStringExtra("USER_NAME");


            if(businessImage == null || businessImage.length() == 0)
            {
                businessImage = logo;
            }
        }

        initialize();
        getPackagesDetails();
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

    private void initialize()
    {

        session = new SpotasManager(PackagesActivity.this);
        ImageView businessBanner = (ImageView)findViewById(R.id.business_banner);
        ImageView businessPic = (ImageView)findViewById(R.id.business_pic);
        TextView connect_status = (TextView)findViewById(R.id.connect_status);
        TextView  merchant_name = (TextView)findViewById(R.id.merchant_name);
        TextView merchant_address = (TextView)findViewById(R.id.merchant_address);

        if(businessName!=null && businessName.length()>0)
        {
            merchant_name.setText(businessName);
        }
        else {
            merchant_name.setVisibility(View.GONE);
        }

        if(businessAddress!=null && businessAddress.length()>0)
        {
            merchant_address.setText(businessAddress);
        }
        else {
            merchant_address.setVisibility(View.GONE);
        }

        if(connectedFlag!=null && connectedFlag.length()>0)
        {
            if(connectedFlag.equalsIgnoreCase("1"))
            {
                connect_status.setVisibility(View.VISIBLE);
            }
            else {
                connect_status.setVisibility(View.GONE);
            }
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int banner_width = metrics.widthPixels;
        int banner_height = metrics.heightPixels;

        RelativeLayout.LayoutParams llparams = new RelativeLayout.LayoutParams(banner_width,  (int)(banner_height/2.8));
        businessBanner.setLayoutParams(llparams);
        businessBanner.setBackgroundResource(R.color.banner_background);

        if(bannerUrl!=null && bannerUrl.length()>0)
        {
            Picasso.with(this)
                    .load(bannerUrl)
                    .into(businessBanner);
        } else
        {
        }

        if(businessImage!=null && businessImage.length()>0)
        {
            Picasso.with(this).load(businessImage)
                    .transform(new RoundTransform(10, 0))
                    .fit()
                    .into(businessPic);
        }
        else
        {
            Picasso.with(this)
                    .load(R.drawable.launcher)
                    .transform(new RoundTransform(10, 0))
                    .fit()
                    .into(businessPic);
        }

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<Fragment>();
        private final List<String> mFragmentTitleList = new ArrayList<String>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void getPackagesDetails()
    {
        final ProgressDialog pDialog = new ProgressDialog(PackagesActivity.this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(true);
        if(pDialog!=null) {
            pDialog.show();
        }


        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("BusinessId", businessId);
            jsonObj.put("SessionToken", session.getSession_token());
            jsonObj.put("DeviceId", Utility.getDeviceId(PackagesActivity.this));
            jsonObj.put("DateTime", Utility.getCurrentDateTimeString());

            Utility.printLog("the params in productlist jsonObj :: " + jsonObj);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Utility.doJsonRequest(VariableConstants.host_url1 + "BusinessDetails", jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(String jsonResponse) {

                Gson gson = new Gson();
                response = gson.fromJson(jsonResponse, CatagoriData.class);
                if(response!=null && response.getData()!=null)
                {
                    if(response.getData().size()>0)
                    {
                        setupViewPager(viewPager,response);
                        tabLayout.setupWithViewPager(viewPager);
                    }
                }

                if(pDialog!=null)
                {
                    pDialog.dismiss();
                }
            }

            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub

                if(pDialog!=null)
                {
                    pDialog.dismiss();
                }
            }
        });
    }

    private void setupViewPager(ViewPager viewPager, CatagoriData response) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for(int i=0;i<response.getData().get(0).getCategories().size();i++)
        {
            adapter.addFragment(new PackageFragment().newInstance(i), response.getData().get(0).getCategories().get(i).getCategory());
        }
        viewPager.setAdapter(adapter);
    }
}
