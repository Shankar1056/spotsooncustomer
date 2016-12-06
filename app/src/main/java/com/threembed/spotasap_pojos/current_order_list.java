package com.threembed.spotasap_pojos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by embed on 5/8/15.
 */
public class current_order_list implements Serializable
{


    String bussiness,addrLine1,addrLine2,apptDt,amount,bid,date,status,PrevDue,Tax,TaxPercent,ConvFee,RenewalDt,PayDt,bussAddr2;
    String statCode,mypackage;
    ArrayList<order_items_list> items;

    public String getMypackage() {
        return mypackage;
    }

    public String getPrevDue() {
        return PrevDue;
    }
    public String getTax() {
        return Tax;
    }
    public String getTaxPercent() {
        return TaxPercent;
    }
    public String getConvFee() {
        return ConvFee;
    }

    public String getRenewalDt() {
        return RenewalDt;
    }

    public String getPayDt() {
        return PayDt;
    }

    public String getBussAddr2() {
        return bussAddr2;
    }

    public String getStatCode() {
        return statCode;
    }

    public ArrayList<order_items_list> getItems() {
        return items;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public String getBussiness() {
        return bussiness;
    }

    public String getAddrLine1() {
        return addrLine1;
    }

    public String getAddrLine2() {
        return addrLine2;
    }

    public String getApptDt() {
        return apptDt;
    }

    public String getAmount() {
        return amount;
    }

    public String getBid() {return bid;}
}
