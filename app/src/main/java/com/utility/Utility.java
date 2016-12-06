package com.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.spotsoon.customer.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by embed on 13/5/15.
 */
public class Utility
{
    /**
     * 3embed
     * method to get the device id
     * @param context
     * @return
     */

    public static String getDeviceId(Context context)
    {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

   /* public static String getDevicId(Context context)
    {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        //return deviceId;
    }*/
    public static ProgressDialog GetProcessDialog(Activity activity)
    {
        // prepare the dialog box
        ProgressDialog dialog = new ProgressDialog(activity,AlertDialog.THEME_HOLO_LIGHT);
        // make the progress bar cancelable
        dialog.setCancelable(false);
        // set a message text
        dialog.setMessage("Bitte warten...");

        // show it
        return dialog;
    }
    public static String getCurrentDateTimeString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String currentDateTimeString=dateFormat.format(date);
        return currentDateTimeString;
    }

    public static double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public static String round(double value) {

        return String.format("%.2f", value);

        /*if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();*/
    }

    public static String roundString(String value) {

        try {
            double d = Double.valueOf(value);
            return String.format("%.2f", d);
        }catch (NumberFormatException e)
        {
            return ""+value;
        }
        catch (Exception e1)
        {
            return ""+value;
        }
    }


    public static void printLog(String... msg)
    {
        String str="";
        for(String i : msg)
        {
            str= str+"\n"+i;
        }
        if(true)
        {
            Log.i("Spotsoon", str);
        }
    }

    public static String getappointmentdate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        Date date = new Date();
        String appointmentdate=dateFormat.format(date);
        return appointmentdate;
    }

    public static void doJsonRequest(String request_Url,JSONObject requestParameters,JsonRequestCallback callbacks)
    {
        Utility.JsonHttpRequestData data = new Utility.JsonHttpRequestData();
        data.request_Url = request_Url;
        data.requestParameter = requestParameters;
        data.callbacks = callbacks;

        new JsonHttpRequest().execute(data);
    }

    private static  class JsonHttpRequestData
    {
        String request_Url;
        JSONObject requestParameter;
        JsonRequestCallback callbacks;
    }
    /********************************************************************************************************************/

    private  static class JsonHttpRequest extends AsyncTask<JsonHttpRequestData, Void, String>
    {
        JsonRequestCallback callbacks;
        boolean error =false;

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(JsonHttpRequestData... params)
        {
            // TODO Auto-generated method stub
            callbacks = params[0].callbacks;
            String result="";

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(params[0].request_Url);
//            post.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
//            post.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 60000); //Timeout Limit
            HttpResponse http_response;

            StringEntity request;
            try
            {
                request = new StringEntity( params[0].requestParameter.toString());

                // To Check sending parameters open following comment
                Utility.printLog("Request Data: "+params[0].requestParameter.toString());

                request.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(request);
                http_response = client.execute(post);
                result = EntityUtils.toString(http_response.getEntity());
            }
            catch (UnsupportedEncodingException e)
            {
                error= true;
                result = e.toString();
                e.printStackTrace();
            }
            catch (ClientProtocolException e)
            {
                error= true;
                result = e.toString();
                e.printStackTrace();
            }
            catch (IOException e)
            {
                error= true;
                result = e.toString();
                e.printStackTrace();
            }
            catch (Exception e)
            {
                error= true;
                result = e.toString();
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            if(!error)
            {
                callbacks.onSuccess(result);
            }
            else
            {
                callbacks.onError(result);
            }
        }
    }
    /********************************************************************************************************************/

    public  interface JsonRequestCallback
    {
        /**
         * Called When Success result of JSON request
         *
         * @param result
         */
         void onSuccess(String result);


        /**
         * Called When Error result of JSON request
         *
         * @param error
         */
         void onError(String error);

    }


    private long FILE_SIZE = 0, totalBytesRead = 0, bytesRemaining;
    private String fileTitle;
    FileInputStream fileInputStream = null;


    private File srcFile;

    int countr=0;
    long chunkLength =524288;


    String encodedString;
    private String state = Environment.getExternalStorageState();
   public static List<NameValuePair> getUploadParameter(String [] params)
{
    List<NameValuePair> namevaluepairs = new ArrayList<NameValuePair>();
    namevaluepairs.add(new BasicNameValuePair("ent_sess_token",params[0]));
    namevaluepairs.add(new BasicNameValuePair("ent_dev_id",params[1]));
    namevaluepairs.add(new BasicNameValuePair("ent_snap_name",params[2]));
    namevaluepairs.add(new BasicNameValuePair("ent_snap_chunk",params[3]));
    namevaluepairs.add(new BasicNameValuePair("ent_upld_from",params[4]));
    namevaluepairs.add(new BasicNameValuePair("ent_snap_type",params[5]));
    namevaluepairs.add(new BasicNameValuePair("ent_offset",params[6]));
    namevaluepairs.add(new BasicNameValuePair("ent_date_time",params[7]));


    return namevaluepairs;
}


    public static String makeHttpRequest(String url, String method,
                                         List<NameValuePair> params) {

        InputStream is = null;
        // Making HTTP request
        try {

            // check for request method
            if(method == "POST"){
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                System.out.println("--------Orignal URL-------"+params);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            }
            else if(method == "GET")
            {
                // request method is GET
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
                HttpConnectionParams.setSoTimeout(httpParameters, 20000);
                DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
                //  DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                System.out.println("--------Orignal URL-------"+params);
                System.out.println("***paramString***"+paramString);
                url += "?" + paramString;
                System.out.println("***url***"+url);
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
                Log.e("is^",is.toString());
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response=null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader( is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            //logDebug("makeHttpRequest  resposns  "+sb.toString());
            response=sb.toString();
            //  json = sb.toString();
        } catch (Exception e)
        {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        //			        try
        //			        {
        //			            jObj = new JSONObject(json);
        //			        }
        //			        catch (JSONException e)
        //			        {
        //			            Log.e("JSON Parser", "Error parsing data " + e.toString());
        //			        }
        //
        // return JSON String
        //   return jObj;
        return response;

    }


    public static boolean isNetworkAvailable(Context context)
    {

        ConnectivityManager connectivity  = null;
        boolean isNetworkAvail = false;

        try
        {
            connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivity != null)
            {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();

                if (info != null)
                {
                    for (int i = 0; i < info.length; i++)
                    {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        {

                            return true;
                        }
                    }
                }
            }
            return false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(connectivity !=null)
            {
                connectivity = null;
            }
        }
        return isNetworkAvail;
    }


    public static int getWidth(Context mContext)
    {
        int width=0;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if(Build.VERSION.SDK_INT >=13){
            Point size = new Point();
            display.getSize(size);
            width = size.x;
        }
        else{
            width = display.getWidth();  // deprecated
        }
        return width;
    }



    public static int getHeight(Context mContext)
    {
        int height=0;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if(Build.VERSION.SDK_INT >=13){
            Point size = new Point();
            display.getSize(size);
            height = size.y;
        }else
        {
            height = display.getHeight();  // deprecated
        }
        return height;
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public static void statusbar(Activity activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
           // window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //int blueColorValue = Color.parseColor("#066CAB");
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_color));

        }
    }

    public static String dateFromAdapter = null;
    public static final int HTTP_TIMEOUT = 60 * 1000; // milliseconds

    private static HttpClient mHttpClient;


    public static final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }


    private static HttpClient getHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new DefaultHttpClient();
            final HttpParams params = mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
            ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);
        }
        return mHttpClient;

    }


    public static String executeHttpPost(String url,
                                         ArrayList<NameValuePair> postParameters) throws Exception {
        BufferedReader in = null;
        try {
            HttpClient client = getHttpClient();
            HttpPost request = new HttpPost(url);
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
                    postParameters);
            request.setEntity(formEntity);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity()
                    .getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();

            String result = sb.toString();
            return result;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String GetTodaysDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(c.getTime());
    }
}
