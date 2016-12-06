package com.threembed.spotasap_pojos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by embed on 17/7/15.
 */
public class myorder_pojo implements Serializable {

    private String errNum;
    private String errFlag;
    private String errMsg;


    ArrayList<current_order_list>   current;
    ArrayList<recurring_order_list>   recurring;
    ArrayList<Past_order_list>   past;


    public String getErrNum() {
        return errNum;
    }


    public String getErrFlag() {
        return errFlag;
    }



    public String getErrMsg() {
        return errMsg;
    }



    public ArrayList<current_order_list> getCurrent() {
        return current;
    }



    public ArrayList<recurring_order_list> getRecurring() {
        return recurring;
    }



    public ArrayList<Past_order_list> getPast() {
        return past;
    }







}
