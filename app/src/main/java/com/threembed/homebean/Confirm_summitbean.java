package com.threembed.homebean;

import java.io.Serializable;

/**
 * Created by embed on 13/7/15.
 */
public class Confirm_summitbean implements Serializable
{
    /*"errNum": "71",
            "errFlag": "1",
            "errMsg": "Currently all our drivers are busy serving passengers, please try after some time.",*/

    String errNum;
    String errFlag;
    String errMsg;
    String bid;
    String payUId;

    public String getPaymentGateway() {
        return PaymentGateway;
    }

    String PaymentGateway;

    public String getPayUId() {
        return payUId;
    }



    public String getBid()
    {
        return bid;
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


}
