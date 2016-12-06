package com.threembed.spotasap_pojos;


import java.io.Serializable;
import java.util.ArrayList;

public class Customers implements Serializable
{
    private String amount;

    private String payStatus;

    ArrayList<Items> items;

    private String month;
    private String bid;
    private String status;
    private String message;
    private String recurringId;
    private String phone;
    private String busName;

    public String getBusName() {
        return busName;
    }



    public String getPhone() {
        return phone;
    }




    public String getRecurringId()
    {
        return recurringId;
    }



    public String getStatus()
    {
        return status;
    }



    public String getMessage()
    {
        return message;
    }



    public String getAmount ()
    {
        return amount;
    }



    public String getPayStatus ()
    {
        return payStatus;
    }



    public ArrayList<Items> getItems() {
        return items;
    }



    public String getMonth ()
    {
        return month;
    }



    public String getBid ()
    {
        return bid;
    }



    @Override
    public String toString()
    {
        return "ClassPojo [amount = "+amount+", payStatus = "+payStatus+", items = "+items+", month = "+month+", bid = "+bid+"]";
    }
}
