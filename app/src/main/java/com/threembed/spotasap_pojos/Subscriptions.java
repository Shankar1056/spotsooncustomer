package com.threembed.spotasap_pojos;

import java.util.ArrayList;

/**
 * Created by varun on 30/6/16.
 */
public class Subscriptions {


    String errNum,errFlag,errMsg;
    ArrayList<SubscriptionsDetailsHelper> subscriptions;

    public String getErrNum() {
        return errNum;
    }

    public String getErrFlag() {
        return errFlag;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public ArrayList<SubscriptionsDetailsHelper> getSubscriptions() {
        return subscriptions;
    }
}
