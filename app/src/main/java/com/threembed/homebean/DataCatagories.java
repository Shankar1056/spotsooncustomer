package com.threembed.homebean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by embed on 12/6/15.
 */
public class DataCatagories implements Serializable
{


  /*      {
        "WorkingHours":"Monday : 9:00 AM To 12:00 PM And 2:00 PM To 6:00 PM\\nTuesday : 8:00 AM To 12:00 PM And 2:00 PM To 9:00 PM\\nWednesday : 8:00 AM To 10:00 AM And 3:00 PM To 6:00 PM\\nThursday : 9:00 AM To 1:00 PM And 3:00 PM To 10:00 PM\\nFriday : 6:00 AM To 10:00 AM And 1:00 PM To 5:00 PM\\nSaturday : 7:00 AM To 12:00 PM And 4:00 PM To 6:00 PM\\nSunday : 6:00 AM To 3:00 PM And 6:00 PM To 11:00 PM",
        "Logo":"https:\/\/s3.amazonaws.com\/postmatesapp\/India\/Bangalore\/file20156453947.png",
        "ImagesFlag":"1",
        "Categories":[],
        "Reviews":[],
        "AverageRatings":"3"
    }*/




    String WorkingHours;
    String Logo;
    String ImagesFlag;
    String AverageRatings;
    ArrayList<Catagories> Categories;
    ArrayList<DataReviews> Reviews;
    String PickupDelivery;
    String DeliveryCharge;
    String ConvenienceCharge ;
    String TaxPercent;





    public String getTaxPercent() {
        return TaxPercent;
    }

    public String getDeliveryCharge()
    {
        return DeliveryCharge;
    }

    public String getConvenienceCharge()
    {
        return ConvenienceCharge;
    }



    public String getPickupDelivery()
    {
        return PickupDelivery;
    }



    public String getWorkingHours() {
        return WorkingHours;
    }


    public ArrayList<DataReviews> getReviews() {
        return Reviews;
    }





    public ArrayList<Catagories> getCategories() {
        return Categories;
    }


    public String getLogo() {
        return Logo;
    }



    public String getAverageRatings() {
        return AverageRatings;
    }




}
