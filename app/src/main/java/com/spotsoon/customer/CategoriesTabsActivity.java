package com.spotsoon.customer;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.threembed.spotasap_pojos.HomeTypesHelper;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by VARUN on 5/1/2016.
 */
public class CategoriesTabsActivity extends AppCompatActivity{

    private ViewPager viewPager;
    private ImageView businessBanner,businessPic;
    private String CategoriesResponseString,latitude,longitude;
    private HomeTypesHelper categoriesResponse;
    private int defaultPosition;
    private String businessesString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(CategoriesTabsActivity.this, new Crashlytics());
        setContentView(R.layout.categories_tabs);

        Toolbar mToolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar_package);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setTitle("");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        Intent intent = getIntent();
        if(intent!=null) {
            CategoriesResponseString = intent.getStringExtra("RESPONSE");
            latitude = intent.getStringExtra("LATITUDE");
            longitude = intent.getStringExtra("LONGITUDE");
            defaultPosition = intent.getIntExtra("POSITION",0);
        }

        if(CategoriesResponseString!=null && CategoriesResponseString.length()>0)
        {
            Gson gson = new Gson();
            categoriesResponse = gson.fromJson(CategoriesResponseString, HomeTypesHelper.class);

            if(categoriesResponse.getBusinessarr()!=null && categoriesResponse.getBusinessarr().size()>0)
            {
                String arr = "";

                for(int i=0;i<categoriesResponse.getBusinessarr().size();i++)
                {
                    arr = arr + categoriesResponse.getBusinessarr().get(i)+",";
                }

                if(arr!=null && arr.length()>0 && arr.charAt(arr.length()-1) == ',')
                {
                    arr = arr.substring(0,arr.length()-1);
                }

                businessesString = arr;
            }

            setupViewPager(viewPager,categoriesResponse);
            tabLayout.setupWithViewPager(viewPager);
        }

        initializeViews();

        viewPager.setCurrentItem(defaultPosition);
        selectBusinessBanner(categoriesResponse.getCategories().get(defaultPosition).getBanner());
        selectBusinessPicture(categoriesResponse.getCategories().get(defaultPosition).getImage());

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());

                selectBusinessBanner(categoriesResponse.getCategories().get(tab.getPosition()).getBanner());
                selectBusinessPicture(categoriesResponse.getCategories().get(tab.getPosition()).getImage());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
        businessBanner = (ImageView)findViewById(R.id.business_banner);
        businessPic = (ImageView)findViewById(R.id.business_pic);
    }

    private void selectBusinessPicture(String  businessImage)
    {
        if(businessImage!=null && businessImage.length()>0)
        {
            Picasso.with(this).load(businessImage)
                    //.transform(new RoundTransform(10, 0))
                    .fit()
                    .into(businessPic);
        }
        else
        {
            Picasso.with(this)
                    .load(R.drawable.launcher)
                            //.transform(new RoundTransform(10, 0))
                    .fit()
                    .into(businessPic);
        }
    }

    private void selectBusinessBanner(String  bannerUrl)
    {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int banner_width = metrics.widthPixels;
        int banner_height = metrics.heightPixels;

        RelativeLayout.LayoutParams llparams = new RelativeLayout.LayoutParams(banner_width,  (banner_height/3)+20);
        businessBanner.setLayoutParams(llparams);
        //businessBanner.setBackgroundResource(R.drawable.default_banner_image);
        businessBanner.setBackgroundResource(R.color.banner_background);

        if(bannerUrl!=null && bannerUrl.length()>0)
        {
            Picasso.with(this)
                    .load(bannerUrl)
                    .into(businessBanner);
        } else
        {
        }
    }

    private void setupViewPager(ViewPager viewPager, HomeTypesHelper response) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for(int i=0;i<response.getCategories().size();i++)
        {
            adapter.addFragment(new CategoriesTabFragment().newInstance(i,
                            response.getCategories().get(i).getId(),
                            response.getCategories().get(i).getCategory(),
                            latitude,
                            longitude,
                            response.getCategories().get(i).getBanner(),
                            businessesString),

                    response.getCategories().get(i).getCategory());
        }
        viewPager.setAdapter(adapter);
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
}
