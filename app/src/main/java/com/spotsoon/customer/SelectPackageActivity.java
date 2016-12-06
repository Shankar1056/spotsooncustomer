package com.spotsoon.customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.threembed.HomeFragmentNew;
import com.threembed.spotasap_pojos.Plans;
import com.threembed.spotasap_pojos.PlansCouponsDetails;
import com.threembed.spotasap_pojos.PlansDetailsHelper;
import com.utility.SpotasManager;
import com.utility.Utility;
import com.utility.VariableConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by varun on 30/6/16.
 */
public class SelectPackageActivity extends AppCompatActivity implements View.OnClickListener{

    private ExpandableListView expandableListView;
    private Button changePlan;
    private String providerName,type,businessId,encodedString,domId,packageId,morePlans,jsonResponse;
    private SpotasManager session;
    private RelativeLayout currentPlansLayout;
    private ArrayList<PlansCouponsDetails> plansList = new ArrayList<PlansCouponsDetails>();
    private boolean from_home;
    private List<PlansDetailsHelper> listDataHeaders = new ArrayList<PlansDetailsHelper>();
    private HashMap<PlansDetailsHelper, List<PlansCouponsDetails>> listDataChild = new HashMap<PlansDetailsHelper, List<PlansCouponsDetails>>();
    private String plansJsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(SelectPackageActivity.this, new Crashlytics());
        setContentView(R.layout.select_package);

        Intent intent = getIntent();
        if(intent!=null) {
            providerName = intent.getStringExtra("PROVIDER");
            type = intent.getStringExtra("TYPE");
            businessId = intent.getStringExtra("BUSINESS_ID");
            encodedString = intent.getStringExtra("ENCODED");
            domId = intent.getStringExtra("DOM_ID");
            packageId = intent.getStringExtra("PACKAGE");
            morePlans = intent.getStringExtra("MorePlans");
            jsonResponse = intent.getStringExtra("JsonResponse");

            from_home = intent.getBooleanExtra("FROM_HOME", false);
            if(from_home)
            {
                plansList = HomeFragmentNew.subscriptions.get(0).getPackages().get(0).getCoupons();
            }
            else {
                plansList = InventumActivity.subscriptions.get(0).getPackages().get(0).getCoupons();
            }
        }

        initializeViews();

        if(morePlans!=null && morePlans.length()>0)
        {
            if(morePlans.equalsIgnoreCase("YES"))
            {
                changePlan.setVisibility(View.VISIBLE);
            }
            else {
                changePlan.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initializeViews() {

        Utility.statusbar(SelectPackageActivity.this);

        session = new SpotasManager(SelectPackageActivity.this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_select_packages);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Plan");

        currentPlansLayout = (RelativeLayout)findViewById(R.id.current_plan_layout);
        ListView currentPlansListview = (ListView)findViewById(R.id.current_plans_listView);
        expandableListView = (ExpandableListView)findViewById(R.id.plans_list);
        TextView currentPlan = (TextView)findViewById(R.id.current_plan);
        changePlan = (Button)findViewById(R.id.change_plan);

        changePlan.setOnClickListener(this);

        if(packageId!=null && packageId.length()>0)
        {
            currentPlan.setText(packageId);
        }

        CurrentPlansAdapter adapter = new CurrentPlansAdapter(SelectPackageActivity.this,plansList);
        currentPlansListview.setAdapter(adapter);

        currentPlansListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String base64_coupon = null;


                if(jsonResponse!=null && jsonResponse.length()>0)
                {
                    try {


                        JSONObject object1 = new JSONObject(jsonResponse);
                        JSONArray subscriptionsArray = object1.getJSONArray("subscriptions");
                        JSONObject subscriptionsObject = subscriptionsArray.getJSONObject(0);


                        JSONArray packagesArray = subscriptionsObject.getJSONArray("Packages");
                        JSONObject packagesObject = packagesArray.getJSONObject(0);


                        JSONArray couponsArray = packagesObject.getJSONArray("Coupons");
                        JSONObject couponsObject = couponsArray.getJSONObject(position);


                        byte[] data_coupon = couponsObject.toString().getBytes("UTF-8");
                        base64_coupon = Base64.encodeToString(data_coupon, Base64.DEFAULT);

                    }
                    catch (JSONException e)
                    {
                    }
                    catch (UnsupportedEncodingException e)
                    {
                    }
                }

                updateUserPlan(base64_coupon,position,0,false);


            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                String base64_coupon = null;
                try {

                    JSONObject object = new JSONObject(plansJsonResponse);
                    JSONArray plansArray = object.getJSONArray("plans");
                    JSONObject plansObject = plansArray.getJSONObject(groupPosition);

                    JSONArray couponsArray = plansObject.getJSONArray("Coupons");
                    JSONObject couponObject = couponsArray.getJSONObject(childPosition);


                    byte[] data = couponObject.toString().getBytes("UTF-8");
                    base64_coupon = Base64.encodeToString(data, Base64.DEFAULT);
                }
                catch (JSONException e)
                {}
                catch (UnsupportedEncodingException e)
                {}

                updateUserPlan(base64_coupon,groupPosition,childPosition,true);



                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.change_plan)
        {
            getPlans();
        }
    }

    private class CurrentPlansAdapter extends ArrayAdapter<PlansCouponsDetails>
    {
        private Context mContext;
        private ArrayList<PlansCouponsDetails> dataList = new ArrayList<PlansCouponsDetails>();

        public CurrentPlansAdapter(Context mContext, ArrayList<PlansCouponsDetails> adrsListData)
        {
            super(mContext, 0, adrsListData);
            // TODO Auto-generated constructor stub
            this.mContext = mContext;
            this.dataList = adrsListData;
        }
        private class ViewHolder
        {
            TextView plan_name,plan_type,plan_price;
            ImageView radioButton;
            private RelativeLayout layout;
        }
        @Override
        public PlansCouponsDetails getItem(int position)
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
            PlansCouponsDetails rowItem = dataList.get(position);
            ViewHolder holder = null;
            if(convertView==null||convertView.getTag()==null)
            {
                holder = new ViewHolder();
                // inflate the layout
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.plans_rowitem, parent, false);
                holder.plan_name= (TextView) convertView.findViewById(R.id.plan_name);
                holder.plan_type= (TextView) convertView.findViewById(R.id.plan_type);
                holder.plan_price= (TextView) convertView.findViewById(R.id.plan_price);
                holder.radioButton=(ImageView)convertView.findViewById(R.id.radio_btn);
                holder.layout = (RelativeLayout)convertView.findViewById(R.id.parent_layout);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.plan_name.setText(rowItem.getDescription());
            holder.plan_type.setText(rowItem.getCouponID());
            holder.plan_price.setText("Rs." + Utility.round(Double.valueOf(rowItem.getTotal())));


            return convertView;
        }
    }

    private void getPlans()
    {
        final ProgressDialog pDialog = new ProgressDialog(SelectPackageActivity.this);
        pDialog.setMessage("Loading plans...");
        pDialog.setCancelable(true);
        if(pDialog!=null) {
            pDialog.show();
        }

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("ent_dev_id",Utility.getDeviceId(this));
            jsonObj.put("ent_sess_token", session.getSession_token());
            jsonObj.put("ent_business_id", businessId);
            jsonObj.put("ent_dom_id", domId);
            jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());
        }
        catch(Exception e)
        {
        }

        //calling doJsonRequest of Utility class
        Utility.doJsonRequest(VariableConstants.host_url + "invGetPlans", jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(final String jsonResponse) {
                // TODO Auto-generated method stub

                try {

                    plansJsonResponse = jsonResponse;

                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.dismiss();
                    }

                    Gson gson = new Gson();
                    Plans plans = gson.fromJson(jsonResponse, Plans.class);

                    if (plans != null) {
                        if (plans.getErrFlag() != null && plans.getErrFlag().equalsIgnoreCase("0")) {

                            if (plans.getPlans() != null && plans.getPlans().size() > 0) {

                                currentPlansLayout.setVisibility(View.GONE);
                                expandableListView.setVisibility(View.VISIBLE);

                                getSupportActionBar().setTitle("Select a plan");

                                listDataHeaders = plans.getPlans();
                                listDataChild = new HashMap<PlansDetailsHelper, List<PlansCouponsDetails>>();

                                for(int i = 0; i < listDataHeaders.size(); i++) {
                                    listDataChild.put(listDataHeaders.get(i),
                                            listDataHeaders.get(i).getCoupons());
                                }

                                ExpandableListAdapter listAdapter = new ExpandableListAdapter(SelectPackageActivity.this, listDataHeaders, listDataChild);
                                // setting list adapter
                                expandableListView.setAdapter(listAdapter);

                            }
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(SelectPackageActivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub
                Utility.printLog("HomeFrag JSON DATA Error" + error);
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }
        });
    }


    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<PlansDetailsHelper> _listDataHeader; // header titles
        private HashMap<PlansDetailsHelper, List<PlansCouponsDetails>> _listDataChild;

        public ExpandableListAdapter(Context context, List<PlansDetailsHelper> listDataHeader,
                                     HashMap<PlansDetailsHelper, List<PlansCouponsDetails>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public PlansCouponsDetails getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final PlansCouponsDetails childItem = (PlansCouponsDetails) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.plans_rowitem, null);
            }

            TextView plan_name = (TextView) convertView.findViewById(R.id.plan_name);
            TextView plan_type= (TextView) convertView.findViewById(R.id.plan_type);
            TextView plan_price= (TextView) convertView.findViewById(R.id.plan_price);
            ImageView radioButton=(ImageView)convertView.findViewById(R.id.radio_btn);

            plan_type.setVisibility(View.GONE);
            plan_name.setText(childItem.getDescription());
            plan_price.setText("Rs."+childItem.getTotal());

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .size();
        }

        @Override
        public PlansDetailsHelper getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            PlansDetailsHelper headerItem = (PlansDetailsHelper) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.plans_list_group, null);
            }

            TextView lblListHeader = (TextView) convertView.findViewById(R.id.plan_name);
            lblListHeader.setText(headerItem.getRatePlan());

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUserPlan(final String newPackage,final int groupPos, final int childPos,final boolean isPlanChange)
    {
        final ProgressDialog pDialog = new ProgressDialog(SelectPackageActivity.this);
        pDialog.setMessage("Please wait, Updating...");
        pDialog.setCancelable(true);
        if(pDialog!=null) {
            pDialog.show();
        }

        Calendar currentCalendar = Calendar.getInstance();
        int toYear = currentCalendar.get(Calendar.YEAR);
        int toMonth = currentCalendar.get(Calendar.MONTH);
        int toDay = currentCalendar.get(Calendar.DAY_OF_MONTH);

        String planActDate = toYear+"-"+makeTwoDigits(toMonth+1)+"-"+makeTwoDigits(toDay);

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("ent_dev_id",Utility.getDeviceId(this));
            jsonObj.put("ent_sess_token", session.getSession_token());
            jsonObj.put("ent_business_id", businessId);
            jsonObj.put("ent_act_id", encodedString);
            jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());
            jsonObj.put("ent_package", newPackage);
            jsonObj.put("rc_scheduled_date", planActDate);

        }
        catch(Exception e)
        {
        }
        //calling doJsonRequest of Utility class
        Utility.doJsonRequest(VariableConstants.host_url + "invGetPayuId", jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                // TODO Auto-generated method stub

                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }

                try {

                    if (jsonResponse != null && jsonResponse.length() > 0) {
                        JSONObject object = new JSONObject(jsonResponse);
                        String errFlag = object.getString("errFlag");
                        if (errFlag != null && errFlag.equalsIgnoreCase("0")) {
                            String payuId = object.getString("payuId");
                            Intent intent = new Intent(SelectPackageActivity.this, SubscriptionsSummaryAvtivity.class);

                            intent.putExtra("PROVIDER",providerName);
                            intent.putExtra("TYPE",type);
                            intent.putExtra("BUSINESS_ID",businessId);
                            intent.putExtra("ENCODED",encodedString);
                            intent.putExtra("DOM_ID", domId);
                            intent.putExtra("FROM_HOME", from_home);
                            intent.putExtra("MorePlans", morePlans);

                            intent.putExtra("PLAN_CHANGED",true);
                            intent.putExtra("PAYU_ID",payuId);

                            if(isPlanChange)//if it is form change plan
                            {
                                PlansCouponsDetails plan =  listDataChild.get(listDataHeaders.get(groupPos)).get(childPos);

                                intent.putExtra("PACKAGE", plan.getCouponID());
                                intent.putExtra("CouponID", plan.getCouponID());
                                intent.putExtra("Description", plan.getDescription());
                                intent.putExtra("Price", plan.getPrice());
                                intent.putExtra("Tax", plan.getTax());
                                intent.putExtra("ParentRatePlan", plan.getParentRatePlan());
                                intent.putExtra("TaxPercent", plan.getTaxPercent());
                                intent.putExtra("SubTotal", plan.getSubTotal());
                                intent.putExtra("convenienceFee", plan.getConvenienceFee());
                                intent.putExtra("Total", plan.getTotal());

                            }
                            else {


                                PlansCouponsDetails plan = plansList.get(groupPos);
                                intent.putExtra("PACKAGE", plan.getParentRatePlan());
                                intent.putExtra("CouponID", plan.getCouponID());
                                intent.putExtra("Description", plan.getDescription());
                                intent.putExtra("Price", plan.getPrice());
                                intent.putExtra("Tax", plan.getTax());
                                intent.putExtra("ParentRatePlan", plan.getParentRatePlan());
                                intent.putExtra("TaxPercent", plan.getTaxPercent());
                                intent.putExtra("SubTotal", plan.getSubTotal());
                                intent.putExtra("convenienceFee", plan.getConvenienceFee());
                                intent.putExtra("Total", plan.getTotal());

                            }

                            if(newPackage!=null)
                                intent.putExtra("CurrentPackage", newPackage);

                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_two, R.anim.anim_one);

                        } else {
                            Toast.makeText(SelectPackageActivity.this, object.getString("errMsg"), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(SelectPackageActivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub
                Utility.printLog("HomeFrag JSON DATA Error" + error);

                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }
        });
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


    @Override
    public void onBackPressed() {

        if(expandableListView.getVisibility() == View.VISIBLE)
        {
            getSupportActionBar().setTitle("Select Plan");
            currentPlansLayout.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.GONE);
        }
        else
        {
            finish();
            overridePendingTransition(R.anim.anim_three, R.anim.anim_four);
        }
    }
}
