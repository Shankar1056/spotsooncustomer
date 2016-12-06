package com.invoice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.spotsoon.customer.R;
import com.utility.SpotasManager;
import com.utility.Utility;
import com.utility.VariableConstants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Shankar on 8/26/2016.
 */
public class PendingPackageInvoice extends AppCompatActivity{

    private TextView mBillStartDate,mBillEndDate,mInvoiceNumber,mPreviousDue,mBillAmount,mPaidAmount,mInvoiceDay,mInvoiceMonthYear,mDueDay,mDueMonthYear;
    private TextView mDaysToDueDate,mDueDateStatus,mCurrentPlan,mConvenienceFee,tax_type;
    private TextView setup_fee,tax,outstanding;
    private String planActDate,invoicenumber = null,order_ID=null,integration_Type=null,completed=null;
    private SpotasManager SessionManager;
    private LinearLayout setupfeelayout,taxlayout;
    private View setupView,taxView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pendinginvoice_invoice);

        Intent intent = getIntent();
         invoicenumber = intent.getStringExtra("invoice_No");

        initializeViews();

          new GetPendingInvoice().execute(VariableConstants.GETINVOICEDETAILS,invoicenumber);

    }
    private void initializeViews()
    {
        Utility.statusbar(PendingPackageInvoice.this);
        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_summary);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Invoice Summary");

        SessionManager =  new SpotasManager(PendingPackageInvoice.this);


        Calendar currentCalendar = Calendar.getInstance();
        int toYear = currentCalendar.get(Calendar.YEAR);
        int toMonth = currentCalendar.get(Calendar.MONTH);
        int toDay = currentCalendar.get(Calendar.DAY_OF_MONTH);

        planActDate = toYear+"-"+makeTwoDigits(toMonth+1)+"-"+makeTwoDigits(toDay);

        mBillStartDate = (TextView)findViewById(R.id.bill_start_date);
        mBillEndDate = (TextView)findViewById(R.id.bill_end_date);
        mInvoiceNumber = (TextView)findViewById(R.id.invoice_number);
        mPreviousDue = (TextView)findViewById(R.id.previous_due_amount);
        mBillAmount = (TextView)findViewById(R.id.bill_amount);
        mPaidAmount = (TextView)findViewById(R.id.paid_amount);
        mInvoiceDay = (TextView)findViewById(R.id.invoice_day);
        mInvoiceMonthYear = (TextView)findViewById(R.id.invoice_month_year);
        mDueDay = (TextView)findViewById(R.id.invoice_due_day);
        mDueMonthYear = (TextView)findViewById(R.id.invoice_due_month_year);
        mDaysToDueDate = (TextView)findViewById(R.id.due_days_remaining);
        mDueDateStatus = (TextView)findViewById(R.id.due_dates_status);
        mCurrentPlan = (TextView)findViewById(R.id.current_plan);
        mConvenienceFee = (TextView)findViewById(R.id.convenience_fee);
        setup_fee = (TextView)findViewById(R.id.setup_fee);
        tax = (TextView)findViewById(R.id.tax);
        outstanding = (TextView)findViewById(R.id.outstanding);
        tax_type = (TextView)findViewById(R.id.tax_type);
        setupfeelayout = (LinearLayout)findViewById(R.id.setupfeelayout);
        taxlayout = (LinearLayout)findViewById(R.id.taxlayout);

        setupView = (View)findViewById(R.id.setupView);
        taxView = (View)findViewById(R.id.taxView);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.anim_three, R.anim.anim_four);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetPendingInvoice extends AsyncTask<String, Void, String> {
        String result1;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        final ProgressDialog pDialog = new ProgressDialog(PendingPackageInvoice.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            if(pDialog!=null) {
                pDialog.show();
            }


        }

        @Override
        protected String doInBackground(String... params) {

            nameValuePairs.add(new BasicNameValuePair("invoiceNumber" ,params[1]));
            nameValuePairs.add(new BasicNameValuePair("ent_sess_token" ,SessionManager.getSession_token()));
            nameValuePairs.add(new BasicNameValuePair("ent_dev_id" , Utility.getDeviceId(PendingPackageInvoice.this)));
            try {
                result1 = Utility.executeHttpPost(params[0], nameValuePairs);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result1;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(pDialog!=null && pDialog.isShowing())
            {
                pDialog.dismiss();

            }
            if (s != null) {
                try {

                    JSONObject jsonObject = new JSONObject(s);
                    String errNum = jsonObject.getString("errNum");
                    String errFlag = jsonObject.getString("errFlag");
                    String msg = jsonObject.getString("errMsg");
                    String errDesc = jsonObject.getString("errDesc");
                    String test = jsonObject.getString("test");
                    if(errFlag.equalsIgnoreCase("0"))
                    {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        setAllDetails(jsonObject1);
                    }
                    else {
                        Toast.makeText(PendingPackageInvoice.this,""+msg, Toast.LENGTH_LONG).show();
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

    private void setAllDetails(JSONObject jsonObject1) {

        try{
            if(invoicenumber!=null && invoicenumber.length()>0)
            {
                mInvoiceNumber.setText("Invoice No : "+invoicenumber);
            }
            if (jsonObject1.has("BillingStartDate"))
            {
                mBillStartDate.setText("Your bill from " +jsonObject1.getString("BillingStartDate"));
            }
            if (jsonObject1.has("BillingEndDate"))
            {
                mBillEndDate.setText(" to " +jsonObject1.getString("BillingEndDate"));
            }
            if (jsonObject1.has("PrevDue")) {
                mPreviousDue.setText(jsonObject1.getString("PrevDue"));
            }

            if (jsonObject1.has("BillAmount"))
            {
                mBillAmount.setText(jsonObject1.getString("BillAmount"));
            }
            if (jsonObject1.has("Payments"))
            {
                mPaidAmount.setText(jsonObject1.getString("Payments"));
            }
            if (jsonObject1.has("InvoiceDate"))
            {
                mInvoiceDay.setText(jsonObject1.getString("InvoiceDate"));
            }
            if (jsonObject1.has("DueDate"))
            {
                mDueDay.setText(jsonObject1.getString("DueDate"));
            }
            if (jsonObject1.has("DaysToDue"))
            {
                mDaysToDueDate.setText(jsonObject1.getString("DaysToDue"));
            }
            if (jsonObject1.has("ProductName"))
            {
                mCurrentPlan.setText(jsonObject1.getString("ProductName"));
            }
            if (jsonObject1.has("SetupFee"))
            {
                if (jsonObject1.getString("SetupFee").equalsIgnoreCase("0"))
                {
                    setupfeelayout.setVisibility(View.GONE);
                    setupView.setVisibility(View.GONE);

                }
                else
                {
                    setupfeelayout.setVisibility(View.VISIBLE);
                    setupView.setVisibility(View.VISIBLE);
                }
                setup_fee.setText(jsonObject1.getString("SetupFee"));
            }

            if (jsonObject1.has("TaxPercent"))
            {
                if (jsonObject1.getString("TaxPercent").equalsIgnoreCase("0"))
                {
                    taxlayout.setVisibility(View.GONE);
                    taxView.setVisibility(View.GONE);
                }
                else
                {
                    taxlayout.setVisibility(View.VISIBLE);
                    taxView.setVisibility(View.VISIBLE);
                }
                tax.setText(jsonObject1.getString("TaxPercent"));
            }

            if (jsonObject1.has("BalanceAmount"))
            {
                outstanding.setText(jsonObject1.getString("BalanceAmount"));
            }
            if (jsonObject1.has("TaxLabel"))
            {
                tax_type.setText(jsonObject1.getString("TaxLabel"));
            }
            else
            {
                tax_type.setText("Tax");
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

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
        finish();
        overridePendingTransition(R.anim.anim_three, R.anim.anim_four);
    }
}
