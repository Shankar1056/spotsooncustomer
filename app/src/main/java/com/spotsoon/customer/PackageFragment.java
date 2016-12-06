package com.spotsoon.customer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.threembed.homebean.CategoryProduct;
import com.utility.Utility;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

/**
 * Created by VARUN on 4/16/2016.
 */
public class PackageFragment extends Fragment{

    private LinearLayout packagesLayout;
    private Button next;
    private ArrayList<CategoryProduct> productsList = new ArrayList<CategoryProduct>();
    private int position = 0;
    private static final String ARG_POSITION = "position";

    public static PackageFragment newInstance(int position) {
        PackageFragment f = new PackageFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.package_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Fabric.with(getActivity(), new Crashlytics());
        next = (Button)getActivity().findViewById(R.id.next);

        packagesLayout = (LinearLayout)view.findViewById(R.id.llPackages);
        productsList = PackagesActivity.response.getData().get(0).getCategories().get(position).getProducts();
        showPackages();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isPackagesSelected())
                {
                    Intent intent = new Intent(getActivity(),AddOrderDetailsActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getActivity(),"Please select your package",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showPackages()
    {
        for(int i = 0; i < productsList.size(); i++)
        {
            final int cnt = i;
            LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View subCatRow = mInflater.inflate(R.layout.packages_rowitem, null);

            final TextView packageName = (TextView) subCatRow.findViewById(R.id.packageName);
            final TextView packageAmount = (TextView) subCatRow.findViewById(R.id.packageAmount);
            final TextView packageCount = (TextView) subCatRow.findViewById(R.id.subCatCount);
            final ImageButton packagePlus = (ImageButton) subCatRow.findViewById(R.id.subCatAdd);
            final ImageButton packageMinus = (ImageButton) subCatRow.findViewById(R.id.subCatMinus);

            final CategoryProduct packageItem = productsList.get(i);

            packageName.setText(packageItem.getProductName());
            packageAmount.setText("₹ " + packageItem.getPrice());

            packagePlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int mCount = Integer.parseInt(packageCount.getText().toString().trim());
                    packageCount.setText("" + (++mCount));
                    packageMinus.setBackgroundResource(R.drawable.minus_on);
                    packageCount.setTextColor(Utility.getColor(getActivity(),R.color.app_blue));
                    PackagesActivity.response.getData().get(0).getCategories().get(position).getProducts().get(cnt).setPackageCount(Integer.parseInt(packageCount.getText().toString().trim()));

                    double amount = Double.parseDouble(PackagesActivity.response.getData().get(0).getCategories().get(position).getProducts().get(cnt).getPrice());
                    amount = Integer.parseInt(packageCount.getText().toString().trim()) * amount;

                    if(PackagesActivity.response.getData().get(0).getTaxPercent() != null && PackagesActivity.response.getData().get(0).getTaxPercent().length() > 0) {
                        if(Double.parseDouble(PackagesActivity.response.getData().get(0).getTaxPercent()) > 0) {

                            String taxPerc = PackagesActivity.response.getData().get(0).getTaxPercent();
                            double taxAmount = getTaxAmount(amount, taxPerc);
                            PackagesActivity.response.getData().get(0).getCategories().get(position).getProducts().get(cnt).setCalculatedTax(taxAmount);
                        }
                    }

                    next.setVisibility(View.VISIBLE);
                }
            });

            packageMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        int mCount = Integer.parseInt(packageCount.getText().toString().trim());
                        --mCount;
                        if (mCount > 0) {
                            packageMinus.setBackgroundResource(R.drawable.minus_on);
                            packageCount.setText("" + mCount);
                            packageCount.setTextColor(Utility.getColor(getActivity(),R.color.app_blue));
                            PackagesActivity.response.getData().get(0).getCategories().get(position).getProducts().get(cnt).setPackageCount(Integer.parseInt(packageCount.getText().toString().trim()));

                            double amount = Double.parseDouble(PackagesActivity.response.getData().get(0).getCategories().get(position).getProducts().get(cnt).getPrice());
                            amount = Integer.parseInt(packageCount.getText().toString().trim()) * amount;

                            if(PackagesActivity.response.getData().get(0).getTaxPercent() != null && PackagesActivity.response.getData().get(0).getTaxPercent().length() > 0) {
                                if(Double.parseDouble(PackagesActivity.response.getData().get(0).getTaxPercent()) > 0) {

                                    String taxPerc = PackagesActivity.response.getData().get(0).getTaxPercent();
                                    double taxAmount = getTaxAmount(amount, taxPerc);
                                    PackagesActivity.response.getData().get(0).getCategories().get(position).getProducts().get(cnt).setCalculatedTax(taxAmount);
                                }
                            }

                        } else {
                            packageMinus.setBackgroundResource(R.drawable.minus_off);
                            packageCount.setText("0");
                            packageCount.setTextColor(Utility.getColor(getActivity(),R.color.background_gray));
                            PackagesActivity.response.getData().get(0).getCategories().get(position).getProducts().get(cnt).setPackageCount(0);
                            PackagesActivity.response.getData().get(0).getCategories().get(position).getProducts().get(cnt).setCalculatedTax(0);
                        }
                    } catch (NumberFormatException e) {
                    }

                    if (isPackagesSelected()) {
                        next.setVisibility(View.VISIBLE);
                    } else {
                        next.setVisibility(View.GONE);
                    }
                }
            });

            packagesLayout.addView(subCatRow);
        }
    }

    private boolean isPackagesSelected()
    {
        boolean isFound = false;
        for(int i=0;i<PackagesActivity.response.getData().get(0).getCategories().size();i++)
        {
            for(int j=0;j<PackagesActivity.response.getData().get(0).getCategories().get(i).getProducts().size();j++)
            {
                if(PackagesActivity.response.getData().get(0).getCategories().get(i).getProducts().get(j).getPackageCount()>0)
                {

                    isFound = true;
                }
            }
        }

        if(isFound)
        {
            return true;
        }
        else {
            return  false;
        }
    }

    private double getTaxAmount(double amount,String taxPercentage)
    {
        double ePer = Double.parseDouble(taxPercentage);
        double per = (amount / 100.0f) * ePer;
        return per;
    }
}