package com.utility;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by embed on 15/7/15.
 */
public class GeoDecoder
{
    public static String callhttpRequest(String url)
    {
        System.out.println("utility url..."+url);
        url=url.replaceAll(" ", "%20");
        String resp = null;
        HttpGet httpRequest;
        try {
            httpRequest = new HttpGet(url);
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 60000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            int timeoutSocket = 60000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpResponse response = httpClient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            final long contentLength = bufHttpEntity.getContentLength();
            if ((contentLength >= 0))
            {
                InputStream is = bufHttpEntity.getContent();
                int tobeRead = is.available();
                System.out.println("Utility callhttpRequest tobeRead.."+tobeRead);
                ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
                int ch;

                while ((ch = is.read()) != -1)
                {
                    bytestream.write(ch);
                }

                resp = new String(bytestream.toByteArray());
                System.out.println("Utility callhttpRequest resp.."+resp);
            }
        } catch (MalformedURLException e) {
            System.out.println("Utility callhttpRequest.."+e);
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            System.out.println("Utility callhttpRequest.."+e);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Utility callhttpRequest.."+e);
            e.printStackTrace();
        }catch (Exception e) {
            System.out.println("Utility Exception.."+e);
        }
        return resp;
    }

    //Http post method

    public static HttpResponse doPost(String url, Map<String, String> kvPairs)
            throws ClientProtocolException, IOException
    {
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpClient httpclient = defaultHttpClient;

        HttpPost httppost = new HttpPost(url);

        if (kvPairs != null || kvPairs.isEmpty() == false)
        {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(kvPairs.size());
            String k, v;
            Iterator<String> itKeys = kvPairs.keySet().iterator();

            while (itKeys.hasNext()) {
                k = itKeys.next();
                v = kvPairs.get(k);
                nameValuePairs.add(new BasicNameValuePair(k, v));
            }

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        }

        HttpResponse response;
        response = httpclient.execute(httppost);
        Log.i("TAG", "doPost response........." + response);
        return response;
    }
    public String getAddress(double latitude, double longitude,Context context) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getLocality()).append("\n");
                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }

}
