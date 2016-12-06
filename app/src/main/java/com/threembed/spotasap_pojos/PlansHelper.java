package com.threembed.spotasap_pojos;

/**
 * Created by VARUN on 4/3/2016.
 */
public class PlansHelper {

    /*"id":"sis25Mb80GB512Kbps",
            "quota":"N\/A",
            "price":"1545.0",
            "plantype":"A",
            "validity":"20 Days",
            "descr":"sis25Mb80GB1mbps",
            "packageAmt":"1545.0",
            "Tax":0,
            "TaxPercent":0,
            "convenienceFee":0,
            "Total":1545*/
    /*{
        "id":"IRIS-1025",
            "description":"10 Mbps - 25 GB",
            "mrp":"1144",
            "price":"1144",
            "name":"IRIS-1025",
            "packageAmt":"1144",
            "Tax":0,
            "TaxPercent":0,
            "convenienceFee":0,
            "Total":1144
    }*/

    //String id,quota,price,plantype,validity,descr;
    String id,price,description,name,packageAmt,Tax,TaxPercent,convenienceFee,Total;

    public String getId() {
        return id;
    }


    public String getPrice() {
        return price;
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

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}

