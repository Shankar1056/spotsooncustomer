package com.threembed;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.spotsoon.customer.R;

import io.fabric.sdk.android.Fabric;

/**
 * Created by administrator on 13/6/15.
 */
public class SupportFragment extends Fragment implements View.OnClickListener{


    RelativeLayout account_layout;
    RelativeLayout payment_layout,mailus_layout,call_us_layout;
    RelativeLayout use_spotasap_layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Fabric.with(getActivity(), new Crashlytics());
        View view = inflater.inflate(R.layout.support_fragment,container,false);

        account_layout= (RelativeLayout) view.findViewById(R.id.account_layout);
        payment_layout= (RelativeLayout) view.findViewById(R.id.payment_layout);

        use_spotasap_layout= (RelativeLayout) view.findViewById(R.id.use_spotasap_layout);
        call_us_layout= (RelativeLayout) view.findViewById(R.id.call_us_layout);
        mailus_layout= (RelativeLayout) view.findViewById(R.id.mailus_layout);

        //Shankar Kumar
        ((TextView)view.findViewById(R.id.tv_pp2)).setOnClickListener(this);
        ((TextView)view.findViewById(R.id.tv_pp3)).setOnClickListener(this);
        ((TextView)view.findViewById(R.id.tv_pp4)).setOnClickListener(this);


        call_us_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callusDialog();
            }
        });


        mailus_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recepientEmail = "support@spotsoon.com";
                Intent intent = new Intent(Intent.ACTION_SENDTO);

                intent.setType("text/plain");
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Assistance Required");
                intent.setData(Uri.parse("mailto:" + recepientEmail));
                startActivity(intent);
            }
        });

        account_layout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),WebDisplay.class);
                i.putExtra("Link","http://www.spotsoon.com/faq-user/");//http://52.27.113.174/FAQs/Account.php  http://www.spotsoon.com/faq-user/
                i.putExtra("Title","Account");
                getActivity().startActivity(i);

            }
        });
        payment_layout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),WebDisplay.class);
                i.putExtra("Link","http://www.spotsoon.com/faq-user/");//http://www.spotsoon.com/faq-user/  http://52.27.113.174/FAQs/Payment.php
                i.putExtra("Title","Payment");
                getActivity().startActivity(i);

            }
        });
        use_spotasap_layout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),WebDisplay.class);
                i.putExtra("Link","http://www.spotsoon.com/faq-user/");//http://www.spotsoon.com/faq-user/   http://52.27.113.174/FAQs/Booking.php
                i.putExtra("Title","Spotsoon");
                getActivity().startActivity(i);

            }
        });


        return view;
    }

     void callusDialog(){


         final CharSequence[] options = { "8197304955", "8197304966","Cancel"};//

         AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
         builder.setTitle("Choose Number!");
         builder.setItems(options, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int item) {
                 if (options[item].equals("8197304955"))
                 {

                     Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:8197304955"));

                startActivity(callIntent);

                 }
                 else if (options[item].equals("8197304966"))
                 {
                     Intent callIntent = new Intent(Intent.ACTION_CALL);
                     callIntent.setData(Uri.parse("tel:8197304966"));

                     startActivity(callIntent);


                 }
                 else if (options[item].equals("Cancel")) {
                     dialog.dismiss();
                 }
             }
         });
         builder.show();}


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tv_pp2:

                Intent intent = new Intent(getActivity(),WebDisplay.class);
                intent.putExtra("Link","http://www.spotsoon.com/termsconditions/");
                intent.putExtra("Title","Terms of Service");
                startActivity(intent);

                break;
            case R.id.tv_pp3:

                Intent intent1 = new Intent(getActivity(),WebDisplay.class);
                intent1.putExtra("Link","http://www.spotsoon.com/privacy-policy/");
                intent1.putExtra("Title","Privacy Policy");
                startActivity(intent1);

                break;
            case R.id.tv_pp4:

                Intent intent2 = new Intent(getActivity(),WebDisplay.class);
                intent2.putExtra("Link","http://www.spotsoon.com/termsconditions/");
                intent2.putExtra("Title","Content Policies");
                startActivity(intent2);

                break;
        }
    }
}
