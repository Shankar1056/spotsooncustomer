package com.threembed.spotasap_pojos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by embed on 5/8/15.
 */
public class recurring_order_list implements Serializable{
    String bussiness;
    ArrayList<order_items_list> items;
    String addrLine1;
    String addrLine2;
    String apptDt;
    String amount;
    String bussinessId;
    String bid;
    String date;
    String status;
    private String statCode;

    public String getStatCode()
    {
        return statCode;
    }



    public String getBussinessId() {
        return bussinessId;
    }


    public String getStatus() {
        return status;
    }








    public String getDate() {
        return date;
    }





    public String getAmount() {
        return amount;
    }



    public String getBid() {
        return bid;
    }



    public String getApptDt() {
        return apptDt;
    }



    public String getAddrLine2() {
        return addrLine2;
    }



    public String getAddrLine1() {
        return addrLine1;
    }



    public ArrayList<order_items_list> getItems() {
        return items;
    }



    public String getBussiness() {
        return bussiness;
    }




}
