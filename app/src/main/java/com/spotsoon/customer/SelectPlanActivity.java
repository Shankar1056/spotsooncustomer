package com.spotsoon.customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.threembed.spotasap_pojos.Plans;
import com.threembed.spotasap_pojos.PlansCouponsDetails;
import com.threembed.spotasap_pojos.PlansDetailsHelper;
import com.utility.SpotasManager;
import com.utility.Utility;
import com.utility.VariableConstants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by VARUN on 7/6/2016.
 */
public class SelectPlanActivity extends AppCompatActivity{

    private ExpandableListView expandableListView;
    private SpotasManager session;
    private List<PlansDetailsHelper> listDataHeaders = new ArrayList<PlansDetailsHelper>();
    private HashMap<PlansDetailsHelper, List<PlansCouponsDetails>> listDataChild = new HashMap<PlansDetailsHelper, List<PlansCouponsDetails>>();
    private String businessId,domId,currentPlan,plansJsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(SelectPlanActivity.this, new Crashlytics());
        setContentView(R.layout.select_package);

        Intent intent = getIntent();
        if(intent!=null)
        {
            businessId = intent.getStringExtra("businessId");
            domId = intent.getStringExtra("domId");
            currentPlan = intent.getStringExtra("PLAN");
        }

        initializeViews();
        getPlans();
    }

    private void initializeViews() {

        Utility.statusbar(SelectPlanActivity.this);

        session = new SpotasManager(SelectPlanActivity.this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_select_packages);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Plan");

        findViewById(R.id.current_plan_layout).setVisibility(View.GONE);
        expandableListView = (ExpandableListView) findViewById(R.id.plans_list);
        expandableListView.setVisibility(View.VISIBLE);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                Intent intent = new Intent();
                intent.putExtra("RESPONSE",plansJsonResponse);
                intent.putExtra("CHILD_POS", childPosition);
                intent.putExtra("GROUP_POS", groupPosition);
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.anim_three, R.anim.anim_four);

                return false;
            }
        });
    }

    private void getPlans()
    {
        final ProgressDialog pDialog = new ProgressDialog(SelectPlanActivity.this);
        pDialog.setMessage("Loading plans...");
        pDialog.setCancelable(true);
        if(pDialog!=null) {
            pDialog.show();
        }

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("ent_dev_id", Utility.getDeviceId(this));
            jsonObj.put("ent_sess_token", session.getSession_token());
            jsonObj.put("ent_business_id", businessId);
            jsonObj.put("ent_dom_id", domId);
            jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());
        }
        catch(Exception e)
        {
        }

        Utility.doJsonRequest(VariableConstants.host_url + "invGetPlans", jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(final String jsonResponse) {
                // TODO Auto-generated method stub

                try {


                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.dismiss();
                    }

                    plansJsonResponse = jsonResponse;

                    Gson gson = new Gson();
                    Plans plans = gson.fromJson(jsonResponse, Plans.class);

                    if (plans != null) {
                        if (plans.getErrFlag() != null && plans.getErrFlag().equalsIgnoreCase("0")) {

                            if (plans.getPlans() != null && plans.getPlans().size() > 0) {

                                listDataHeaders = plans.getPlans();
                                listDataChild = new HashMap<PlansDetailsHelper, List<PlansCouponsDetails>>();

                                for(int i = 0; i < listDataHeaders.size(); i++) {
                                    listDataChild.put(listDataHeaders.get(i),
                                            listDataHeaders.get(i).getCoupons());
                                }

                                ExpandableListAdapter listAdapter = new ExpandableListAdapter(SelectPlanActivity.this, listDataHeaders, listDataChild);
                                // setting list adapter
                                expandableListView.setAdapter(listAdapter);
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(SelectPlanActivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub
                Utility.printLog("HomeFrag JSON DATA Error" + error);
               // Log.i("Spotsoon", "cancelDialog 6");
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }
        });
    }


    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<PlansDetailsHelper> _listDataHeader; // header titles
        // child data in format of header title, child title
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

            if(currentPlan!=null && currentPlan.length()>0)
            {
                if(currentPlan.equalsIgnoreCase(childItem.getDescription()))
                {
                    radioButton.setSelected(true);
                }
                else {
                    radioButton.setSelected(false);
                }
            }

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

    @Override
    public void onBackPressed() {

        finish();
        overridePendingTransition(R.anim.anim_three, R.anim.anim_four);
    }
}
