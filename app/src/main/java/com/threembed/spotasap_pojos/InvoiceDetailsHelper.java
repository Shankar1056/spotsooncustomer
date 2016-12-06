package com.threembed.spotasap_pojos;

/**
 * Created by VARUN on 7/3/2016.
 */
public class InvoiceDetailsHelper {

    /*"invoiceDate":"01 Jun 2016",
            "dueDate":"11 Jun 2016",
            "billStartDate":"01 Jun 2016",
            "billEndDate":"01 Jul 2016",
            "billAmount":"767",
            "pendingAmount":"0",
            "payableAmount":"767",
            "convenienceRate":"2",
            "convenienceFee":"15.34",
            "Total":"782.34",
            "amountEditable":"YES",
            "invoiceHTML":"\n\n<!DOCTYPE html */

    /*"invoiceDate":"01 Jul 2016",
            "invoiceNo":"313126",
            "dueDate":"11 Jul 2016",
            "daysToDue":6,
            "billStartDate":"01 Jul 2016",
            "billEndDate":"01 Aug 2016",
            "billAmount":"1276",
            "pendingAmount":"777",
            "openingBalance":"-2",
            "payableAmount":"775",
            "paymentsDone":"501",
            "convenienceRate":"0",
            "convenienceFee":"0",
            "Total":"775",
            "amountEditable":"YES"*/

    String invoiceDate;
    String daysToDue;
    String invoiceNo;
    String paymentsDone;
    String dueDate;
    String billStartDate;
    String billEndDate;
    String billAmount;
    String pendingAmount;
    String openingBalance;
    String payableAmount;
    String convenienceRate;
    String convenienceFee;
    String Total;
    String amountEditable;
    String invoiceHTML;

    public String getInvoiceNumber() {
        return InvoiceNumber;
    }

    String InvoiceNumber;

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getBillStartDate() {
        return billStartDate;
    }

    public String getBillEndDate() {
        return billEndDate;
    }

    public String getBillAmount() {
        return billAmount;
    }

    public String getPayableAmount() {
        return payableAmount;
    }

    public String getConvenienceRate() {
        return convenienceRate;
    }

    public String getConvenienceFee() {
        return convenienceFee;
    }

    public String getTotal() {
        return Total;
    }

    public String getAmountEditable() {
        return amountEditable;
    }

    public String getInvoiceHTML() {
        return invoiceHTML;
    }

    public String getPendingAmount() {
        return pendingAmount;
    }

    public String getDaysToDue() {
        return daysToDue;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public String getPaymentsDone() {
        return paymentsDone;
    }

    public String getOpeningBalance() {
        return openingBalance;
    }
}
