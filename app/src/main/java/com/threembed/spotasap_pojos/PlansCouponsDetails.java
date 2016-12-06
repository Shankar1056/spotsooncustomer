package com.threembed.spotasap_pojos;

/**
 * Created by bala on 29/6/16.
 */
public class PlansCouponsDetails {

    /*{
        "CouponID":"TST1-500-1",
            "Description":"FUP 60GB - 16Mbps - 1Mbps - 30days",
            "Price":"399",
            "ShowInMorePlans":"YES",
            "Tax":59.85,
            "ParentRatePlan":"TestingPlan1",
            "TaxPercent":"15",
            "SubTotal":458.85,
            "convenienceFee":0,
            "Total":458.85
    }*/

    String CouponID,Description,Price,ShowInMorePlans,Tax,ParentRatePlan,TaxPercent,SubTotal,
            convenienceFee,Total;

    public String getCouponID() {
        return CouponID;
    }

    public String getDescription() {
        return Description;
    }

    public String getPrice() {
        return Price;
    }

    public String getShowInMorePlans() {
        return ShowInMorePlans;
    }

    public String getTax() {
        return Tax;
    }

    public String getParentRatePlan() {
        return ParentRatePlan;
    }

    public String getTaxPercent() {
        return TaxPercent;
    }

    public String getSubTotal() {
        return SubTotal;
    }

    public String getConvenienceFee() {
        return convenienceFee;
    }

    public String getTotal() {
        return Total;
    }
}
