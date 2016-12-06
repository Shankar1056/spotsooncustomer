package com.threembed.homebean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by embed on 12/6/15.
 */
public class CatagoriData implements Serializable
{
     String Message;
    ArrayList<DataCatagories> Data;

    public ArrayList<DataCatagories> getData() {
        return Data;
    }


    public String getMessage() {
        return Message;
    }




}
