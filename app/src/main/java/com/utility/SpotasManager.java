package com.utility;

import android.content.Context;
import android.content.SharedPreferences;
/**
 * Created by embed on 1/6/15.
 *
 */

public class SpotasManager {

    public static final int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SpotasApp";
    private static final String NOT_PEF="Notification";
    private static final String USER_ID = "user_id";
    private static final String Session_token = "session_token";
    private static final String Coupon="coupon";
    private static final String Phone="phone";
    private static final String Fname="fname";
    private static final String Lname="lname";
    private static final String EMAIL_ID = "email_id";
    private static final String DESLatitude = "latitude";
    private static final String DESLongitude = "longitude";
    private static final String PICKUPDELIVER = "pickupdelivery";
    private static final String MINIMUM_AMOUNT = "minimum_amount";
    private static final String OPENCOLSESTATUS = "openclosestatus";
    private static final String ProfilePic="profilePic";
    private static final String DBHOUSENO = "houseno";
    private static final String DBLANDMARK = "landmark";
    private static final String DBCLATITUDE = "clatitude";
    private static final String DBCLONGITUDE = "clongitude";
    private static final String DBFULLADDRESS = "fulladdress";
    private static final String LIVELATI = "livelati";
    private static final String LIVELONGI = "livelongi";
    private static final String PAYMENT_TYPE = "paymenttype";
    private static final String SAVINGSEARCHEDADRESS="searchadress";
    private static final String BUSINESSTYPE = "businesstype";
    private static final String CURRENTLATITUDE="currentlatitude";
    private static final String CURRENTLONGITUDE = "currentlongitude";
    private static final String CUSTOMER_ID = "curtomer_id";
    private static final String ISRETURNEDFROMSAVEDADDRESS = "isreturnedfromsavedaddress";
    private static final String NotificationList="notificationlist";
    private static final String ADDTOFAVOURIT = "addtofavourit";
    private static final String BUSINESSNAME = "businessname";
    private static final String TIMINGATLIVEBOOKING = "timingatlivebooking";
    private static final String PUSHTOKEN_UPDATED = "push_token";
    private static final String SERVER_API = "server_api";

    private boolean isPendingScreenNotificationcame;
    public static final String firstScreen =null;
    public static String ACTION="Signup_action";
    private String payload="Signup_message";
     SharedPreferences pref,notification_session;
     SharedPreferences.Editor editor,notification_editor;
    Context mcontext;

    public SpotasManager(Context context)
    {
        this.mcontext = context;
        pref = mcontext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        notification_session=mcontext.getSharedPreferences(NOT_PEF,PRIVATE_MODE);
        notification_editor=notification_session.edit();
        editor = pref.edit();
    }

    public Object clear_notification()
    {
        return notification_editor.clear();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean("IS_LOGIN", false);
    }

    public void setIsLogin(boolean value) {
        editor.putBoolean("IS_LOGIN", value);
        editor.commit();
    }

    public String getUserId() {
        return pref.getString(USER_ID, null);
    }

    public void setUserId(String userid) {
        editor.putString(USER_ID, userid);
        editor.commit();
    }
    public String getCustomerId() {
        return pref.getString(CUSTOMER_ID, null);
    }

    public void setCustomerId(String userid) {
        editor.putString(CUSTOMER_ID, userid);
        editor.commit();
    }

    public String getSession_token() {
        return pref.getString(Session_token, null);
    }

    public void setSession_token(String session_token) {
        editor.putString(Session_token, session_token);
        editor.commit();
    }

   /* *
     * Notification List...... getter and Setter..*/
    public void setNotificationList(String notificationList)
    {
        notification_editor.putString(NotificationList,notificationList);
        notification_editor.commit();
    }

    public String getNotificationList()
    {
        return notification_session.getString(NotificationList, null);
    }
    /**
     * for seving the address
     *
     * @return string
     */
    public String getDbhouseno()
    {
    return pref.getString(DBHOUSENO, null);
    }
    public void setDbhouseno(String dbhouseno)
    {
        editor.putString(DBHOUSENO,dbhouseno);
        editor.commit();
    }
    public String getDblandmark()
    {
        return pref.getString(DBLANDMARK, null);
    }
    public void setDblandmark(String dblandmark)
    {
        editor.putString(DBLANDMARK,dblandmark);
        editor.commit();
    }
    public String getDbfulladdress()
    {
        return pref.getString(DBFULLADDRESS, null);
    }
    public void setDbfulladdress(String dbfulladdress)
    {
        editor.putString(DBFULLADDRESS,dbfulladdress);
        editor.commit();
    }
    public void setDbclatitude(String dbclatitude)
    {
        editor.putString(DBCLATITUDE,dbclatitude);
        editor.commit();
    }
    public String getDbclatitude()
    {
        return pref.getString(DBCLATITUDE,null);
    }
    public void setDbclongitude(String dbclongitude)
    {
        editor.putString(DBCLONGITUDE,dbclongitude);
        editor.commit();
    }
    public String getDbclongitude()
    {
        return pref.getString(DBCLONGITUDE,null);
    }
    /******************************/
    public String getCoupon()
    {
        return pref.getString(Coupon, null);
    }
    public void setCoupon(String coupon)
    {
        editor.putString(Coupon,coupon);
        editor.commit();
    }
    /***********************/

    public String getPhone() {
        return pref.getString(Phone, null);
    }

    public void setPhone(String phone) {
        editor.putString(Phone, phone);
        editor.commit();
    }
    public String getEmailId() {
        return pref.getString(EMAIL_ID, null);
    }

    public void setEmailId(String emailId) {
        editor.putString(EMAIL_ID, emailId);
        editor.commit();
    }

    public String getFname() {
        return pref.getString(Fname, null);
    }

    public void setFname(String fname) {
        editor.putString(Fname, fname);
        editor.commit();
    }

    /*public String getLname() {
        return pref.getString(Lname, null);
    }

    public void setLname(String lname) {
        editor.putString(Lname, lname);
        editor.commit();
    }*/

    public  String getDESLatitude()
    {
        return pref.getString(DESLatitude,null);
    }
    public void setDESLatitude(String latitude)
    {
        editor.putString(DESLatitude,latitude);

        editor.commit();
    }
    public  String getDESLongitude()
    {
        return pref.getString(DESLongitude,null);
    }
    public void setDESLongitude(String longitude)
    {
        editor.putString(DESLatitude,longitude);

        editor.commit();
    }
    public  String getPickupdeliver()
    {
        return pref.getString(PICKUPDELIVER, null);
    }
    public void setPickupdeliver(String pickupdeliver)
    {
        editor.putString(PICKUPDELIVER, pickupdeliver);
        editor.commit();
    }
    public  String getMinimumAmount()
    {
        return pref.getString(MINIMUM_AMOUNT, null);
    }
    public void setMinimumAmount(String minimumAmount)
    {
        editor.putString(MINIMUM_AMOUNT, minimumAmount);
        editor.commit();
    }

    public  boolean isPushTokenUpdated()
    {
        return pref.getBoolean(PUSHTOKEN_UPDATED, false);
    }

    public void setPushTokenUpdate(boolean status)
    {
        editor.putBoolean(PUSHTOKEN_UPDATED, status);
        editor.commit();
    }


    public  String getOpencolsestatus()
    {
        return pref.getString(OPENCOLSESTATUS, null);
    }
    public void setOpencolsestatus(String opencolsestatus)
    {
        editor.putString(OPENCOLSESTATUS, opencolsestatus);

        editor.commit();
    }
    public  String getProfilePic()
    {
        return pref.getString(ProfilePic,null);
    }
    public void setProfilePic(String profilePic)
    {
        editor.putString(ProfilePic,profilePic);

        editor.commit();
    }
    public  String getLivelati()
    {
        return pref.getString(LIVELATI,null);
    }
    public void setLivelati(String livelati)
    {
        editor.putString(LIVELATI,livelati);

        editor.commit();
    }


    public  String getLivelongi()
    {
        return pref.getString(LIVELONGI,null);
    }
    public void setLivelongi(String livelongi)
    {
        editor.putString(LIVELONGI,livelongi);

        editor.commit();
    }
    public  String getPaymentType()
    {
        return pref.getString(PAYMENT_TYPE,null);
    }
    public void setPaymentType(String paymenttype)
    {
        editor.putString(PAYMENT_TYPE,paymenttype);

        editor.commit();
    }

    public  String getSavingsearchedadress()
    {
        return pref.getString(SAVINGSEARCHEDADRESS,null);
    }
    public void setSavingsearchedadress(String adressadata)
    {
        editor.putString(SAVINGSEARCHEDADRESS,adressadata);
        editor.commit();
    }
    public  String getBusinesstype()
    {
        return pref.getString(BUSINESSTYPE,null);
    }
    public void setBusinesstype(String businesstype)
    {
        editor.putString(BUSINESSTYPE,businesstype);

        editor.commit();
    }

    public  String getBusinessname()
    {
        return pref.getString(BUSINESSNAME,null);
    }
    public void setBusinessname(String businessname)
    {
        editor.putString(BUSINESSNAME,businessname);

        editor.commit();
    }

    public  String getTimingatlivebooking()
    {
        return pref.getString(TIMINGATLIVEBOOKING,null);
    }
    public void setTimingatlivebooking(String timingatlivebooking)
    {
        editor.putString(TIMINGATLIVEBOOKING,timingatlivebooking);

        editor.commit();
    }

    public  String getAddtofavourit()
    {
        return pref.getString(ADDTOFAVOURIT,null);
    }
    public void setAddtofavourit(String businesstype)
    {
        editor.putString(ADDTOFAVOURIT,businesstype);

        editor.commit();
    }

    public  String getCurrentlatitude()
    {
        return pref.getString(CURRENTLATITUDE,"0");
    }
    public void setCurrentlatitude(String clatitude)
    {
        editor.putString(CURRENTLATITUDE,clatitude);

        editor.commit();
    }
    public  String getCurrentlongitude()
    {
        return pref.getString(CURRENTLONGITUDE,"0");
    }
    public void setCurrentlongitude(String clongitude)
    {
        editor.putString(CURRENTLONGITUDE,clongitude);
        editor.commit();
    }
    /**********************************************************************************************/
    public void setIsPendingScreenNotificationcame(boolean isPendingScreenNotificationcame) {
        this.isPendingScreenNotificationcame = isPendingScreenNotificationcame;
    }
    public void setIsreturnedfromsavedaddress(String isreturnedfromsavedaddress) {
        editor.putString(ISRETURNEDFROMSAVEDADDRESS, isreturnedfromsavedaddress);
        editor.commit();
    }
   public String getIsreturnedfromsavedaddress()
   {
       return pref.getString(ISRETURNEDFROMSAVEDADDRESS, "");
   }



    public boolean isFirstScreen() {
        return pref.getBoolean(firstScreen, false);
    }


    public void setACTION(String ACTION) {
        SpotasManager.ACTION = ACTION;
    }
//    public boolean isPayPalAdded()
//      {
//         return pref.getBoolean(PayPal_Added, false);
//       }
       public void setPayload(String payload) {
       this.payload = payload;
   }


    public void storeServerKey(String key) {

        editor.putString(SERVER_API, key);
        editor.commit();
    }
    public String getServerKey()
    {
        return pref.getString(SERVER_API, "");
    }

    public void clearSession()
    {
        editor.clear();
        editor.commit();
    }
    /***********************************************************************************************/
}




