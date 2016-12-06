package com.threembed.homebean;

import java.util.ArrayList;

/**
 * Created by VARUN on 2/13/2016.
 */
public class PendingBillsHelper {

    /*{

        "recurringId":"1247",
            "bid":"990",
            "month":"2016-02-13",
            "items":[
        {
            "ItemId":"563447968b424a47650775de",
                "ItemName":"Bangalore Mirror",
                "Price":"80",
                "Quantity":"1",
                "ItemTotal":80,
                "Status":"1"
        }
        ],
        "amount":"81",
            "payStatus":"1",
            "busName":"Manjunath News Agency",
            "payUId":"10001236"

    }*/


    String recurringId,bid,month,amount,payStatus,busName,payUId,itemComma;
    ArrayList<PendingBillsItemsHelper> items;

    public String getItemComma() {
        return itemComma;
    }

    public String getRecurringId() {
        return recurringId;
    }

    public String getBid() {
        return bid;
    }

    public String getMonth() {
        return month;
    }

    public String getAmount() {
        return amount;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public String getBusName() {
        return busName;
    }

    public String getPayUId() {
        return payUId;
    }

    public ArrayList<PendingBillsItemsHelper> getItems() {
        return items;
    }
}
