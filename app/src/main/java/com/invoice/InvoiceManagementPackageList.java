package com.invoice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.spotsoon.customer.PostpayInvoiceSummaryActivity;
import com.spotsoon.customer.R;
import com.threembed.homebean.InvoiceManagementPackageListModel;
import com.utility.Utility;

import java.util.ArrayList;

/**
 * Created by Shankar on 8/13/2016.
 */
public class InvoiceManagementPackageList extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ListView listView;
    Bundle bundle = null;

    ArrayList<InvoiceManagementPackageListModel> invoicepackageList = new ArrayList<InvoiceManagementPackageListModel>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoicemgnt_packagelist);

        initializeViews();

    }
    private void initializeViews() {
        Utility.statusbar(InvoiceManagementPackageList.this);
        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_subcriptions);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select to pay" +
                "");

        listView = (ListView)findViewById(R.id.connected_list);
        listView.setOnItemClickListener(this);

        getalldata();

    }

    private void getalldata() {
        bundle = InvoiceManagementPackageList.this.getIntent().getExtras();
        if (invoicepackageList.size()>0){
            invoicepackageList.clear();
        }
        invoicepackageList =  bundle.getParcelableArrayList("allinvoiceList");
if (invoicepackageList.size()>0 && invoicepackageList!=null) {
    listView.setAdapter(new ConnectedBillsAdapter(InvoiceManagementPackageList.this, invoicepackageList));
}
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Double payableamnt = null,confee = null;
        try {

        Double total = getTotal(invoicepackageList.get(position).getPrevDue(),invoicepackageList.get(position).getBillAmount(),invoicepackageList.get(position).getPayments());
         confee = getConFee(invoicepackageList.get(position).getConvenienceFeeRate(),total);
         payableamnt = total + confee;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Intent intent = new Intent(InvoiceManagementPackageList.this, PostpayInvoiceSummaryActivity.class);
        intent.putExtra("PROVIDER", bundle.getString("PROVIDER"));
        intent.putExtra("TYPE",bundle.getString("TYPE"));
        intent.putExtra("BUSINESS_ID", bundle.getString("BUSINESS_ID"));
        intent.putExtra("ENCODED", bundle.getString("ENCODED"));
        intent.putExtra("DOM_ID", "");
        intent.putExtra("PACKAGE", invoicepackageList.get(position).getProductName());
        intent.putExtra("MorePlans",bundle.getString("MorePlans"));

        intent.putExtra("invoiceDate", invoicepackageList.get(position).getInvoiceDate());
        intent.putExtra("dueDate", invoicepackageList.get(position).getDueDate());
        intent.putExtra("billStartDate", invoicepackageList.get(position).getBillingStartDate());
        intent.putExtra("billEndDate", invoicepackageList.get(position).getBillingEndDate());
        intent.putExtra("billAmount", invoicepackageList.get(position).getBillAmount());
        intent.putExtra("pendingAmount",invoicepackageList.get(position).getPrevDue());
        intent.putExtra("payableAmount", ""+payableamnt);
        intent.putExtra("convenienceRate", invoicepackageList.get(position).getConvenienceFeeRate());
        intent.putExtra("convenienceFee",""+confee);
        intent.putExtra("Total", ""+payableamnt);
        intent.putExtra("amountEditable",invoicepackageList.get(position).getPartialPayment());
        intent.putExtra("invoiceHTML", "");
        intent.putExtra("daysToDue", invoicepackageList.get(position).getDaysToDue());
        intent.putExtra("invoiceNo", invoicepackageList.get(position).getInvoiceNum());
        intent.putExtra("paymentsDone", invoicepackageList.get(position).getPayments());
        intent.putExtra("openingBalance",invoicepackageList.get(position).getPrevDue());
        //  intent.putExtra("TaxAmount", jsonObject1.getString("TaxAmount"));


        startActivity(intent);
        overridePendingTransition(R.anim.anim_two, R.anim.anim_one);

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        return true;
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
    private class ConnectedBillsAdapter extends ArrayAdapter<InvoiceManagementPackageListModel>
    {
        private Context mContext;
        private ArrayList<InvoiceManagementPackageListModel> dataList = new ArrayList<InvoiceManagementPackageListModel>();

        public ConnectedBillsAdapter(Context mContext, ArrayList<InvoiceManagementPackageListModel> adrsListData)
        {
            super(mContext, 0, adrsListData);
            // TODO Auto-generated constructor stub
            this.mContext = mContext;
            this.dataList = adrsListData;
        }
        private class ViewHolder
        {
            TextView packageName,dueDate,view_plans;
        }
        @Override
        public InvoiceManagementPackageListModel getItem(int position)
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
        @SuppressLint("LongLogTag")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            InvoiceManagementPackageListModel rowItem = dataList.get(position);
            ViewHolder holder = null;
            if(convertView==null||convertView.getTag()==null)
            {
                holder = new ViewHolder();
                // inflate the layout
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.invoicemgnt_packagelis_item, parent, false);
                holder.packageName= (TextView) convertView.findViewById(R.id.packageName);
                holder.dueDate= (TextView) convertView.findViewById(R.id.dueDate);
                holder.view_plans= (TextView) convertView.findViewById(R.id.view_plans);


                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            if (rowItem.getPrevDue().equalsIgnoreCase("null")){
                rowItem.setPrevDue("0");
            }
            if (rowItem.getBillAmount().equalsIgnoreCase("null")){
                rowItem.setBillAmount("0");
            }
            if (rowItem.getPayments().equalsIgnoreCase("null")){
                rowItem.setPayments("0");
            }
if (rowItem.getPrevDue()!=null && rowItem.getBillAmount()!=null && rowItem.getPayments()!=null) {
    try {

        Double total = getTotal(rowItem.getPrevDue(), rowItem.getBillAmount(), rowItem.getPayments());
        Double confee = getConFee(rowItem.getConvenienceFeeRate(), total);
        Double payableamnt = total + confee;
        holder.view_plans.setText("" + payableamnt);
    }
    catch (Exception e){
        Log.i("invoice management package list",e.getMessage());
    }
}

            holder.packageName.setText(rowItem.getProductName());
            holder.dueDate.setText("Due Date : "+rowItem.getDueDate());

            return convertView;
        }
    }

    public static Double getConFee(String convenienceFeeRate, Double total) {
        double conveniencefeerate, totl,confee;
        conveniencefeerate =  Double.valueOf(convenienceFeeRate);
        totl = total;
        confee = ((conveniencefeerate * totl) /100);

        return confee;
    }
    public  Double getTotal(String prevDue, String billAmount, String payments) {

        double prevdue, billamount,payment,total;
        prevdue = Double.valueOf(prevDue);
        billamount = Double.valueOf(billAmount);
        payment = Double.valueOf(payments);
        total =((prevdue + billamount)-payment);
        return total;
    }
}
