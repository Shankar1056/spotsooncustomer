package com.threembed.homebean;

import java.io.Serializable;

/**
 * Created by embed on 12/6/15.
 */
public class CategoryProduct implements Serializable {


    private String ProductName;
    private String Id;
    private ProductImage Image;
    private String Price;
    private String Favorite;
    private int packageCount;
    private double calculatedTax;

    public double getCalculatedTax() {
        return calculatedTax;
    }

    public void setCalculatedTax(double calculatedTax) {
        this.calculatedTax = calculatedTax;
    }

    public int getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }

    public String getProductName() {
        return ProductName;
    }



    public String getId() {
        return Id;
    }



    public ProductImage getImage() {
        return Image;
    }



    public String getPrice() {
        return Price;
    }



    public String getFavorite() {
        return Favorite;
    }


}
