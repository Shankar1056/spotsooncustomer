package com.threembed.homebean;

import java.io.Serializable;

/**
 * Created by embed on 28/5/15.
 */
public class Data implements Serializable
{
    String Id;
    String Category;
    String Description;
    String Image;
    String TotalBusinesses;
    String ClosestBusinessDistance;
    String ClosestBusiness;
    String Banner;


    public String getBanner() {
        return Banner;
    }

    public String getClosestBusinessDistance() {
        return ClosestBusinessDistance;
    }



    public String getTotalBusinesses() {
        return TotalBusinesses;
    }


    public String getClosestBusiness() {
        return ClosestBusiness;
    }





    public String getDescription() {
        return Description;
    }


    public String getId() {
        return Id;
    }


    public String getCategory() {
        return Category;
    }


    public String getImage() {
        return Image;
    }



}
