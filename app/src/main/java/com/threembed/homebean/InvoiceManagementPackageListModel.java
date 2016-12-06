package com.threembed.homebean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Shankar on 8/13/2016.
 */
public class InvoiceManagementPackageListModel implements Parcelable {

    String BillingStartDate,BillingEndDate,InvoiceNum,PrevDue,BillAmount,Payments,TaxPercent,TaxAmount,ConvenienceFeeRate,
            PartialPayment,OnlinePayments,InvoiceDate,DueDate,PostDuePenalty,DaysToDue,ProductName;
    public InvoiceManagementPackageListModel(String BillingStartDate, String BillingEndDate,String InvoiceNum, String PrevDue,
                                             String BillAmount, String Payments,String TaxPercent, String TaxAmount,
                                             String ConvenienceFeeRate, String PartialPayment,String OnlinePayments, String InvoiceDate,
                                             String DueDate, String PostDuePenalty,String DaysToDue, String ProductName){
        this.BillingStartDate=BillingStartDate;
        this.BillingEndDate=BillingEndDate;
        this.InvoiceNum=InvoiceNum;
        this.PrevDue=PrevDue;
        this.BillAmount=BillAmount;
        this.Payments=Payments;
        this.TaxPercent=TaxPercent;
        this.TaxAmount=TaxAmount;
        this.ConvenienceFeeRate=ConvenienceFeeRate;
        this.PartialPayment=PartialPayment;
        this.OnlinePayments=OnlinePayments;
        this.InvoiceDate=InvoiceDate;
        this.DueDate=DueDate;
        this.PostDuePenalty=PostDuePenalty;
        this.DaysToDue=DaysToDue;
        this.ProductName=ProductName;
    }

    protected InvoiceManagementPackageListModel(Parcel in) {
        BillingStartDate = in.readString();
        BillingEndDate = in.readString();
        InvoiceNum = in.readString();
        PrevDue = in.readString();
        BillAmount = in.readString();
        Payments = in.readString();
        TaxPercent = in.readString();
        TaxAmount = in.readString();
        ConvenienceFeeRate = in.readString();
        PartialPayment = in.readString();
        OnlinePayments = in.readString();
        InvoiceDate = in.readString();
        DueDate = in.readString();
        PostDuePenalty = in.readString();
        DaysToDue = in.readString();
        ProductName = in.readString();
    }

    public static final Creator<InvoiceManagementPackageListModel> CREATOR = new Creator<InvoiceManagementPackageListModel>() {
        @Override
        public InvoiceManagementPackageListModel createFromParcel(Parcel in) {
            return new InvoiceManagementPackageListModel(in);
        }

        @Override
        public InvoiceManagementPackageListModel[] newArray(int size) {
            return new InvoiceManagementPackageListModel[size];
        }
    };

    public String getBillingStartDate() {
        return BillingStartDate;
    }

    public void setBillingStartDate(String billingStartDate) {
        BillingStartDate = billingStartDate;
    }

    public String getBillingEndDate() {
        return BillingEndDate;
    }

    public void setBillingEndDate(String billingEndDate) {
        BillingEndDate = billingEndDate;
    }

    public String getInvoiceNum() {
        return InvoiceNum;
    }

    public void setInvoiceNum(String invoiceNum) {
        InvoiceNum = invoiceNum;
    }

    public String getPrevDue() {
        return PrevDue;
    }

    public void setPrevDue(String prevDue) {
        PrevDue = prevDue;
    }

    public String getBillAmount() {
        return BillAmount;
    }

    public void setBillAmount(String billAmount) {
        BillAmount = billAmount;
    }

    public String getPayments() {
        return Payments;
    }

    public void setPayments(String payments) {
        Payments = payments;
    }

    public String getTaxPercent() {
        return TaxPercent;
    }

    public void setTaxPercent(String taxPercent) {
        TaxPercent = taxPercent;
    }

    public String getTaxAmount() {
        return TaxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        TaxAmount = taxAmount;
    }

    public String getConvenienceFeeRate() {
        return ConvenienceFeeRate;
    }

    public void setConvenienceFeeRate(String convenienceFeeRate) {
        ConvenienceFeeRate = convenienceFeeRate;
    }

    public String getPartialPayment() {
        return PartialPayment;
    }

    public void setPartialPayment(String partialPayment) {
        PartialPayment = partialPayment;
    }

    public String getOnlinePayments() {
        return OnlinePayments;
    }

    public void setOnlinePayments(String onlinePayments) {
        OnlinePayments = onlinePayments;
    }

    public String getInvoiceDate() {
        return InvoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        InvoiceDate = invoiceDate;
    }

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String dueDate) {
        DueDate = dueDate;
    }

    public String getPostDuePenalty() {
        return PostDuePenalty;
    }

    public void setPostDuePenalty(String postDuePenalty) {
        PostDuePenalty = postDuePenalty;
    }

    public String getDaysToDue() {
        return DaysToDue;
    }

    public void setDaysToDue(String daysToDue) {
        DaysToDue = daysToDue;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(BillingStartDate);
        dest.writeString(BillingEndDate);
        dest.writeString(InvoiceNum);
        dest.writeString(PrevDue);
        dest.writeString(BillAmount);
        dest.writeString(Payments);
        dest.writeString(TaxPercent);
        dest.writeString(TaxAmount);
        dest.writeString(ConvenienceFeeRate);
        dest.writeString(PartialPayment);
        dest.writeString(OnlinePayments);
        dest.writeString(InvoiceDate);
        dest.writeString(DueDate);
        dest.writeString(PostDuePenalty);
        dest.writeString(DaysToDue);
        dest.writeString(ProductName);
    }
}
