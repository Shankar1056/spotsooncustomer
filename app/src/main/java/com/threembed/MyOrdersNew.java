package com.threembed;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.invoice.PendingPackageInvoice;
import com.spotsoon.customer.R;
import com.threembed.spotasap_pojos.MyOrdersHelper;
import com.threembed.spotasap_pojos.MyOrdersPojo;
import com.utility.SpotasManager;
import com.utility.Utility;
import com.utility.VariableConstants;

import org.json.JSONObject;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

/**
 * Created by VARUN on 3/26/2016.
 */
public class MyOrdersNew extends Fragment{

    private ListView myOrders;
    private SpotasManager spManager;
    private RelativeLayout noOrdersLayout;
    ArrayList<MyOrdersHelper> current_order_lists = new ArrayList<MyOrdersHelper>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_orders_new, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Fabric.with(getActivity(), new Crashlytics());

        myOrders = (ListView)view.findViewById(R.id.my_orders);
        noOrdersLayout = (RelativeLayout)view.findViewById(R.id.no_orders);
        spManager = new SpotasManager(getActivity());


        getMyOrders();


    }

    private void getMyOrders()
    {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(true);
        pDialog.show();

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("ent_sess_token",spManager.getSession_token());
            jsonObj.put("ent_dev_id",Utility.getDeviceId(getActivity()));
            jsonObj.put("ent_appnt_dt",Utility.getappointmentdate()); // yyyy-mm format
            jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());

        }
        catch(Exception e)
        {}
        Utility.doJsonRequest(VariableConstants.GET_PAYMENTSTATUS + "getSlaveAppointments", jsonObj, new Utility.JsonRequestCallback() {

            @Override
            public void onSuccess(String jsonResponse) {
                if (pDialog != null) {
                    pDialog.dismiss();
                }
                if (jsonResponse != null && jsonResponse.length() > 0) {
                    try
                    {
                    callServiceResponse(jsonResponse);
                }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
            }

                else {

                    noOrdersLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                if(pDialog != null) {
                    pDialog.dismiss();
                }
                Utility.printLog("HomeFrag JSON DATA Error" + error);
            }
        });
    }

    private void callServiceResponse(String jsonResponse)
    {

        try {
            Gson gson= new Gson();
            MyOrdersPojo myorder_pojo = gson.fromJson(jsonResponse, MyOrdersPojo.class);

            if(myorder_pojo.getErrFlag().equals("0")) {

                current_order_lists = myorder_pojo.getCurrent();
                MyOrdersAdapter adapter = new MyOrdersAdapter(getActivity(),current_order_lists);
                myOrders.setAdapter(adapter);

                if(current_order_lists.size()>0)
                {
                    noOrdersLayout.setVisibility(View.GONE);
                }
                else {
                    noOrdersLayout.setVisibility(View.VISIBLE);
                }
            }else if(myorder_pojo.getErrFlag().equals("1")){
                noOrdersLayout.setVisibility(View.VISIBLE);
            }
            else {
                Toast.makeText(getActivity(), "Invalid token, please login or register", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            Log.i("My Order Exception :", e.getMessage());
            Toast.makeText(getActivity(), "Something went wrong, please try again.", Toast.LENGTH_LONG).show();
        }
    }



    private class MyOrdersAdapter extends ArrayAdapter<MyOrdersHelper>
    {
        private Context mContext;
        private ArrayList<MyOrdersHelper> orderListData = new ArrayList<MyOrdersHelper>();
        private ViewHolder holder;
        public MyOrdersAdapter(Context mContext, ArrayList<MyOrdersHelper> orderListData)
        {
            super(mContext,R.layout.my_orders_new_rowitem,orderListData);
            this.mContext = mContext;
            this.orderListData = orderListData;
        }

        /*private view holder class*/
        private class ViewHolder
        {
            //TextView order_ontheway, order_date_time,order_court_name,order_location,order_no,order_price;
            ImageView arrow;
            SeekBar mSeekBar;
            LinearLayout moreDetailsTv,moreDetailsLayout;
            RelativeLayout previousDueLayout,taxLayout,convinienceFeeLayout,getInvoiceLayout;
            TextView businessName,businessAddress,orderId,latsPaymentDate,renewalDate,tv_renewal,paymentType;
            TextView packageName,packageAmount,previousDueAmount,tax,taxAmount,convinienceFee,totalAmount,getInvoice;
            LinearLayout more_details_invoice_layout;
            TextView totalpayable_invoice,paid_invoice,outstanding_invoice;
        }

        @Override
        public int getViewTypeCount() {

            return getCount();
        }

        @Override
        public int getItemViewType(int position) {

            return position;
        }

        @Override
        public MyOrdersHelper getItem(int position)
        {
            // TODO Auto-generated method stub
            return orderListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount()
        {
            // TODO Auto-generated method stub
            return orderListData.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView==null || convertView.getTag()==null)
            {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.my_orders_new_rowitem, parent, false);
                holder.arrow = (ImageView)convertView.findViewById(R.id.arrow_image);
                holder.mSeekBar = (SeekBar)convertView.findViewById(R.id.status_seekbar);
                holder.moreDetailsTv = (LinearLayout)convertView.findViewById(R.id.details_layout);
                holder.moreDetailsLayout = (LinearLayout)convertView.findViewById(R.id.more_details_layout);

                holder.previousDueLayout = (RelativeLayout)convertView.findViewById(R.id.pre_due_layout);
                holder.taxLayout = (RelativeLayout)convertView.findViewById(R.id.tax_layout);
                holder.convinienceFeeLayout = (RelativeLayout)convertView.findViewById(R.id.convinience_layout);
                holder.getInvoiceLayout = (RelativeLayout)convertView.findViewById(R.id.invoice_layout);

                holder.businessName= (TextView) convertView.findViewById(R.id.business_name);
                holder.businessAddress= (TextView) convertView.findViewById(R.id.business_name_address);
                holder.orderId= (TextView) convertView.findViewById(R.id.order_id);
                holder.latsPaymentDate=(TextView)convertView.findViewById(R.id.last_payment_date);
                holder.renewalDate= (TextView) convertView.findViewById(R.id.renewal_date);

                holder.tv_renewal = (TextView)convertView.findViewById(R.id.tv_renewal);
                holder.packageName=(TextView)convertView.findViewById(R.id.package_name);
                holder.packageAmount= (TextView) convertView.findViewById(R.id.package_price);
                holder.previousDueAmount= (TextView) convertView.findViewById(R.id.pre_due_amount);
                holder.tax= (TextView) convertView.findViewById(R.id.tv_tax);
                holder.taxAmount=(TextView)convertView.findViewById(R.id.tax_amount);
                holder.convinienceFee= (TextView) convertView.findViewById(R.id.convinience_amount);
                holder.totalAmount=(TextView)convertView.findViewById(R.id.totalamount_price);
                holder.getInvoice=(TextView)convertView.findViewById(R.id.get_invoice);
                holder.paymentType = (TextView)convertView.findViewById(R.id.paymentType);

                holder.totalpayable_invoice= (TextView) convertView.findViewById(R.id.totalpayable_invoice);
                holder.paid_invoice=(TextView)convertView.findViewById(R.id.paid_invoice);
                holder.outstanding_invoice = (TextView) convertView.findViewById(R.id.outstanding_invoice);

                holder.more_details_invoice_layout = (LinearLayout) convertView.findViewById(R.id.more_details_invoice_layout);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            //holder.mSeekBar.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress_bar));
            holder.mSeekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Utility.getColor(getActivity(),R.color.app_blue), PorterDuff.Mode.MULTIPLY));
            holder.mSeekBar.setEnabled(false);

            if (orderListData.get(position).getOrderType().equalsIgnoreCase(getString(R.string.invoice_managment)))
            {
                holder.arrow.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.arrow.setVisibility(View.VISIBLE);
            }


            holder.moreDetailsTv.setOnClickListener(new OnDetailsClickListner(holder.moreDetailsLayout, holder.more_details_invoice_layout, holder.arrow,orderListData.get(position).getOrderType(),orderListData.get(position).getInvoice().getInvoiceNumber()));
            holder.getInvoice.setOnClickListener(new GetInvoiceClickListner(holder.getInvoice, position,orderListData.get(position).getBid()));

            if(orderListData.get(position).getMypackage()!=null && orderListData.get(position).getMypackage().length()>0)
            {
                holder.businessName.setVisibility(View.VISIBLE);
                holder.businessName.setText(orderListData.get(position).getMypackage());
            }
            else {
                holder.businessName.setVisibility(View.GONE);
            }


           if (orderListData.get(position).getOrderType().equalsIgnoreCase(getString(R.string.icenet))) {

               holder.paid_invoice.setText(orderListData.get(position).getAmountPaid());
               holder.outstanding_invoice.setText(orderListData.get(position).getBalance());
               holder.totalpayable_invoice.setText(orderListData.get(position).getTotalPayable());
           }





            if (orderListData.get(position).getPaymentMethod()!=null && orderListData.get(position).getPaymentMethod().length()>0)
            {
                holder.paymentType.setText(orderListData.get(position).getPaymentMethod());
            }

            if(orderListData.get(position).getBussiness()!=null && orderListData.get(position).getBussiness().length()>0)
            {
                holder.businessAddress.setVisibility(View.VISIBLE);
                if(orderListData.get(position).getAddrLine2()!=null && orderListData.get(position).getAddrLine2().length()>0)
                {
                    holder.businessAddress.setText(orderListData.get(position).getBussiness() + ", " + orderListData.get(position).getAddrLine2());
                }
                else {
                    holder.businessAddress.setText(orderListData.get(position).getBussiness());
                }
            }
            else {
                holder.businessAddress.setVisibility(View.GONE);
            }

            if(orderListData.get(position).getBid()!=null && orderListData.get(position).getBid().length()>0)
            {
                holder.orderId.setVisibility(View.VISIBLE);
                holder.orderId.setText("Order ID : " + orderListData.get(position).getBid());
            }
            else {
                holder.orderId.setVisibility(View.GONE);
            }

            if(orderListData.get(position).getPrevDue()!=null && orderListData.get(position).getPrevDue().length()>0)
            {
                try {

                    double amt = Double.valueOf(orderListData.get(position).getPrevDue());
                    if(amt > 0)
                    {
                        holder.previousDueAmount.setText(Utility.round(amt));
                        holder.previousDueLayout.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.previousDueLayout.setVisibility(View.GONE);
                    }
                }
                catch (NumberFormatException e )
                {
                    holder.previousDueLayout.setVisibility(View.GONE);
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    holder.previousDueLayout.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
            else {
                holder.previousDueLayout.setVisibility(View.GONE);
            }

            if(orderListData.get(position).getTax()!=null && orderListData.get(position).getTax().length()>0)
            {
                if (orderListData.get(position).getTaxLabel()!=null) {
                    holder.tax.setText(orderListData.get(position).getTaxLabel() + "(" + orderListData.get(position).getTaxPercent() + "%)");
                }
                else {
                    holder.tax.setText( "Tax(" + orderListData.get(position).getTaxPercent() + "%)");

                }
                try {

                    double amt = Double.valueOf(orderListData.get(position).getTax());
                    if(amt > 0)
                    {
                        holder.taxAmount.setText(Utility.round(amt));
                        holder.taxLayout.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.taxLayout.setVisibility(View.GONE);
                    }
                }
                catch(NumberFormatException e)
                {
                    holder.taxLayout.setVisibility(View.GONE);
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    holder.taxLayout.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
            else {
                holder.taxLayout.setVisibility(View.GONE);
            }

            if(orderListData.get(position).getConvFee()!=null && orderListData.get(position).getConvFee().length()>0)
            {
                try {
                    double amt = Double.valueOf(orderListData.get(position).getConvFee());
                    if(amt > 0)
                    {
                        holder.convinienceFee.setText(Utility.round(amt));
                        holder.convinienceFeeLayout.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.convinienceFeeLayout.setVisibility(View.GONE);
                    }
                }
                catch (NumberFormatException e )
                {
                    holder.convinienceFeeLayout.setVisibility(View.GONE);
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    holder.convinienceFeeLayout.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
            else {
                holder.convinienceFeeLayout.setVisibility(View.GONE);
            }

            if(orderListData.get(position).getTotal()!=null && orderListData.get(position).getTotal().length()>0)
            {
                holder.totalAmount.setText(Utility.round(Double.valueOf(orderListData.get(position).getTotal())));
            }

            if(orderListData.get(position).getPayDt()!=null && orderListData.get(position).getPayDt().length()>0)
            {
                holder.latsPaymentDate.setVisibility(View.VISIBLE);
                holder.latsPaymentDate.setText(orderListData.get(position).getPayDt());
            }
            else {
                holder.latsPaymentDate.setVisibility(View.GONE);
            }

            if(orderListData.get(position).getRenewalDt()!=null && orderListData.get(position).getRenewalDt().length()>0)
            {
                holder.renewalDate.setVisibility(View.VISIBLE);
                holder.renewalDate.setText(orderListData.get(position).getRenewalDt());
            }
            else {
                holder.renewalDate.setVisibility(View.GONE);
            }

            if(orderListData.get(position).getStatus()!= null && orderListData.get(position).getStatus().length()>0)
            {
                if(orderListData.get(position).getStatus().equalsIgnoreCase("0"))
                {
                    holder.mSeekBar.setProgress(0);
                }
                else if(orderListData.get(position).getStatus().equalsIgnoreCase("1"))
                {
                    holder.mSeekBar.setProgress(1);
                }
                else if(orderListData.get(position).getStatus().equalsIgnoreCase("2"))
                {
                    holder.mSeekBar.setProgress(2);
                }
                else if(orderListData.get(position).getStatus().equalsIgnoreCase("3"))
                {
                    holder.mSeekBar.setProgress(2);

                    if(orderListData.get(position).getOrderType()!= null && orderListData.get(position).getOrderType().length()>0)
                    {
                        if(orderListData.get(position).getOrderType().equalsIgnoreCase(getString(R.string.inventum)))
                        {
                            holder.tv_renewal.setText("Recharge Scheduled");
                        }
                        else {
                            holder.tv_renewal.setText("Renewal");
                        }
                    }
                }
            }

            if(orderListData.get(position).getSendInvoice()!= null && orderListData.get(position).getSendInvoice().length()>0)
            {
                if(orderListData.get(position).getSendInvoice().equalsIgnoreCase("YES"))
                {
                    holder.getInvoice.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.getInvoiceLayout.setVisibility(View.GONE);
                }
            }

            holder.packageName.setText(orderListData.get(position).getMypackage());



            if(orderListData.get(position).getAmount()!=null && orderListData.get(position).getAmount().length()>0)
            {
                holder.packageAmount.setText(Utility.round(Double.valueOf(orderListData.get(position).getAmount())));
            }

            if(orderListData.get(position).getPurchaseType()!=null && orderListData.get(position).getPurchaseType().length()>0)
            {
                if(orderListData.get(position).getPurchaseType().equalsIgnoreCase(getString(R.string.invoice)))
                {

                    try {

                        if(orderListData.get(position).getInvoice()!=null)
                        {
                            if(orderListData.get(position).getInvoice().getPendingAmount() !=null && orderListData.get(position).getInvoice().getPendingAmount().length()>0)
                            {
                                holder.packageAmount.setText(Utility.round(Double.valueOf(orderListData.get(position).getInvoice().getPendingAmount())));
                            }
                            else {
                                holder.packageAmount.setText(Utility.round(Double.valueOf(orderListData.get(position).getAmount())));
                            }

                            if(orderListData.get(position).getInvoice().getInvoiceNo()!=null && orderListData.get(position).getInvoice().getInvoiceNo().length()>0) {
                                holder.tv_renewal.setText("Invoice No");
                                holder.renewalDate.setText(orderListData.get(position).getInvoice().getInvoiceNo());
                            }
                            else {
                                holder.tv_renewal.setVisibility(View.GONE);
                                holder.renewalDate.setVisibility(View.GONE);
                            }
                        }
                        else {
                            holder.tv_renewal.setVisibility(View.GONE);
                            holder.renewalDate.setVisibility(View.GONE);
                        }
                    }
                    catch (JsonSyntaxException e)
                    {}
                }
            }


            return convertView;
        }
    }





    private void rotate(float degree,View view) {
        final RotateAnimation rotateAnim = new RotateAnimation(0.0f, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(0);
        rotateAnim.setFillAfter(true);
        view.startAnimation(rotateAnim);
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private class  OnDetailsClickListner implements View.OnClickListener
    {
        private View detailsView,arrowView,view_Icenet;
        private String order_Type,invoice_No;

        public OnDetailsClickListner(View view, View viewIcenet, View arrView, String orderType, String invoiceNo) {

            detailsView = view;
            view_Icenet = viewIcenet;
            arrowView = arrView;
            order_Type = orderType;
            invoice_No = invoiceNo;
        }
        @Override
        public void onClick(View v) {


            if(detailsView!=null)
            {
                if (order_Type.equalsIgnoreCase(getString(R.string.invoice_managment)))
                {
                    Intent intent = new Intent(getActivity(),PendingPackageInvoice.class);
                    intent.putExtra("invoice_No",invoice_No);
                    startActivity(intent);
                }
                else if (order_Type.equalsIgnoreCase(getString(R.string.icenet)))
                {
                    if(view_Icenet.getVisibility() == View.VISIBLE)
                    {
                        if(arrowView!=null)
                        {
                            rotate(0, arrowView);
                        }
                        collapse(view_Icenet);
                    }
                    else {

                        if(arrowView!=null)
                        {
                            rotate(180, arrowView);
                        }
                        expand(view_Icenet);
                    }
                }

                else {
                if(detailsView.getVisibility() == View.VISIBLE)
                {
                    if(arrowView!=null)
                    {
                        rotate(0, arrowView);
                    }
                    collapse(detailsView);
                }
                else {

                    if(arrowView!=null)
                    {
                        rotate(180, arrowView);
                    }
                   expand(detailsView);
                }
            }
            }

        }
    }

    private class  GetInvoiceClickListner implements View.OnClickListener
    {
        private View invoiceView;
        private int position;
        private String TxonId;
        public GetInvoiceClickListner(View view,int pos,String txnId) {

            invoiceView = view;
            position = pos;
            TxonId = txnId;

        }

        @Override
        public void onClick(View v) {

            if(invoiceView!=null)
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Invoice");
                    builder.setMessage("Invoice will be sent to registered Email Address");
                    builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            getInvoice(TxonId);
                        }
                    });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
            else {
            }
        }
    }

    private void getInvoice(String txonId)
    {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Sending invoice...");
        pDialog.setCancelable(true);
        pDialog.show();

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("ent_sess_token",spManager.getSession_token());
            jsonObj.put("ent_dev_id",Utility.getDeviceId(getActivity()));
           // jsonObj.put("ent_appnt_dt",Utility.getappointmentdate()); // yyyy-mm format
            jsonObj.put("ent_date_time", Utility.getCurrentDateTimeString());
            jsonObj.put("ent_invoice_id",txonId);

        }
        catch(Exception e)
        { }
        //getSlaveAppointments
        Utility.doJsonRequest(VariableConstants.host_url + "getInvoice", jsonObj, new Utility.JsonRequestCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                if (pDialog != null) {
                    pDialog.dismiss();
                }

                Gson gson= new Gson();
                MyOrdersPojo myorder_pojo = gson.fromJson(jsonResponse, MyOrdersPojo.class);

                if(myorder_pojo.getErrFlag().equals("0")) {

                    Toast.makeText(getActivity(),"Invoice sent to registered email address",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getActivity(),"Something went wrong, please try again.",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String error) {
                if (pDialog != null) {
                    pDialog.dismiss();
                }
                Utility.printLog("getInvoice JSON DATA Error" + error);
            }
        });
    }
}
