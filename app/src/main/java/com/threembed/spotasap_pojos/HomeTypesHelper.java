package com.threembed.spotasap_pojos;

import java.util.ArrayList;

/**
 * Created by VARUN on 4/3/2016.
 */
public class HomeTypesHelper {

    private String errNum;
    private String errFlag;
    private String errMsg;
    private String androidVersionCode,androidLocationAPIKey,mandatoryUpdate;

    private ArrayList<TypesDetailsHelper> categories;
    private ArrayList<TypesDetailsHelper> PrevBookings;
    private ArrayList<PlansHelper> plans;
    private ArrayList<String> businessarr;


    public String getAndroidVersionCode() {
        return androidVersionCode;
    }

    public String getAndroidLocationAPIKey() {
        return androidLocationAPIKey;
    }

    public String getMandatoryUpdate() {
        return mandatoryUpdate;
    }

    public ArrayList<PlansHelper> getPlans() {
        return plans;
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

    public ArrayList<TypesDetailsHelper> getCategories() {
        return categories;
    }

    public ArrayList<TypesDetailsHelper> getPrevBookings() {
        return PrevBookings;
    }

    public ArrayList<String> getBusinessarr() {
        return businessarr;
    }
}
