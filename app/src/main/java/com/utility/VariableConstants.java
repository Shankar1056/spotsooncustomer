package com.utility;

/**
 * Created by embed on 13/6/15.
 */
public class VariableConstants
{


   //public static String BASE_URL = "http://162.243.89.41/spotsoon/";
    public static String BASE_URL = "http://admin.spotsoon.com/";

    public static final String host_url  = BASE_URL+"spotsoon/services_v8.php/";
    public static final String GET_PAYMENTSTATUS  = BASE_URL+"spotsoon/services_v8.php/";
    public static final String INVOICE_MGMT_SUBSCRIPTION =  BASE_URL+"index.php/api/sms/getCustomer";
    public static final String PayUTxnId_INVOICE_MGMT =  BASE_URL+"index.php/api/sms/createTransaction";
    public static final String host_url1 =  BASE_URL+"Services/process_v3.php/"; //Get BusinessList Working, Now Commented
    public static final String payU_Success_Url = BASE_URL+"SuccessPayU_v4.php";
    public static final String payU_Failure_Url = BASE_URL+"spotsoon/payU/failure.php";
    public static final String image_path= BASE_URL+"spotsoon/pics/";

    public static final String GETINVOICEDETAILS = BASE_URL+"spotsoon/services_v8.php/getSMSInvoiceDetails";


    public static final String serverapikey = "AIzaSyCBjqTTBqueTKZCvxYdyAqx0lUPTxjZ_xw";
    public static boolean lastorder_summery=false;
    public static final String SENDER_ID_PUSH ="914229093840";

    public static final String noitfy_id="Notificationid";

    public static boolean showmylocation = false;

    public static boolean fromConformOrder=false;

    public static boolean fromCurrent=false;

    public static boolean forSubscription=false;

    public static boolean fromHistory=false;

    public static boolean fromRecuringbusiness=false;

    public static boolean fromLastordertomyorder=false;

    public static int counterfornotifiation=0;

    //public static boolean fromnotificationaleret=false;

    //public static boolean isNotificationaleretpageOpened=false;

    public static boolean neworder=false;

    public static boolean isFromnotification=false;

    public static boolean isAppalive=false;

    public static boolean isMenuActivityActivity=false;

    public static int notificationNumber=0;

    public static boolean isUserwantscurrentLocation=true;

}
