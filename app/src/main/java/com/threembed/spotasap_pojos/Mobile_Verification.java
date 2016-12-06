package com.threembed.spotasap_pojos;

import java.io.Serializable;

/**
 * Created by embed on 22/5/15.
 */
public class Mobile_Verification implements Serializable
{
    String errNum;
    String errFlag;
    String errMsg;
    String test;
    String code;
    String token;
    String pic;
    String fname;
    String flag,email;


    public String getEmail() {
        return email;
    }

    public String getFlag() {
        return flag;
    }

    public String getToken() {
        return token;
    }

    public String getPic() {
        return pic;
    }

    public String getFname() {
        return fname;
    }

    public String getCode() {
        return code;
    }

    public String getErrMsg() {
        return errMsg;
    }



    public String getErrNum() {
        return errNum;
    }



    public String getErrFlag() {
        return errFlag;
    }

    public String getTest() {
        return test;
    }

}
