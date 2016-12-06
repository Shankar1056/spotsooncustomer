package com.threembed.spotasap_pojos;

import java.util.ArrayList;

/**
 * Created by varun on 30/6/16.
 */
public class SubscriptionsDetailsHelper {

    String startdt,install_name,customertype,actcat,pkgtype,pkgid,status,install_email,
            actno,actname,domno,startDate,svctype,install_phone,ratePlan,svcdescr,renewdt,
            subsno,sameasbill,domid,actid,expirydt,renewalDate,nextRenewalDate,TaxPercent,
            current_plan_type,account_state,renew_type,planType,packageAmount,billCycle,convenienceRate,convenienceFee,Total;

    ArrayList<PlansDetailsHelper> Packages;
    ArrayList<InvoiceDetailsHelper> invoice;
    CustomerHelper Customer;



    public String getStartdt() {
        return startdt;
    }

    public String getInstall_name() {
        return install_name;
    }

    public String getCustomertype() {
        return customertype;
    }

    public String getActcat() {
        return actcat;
    }

    public String getPkgtype() {
        return pkgtype;
    }

    public String getPkgid() {
        return pkgid;
    }

    public String getStatus() {
        return status;
    }

    public String getInstall_email() {
        return install_email;
    }

    public String getActno() {
        return actno;
    }

    public String getActname() {
        return actname;
    }

    public String getDomno() {
        return domno;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getSvctype() {
        return svctype;
    }

    public String getInstall_phone() {
        return install_phone;
    }

    public String getRatePlan() {
        return ratePlan;
    }

    public String getSvcdescr() {
        return svcdescr;
    }

    public String getRenewdt() {
        return renewdt;
    }

    public String getSubsno() {
        return subsno;
    }

    public String getSameasbill() {
        return sameasbill;
    }

    public String getDomid() {
        return domid;
    }

    public String getActid() {
        return actid;
    }

    public String getExpirydt() {
        return expirydt;
    }

    public String getRenewalDate() {
        return renewalDate;
    }

    public String getNextRenewalDate() {
        return nextRenewalDate;
    }

    public ArrayList<PlansDetailsHelper> getPackages() {
        return Packages;
    }

    public ArrayList<InvoiceDetailsHelper> getInvoice() {
        return invoice;
    }

    public String getCurrent_plan_type() {
        return current_plan_type;
    }

    public String getAccount_state() {
        return account_state;
    }

    public String getPlanType() {
        return planType;
    }

    public String getPackageAmount() {
        return packageAmount;
    }

    public String getRenew_type() {
        return renew_type;
    }

    public String getBillCycle() {
        return billCycle;
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

    public CustomerHelper getCustomer() {
        return Customer;
    }

}
