package com.threembed.homebean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by embed on 12/6/15.
 */
public class Catagories implements Serializable
{
    private  String id;
    private String Category;
    private ArrayList<CategoryProduct> Products;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return Category;
    }



    public ArrayList<CategoryProduct> getProducts() {
        return Products;
    }

}
