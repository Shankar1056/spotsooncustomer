package com.threembed.closeandopenbean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by VARUN on 2/21/2016.
 */
public class SubscriptionsHelper implements Serializable{




    String errNum;
    String errFlag;
    String errMsg;
    String errDesc;
    String nextRenewalDate;
    String adv;

    public String getRateUs() {
        return rateUs;
    }

    String rateUs;

    ArrayList<SubscriptionsListHelper> subscriptions;

    public String getNextRenewalDate() {
        return nextRenewalDate;
    }

    public String getAdv() {
        return adv;
    }

    public String getErrNum() {
        return errNum;
    }

    public String getErrFlag() {
        return errFlag;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public String getErrDesc() {
        return errDesc;
    }

    public ArrayList<SubscriptionsListHelper> getSubscriptions() {
        return subscriptions;
    }
}
