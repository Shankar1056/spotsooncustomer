package com.threembed.spotasap_pojos;

import java.util.ArrayList;

/**
 * Created by VARUN on 4/3/2016.
 */
public class MyOrdersPojo {


    private String errNum;
    private String errFlag;
    private String errMsg;

    ArrayList<MyOrdersHelper> current;

    public String getErrNum() {
        return errNum;
    }

    public String getErrFlag() {
        return errFlag;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public ArrayList<MyOrdersHelper> getCurrent() {
        return current;
    }
}
