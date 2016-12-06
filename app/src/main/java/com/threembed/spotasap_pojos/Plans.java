package com.threembed.spotasap_pojos;

import java.util.ArrayList;

/**
 * Created by bala on 29/6/16.
 */
public class Plans {

    String errNum;
    String errFlag;
    String errMsg;

    ArrayList<PlansDetailsHelper> plans;

    public String getErrNum() {
        return errNum;
    }

    public String getErrFlag() {
        return errFlag;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public ArrayList<PlansDetailsHelper> getPlans() {
        return plans;
    }
}
