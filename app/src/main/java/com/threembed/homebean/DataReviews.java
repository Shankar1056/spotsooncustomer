package com.threembed.homebean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by embed on 7/7/15.
 */
public class DataReviews implements Serializable
{
   // ArrayList<UserDetail>UserDetails;
    String Review;
    String Rating;
    String DateTime;
   private UserDetail UserDetails;

    public UserDetail getUserDetails() {
        return UserDetails;
    }






    public String getReview() {
        return Review;
    }



    public String getRating() {
        return Rating;
    }



    public String getDateTime() {
        return DateTime;
    }


}
