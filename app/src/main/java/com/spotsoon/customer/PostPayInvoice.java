package com.spotsoon.customer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.utility.Utility;

/**
 * Created by VARUN on 7/3/2016.
 */
public class PostPayInvoice extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.show_invoice);

        Utility.statusbar(PostPayInvoice.this);

        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_invoice);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Invoice");

        String code = getIntent().getStringExtra("HTML");
        TextView mHTMLCODE = (TextView)findViewById(R.id.invoice_texview);
        mHTMLCODE.setText(Html.fromHtml(code));
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_three, R.anim.anim_four);
    }
}