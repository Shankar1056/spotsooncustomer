package com.spotsoon.customer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.grumoon.pulllistview.PullListView;
import com.squareup.picasso.Picasso;
import com.threembed.closeandopenbean.CloseAndOpenResonse;
import com.threembed.closeandopenbean.DataInCloseOpen;
import com.utility.SpotasManager;
import com.utility.Utility;
import com.utility.VariableConstants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import io.fabric.sdk.android.Fabric;

/**
 * Created by VARUN on 5/1/2016.
 */
public class CategoriesTabFragment extends Fragment{

    private PullListView pullListView;
    private SpotasManager spmanager;
    private int count = 1;
    private String typeId, warningMessage;
    private int index = 0;
    private ArrayList<DataInCloseOpen> coList = new ArrayList<DataInCloseOpen>();
    private BussinessAdapter closeAndOpenAdapter;
    private String title,latitude,longitude,bannerUrl,businessString;
    private RelativeLayout nobusiness;
    private static final String ARG_POSITION = "position";
    private static final String ARG_TYPE = "type";
    private static final String ARG_TITLE = "title";
    private static final String ARG_BANNER = "banner";
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";
    private static final String ARG_BUSINESS_STRING = "businessStr";

    private ProgressBar progressBar;

    public static CategoriesTabFragment newInstance(int position,String type,String title,String latitude,String longitude,String bannerUrl,String businessString) {
        CategoriesTabFragment f = new CategoriesTabFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        b.putString(ARG_TYPE, type);
        b.putString(ARG_TITLE,title);
        b.putString(ARG_LATITUDE,latitude);
        b.putString(ARG_LONGITUDE,longitude);
        b.putString(ARG_BANNER,bannerUrl);
        b.putString(ARG_BUSINESS_STRING,businessString);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // int position = getArguments().getInt(ARG_POSITION);
        typeId = getArguments().getString(ARG_TYPE);
        title = getArguments().getString(ARG_TITLE);
        latitude = getArguments().getString(ARG_LATITUDE);
        longitude = getArguments().getString(ARG_LONGITUDE);
        bannerUrl = getArguments().getString(ARG_BANNER);
        businessString = getArguments().getString(ARG_BUSINESS_STRING);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.categories_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Fabric.with(getActivity(), new Crashlytics());
        spmanager = new SpotasManager(getActivity());
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        nobusiness = (RelativeLayout) view.findViewById(R.id.nobusiness);
        pullListView = (PullListView) view.findViewById(R.id.plv_data);

        closeAndOpenAdapter = new BussinessAdapter(getActivity(),coList);
        pullListView.setAdapter(closeAndOpenAdapter);
        setUpPullListView();
        if(coList!=null && coList.size()>0)
        {

        }
        else {
            getBusinessList();
        }
    }

    private void setUpPullListView()
    {
        pullListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if(coList.get(position - 1).getMemberFlag() != null && coList.get(position - 1).getMemberFlag().equalsIgnoreCase("0")) {
                    startActivity(position);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Alert");
                    builder.setMessage(warningMessage);
                    builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(position);
                        }
                    });//second parameter used for onclicklistener
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });
        pullListView.setOnRefreshListener(new PullListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        index = 0;
                        getBusinessList();
                        pullListView.refreshComplete();
                    }
                }, 2000);

            }
        });

        pullListView.setOnGetMoreListener(new PullListView.OnGetMoreListener() {

            @Override
            public void onGetMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        index++;
                        getBusinessList();

                        pullListView.getMoreComplete();

                        nomoredata();
                    }
                }, 2000);

            }
        });
    }

    private void nomoredata()
    {
        Iterator iterator = coList.iterator();

        while (!iterator.hasNext()) {

            pullListView.setNoMore();
        }
    }

    private void getBusinessList()
    {
        progressBar.setVisibility(View.VISIBLE);

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("UserId", spmanager.getSession_token());
            jsonObj.put("TypeId",typeId);
            jsonObj.put("Latitude", latitude);
            jsonObj.put("Longitude", longitude);
            jsonObj.put("DateTime", Utility.getCurrentDateTimeString());
            jsonObj.put("DeviceId",Utility.getDeviceId(getActivity()));
            jsonObj.put("businessesString", businessString);
            jsonObj.put("Index", index+"");

            //Shankar Kumar
        }
        catch(Exception e)
        {
        }

        Utility.doJsonRequest(VariableConstants.host_url1 + "GetBusinesses", jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                // TODO Auto-generated method stub

                progressBar.setVisibility(View.GONE);
                callServiceResponse(jsonResponse);
            }

            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub
                Utility.printLog("HomeFrag JSON DATA Error" + error);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void callServiceResponse(String jsonResponse)
    {
        try
        {
            Gson gson= new Gson();
            CloseAndOpenResonse closeAndOpenResponse = gson.fromJson(jsonResponse, CloseAndOpenResonse.class);
            if(closeAndOpenResponse.getErrNum().equals("0"))
            {
                warningMessage = closeAndOpenResponse.getAlreadyAMemberText();
                if(closeAndOpenResponse.getData().size() > 0) {

                    count++;
                    nobusiness.setVisibility(View.GONE);
                    Utility.printLog("the params in before response.getData is  " + closeAndOpenResponse.getData().size());
                    if (index == 0) {
                        coList.clear();
                    }
                    coList.addAll(closeAndOpenResponse.getData());
                    closeAndOpenAdapter.notifyDataSetChanged();

                } else {
                    if (count == 1) {
                        nobusiness.setVisibility(View.VISIBLE);
                    }
                }
            }
            else if(closeAndOpenResponse.getErrNum().equals("7"))
            {
                spmanager.setIsLogin(false);
                Intent intent1=new Intent(getActivity(), SpotAsap.class);
                startActivity(intent1);
                getActivity().finish();
            }

        }
        catch(Exception e)
        {
            Utility.printLog(e+"");
        }
    }

    private class BussinessAdapter extends ArrayAdapter
    {
        private Context mContext;
        private ArrayList<DataInCloseOpen> adrsListData = new ArrayList<DataInCloseOpen>();

        /****************************************************************************************************************/
        public BussinessAdapter(Context mContext, ArrayList<DataInCloseOpen> adrsListData)
        {
            super(mContext, 0, adrsListData);
            // TODO Auto-generated constructor stub
            this.mContext = mContext;
            this.adrsListData = adrsListData;

        }
        /**********************************************************************************************************************/
	/*private view holder class*/
        private class ViewHolder
        {
            ImageView businessPic;
            TextView businessName,businessType,businessAddress;
        }
        @Override
        public DataInCloseOpen getItem(int position)
        {
            // TODO Auto-generated method stub
            return adrsListData.get(position);
        }
        @Override
        public int getCount()
        {
            // TODO Auto-generated method stub
            return adrsListData.size();
        }
        /*********************************************************************************************************************/
        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            final DataInCloseOpen adrsItem=adrsListData.get(position);
            ViewHolder holder = null;

            ImageView imageVew;
            if(convertView==null||convertView.getTag()==null)
            {
                holder = new ViewHolder();
                imageVew=new ImageView(mContext);
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.closeandopenadapter, parent, false);
                holder.businessName= (TextView) convertView.findViewById(R.id.business_name);
                holder.businessType= (TextView) convertView.findViewById(R.id.business_type);
                holder.businessAddress= (TextView) convertView.findViewById(R.id.business_address);
                holder.businessPic= (ImageView) convertView.findViewById(R.id.business_image);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.businessName.setText(adrsListData.get(position).getName());
            holder.businessAddress.setText(adrsListData.get(position).getAddress());
            holder.businessType.setText(title);

            String url=adrsItem.getLogo();
            Utility.printLog("urlresponse of image " + url);

            if(!url.equals("") && !url.equals(null)) {
                Picasso.with(mContext).load(url)
                        .placeholder(R.drawable.news_paper_details_default_image_frame)
                        .transform(new CircleTransform())
                        .into(holder.businessPic);
            } else {
                Picasso.with(mContext).load(R.drawable.news_paper_details_default_image_frame)
                        .transform(new CircleTransform())
                        .into(holder.businessPic);// .resize((int) Math.round(width), (int) Math.round(height))
            }
            return convertView;
        }
        /*****************************************************************************************************************/
    }

    private void startActivity(int position)
    {
        if(coList.get(position-1).getIntegrationType()!=null)
        {
            String a =coList.get(position-1).getIntegrationType();
            if(coList.get(position-1).getIntegrationType().equalsIgnoreCase(getString(R.string.traditional)) || coList.get(position-1).getIntegrationType().equals(""))
            {
                Intent i = new Intent(getActivity(), PackagesActivity.class);
                i.putExtra("BANNER",bannerUrl);
                i.putExtra("Image", "");
                i.putExtra("BusinessName",coList.get(position - 1).getName());
                i.putExtra("BusinessImage",coList.get(position - 1).getLogo());
                i.putExtra("BusinessAddress",coList.get(position - 1).getAddress());
                i.putExtra("businessId", coList.get(position - 1).getId());
                i.putExtra("ConnectedFlag",coList.get(position-1).getConnectedFlag());
                i.putExtra("getCustomerNotesCheck",coList.get(position-1).getCustomerNotesCheck());
                i.putExtra("CustomerNotes", coList.get(position - 1).getCustomerNotes());
                i.putExtra("SHOW_ADDRESS", coList.get(position - 1).getDelivery_Pickup());
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.anim_two, R.anim.anim_one);


            }
            else if(coList.get(position-1).getIntegrationType().equalsIgnoreCase(getString(R.string.invoice_managment)))
            {
                Intent inventumIntent = new Intent(getActivity(), InventumActivity.class);
                inventumIntent.putExtra("BUSINESS_ID", coList.get(position - 1).getId());
                inventumIntent.putExtra("Name", coList.get(position - 1).getName());
                inventumIntent.putExtra("Image", coList.get(position - 1).getLogo());
                inventumIntent.putExtra("URL", "invMgmtGetSubcscriptions");
                inventumIntent.putExtra("TYPE", getString(R.string.invoice_managment));
                inventumIntent.putExtra("BANNER",bannerUrl);
                inventumIntent.putExtra("ADDRESS", coList.get(position - 1).getAddress());
                inventumIntent.putExtra("shoplatitude", coList.get(position - 1).getLatitude());
                inventumIntent.putExtra("shoplongitude", coList.get(position - 1).getLongitude());
                inventumIntent.putExtra("ConnectedFlag", coList.get(position - 1).getConnectedFlag());
                inventumIntent.putExtra("MorePlans", coList.get(position - 1).getShowMorePlans());

                startActivity(inventumIntent);
                getActivity().overridePendingTransition(R.anim.anim_two, R.anim.anim_one);

            }
            else if((coList.get(position-1).getIntegrationType().equalsIgnoreCase(getString(R.string.inventum))) || (coList.get(position-1).getIntegrationType().equalsIgnoreCase("ICENET")))
            {
                Intent inventumIntent = new Intent(getActivity(), InventumActivity.class);
                inventumIntent.putExtra("BUSINESS_ID", coList.get(position - 1).getId());
                inventumIntent.putExtra("Name", coList.get(position - 1).getName());
                inventumIntent.putExtra("Image", coList.get(position - 1).getLogo());
                inventumIntent.putExtra("URL", "invGetSubscriptions");
                inventumIntent.putExtra("TYPE",getString(R.string.inventum));
                inventumIntent.putExtra("BANNER", bannerUrl);
                inventumIntent.putExtra("ADDRESS", coList.get(position - 1).getAddress());
                inventumIntent.putExtra("shoplatitude", coList.get(position - 1).getLatitude());
                inventumIntent.putExtra("shoplongitude", coList.get(position - 1).getLongitude());
                inventumIntent.putExtra("ConnectedFlag",coList.get(position-1).getConnectedFlag());
                inventumIntent.putExtra("MorePlans", coList.get(position - 1).getShowMorePlans());

                startActivity(inventumIntent);
                getActivity().overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
            }
            else if(coList.get(position-1).getIntegrationType().equalsIgnoreCase(getString(R.string.tacitine)))//if IntegrationType is TRADITIONAL
            {
                Intent inventumIntent = new Intent(getActivity(), InventumActivity.class);
                inventumIntent.putExtra("BUSINESS_ID", coList.get(position - 1).getId());
                inventumIntent.putExtra("Name", coList.get(position - 1).getName());
                inventumIntent.putExtra("Image", coList.get(position - 1).getLogo());
                inventumIntent.putExtra("URL", "invGetSubscriptions");
                inventumIntent.putExtra("TYPE",getString(R.string.tacitine));
                inventumIntent.putExtra("BANNER", bannerUrl);
                inventumIntent.putExtra("ADDRESS", coList.get(position - 1).getAddress());
                inventumIntent.putExtra("shoplatitude", coList.get(position - 1).getLatitude());
                inventumIntent.putExtra("shoplongitude", coList.get(position - 1).getLongitude());
                inventumIntent.putExtra("ConnectedFlag",coList.get(position-1).getConnectedFlag());
                inventumIntent.putExtra("MorePlans", coList.get(position - 1).getShowMorePlans());

                startActivity(inventumIntent);
                getActivity().overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
            }
        }
    }
}