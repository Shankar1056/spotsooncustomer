package com.threembed.homebean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by embed on 28/5/15.
 */
public class HomeClass
{

    //{"errNo":0,"errFlag":0,"msg":"","data":[{"Category":"Milk Subscription","Description":"","Image":"https:\/\/s3-us-west-2.amazonaws.com\/spotasap\/admin\/fileimg2016329164030.png","Recurring":"undefined","order":3,"CustomerNotes":"","BannerImage":"https:\/\/s3-us-west-2.amazonaws.com\/spotasap\/admin\/filebanner2016329164031.png","id":"557ff2548b424a0f09776ba5"},{"Category":"Cable Subscription","Description":"","Image":"https:\/\/s3-us-west-2.amazonaws.com\/spotasap\/admin\/fileimg201632916411.png","Recurring":"undefined","order":2,"CustomerNotes":"","BannerImage":"https:\/\/s3-us-west-2.amazonaws.com\/spotasap\/admin\/filebanner201632916412.png","id":"557ff3208b424a0b09776ba5"},{"Category":"Fitness","Description":"","Image":"https:\/\/s3-us-west-2.amazonaws.com\/spotasap\/admin\/fileimg2016329164148.png","Recurring":"undefined","order":5,"CustomerNotes":"","BannerImage":"https:\/\/s3-us-west-2.amazonaws.com\/spotasap\/admin\/filebanner2016329164148.png","id":"557ff3f98b424a1009776ba5"},{"Category":"Newspaper Subscription","Description":"","Image":"https:\/\/s3-us-west-2.amazonaws.com\/spotasap\/admin\/fileimg2016329164223.png","Recurring":"undefined","order":4,"CustomerNotes":"","BannerImage":"https:\/\/s3-us-west-2.amazonaws.com\/spotasap\/admin\/filebanner2016329164224.png","id":"556edf5f8b424a616685652b"},{"Category":"Tuitions","Description":"","Image":"https:\/\/s3-us-west-2.amazonaws.com\/spotasap\/admin\/fileimg2016329164250.png","Recurring":"undefined","order":6,"CustomerNotes":"","BannerImage":"https:\/\/s3-us-west-2.amazonaws.com\/spotasap\/admin\/filebanner2016329164250.png","id":"5570465d8b424ab46585652f"},{"Category":"Internet Recharge","Description":"","Image":"https:\/\/s3-us-west-2.amazonaws.com\/spotasap\/admin\/fileimg2016329164310.png","order":1,"CustomerNotes":"","Recurring":"undefined","BannerImage":"https:\/\/s3-us-west-2.amazonaws.com\/spotasap\/admin\/filebanner2016329164310.png","id":"56a7396394b9a46a3d81e1a6"}],"message":"Business Categories Returned"}


    String Message,flag;
    ArrayList<Data> Data;

    public ArrayList<Data> getData() {
        return Data;
    }

    public String getMessage() {
        return Message;
    }

    public String getFlag() {
        return flag;
    }
}
