package com.threembed.spotasap_pojos;

import java.util.ArrayList;

/**
 * Created by VARUN on 5/21/2016.
 */
public class CustomerProfileData {

    ArrayList<CustomerSubscriptions> subscriptions;
    String showMorePlans;

    public ArrayList<CustomerSubscriptions> getSubscriptions() {
        return subscriptions;
    }

    public String getShowMorePlans() {
        return showMorePlans;
    }
}
