package com.threembed.spotasap_pojos;

import java.io.Serializable;

/**
 * Created by embed on 11/8/15.
 */
public class order_items_list implements Serializable {

    String ItemId;
    String ItemName;
    String Price;
    String Quantity;


    public String getItemName() {
        return ItemName;
    }



    public String getItemId() {
        return ItemId;
    }



    public String getPrice() {
        return Price;
    }



    public String getQuantity() {
        return Quantity;
    }



}
