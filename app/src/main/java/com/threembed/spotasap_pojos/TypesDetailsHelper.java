package com.threembed.spotasap_pojos;

/**
 * Created by VARUN on 4/3/2016.
 */
public class TypesDetailsHelper {

    /*"Image":"https:\/\/s3.amazonaws.com\/spotsoon-admin\/India\/Bengaluru\/file2016325143334.png",
            "BussName":"Your Internet Provider",
            "Category":"Internet Recharge",
            "BussAddr1":"#451, 1st Main, 2nd Cross",
            "PayUId":"1026",
            "BussAddr2":"Bengaluru",
            "PayDt":"14 Feb 2016",
            "DueDt":"14 Mar 2016",
            "type":"traditional",
            "renewalDate":"14 Feb 2016",
            "nextRenewalDate":"14 Mar 2016",
            "packageAmt":"3",
            "Tax":"0",
            "TaxPercent":"0",
            "convenienceFee":"0",
            "Total":"3",
            "items":[
    {
        "name":"Your Existing Data Plan",
            "onTop":3,
            "details":3
    }
    ]*/
    String Image,BussName,BussAddr1,BussAddr2,PayDt,DueDt,Id,Category,Description,Banner,username,BusinessId,domid;
    String PayUId,type,renewalDate,nextRenewalDate,packageAmt,Tax,TaxPercent,convenienceFee,Total,Status,purchaseType;
    String viewMore;
    String BussLat;
    String BussLong;
    String CustomerNotes;
    String CustomerNotesCheck;
    String ConnectedFlag;
    String bannerUrl;
    String Delivery_Pickup;
    String UserNotes;

    public String getUserMobile() {
        return userMobile;
    }

    String userMobile;

    public String getStatus() {
        return Status;
    }

    public String getBussLat() {
        return BussLat;
    }

    public String getBussLong() {
        return BussLong;
    }

    public String getCustomerNotes() {
        return CustomerNotes;
    }

    public String getDelivery_Pickup() {
        return Delivery_Pickup;
    }

    public String getUserNotes() {
        return UserNotes;
    }
    public String getCustomerNotesCheck() {
        return CustomerNotesCheck;
    }

    public String getConnectedFlag() {
        return ConnectedFlag;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public String getViewMore() {
        return viewMore;
    }

    public String getUsername() {
        return username;
    }

    public String getBusinessId() {
        return BusinessId;
    }

    public String getDomid() {
        return domid;
    }

    public String getImage() {
        return Image;
    }

    public String getBussName() {
        return BussName;
    }

    public String getBussAddr1() {
        return BussAddr1;
    }

    public String getBussAddr2() {
        return BussAddr2;
    }

    public String getPayDt() {
        return PayDt;
    }

    public String getDueDt() {
        return DueDt;
    }

    public String getId() {
        return Id;
    }

    public String getCategory() {
        return Category;
    }

    public String getDescription() {
        return Description;
    }

    public String getBanner() {
        return Banner;
    }

    public String getPayUId() {
        return PayUId;
    }

    public String getType() {
        return type;
    }

    public String getRenewalDate() {
        return renewalDate;
    }

    public String getNextRenewalDate() {
        return nextRenewalDate;
    }

    public String getPackageAmt() {
        return packageAmt;
    }

    public String getTax() {
        return Tax;
    }

    public String getTaxPercent() {
        return TaxPercent;
    }

    public String getConvenienceFee() {
        return convenienceFee;
    }

    public String getTotal() {
        return Total;
    }

    public String getPurchaseType() {
        return purchaseType;
    }
}
