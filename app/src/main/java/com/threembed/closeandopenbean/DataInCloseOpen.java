package com.threembed.closeandopenbean;

import java.io.Serializable;

/**
 * Created by embed on 2/6/15.
 */
public class DataInCloseOpen implements Serializable
{
    String Id;
    String Address;
    String Logo;
    String Email;
    String Name;
    String Latitude;
    String Delivery_Pickup;
    String PaymentType;
    String MinimumOrder;
    String Longitude;
    String Distance;
    String Favorite;
    String Open;
    String BusinessType;
    String CustomerNotes;
    String MemberFlag;
    String CustomerNotesCheck;
    String IntegrationType;
    String ConnectedFlag;
    String ShowMorePlans;


    public String getShowMorePlans() {
        return ShowMorePlans;
    }
    public String getConnectedFlag() {
        return ConnectedFlag;
    }

    public String getIntegrationType() {
        return IntegrationType;
    }
    public String getCustomerNotesCheck() {
        return CustomerNotesCheck;
    }

    public String getMemberFlag() {
        return MemberFlag;
    }

    public String getCustomerNotes() {
        return CustomerNotes;
    }

    public void setCustomerNotes(String customerNotes) {
        CustomerNotes = customerNotes;
    }

    public String getBusinessType()
    {
        return BusinessType;
    }

    public void setBusinessType(String businessType) {
        BusinessType = businessType;
    }

    public String getId() {
        return Id;
    }

    public String getOpen() {
        return Open;
    }

    public void setOpen(String open) {
        Open = open;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getLogo() {
        return Logo;
    }

    public void setLogo(String logo) {
        Logo = logo;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getDelivery_Pickup() {
        return Delivery_Pickup;
    }

    public void setDelivery_Pickup(String delivery_Pickup) {
        Delivery_Pickup = delivery_Pickup;
    }

    public String getPaymentType() {
        return PaymentType;
    }

    public void setPaymentType(String paymentType) {
        PaymentType = paymentType;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getMinimumOrder() {
        return MinimumOrder;
    }

    public void setMinimumOrder(String minimumOrder) {
        MinimumOrder = minimumOrder;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public String getFavorite() {
        return Favorite;
    }

    public void setFavorite(String favorite) {
        Favorite = favorite;
    }
}
