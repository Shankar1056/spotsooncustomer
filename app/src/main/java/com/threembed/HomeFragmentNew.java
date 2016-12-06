package com.threembed;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.invoice.InvoiceManagementPackageList;
import com.spotsoon.customer.CategoriesTabsActivity;
import com.spotsoon.customer.CircleTransform;
import com.spotsoon.customer.InventumActivity;
import com.spotsoon.customer.PackagesActivity;
import com.spotsoon.customer.PostpayInvoiceSummaryActivity;
import com.spotsoon.customer.R;
import com.spotsoon.customer.SearchAddressGooglePlacesActivity;
import com.spotsoon.customer.SelectPackageActivity;
import com.spotsoon.customer.SpotAsap;
import com.spotsoon.customer.SubscriptionsSummaryAvtivity;
import com.spotsoon.customer.TacitineSummaryActivity;
import com.squareup.picasso.Picasso;
import com.threembed.homebean.InvoiceManagementPackageListModel;
import com.threembed.spotasap_pojos.HomeTypesHelper;
import com.threembed.spotasap_pojos.InvoiceDetailsHelper;
import com.threembed.spotasap_pojos.PlansCouponsDetails;
import com.threembed.spotasap_pojos.Subscriptions;
import com.threembed.spotasap_pojos.SubscriptionsDetailsHelper;
import com.threembed.spotasap_pojos.TypesDetailsHelper;
import com.utility.GeoDecoder;
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
import java.util.Date;

import io.fabric.sdk.android.Fabric;

/**
 * Created by VARUN on 4/3/2016.
 */
public class HomeFragmentNew extends Fragment implements LocationListener,AdapterView.OnItemClickListener{

    private SpotasManager spotasManager;
    private ArrayList<TypesDetailsHelper> typesList = new ArrayList<TypesDetailsHelper>();
    private ArrayList<TypesDetailsHelper> prevBookingsList = new ArrayList<TypesDetailsHelper>();
    private ListView listView;
    private LinearLayout headerLayout,headerLayout_home_parent;
    private TextView localitytv;//roadtv,
    private RelativeLayout no_bookings;
    private static double[] latLng = new double[]{0,0};
    private boolean isCheckForLocation = true;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar snackbar;

    protected LocationManager locationManager;
    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;
    protected static final int RESULT_SPEECH = 2;
    public static ArrayList<SubscriptionsDetailsHelper> subscriptions = new ArrayList<SubscriptionsDetailsHelper>();
    private String categoriesResponse;
    private String purchase_new_pack = null;
    private boolean showmore = true;
    ArrayList<InvoiceManagementPackageListModel> invoicepackageList = new ArrayList<InvoiceManagementPackageListModel>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle bundle = getActivity().getIntent().getExtras();
        if(bundle!=null)
        {
          Boolean  hasHomeValues = bundle.getBoolean("HAS_VALUE", false);

            if(hasHomeValues)
            {
                categoriesResponse = bundle.getString("VALUE");
            }
        }
        else {
        }


        return inflater.inflate(R.layout.homepagenew_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Fabric.with(getActivity(), new Crashlytics());
        spotasManager = new SpotasManager(getActivity());

        listView = (ListView)view.findViewById(R.id.connected_list);
        listView.setOnItemClickListener(this);
        headerLayout_home_parent = (LinearLayout)view.findViewById(R.id.headerLayout_home_parent);
        no_bookings = (RelativeLayout)view.findViewById(R.id.no_bookings);
        headerLayout_home_parent.setVisibility(View.GONE);
        no_bookings.setVisibility(View.GONE);
        coordinatorLayout = (CoordinatorLayout)view.findViewById(R.id.coordinatorLayout);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.home_headerview, listView, false);
        headerLayout = (LinearLayout)header.findViewById(R.id.headerLayout);
        listView.addHeaderView(header, null, false);

        intializeactionbar();

        if(Utility.isNetworkAvailable(getActivity()))
        {
            if(categoriesResponse!=null && categoriesResponse.length()>0)
            {
                showHomeDetails(categoriesResponse,view);
            }
            else {

                getCategories(view);
            }
        }
        else
        {
            showSnackBar();
        }
    }

    private void showSnackBar()
    {
        snackbar = Snackbar
                .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(Utility.isNetworkAvailable(getActivity()))
                        {
                            getCategories(view);
                            getCurrentLocation();
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

    private void intializeactionbar() {

        Utility.statusbar(getActivity());



        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("");
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_search_address);

        localitytv = (TextView) getActivity().findViewById(R.id.localitytv);
      LinearLayout search_location = (LinearLayout) getActivity().findViewById(R.id.search_location);

        search_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.printLog("On Click cliked");
                spotasManager.setSavingsearchedadress("Homefragment");
                Intent addressIntent = new Intent(getActivity(), SearchAddressGooglePlacesActivity.class);
                addressIntent.putExtra("curlat", latLng[0]);
                addressIntent.putExtra("curlong", latLng[1]);
                VariableConstants.showmylocation = true;
                startActivityForResult(addressIntent, 20);
                getActivity().overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();

        if(locationManager!=null)
            locationManager.removeUpdates(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(Utility.isNetworkAvailable(getActivity()))
        {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation()
    {
        if(isCheckForLocation) {

            if(latLng[0] != 0 && latLng[1] != 0)
            {

                isCheckForLocation = false;

                spotasManager.setCurrentlatitude("" + latLng[0]);
                spotasManager.setCurrentlongitude("" + latLng[1]);

                getaddress();
            }
            else
            {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if(getActivity() == null)
                            return;

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                double currentLatitude = 0, currentLongitude = 0;
                                Location gpsLocation = getCurrentLocation(LocationManager.GPS_PROVIDER);
                                if (gpsLocation != null) {
                                    currentLatitude = gpsLocation.getLatitude();
                                    currentLongitude = gpsLocation.getLongitude();



                                } else {
                                    Location nwLocation = getCurrentLocation(LocationManager.NETWORK_PROVIDER);
                                    if (nwLocation != null) {
                                        currentLatitude = nwLocation.getLatitude();
                                        currentLongitude = nwLocation.getLongitude();

                                    }
                                }

                                if (currentLatitude == 0.0 || currentLongitude == 0.0) {
                                    try
                                    {
                                        showSettingsAlert();
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }

                                } else {


                                    isCheckForLocation = false;

                                    spotasManager.setCurrentlatitude("" + currentLatitude);
                                    spotasManager.setCurrentlongitude("" + currentLongitude);

                                    Utility.printLog("homelatlon " + spotasManager.getCurrentlatitude() + " " + spotasManager.getCurrentlongitude());
                                    latLng[0] = Double.parseDouble(spotasManager.getCurrentlatitude());
                                    latLng[1] = Double.parseDouble(spotasManager.getCurrentlongitude());

                                    getaddress();
                                }
                            }
                        });
                    }
                }).start();
            }
        }
    }


    private void getaddress()
    {
        if(locationManager!=null)
        {
            locationManager.removeUpdates(this);
            locationManager = null;
        }

        new BackgroundTaskforAddress().execute();
    }


    private void  showSettingsAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("GPS Settings");
        alertDialog.setCancelable(false);

        // Setting Dialog Message
        alertDialog.setMessage("Please help us determine your location to show businesess near you. Click on Settings and turn on. Thanks");

        // On pressing Settings button
        alertDialog.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                spotasManager.setSavingsearchedadress("Homefragment");
                Intent addressIntent = new Intent(getActivity(), SearchAddressGooglePlacesActivity.class);
                addressIntent.putExtra("curlat", latLng[0]);
                addressIntent.putExtra("curlong", latLng[1]);
                VariableConstants.showmylocation = true;
                startActivityForResult(addressIntent, 20);
                getActivity().overridePendingTransition(R.anim.mainfadein, R.anim.splashfadeout);

            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivity(intent);
            }
        });

        alertDialog.show();
    }

    private void getCategories(final View view)
    {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        if(pDialog!=null) {
            pDialog.show();
        }

        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("ent_dev_id", Utility.getDeviceId(getActivity()));
            jsonObj.put("ent_sess_token", spotasManager.getSession_token());
            jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());


        } catch (JSONException e) {
            e.printStackTrace();
            Utility.printLog("printStackTrace " + e);
        }

        //GetBusinessTypes
        Utility.doJsonRequest(VariableConstants.host_url + "getCategories", jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(String jsonResponse) {

                if (pDialog != null) {
                    pDialog.dismiss();
                }
                Log.i("Spotsoon", "HomeFragmentNew response business types ::" + jsonResponse);

                categoriesResponse = jsonResponse;
                showHomeDetails(jsonResponse, view);
            }

            @Override
            public void onError(String error) {

                if (pDialog != null) {
                    pDialog.dismiss();
                }

                Utility.printLog("HomeFragmentNew JSON DATA Error" + error);
            }
        });
    }

    private void showHomeDetails(String jsonResponse,View view)
    {
        try
        {
            Gson gson = new Gson();
            HomeTypesHelper typesResponse = gson.fromJson(jsonResponse, HomeTypesHelper.class);
            if (typesResponse != null) {
                if (typesResponse.getErrFlag() != null && typesResponse.getErrFlag().equalsIgnoreCase("0")) {

                    if(typesResponse.getAndroidLocationAPIKey()!=null && typesResponse.getAndroidLocationAPIKey().length()>0)
                    {
                        spotasManager.storeServerKey(typesResponse.getAndroidLocationAPIKey());
                    }

                    if(typesResponse.getPrevBookings() != null && typesResponse.getPrevBookings().size() > 0) {
                        prevBookingsList.clear();
                        prevBookingsList.addAll(typesResponse.getPrevBookings());
                        listView.setAdapter(new ConnectedBillsAdapter(getActivity(), prevBookingsList));
                    } else {

                        //if prevBookings are empty, header view will not work so adding it as separate layout

                        listView.setVisibility(View.GONE);
                        no_bookings.setVisibility(View.VISIBLE);
                        headerLayout_home_parent.setVisibility(View.VISIBLE);
                        headerLayout = (LinearLayout) view.findViewById(R.id.headerLayout_home);
                    }

                    if (typesResponse.getCategories() != null && typesResponse.getCategories().size() > 0) {

                        typesList.clear();
                        typesList.addAll(typesResponse.getCategories());
                        //gridViewTypes.setAdapter(new CustomImageAdapter(getActivity(), typesList));

                        addImageView(typesResponse.getCategories().size(), typesResponse);

                        if(typesResponse.getAndroidVersionCode()!=null && typesResponse.getAndroidVersionCode().length()>0)
                        {
                            try {

                                int latestVersion = Integer.parseInt(typesResponse.getAndroidVersionCode());

                                PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                                int version = pInfo.versionCode;

                                if(latestVersion > version)
                                {
                                    if(typesResponse.getMandatoryUpdate()!=null && typesResponse.getMandatoryUpdate().length()>0)
                                    {
                                        if(typesResponse.getMandatoryUpdate().equalsIgnoreCase("YES"))
                                        {
                                            showMandatoryUpdateDialog();
                                        } else {
                                            showOptionalUpdateDialog();
                                        }
                                    }
                                }
                            }
                            catch (PackageManager.NameNotFoundException e)
                            {
                            }
                        }
                    }
                } else if (typesResponse.getErrNum() != null && (typesResponse.getErrNum().equalsIgnoreCase("101") || typesResponse.getErrNum().equalsIgnoreCase("7"))) {

                    spotasManager.setIsLogin(false);
                    Toast.makeText(getActivity(), typesResponse.getErrMsg(), Toast.LENGTH_LONG).show();
                    Intent intent1 = new Intent(getActivity(), SpotAsap.class);
                    startActivity(intent1);
                    getActivity().finish();
                }
            }
        }
        catch (Exception e)
        {}

    }

    private void showMandatoryUpdateDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Upgrade Please");
        builder.setMessage("You are using an older version of SpotSoon, please update now.");
        builder.setCancelable(false);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    getActivity().finish();
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.spotsoon.customer")));
                    getActivity().finish();
                }
            }
        });
        builder.show();
    }

    private void showOptionalUpdateDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Upgrade Please");
        builder.setCancelable(true);
        builder.setMessage("A new version of SpotSoon is ready for installation. Do you want to install it now?");
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    getActivity().finish();
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.spotsoon.customer")));
                    getActivity().finish();
                }
            }
        });

        builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void addImageView(int size, final HomeTypesHelper typesResponse )
    {

        int rows = size/3;
        int count = -1;



        for(int j=0; j<=rows; j++)
        {
            LinearLayout linearlayout = new LinearLayout(getActivity());
            for(int k=0;(k<size && k<3);k++)
            {
                count++;
                linearlayout.setOrientation(LinearLayout.HORIZONTAL);
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View view = inflater.inflate(R.layout.homenew_griditem, headerLayout, false);
                final TextView textView = (TextView)view.findViewById(R.id.service_name);
                ImageView imageView = (ImageView)view.findViewById(R.id.service_pic);


                    if (j==1 && k==2 && !showmore){
                        textView.setText("MORE");
                        Picasso.with(getActivity()).load("http://downloadicons.net/sites/default/files/right-double-arrow-icon-91562.png")
                                .placeholder(R.drawable.news_paper_details_default_image_frame)
                                .transform(new CircleTransform())
                                .into(imageView);
                    }
                    else
                    {
                        textView.setText(typesResponse.getCategories().get(count).getCategory());
                        String a =typesResponse.getCategories().get(count).getImage();
                        Picasso.with(getActivity()).load(typesResponse.getCategories().get(count).getImage())
                                .placeholder(R.drawable.news_paper_details_default_image_frame)
                                .transform(new CircleTransform())
                                .into(imageView);
                    }





                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;

                LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams((width/3)-2, ViewGroup.LayoutParams.WRAP_CONTENT);
                //LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams((width/4), (width/4)+10);
                llparams.setMargins(1, 0, 0, 1);
                view.setLayoutParams(llparams);
                view.setTag(count);
                linearlayout.addView(view);


                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        if (textView.getText().toString().equalsIgnoreCase("MORE")) {
                            Toast.makeText(getActivity(), "" + textView.getText(), Toast.LENGTH_SHORT).show();
                        }
                        else {
                             if (latLng[0] != 0 && latLng[1] != 0) {
                                Intent intent = new Intent(getActivity(), CategoriesTabsActivity.class);
                                intent.putExtra("RESPONSE", categoriesResponse);
                                intent.putExtra("BANNER", typesResponse.getCategories().get((Integer) view.getTag()).getBanner());
                                intent.putExtra("POSITION", (Integer) view.getTag());
                                intent.putExtra("LATITUDE", "" + latLng[0]);
                                intent.putExtra("LONGITUDE", "" + latLng[1]);
                                startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
                            } else {
                                Toast.makeText(getActivity(), "Please select your location", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
            size = size - 3;
            headerLayout.addView(linearlayout);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TypesDetailsHelper selectedFromList =(TypesDetailsHelper) (listView.getItemAtPosition(position));

        if(selectedFromList!=null)
        {
                if(selectedFromList.getType()!=null && (selectedFromList.getType().equalsIgnoreCase("inventum")) || (selectedFromList.getType().equalsIgnoreCase("ICENET")))
            {
                RequestForSubscriptions(selectedFromList.getUsername(), selectedFromList.getBussName(), selectedFromList.getBusinessId(),
                        selectedFromList.getViewMore(), selectedFromList.getBussAddr2(),
                        selectedFromList.getImage(),selectedFromList.getBannerUrl());
            }

          else if (selectedFromList.getType()!=null && selectedFromList.getType().equalsIgnoreCase("TACITINE"))
            {
                RequestForTacitineSubscriptions(selectedFromList.getBusinessId(), selectedFromList.getUsername(),"invGetSubscriptions",
                        getString(R.string.tacitine),selectedFromList.getBussName(),selectedFromList.getViewMore(),
                        selectedFromList.getBussAddr2(),selectedFromList.getImage(),selectedFromList.getBannerUrl());
            }
                //Shankar Kumar
                else if((selectedFromList.getType()!=null && selectedFromList.getType().equalsIgnoreCase("TACITINE")) && (selectedFromList.getUsername().equalsIgnoreCase("")))
                {
                    Intent inventumIntent = new Intent(getActivity(), InventumActivity.class);
                    inventumIntent.putExtra("BUSINESS_ID", selectedFromList.getBusinessId());
                    inventumIntent.putExtra("Name", selectedFromList.getBussName());
                    inventumIntent.putExtra("Image",selectedFromList.getImage());
                    inventumIntent.putExtra("URL", "invGetSubscriptions");
                    inventumIntent.putExtra("TYPE",getString(R.string.tacitine));
                    inventumIntent.putExtra("BANNER", selectedFromList.getBannerUrl());
                    inventumIntent.putExtra("ADDRESS", selectedFromList.getBussAddr1());
                    inventumIntent.putExtra("shoplatitude", selectedFromList.getBussLat());
                    inventumIntent.putExtra("shoplongitude", selectedFromList.getBussLong());
                    inventumIntent.putExtra("ConnectedFlag",selectedFromList.getConnectedFlag());
                    inventumIntent.putExtra("MorePlans", selectedFromList.getViewMore());

                    startActivity(inventumIntent);
                    getActivity().overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
                }

                else if (selectedFromList.getType()!=null && selectedFromList.getType().equalsIgnoreCase(getString(R.string.invoice_managment)))
                {
                    new RequestFor_Invoice_Mgmt_Subscriptions().execute(VariableConstants.INVOICE_MGMT_SUBSCRIPTION,
                            selectedFromList.getBusinessId(),selectedFromList.getUserMobile(),getString(R.string.invoice_managment),
                            selectedFromList.getBussName(),selectedFromList.getViewMore(),selectedFromList.getBussAddr2(),
                            selectedFromList.getImage(),selectedFromList.getBannerUrl());

                }


            else {


                Intent intent = new Intent(getActivity(), PackagesActivity.class);
                intent.putExtra("BANNER",selectedFromList.getBannerUrl());
                intent.putExtra("Image", selectedFromList.getImage());
                intent.putExtra("BusinessName",selectedFromList.getBussName());
                intent.putExtra("BusinessImage",selectedFromList.getImage());
                intent.putExtra("BusinessAddress",selectedFromList.getBussAddr2());
                intent.putExtra("businessId", selectedFromList.getBusinessId());
                intent.putExtra("getCustomerNotesCheck",selectedFromList.getCustomerNotesCheck());
                intent.putExtra("ConnectedFlag",selectedFromList.getConnectedFlag());
                intent.putExtra("CustomerNotes", selectedFromList.getCustomerNotes());
                intent.putExtra("SHOW_ADDRESS", selectedFromList.getDelivery_Pickup());
                intent.putExtra("USER_NAME", selectedFromList.getUserNotes());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
            }
        }
    }



    private class ConnectedBillsAdapter extends ArrayAdapter<TypesDetailsHelper>
    {
        private Context mContext;
        private ArrayList<TypesDetailsHelper> dataList = new ArrayList<TypesDetailsHelper>();

        public ConnectedBillsAdapter(Context mContext, ArrayList<TypesDetailsHelper> adrsListData)
        {
            super(mContext, 0, adrsListData);
            // TODO Auto-generated constructor stub
            this.mContext = mContext;
            this.dataList = adrsListData;
        }
        private class ViewHolder
        {
            TextView business_name,business_type,business_address,last_payment,payment_due,click_pay;
            ImageView business_pic;
            LinearLayout dueLayout;
        }
        @Override
        public TypesDetailsHelper getItem(int position)
        {
            // TODO Auto-generated method stub
            return dataList.get(position);
        }
        @Override
        public int getCount()
        {
            // TODO Auto-generated method stub
            return dataList.size();
        }

        /*********************************************************************************************************************/
        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            TypesDetailsHelper rowItem = dataList.get(position);
            ViewHolder holder = null;
            //convertView.setEnabled(false);
            if(convertView==null||convertView.getTag()==null)
            {
                holder = new ViewHolder();
                // inflate the layout
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.homenew_list_rowitem, parent, false);
                holder.business_name= (TextView) convertView.findViewById(R.id.business_name);
                holder.business_type= (TextView) convertView.findViewById(R.id.business_type);
                holder.business_address= (TextView) convertView.findViewById(R.id.business_address);
                holder.last_payment=(TextView)convertView.findViewById(R.id.last_payment);
                holder.payment_due=(TextView)convertView.findViewById(R.id.payment_due);
                holder.click_pay=(TextView)convertView.findViewById(R.id.click_pay);
                holder.business_pic = (ImageView)convertView.findViewById(R.id.business_image);
                holder.dueLayout = (LinearLayout)convertView.findViewById(R.id.due_layout);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.business_name.setText(rowItem.getBussName());
            holder.business_type.setText(rowItem.getCategory());
            if(rowItem.getBussAddr2()!=null && rowItem.getBussAddr2().length()>0)
            {
                holder.business_address.setText(rowItem.getBussAddr2());
            }
            else if(rowItem.getBussAddr1()!=null && rowItem.getBussAddr1().length()>0)
            {
                holder.business_address.setVisibility(View.VISIBLE);
                holder.business_address.setText(rowItem.getBussAddr1());
            }
            else
            {
                holder.business_address.setVisibility(View.GONE);
            }

            if(rowItem.getPayDt()!=null && rowItem.getPayDt().length()>0)
            {
                holder.last_payment.setVisibility(View.VISIBLE);
                holder.last_payment.setText("Last Payment : "+rowItem.getPayDt());
            }
            else {
                holder.last_payment.setVisibility(View.GONE);
            }

            if(rowItem.getDueDt()!=null && rowItem.getDueDt().length()>0)
            {
                if(rowItem.getPurchaseType()!=null && rowItem.getPurchaseType().length()>0)
                {
                    if(rowItem.getPurchaseType().equalsIgnoreCase(getString(R.string.invoice)))
                    {
                        holder.payment_due.setVisibility(View.GONE);
                    }
                    else {
                        holder.payment_due.setVisibility(View.VISIBLE);
                        setPaymentDate(holder.payment_due, rowItem.getStatus(), rowItem.getDueDt());
                    }
                }
                else {
                    holder.payment_due.setVisibility(View.VISIBLE);
                    setPaymentDate(holder.payment_due, rowItem.getStatus(), rowItem.getDueDt());
                }
            }
            else {
                holder.payment_due.setVisibility(View.GONE);
            }

            if(rowItem.getImage()!=null && rowItem.getImage().length()>0)
            {
                String a = rowItem.getImage();
                Picasso.with(mContext)
                        .load(rowItem.getImage())
                        .placeholder(R.drawable.news_paper_details_default_image_frame)
                        .transform(new CircleTransform())
                        .into(holder.business_pic);
            }
            else
            {
                Picasso.with(getActivity())
                        .load(R.drawable.news_paper_details_default_image_frame)
                        .transform(new CircleTransform())
                        .into(holder.business_pic);
            }

            return convertView;
        }
    }

    private void setPaymentDate(TextView textView,String status,String date)
    {
        if(status!=null && status.length()>0)
        {
            if(status.equalsIgnoreCase("3"))
            {
                textView.setText("Recharge Scheduled " + date);
            }
            else {
                textView.setText("Payment Due : " + date);
            }
        }
        else {
            textView.setText("Payment Due : " + date);
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            Date strDate = sdf.parse(date);
            if (System.currentTimeMillis() > strDate.getTime()) {

                //catalog_outdated = 1;
                textView.setTextColor(Utility.getColor(getActivity(),R.color.price_color));
            }
            else {
                textView.setTextColor(Utility.getColor(getActivity(),R.color.lightygreen));
            }
        }
        catch (ParseException e1)
        {
        }
    }

    private void RequestForSubscriptions(String userId, final String name, final String businessId,final String viewMore,final String address,final String image,final String bannerUrl)
    {


        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        if(pDialog!=null) {
            pDialog.show();
        }

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("ent_dev_id",Utility.getDeviceId(getActivity()));
            jsonObj.put("ent_sess_token", spotasManager.getSession_token());
            jsonObj.put("ent_business_id", businessId);
            jsonObj.put("ent_actid", userId);
            jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());

        }
        catch(Exception e)
        {
        }
        //calling doJsonRequest of Utility class
        Utility.doJsonRequest(VariableConstants.host_url + "invGetSubscriptions", jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                // TODO Auto-generated method stub

                if (pDialog != null) {
                    pDialog.dismiss();
                }

                showSubscriptionsList(jsonResponse,name,businessId,viewMore,address,image,bannerUrl);
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

    private void showSubscriptionsList(String jsonResponse,String name,String businessId,String viewMorePlans,String address,String image,String bannerUrl)
    {
        try {

            Gson gson= new Gson();
            Subscriptions subscriptionsResponse = gson.fromJson(jsonResponse, Subscriptions.class);

            if(subscriptionsResponse!=null)
            {
                if(subscriptionsResponse.getErrFlag().equalsIgnoreCase("0"))
                {
                    if(subscriptionsResponse.getSubscriptions()!=null && subscriptionsResponse.getSubscriptions().size()>0)
                    {
                        subscriptions.clear();
                        subscriptions = subscriptionsResponse.getSubscriptions();

                        JSONObject object = new JSONObject(jsonResponse);
                        JSONArray objectSub = object.getJSONArray("subscriptions");
                        JSONObject jsonObject = objectSub.getJSONObject(0);

                        byte[] data = jsonObject.toString().getBytes("UTF-8");
                        String base64 = Base64.encodeToString(data, Base64.DEFAULT);

                        if(subscriptionsResponse.getSubscriptions().get(0).getInvoice()!=null && subscriptionsResponse.getSubscriptions().get(0).getInvoice().size()>0)
                        {
                            Intent intent = new Intent(getActivity(), PostpayInvoiceSummaryActivity.class);
                            intent.putExtra("PROVIDER",name);
                            intent.putExtra("TYPE",getString(R.string.inventum));
                            intent.putExtra("BUSINESS_ID",businessId);
                            intent.putExtra("ENCODED",base64);
                            intent.putExtra("DOM_ID",subscriptionsResponse.getSubscriptions().get(0).getDomid());
                            intent.putExtra("PACKAGE",subscriptionsResponse.getSubscriptions().get(0).getPkgid());
                            intent.putExtra("MorePlans", viewMorePlans);

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
                            getActivity().overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
                        }
                        else {

                            Intent intent = null;
                            if(subscriptionsResponse.getSubscriptions().get(0).getPackages()!=null &&subscriptionsResponse.getSubscriptions().get(0).getPackages().size()>0)
                            {
                                if(subscriptionsResponse.getSubscriptions().get(0).getPackages().get(0).getCoupons().size()>1)
                                {
                                    intent = new Intent(getActivity(), SelectPackageActivity.class);
                                    intent.putExtra("JsonResponse",jsonResponse);
                                }
                                else if(subscriptionsResponse.getSubscriptions().get(0).getPackages().get(0).getCoupons().size()>0){

                                    intent = new Intent(getActivity(), SubscriptionsSummaryAvtivity.class);

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

                            if(intent!=null)
                            {
                                intent.putExtra("PROVIDER",name);
                                intent.putExtra("TYPE",getString(R.string.inventum));
                                intent.putExtra("BUSINESS_ID",businessId);
                                intent.putExtra("ENCODED",base64);
                                intent.putExtra("DOM_ID",subscriptionsResponse.getSubscriptions().get(0).getDomid());
                                intent.putExtra("PACKAGE",subscriptionsResponse.getSubscriptions().get(0).getPkgid());
                                intent.putExtra("MorePlans", viewMorePlans);
                                intent.putExtra("FROM_HOME", true);

                                startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
                            }
                            else
                            {
                                Toast.makeText(getActivity(),"We're unable to find your package details",Toast.LENGTH_LONG).show();
                            }
                        }



                    }
                }
                else if(subscriptionsResponse.getErrFlag().equalsIgnoreCase("1"))
                {
                    Intent inventumIntent = new Intent(getActivity(), InventumActivity.class);
                    inventumIntent.putExtra("BUSINESS_ID", businessId);
                    inventumIntent.putExtra("Name", name);
                    inventumIntent.putExtra("Image", image);
                    inventumIntent.putExtra("URL", "invGetSubscriptions");
                    inventumIntent.putExtra("TYPE", getString(R.string.inventum));
                    inventumIntent.putExtra("BANNER", bannerUrl);
                    inventumIntent.putExtra("ADDRESS", address);
                    inventumIntent.putExtra("ConnectedFlag","YES");
                    inventumIntent.putExtra("MorePlans", viewMorePlans);

                    startActivity(inventumIntent);
                    getActivity().overridePendingTransition(R.anim.anim_two, R.anim.anim_one);


                }
                else {

                }
            }
        }
        catch (Exception e)
        {

        }
    }



    private void RequestForTacitineSubscriptions(final String businessId, String username, String invGetSubscriptions,
                                                 final String type, final String bussName, final String viewMore,final String bussAddr2,
                                                 final String image,final String bannerUrl)
    {

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(true);
        if(pDialog!=null) {
            pDialog.show();
        }

        final JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("ent_dev_id",Utility.getDeviceId(getActivity()));
            jsonObj.put("ent_sess_token", spotasManager.getSession_token());
            jsonObj.put("ent_business_id", businessId);
            jsonObj.put("ent_actid", username);
            jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());
        }
        catch(Exception e)
        {
        }
        //calling doJsonRequest of Utility class
        Utility.doJsonRequest(VariableConstants.host_url + invGetSubscriptions, jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                // TODO Auto-generated method stub

                if(pDialog!=null) {
                    pDialog.dismiss();
                }

                String packageAmt=null;
                try {
                    JSONObject jsonObject1 = null;
                    JSONArray jsonArray = null;
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    if (jsonObject.has("subscriptions")) {
                         jsonArray = jsonObject.getJSONArray("subscriptions");
                        jsonObject1 = jsonArray.getJSONObject(0);



                    if (jsonObject1.has("purchase_new_pack")) {
                        purchase_new_pack = jsonObject1.getString("purchase_new_pack");
                    }
                    if (jsonObject1.has("packageAmt"))
                    {
                        packageAmt  = jsonObject1.getString("packageAmt");
                    }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showTacitineSubscriptionsList(jsonResponse,packageAmt,type,bussName,viewMore,businessId,bussAddr2,image,bannerUrl);
            }

            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub
                Utility.printLog("HomeFrag JSON DATA Error" + error);

                if(pDialog!=null) {
                    pDialog.dismiss();
                }
            }
        });
    }

    private void showTacitineSubscriptionsList(final String jsonResponse, String packageAmt, String type, String name, String showPlans
            , String businessId, String bussAddr2, String image, String bannerUrl)
    {
        try {

            Gson gson= new Gson();
            final Subscriptions subscriptionsResponse = gson.fromJson(jsonResponse, Subscriptions.class);

            if(subscriptionsResponse!=null)
            {
                if(subscriptionsResponse.getErrFlag().equalsIgnoreCase("0"))
                {
                    if(subscriptionsResponse.getSubscriptions()!=null && subscriptionsResponse.getSubscriptions().size()>0)
                    {
                        subscriptions.clear();
                        subscriptions = subscriptionsResponse.getSubscriptions();



                        if(type!=null && type.equalsIgnoreCase(getString(R.string.tacitine)))
                        {
                            if((subscriptions.get(0).getAccount_state()!=null && subscriptions.get(0).getAccount_state().length()>0) &&
                                    (subscriptions.get(0).getCurrent_plan_type()!=null && subscriptions.get(0).getCurrent_plan_type().length()>0))
                            {
                                if(subscriptions.get(0).getAccount_state().equalsIgnoreCase("active")
                                        && subscriptions.get(0).getCurrent_plan_type().equalsIgnoreCase("extended"))
                                {


                                    Intent intent = new Intent(getActivity(), TacitineSummaryActivity.class);
                                    intent.putExtra("PROVIDER",name);
                                    intent.putExtra("TYPE",type);
                                    intent.putExtra("BUSINESS_ID",businessId);
                                    intent.putExtra("ENCODED","");
                                    intent.putExtra("MorePlans", showPlans);
                                    intent.putExtra("DOM_ID","");
                                    //intent.putExtra("DOM_ID",subscriptionsResponse.getSubscriptions().get(0).getDomid());

                                    SubscriptionsDetailsHelper detailsHelper = subscriptionsResponse.getSubscriptions().get(0);
                                    intent.putExtra("PACKAGE",detailsHelper.getPkgid());
                                    intent.putExtra("planType",detailsHelper.getPlanType());
                                    intent.putExtra("actid",detailsHelper.getActid());
                                    intent.putExtra("packageAmount",packageAmt);
                                    intent.putExtra("billCycle",detailsHelper.getBillCycle());
                                    intent.putExtra("nextRenewalDate",detailsHelper.getNextRenewalDate());
                                    intent.putExtra("convenienceRate",detailsHelper.getConvenienceRate());
                                    intent.putExtra("convenienceFee",detailsHelper.getConvenienceFee());
                                    intent.putExtra("Total",detailsHelper.getTotal());
                                    intent.putExtra("purchase_new_pack",purchase_new_pack);
                                    intent.putExtra("jsonResponse",jsonResponse);

                                    if(detailsHelper.getCustomer()!=null)
                                    {
                                        intent.putExtra("name",detailsHelper.getCustomer().getName());
                                        intent.putExtra("phone",detailsHelper.getCustomer().getPhone());
                                        intent.putExtra("email",detailsHelper.getCustomer().getEmail());
                                    }

                                    startActivity(intent);
                                    getActivity().overridePendingTransition(R.anim.anim_two, R.anim.anim_one);

                                }
                                else {
                                    String base64 = getBase64String(jsonResponse);

                                    Intent intent = new Intent(getActivity(), TacitineSummaryActivity.class);
                                    intent.putExtra("PROVIDER",name);
                                    intent.putExtra("TYPE",type);
                                    intent.putExtra("BUSINESS_ID",businessId);
                                    intent.putExtra("ENCODED",base64);
                                    intent.putExtra("MorePlans", showPlans);
                                    intent.putExtra("DOM_ID","");
                                    //intent.putExtra("DOM_ID",subscriptionsResponse.getSubscriptions().get(0).getDomid());

                                    SubscriptionsDetailsHelper detailsHelper = subscriptionsResponse.getSubscriptions().get(0);
                                    intent.putExtra("PACKAGE",detailsHelper.getPkgid());
                                    intent.putExtra("planType",detailsHelper.getPlanType());
                                    intent.putExtra("actid",detailsHelper.getActid());
                                    intent.putExtra("packageAmount",packageAmt);
                                    intent.putExtra("billCycle",detailsHelper.getBillCycle());
                                    intent.putExtra("nextRenewalDate",detailsHelper.getNextRenewalDate());
                                    intent.putExtra("convenienceRate",detailsHelper.getConvenienceRate());
                                    intent.putExtra("convenienceFee",detailsHelper.getConvenienceFee());
                                    intent.putExtra("Total",detailsHelper.getTotal());
                                    intent.putExtra("purchase_new_pack",purchase_new_pack);
                                    intent.putExtra("jsonResponse",jsonResponse);
                                    if(detailsHelper.getCustomer()!=null)
                                    {
                                        intent.putExtra("name",detailsHelper.getCustomer().getName());
                                        intent.putExtra("phone",detailsHelper.getCustomer().getPhone());
                                        intent.putExtra("email",detailsHelper.getCustomer().getEmail());
                                    }

                                    startActivity(intent);
                                   getActivity().overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
                                }
                            }
                        }
                        else {


                            String base64 = getBase64String(jsonResponse);

                            if(subscriptionsResponse.getSubscriptions().get(0).getInvoice()!=null && subscriptionsResponse.getSubscriptions().get(0).getInvoice().size()>0)
                            {
                                Intent intent = new Intent(getActivity(), PostpayInvoiceSummaryActivity.class);
                                intent.putExtra("PROVIDER",name);
                                intent.putExtra("TYPE",type);
                                intent.putExtra("BUSINESS_ID",businessId);
                                intent.putExtra("ENCODED",base64);
                                intent.putExtra("DOM_ID",subscriptionsResponse.getSubscriptions().get(0).getDomid());
                                intent.putExtra("PACKAGE",subscriptionsResponse.getSubscriptions().get(0).getPkgid());
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
                                getActivity().overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
                            }
                            else {

                                Intent intent = null;
                                if(subscriptionsResponse.getSubscriptions().get(0).getPackages()!=null &&subscriptionsResponse.getSubscriptions().get(0).getPackages().size()>0)
                                {
                                    if(subscriptionsResponse.getSubscriptions().get(0).getPackages().get(0).getCoupons().size()>1)
                                    {
                                        intent = new Intent(getActivity(), SelectPackageActivity.class);
                                        intent.putExtra("JsonResponse",jsonResponse);
                                    }
                                    else if(subscriptionsResponse.getSubscriptions().get(0).getPackages().get(0).getCoupons().size()>0){

                                        intent = new Intent(getActivity(), SubscriptionsSummaryAvtivity.class);

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

                                if(intent!=null)
                                {
                                    intent.putExtra("PROVIDER",name);
                                    intent.putExtra("TYPE",type);
                                    intent.putExtra("BUSINESS_ID",businessId);
                                    intent.putExtra("ENCODED",base64);
                                    intent.putExtra("DOM_ID",subscriptionsResponse.getSubscriptions().get(0).getDomid());
                                    intent.putExtra("PACKAGE",subscriptionsResponse.getSubscriptions().get(0).getPkgid());
                                    intent.putExtra("MorePlans", showPlans);

                                    startActivity(intent);
                                    getActivity().overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
                                }
                                else
                                {
                                    Toast.makeText(getActivity(),"We're unable to find your package details",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                }
                else if(subscriptionsResponse.getErrFlag().equalsIgnoreCase("1"))
                {

                   /* DisplayMetrics metrics = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    int height = metrics.heightPixels - 150;

                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.invalid_username);
                    RelativeLayout layout1 = (RelativeLayout)dialog.findViewById(R.id.layout1);
                    RelativeLayout layout2 = (RelativeLayout)dialog.findViewById(R.id.layout2);

                    LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height/2);
                    layout1.setLayoutParams(llparams);
                    layout2.setLayoutParams(llparams);
                    TextView gotit = (TextView)dialog.findViewById(R.id.tv_gotid);
                    gotit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();*/
                    Intent inventumIntent = new Intent(getActivity(), InventumActivity.class);
                    inventumIntent.putExtra("BUSINESS_ID", businessId);
                    inventumIntent.putExtra("Name", name);
                    inventumIntent.putExtra("Image", image);
                    inventumIntent.putExtra("URL", "invGetSubscriptions");
                    inventumIntent.putExtra("TYPE", getString(R.string.tacitine));
                    inventumIntent.putExtra("BANNER", bannerUrl);
                    inventumIntent.putExtra("ADDRESS", bussAddr2);
                    inventumIntent.putExtra("ConnectedFlag","YES");
                    inventumIntent.putExtra("MorePlans", showPlans);

                    startActivity(inventumIntent);
                    getActivity().overridePendingTransition(R.anim.anim_two, R.anim.anim_one);


                }
                else {
                }
            }
        }
        catch (Exception e)
        {

        }
    }

    private Location getCurrentLocation(String provider)
    {
        Location location;
        try {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            //  Log.i("Spotsoon","provider enabled = "+provider+" -"+locationManager.isProviderEnabled(provider));
            if (locationManager.isProviderEnabled(provider)) {
                locationManager.requestLocationUpdates(provider,
                        MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);

                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(provider);

                    return location;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==20 && resultCode== Activity.RESULT_OK)
        {
            if(data!=null)
            {
                boolean isCurrentLocation = data.getBooleanExtra("CURRENT", false);
                if(isCurrentLocation)//if user selects current location
                {
                    isCheckForLocation = false;

                    String latitudeString = data.getStringExtra("LATITUDE_SEARCH");
                    String logitudeString = data.getStringExtra("LONGITUDE_SEARCH");

                    spotasManager.setCurrentlatitude(latitudeString);
                    spotasManager.setCurrentlongitude(logitudeString);

                    latLng[0] = Double.parseDouble(latitudeString);
                    latLng[1] = Double.parseDouble(logitudeString);

                    localitytv.setText("Fetching location...");
                    //getCategories();
                    getaddress();
                }
                else {

                    String location = data.getStringExtra("SearchAddress");
                    if(location !=null ){
                        String latitudeString = data.getStringExtra("LATITUDE_SEARCH");
                        String logitudeString = data.getStringExtra("LONGITUDE_SEARCH");

                        latLng[0] = Double.parseDouble(latitudeString);
                        latLng[1] = Double.parseDouble(logitudeString);

                        spotasManager.setCurrentlatitude(latitudeString);
                        spotasManager.setCurrentlongitude(logitudeString);

                        Utility.printLog("searchlatont inOnresult " + latLng[0] + " " + latLng[1]);

                        String[] parts = location.split(",");

                        if(parts.length>0)
                            localitytv.setText(parts[0]);


                    } else {
                        Utility.printLog("is it coming here ");

                        Toast.makeText(getActivity(),"Error, please try again",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    class BackgroundTaskforAddress extends AsyncTask<String, Void, String>
    {
        com.com.locationhelpers.GeocodingResponse response;
        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params1)
        {
            // TODO Auto-generated method stub
            String url="https://maps.google.com/maps/api/geocode/json?latlng="+latLng[0]+","+latLng[1] +"&sensor=false";

            Utility.printLog("Geocoding url: " + url);

            String stringResponse =  GeoDecoder.callhttpRequest(url);

            if(stringResponse!=null)
            {
                Gson gson=new Gson();
                response=gson.fromJson(stringResponse, com.com.locationhelpers.GeocodingResponse.class);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if(response!=null)
            {
                if(response.getStatus().equals("OK") && response.getResults()!=null && response.getResults().size()>0)
                {
                    fetchLocation(response, "sublocality_level_1");

                    if(localitytv.getText().toString().trim().equalsIgnoreCase("Fetching location...") ||
                            localitytv.getText().toString().trim().length()==0)
                    {
                        fetchLocation(response,"sublocality_level_2");
                    }

                    if(localitytv.getText().toString().trim().equalsIgnoreCase("Fetching location...") ||
                            localitytv.getText().toString().trim().length()==0)
                    {
                        fetchLocation(response,"sublocality_level_3");
                    }


                }
            }
        }
    }

    private void fetchLocation(com.com.locationhelpers.GeocodingResponse response,String localityLevel)
    {
        if(response.getStatus().equals("OK") && response.getResults()!=null && response.getResults().size()>0)
        {
            for (int i=0;i<response.getResults().size();i++)
            {
                for (int j=0;j<response.getResults().get(i).getTypes().size();j++)
                {
                    if(response.getResults().get(i).getTypes().get(j).equals(localityLevel))
                    {
                        String s= response.getResults().get(i).getFormatted_address();
                        localitytv.setText(response.getResults().get(i).getFormatted_address());

                        for(int k=0; k<response.getResults().get(i).getAddress_components().size() ;k++)
                        {
                            for(int l=0;l<response.getResults().get(i).getAddress_components().get(k).getTypes().size();l++)
                            {
                                if(response.getResults().get(i).getAddress_components().get(k).getTypes().get(l).equals(localityLevel)
                                        || response.getResults().get(i).getAddress_components().get(k).getTypes().get(l).equals("sublocality"))
                                {
                                    String s1 = response.getResults().get(i).getAddress_components().get(k).getLong_name();
                                    localitytv.setText(response.getResults().get(i).getAddress_components().get(k).getLong_name());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private String getBase64String(String jsonObject)
    {
        try {

            JSONObject object1 = new JSONObject(jsonObject);
            JSONArray objectSub1 = object1.getJSONArray("subscriptions");
            JSONObject jsonObject1 = objectSub1.getJSONObject(0);

            byte[] data = jsonObject1.toString().getBytes("UTF-8");
            return Base64.encodeToString(data, Base64.DEFAULT);
        }
        catch (JSONException e)
        {
        }
        catch (UnsupportedEncodingException e)
        {
        }

        return null;
    }

    private class RequestFor_Invoice_Mgmt_Subscriptions extends AsyncTask<String, Void, String> {
        String result1,businessId,username,type, businessnmae, showplan;
        String getBussAddr2,image,bannerurl;
        ArrayList<NameValuePair> namevaluepairs = new ArrayList<NameValuePair>();
        final ProgressDialog pDialog = new ProgressDialog(getActivity());

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

            businessId = params[1];
            username = params[2];
            type = params[3];
            businessnmae = params[4];
            showplan = params[5];
            getBussAddr2= params[6];
            image = params[7];
            bannerurl = params[8];

            namevaluepairs.add(new BasicNameValuePair("deviceID", Utility.getDeviceId(getActivity())));
            namevaluepairs.add(new BasicNameValuePair("sessionToken", spotasManager.getSession_token()));
            namevaluepairs.add(new BasicNameValuePair("BusinessID", businessId));
            namevaluepairs.add(new BasicNameValuePair("searchKey", username));
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
                            gotoPostpayInvoicePackageList(jsonArray.length(), jsonArray,base64,businessId,type,businessnmae,showplan);
                        } else {
                            gotoPostpayInvoiceSummaryActivity(jsonArray.length(), jsonArray,base64,businessId,type,businessnmae,showplan);
                        }
                    }
                    else if(errFlag.equalsIgnoreCase("1")){

                        Intent inventumIntent = new Intent(getActivity(), InventumActivity.class);
                        inventumIntent.putExtra("BUSINESS_ID", businessId);
                        inventumIntent.putExtra("Name", businessnmae);
                        inventumIntent.putExtra("Image", image);
                        inventumIntent.putExtra("URL", "");
                        inventumIntent.putExtra("TYPE", getString(R.string.invoice_managment));
                        inventumIntent.putExtra("BANNER", bannerurl);
                        inventumIntent.putExtra("ADDRESS", getBussAddr2);
                        inventumIntent.putExtra("ConnectedFlag","YES");
                        inventumIntent.putExtra("MorePlans", showplan);
                        startActivity(inventumIntent);
                        getActivity().overridePendingTransition(R.anim.anim_two, R.anim.anim_one);

                    }
                    else
                    {
                         Toast.makeText(getActivity(), ""+msg, Toast.LENGTH_SHORT).show();
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
    private void gotoPostpayInvoicePackageList(int length, JSONArray jsonArray, String base64, String businessId, String type, String businessnmae, String showplan) {
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
        Intent sendinvoicelist= new Intent(getActivity(), InvoiceManagementPackageList.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("allinvoiceList", invoicepackageList);
        bundle.putString("PROVIDER", businessnmae);
        bundle.putString("TYPE", type);
        bundle.putString("BUSINESS_ID", businessId);
        bundle.putString("ENCODED", base64);
        bundle.putString("DOM_ID", "");
        bundle.putString("MorePlans", showplan);
        sendinvoicelist.putExtras(bundle);
        startActivity(sendinvoicelist);
    }
    private void gotoPostpayInvoiceSummaryActivity(int length, JSONArray jsonArray, String base64, String businessId, String type, String businessnmae, String showplan) {

        for (int i = 0 ; i<length; i++){
            JSONObject invoiceonj = null;
            try {
                invoiceonj = jsonArray.getJSONObject(i);

                if (invoiceonj!=null && invoiceonj.length()>0) {
                    Double payableamnt = null,confee=null;
try {
    Double total = getTotal(invoiceonj.getString("PrevDue"), invoiceonj.getString("BillAmount"), invoiceonj.getString("Payments"));
     confee = getConFee(invoiceonj.getString("ConvenienceFeeRate"), total);
     payableamnt = total + confee;
}
catch (Exception e)
{
    e.printStackTrace();
}

                    Intent intent = new Intent(getActivity(), PostpayInvoiceSummaryActivity.class);
                    intent.putExtra("PROVIDER", businessnmae);
                    intent.putExtra("TYPE", type);
                    intent.putExtra("BUSINESS_ID", businessId);
                    intent.putExtra("ENCODED", base64);
                    intent.putExtra("DOM_ID", "");
                    intent.putExtra("PACKAGE", invoiceonj.getString("ProductName"));
                    intent.putExtra("MorePlans", showplan);

                    intent.putExtra("invoiceDate", invoiceonj.getString("InvoiceDate"));
                    intent.putExtra("dueDate", invoiceonj.getString("DueDate"));
                    intent.putExtra("billStartDate", invoiceonj.getString("BillingStartDate"));
                    intent.putExtra("billEndDate", invoiceonj.getString("BillingEndDate"));
                    intent.putExtra("billAmount", invoiceonj.getString("BillAmount"));
                    intent.putExtra("pendingAmount",invoiceonj.getString("PrevDue"));
                    intent.putExtra("payableAmount", ""+payableamnt);
                    intent.putExtra("convenienceRate", invoiceonj.getString("ConvenienceFeeRate"));
                    intent.putExtra("convenienceFee",""+confee);
                    intent.putExtra("Total", ""+payableamnt+"0");
                    intent.putExtra("amountEditable",invoiceonj.getString("PartialPayment"));
                    intent.putExtra("invoiceHTML", "");
                    intent.putExtra("daysToDue", invoiceonj.getString("DaysToDue"));
                    intent.putExtra("invoiceNo", invoiceonj.getString("InvoiceNum"));
                    intent.putExtra("paymentsDone", invoiceonj.getString("Payments"));
                    intent.putExtra("openingBalance", invoiceonj.getString("PrevDue"));
                    //  intent.putExtra("TaxAmount", jsonObject1.getString("TaxAmount"));


                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
    public static Double getConFee(String convenienceFeeRate, Double total) {
        double conveniencefeerate, totl,confee;
        conveniencefeerate =  Double.valueOf(convenienceFeeRate);
        totl =  total;
        confee = ((conveniencefeerate * totl) /100);

        return confee;
    }
    public  Double getTotal(String prevDue, String billAmount, String payments) {

        double prevdue, billamount,payment,total;
        prevdue = Double.valueOf(prevDue);
        billamount = Double.valueOf(billAmount);
        payment = Double.valueOf(payments);
        total = Math.round(((prevdue + billamount)-payment));
        return total;
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
}
