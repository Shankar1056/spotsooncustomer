package com.spotsoon.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.utility.Utility;

import io.fabric.sdk.android.Fabric;

/**
 * Created by VARUN on 4/16/2016.
 */
public class AddOrderDetailsActivity extends AppCompatActivity{

    private EditText userName,houseNumber,landMark,city,pincode;
    private TextView cusomerNotes;
    private boolean isAddressMandatory,isUserNameMandatory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(AddOrderDetailsActivity.this, new Crashlytics());
        setContentView(R.layout.addorder_details);
        initializeViews();

    }

    private void initializeViews()
    {
        Utility.statusbar(AddOrderDetailsActivity.this);

        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_subcriptions);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setTitle("Add Order Details");

        cusomerNotes = (TextView)findViewById(R.id.title);
        userName = (EditText)findViewById(R.id.enter_username);
        houseNumber = (EditText)findViewById(R.id.house_number);
        landMark = (EditText)findViewById(R.id.landmark);
        city = (EditText)findViewById(R.id.city);
        pincode = (EditText)findViewById(R.id.pincode);
        Button  done = (Button)findViewById(R.id.all_done);
        RelativeLayout addressLayout = (RelativeLayout)findViewById(R.id.layout2);

        if(PackagesActivity.showAddress!=null && PackagesActivity.showAddress.length()>0)
        {
            if(PackagesActivity.showAddress.equalsIgnoreCase("2"))//1-show, 2-don't show
            {
                isAddressMandatory = true;
                addressLayout.setVisibility(View.VISIBLE);
            }
            else {
                isAddressMandatory = false;
                addressLayout.setVisibility(View.GONE);
            }
        }

        if(PackagesActivity.getCustomerNotesCheck!=null && PackagesActivity.getCustomerNotesCheck.length()>0)
        {
            if(PackagesActivity.getCustomerNotesCheck.equalsIgnoreCase("YES") || PackagesActivity.getCustomerNotesCheck.equalsIgnoreCase("1"))
            {
                isUserNameMandatory = true;
            }
            else {
                isUserNameMandatory = false;
            }
        }

        if(PackagesActivity.customerNotes!=null && PackagesActivity.customerNotes.length()>0)
        {
            cusomerNotes.setText(PackagesActivity.customerNotes);
        }

        if(PackagesActivity.userName!=null && PackagesActivity.userName.length()>0)
        {
            userName.setText(PackagesActivity.userName);
        }

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAddressMandatory)//address mandatory
                {
                    if(validateTextFields())
                    {
                        Intent intent = new Intent(AddOrderDetailsActivity.this, TraditionalSummaryActivity.class);
                        intent.putExtra("USERNAME",userName.getText().toString().trim());
                        intent.putExtra("HOUSE_NUMBER",houseNumber.getText().toString().trim());
                        intent.putExtra("CITY",city.getText().toString().trim());
                        intent.putExtra("PINCODE",pincode.getText().toString().trim());
                        if(landMark.getText().toString().trim().length()>0)
                        {
                            intent.putExtra("LANDMARK",landMark.getText().toString().trim());
                        }
                        else {
                            intent.putExtra("LANDMARK","");
                        }

                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
                    }
                }
                else {

                    if(isUserNameMandatory)
                    {
                        if(userName.getText().toString().trim().length()>0)
                        {
                            Intent intent = new Intent(AddOrderDetailsActivity.this, TraditionalSummaryActivity.class);
                            intent.putExtra("USERNAME",userName.getText().toString().trim());
                            intent.putExtra("HOUSE_NUMBER","");
                            intent.putExtra("CITY","");
                            intent.putExtra("PINCODE","");
                            intent.putExtra("LANDMARK","");

                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
                        }
                        else {
                            Toast.makeText(AddOrderDetailsActivity.this, cusomerNotes.getText().toString().trim() ,Toast.LENGTH_LONG).show();
                        }
                    }
                    else {

                        Intent intent = new Intent(AddOrderDetailsActivity.this, TraditionalSummaryActivity.class);
                        intent.putExtra("USERNAME",userName.getText().toString().trim());
                        intent.putExtra("HOUSE_NUMBER","");
                        intent.putExtra("CITY","");
                        intent.putExtra("PINCODE","");
                        intent.putExtra("LANDMARK","");

                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_two, R.anim.anim_one);
                    }

                }
            }
        });
    }

    private boolean validateTextFields()
    {
        if(houseNumber.getText().toString().trim().length()==0)
        {
            Toast.makeText(AddOrderDetailsActivity.this,"Please enter house number",Toast.LENGTH_LONG).show();
            return false;
        }

        else if(city.getText().toString().trim().length()==0)
        {
            Toast.makeText(AddOrderDetailsActivity.this,"Please enter city name",Toast.LENGTH_LONG).show();
            return false;
        }
        else if(pincode.getText().toString().trim().length()==0)
        {
            Toast.makeText(AddOrderDetailsActivity.this,"Please enter pincode",Toast.LENGTH_LONG).show();
            return false;
        }
        else if(PackagesActivity.getCustomerNotesCheck!=null && (PackagesActivity.getCustomerNotesCheck.equalsIgnoreCase("YES") || PackagesActivity.getCustomerNotesCheck.equalsIgnoreCase("1")))//mandatory
        {
            if(pincode.getText().toString().trim().length()==0)
            {
                Toast.makeText(AddOrderDetailsActivity.this, cusomerNotes.getText().toString().trim(), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
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
}
