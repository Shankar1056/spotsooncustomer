package com.threembed.spotasap_pojos;

/**
 * Created by VARUN on 4/3/2016.
 */
public class MyOrdersHelper {

    String sendInvoice;
    String bussiness;
    String addrLine1;
    String addrLine2;
    String amount;
    String bid;
    String Status;
    String PrevDue;
    String Tax;
    String TaxPercent;
    String ConvFee;
    String RenewalDt;
    String PayDt;
    String bussAddr2;
    String mypackage;
    String Total;
    String OrderType;
    String purchaseType;
    String PaymentMethod;


    String TotalPayable;
    String AmountPaid;
    String Balance;

    public String getPaymentMethod() {
        return PaymentMethod;
    }
    public String getTaxLabel() {
        return TaxLabel;
    }

    String TaxLabel;
    InvoiceDetailsHelper invoice;


    public String getSendInvoice() {
        return sendInvoice;
    }

    public String getOrderType() {
        return OrderType;
    }

    public String getTotal() {
        return Total;
    }

    public String getBussiness() {
        return bussiness;
    }

    public String getAddrLine1() {
        return addrLine1;
    }

    public String getAddrLine2() {
        return addrLine2;
    }

    public String getAmount() {
        return amount;
    }

    public String getBid() {
        return bid;
    }

    public String getStatus() {
        return Status;
    }

    public String getPrevDue() {
        return PrevDue;
    }

    public String getTax() {
        return Tax;
    }

    public String getTaxPercent() {
        return TaxPercent;
    }

    public String getConvFee() {
        return ConvFee;
    }

    public String getRenewalDt() {
        return RenewalDt;
    }

    public String getPayDt() {
        return PayDt;
    }

    public String getBussAddr2() {
        return bussAddr2;
    }

    public String getMypackage() {
        return mypackage;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public InvoiceDetailsHelper getInvoice() {
        return invoice;
    }
    public String getTotalPayable() {
        return TotalPayable;
    }


    public String getAmountPaid() {
        return AmountPaid;
    }

    public String getBalance() {
        return Balance;
    }

}
