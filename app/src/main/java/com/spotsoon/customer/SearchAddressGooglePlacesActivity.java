package com.spotsoon.customer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.utility.DBLocations;
import com.utility.DatabaseDropHandler;
import com.utility.DatabasePickupHandler;
import com.utility.PlaceDetailsJSONParser;
import com.utility.PlaceJSONParser;
import com.utility.SpotasManager;
import com.utility.Utility;
import com.utility.VariableConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;


public class SearchAddressGooglePlacesActivity extends Activity implements LocationListener
{
	EditText atvPlaces;
	PlacesTask placesTask;
	ParserTask parserTask;
	
	private ListView searchAddressListview;
	/***********************************/
	RelativeLayout cureentlocationtv;
	/************************/
	double currentLatitude,currentLongitude;
	private String HintText;
	private ArrayList<String> reference_id_list = new ArrayList<String>();
	private ArrayList<String> address_list = new ArrayList<String>();
	ParserTask placeDetailsParserTask,placesParserTask;
	final int PLACES=0;
	final int PLACES_DETAILS=1;	
	DownloadTask placeDetailsDownloadTask;
	private static int clicked_index=0;
	private DatabasePickupHandler dbPickup;
	private DatabaseDropHandler dbDrop;
	private List<DBLocations>  dbLocations;
	private List<String> resultDataList = new ArrayList<String>();
	private SpotasManager spotasManager;
	private ProgressBar progressBar;

	protected LocationManager locationManager;
	private static final long MIN_DISTANCE_FOR_UPDATE = 10;
	private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;
	protected static final int RESULT_SPEECH = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		Utility.statusbar(SearchAddressGooglePlacesActivity.this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Fabric.with(SearchAddressGooglePlacesActivity.this, new Crashlytics());

		setContentView(R.layout.activity_main_search);
		spotasManager=new SpotasManager(this);
		atvPlaces = (EditText) findViewById(R.id.atv_places);
		atvPlaces.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
		ImageButton cancel = (ImageButton)findViewById(R.id.cancel_search);
		searchAddressListview=(ListView) findViewById(R.id.search_address_listview);
		cureentlocationtv = (RelativeLayout) findViewById(R.id.cureentlocationtv);
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		currentLatitude = getIntent().getDoubleExtra("curlat",0);
		currentLongitude = getIntent().getDoubleExtra("curlong",0);
		Utility.printLog("the latlong is " + currentLatitude + " " + currentLongitude);

		if(VariableConstants.showmylocation) {
			cureentlocationtv.setVisibility(View.VISIBLE);
		}
		else
		{
			cureentlocationtv.setVisibility(View.GONE);
		}

		cureentlocationtv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				CurrentLocation();
			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

					finish();
					overridePendingTransition(R.anim.anim_three, R.anim.anim_four);
			}
		});

		if(spotasManager.getSavingsearchedadress().equals("Homefragment"))
		{
			dbPickup = new DatabasePickupHandler(this);
			dbLocations = dbPickup.getAllPickupLocations();
			
			for(int i=0;i<dbLocations.size() && i<7;i++)
			{
				resultDataList.add(dbLocations.get(i).getFormattedAddress());
			}
		}
		else if(spotasManager.getSavingsearchedadress().equals("CurrentLocation"))
		{
			dbDrop = new DatabaseDropHandler(this);
			dbLocations = dbDrop.getAllDropLocations();
			
			for(int i=0;i<dbLocations.size();i++)
			{
				resultDataList.add(dbLocations.get(i).getFormattedAddress());
			}
		}
		
		if(resultDataList.size()>0)
		{
			AddressAdapterNew adapter=new AddressAdapterNew(SearchAddressGooglePlacesActivity.this, resultDataList);
			searchAddressListview.setAdapter(adapter);
		}
		

		atvPlaces.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				placesTask = new PlacesTask();				
				placesTask.execute(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) 
			{
				// TODO Auto-generated method stub
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub				
			}
		});	
		
		searchAddressListview.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				VariableConstants.isUserwantscurrentLocation=false;

				clicked_index = arg2;
                placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);
                
                if(reference_id_list.size()>0 && reference_id_list.get(arg2)!=null)
                {
    				String url = getPlaceDetailsUrl(reference_id_list.get(arg2));
    				placeDetailsDownloadTask.execute(url);
    				return;
                }
                
                if(dbLocations.size()>0)
                {
				     Intent returnIntent = new Intent();
					 returnIntent.putExtra("LATITUDE_SEARCH",dbLocations.get(clicked_index).getLatitude());
					 returnIntent.putExtra("LONGITUDE_SEARCH",dbLocations.get(clicked_index).getLongitude());
					 returnIntent.putExtra("SearchAddress",dbLocations.get(clicked_index).getFormattedAddress());
					 setResult(RESULT_OK,returnIntent);
					 finish();
                }
			}
		});
	}
	
	
		private class DownloadTask extends AsyncTask<String, Void, String>{
			
			private int downloadType=0;
			
			// Constructor
			public DownloadTask(int type){
				this.downloadType = type;			
			}

			@Override
			protected String doInBackground(String... url) {
				
				String data = "";
				
				try{
					data = downloadUrl(url[0]);
				}catch(Exception e){
				}
				return data;		
			}
			
			@Override
			protected void onPostExecute(String result) {			
				super.onPostExecute(result);		
				
				switch(downloadType){
				case PLACES:
					// Creating ParserTask for parsing Google Places
					placesParserTask = new ParserTask(PLACES);
					
					placesParserTask.execute(result);
					
					break;
					
				case PLACES_DETAILS : 
					placeDetailsParserTask = new ParserTask(PLACES_DETAILS);
					
					placeDetailsParserTask.execute(result);
				}
			}
		}
		
		private String getPlaceDetailsUrl(String ref)
		{
			String key = "";
			if(spotasManager.getServerKey()!=null && spotasManager.getServerKey().length()>0)
			{
				key = "key="+spotasManager.getServerKey();
			}
			else {
				key = "key="+VariableConstants.serverapikey;
			}

			String reference = "reference="+ref;
						
			String sensor = "sensor=false";
						
			String parameters = reference+"&"+sensor+"&"+key;
						
			String output = "json";
			
			String url = "https://maps.googleapis.com/maps/api/place/details/"+output+"?"+parameters;

			return url;
		}
	@Override
	protected void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private class AddressAdapterNew extends ArrayAdapter<String>
	{
		List<String> objects;
		Activity activity;
		public AddressAdapterNew(Activity activity, List<String> objects) {
			super(activity, R.layout.activity_main_search, objects);
			this.objects=objects;
			this.activity=activity;
			// TODO Auto-generated constructor stub
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{        
			// TODO Auto-generated method stub
			if(convertView==null)
			{
				convertView=activity.getLayoutInflater().inflate(R.layout.address_item	, null);
			}
			
			TextView locationName=(TextView) convertView.findViewById(R.id.location_name);
		    TextView addressTextview=(TextView) convertView.findViewById(R.id.address_textview);
			ImageView timingiv = (ImageView) convertView.findViewById(R.id.timingiv);
		    String[] total_addressStrings = objects.get(position).split(",") ;
			if(VariableConstants.showmylocation) {
				timingiv.setVisibility(View.VISIBLE);
			}
			else
			{
				timingiv.setVisibility(View.GONE);
			}
		
			if(total_addressStrings.length>0)
			{
				String first_name = total_addressStrings[0];
				
				String last_name="";
				for(int i=1;i<total_addressStrings.length;i++)
				{
					last_name= last_name+total_addressStrings[i];
				}
				locationName.setText(first_name);
				addressTextview.setText(last_name);
			}
			return convertView;
		}
	}
	
	
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
                URL url = new URL(strUrl);                

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb  = new StringBuffer();

                String line = "";
                while( ( line = br.readLine())  != null){
                        sb.append(line);
                }
                
                data = sb.toString();

                br.close();

        }catch(Exception e){
        }finally{
                iStream.close();
                urlConnection.disconnect();
        }
        return data;
     }	

	// Fetches all places from GooglePlaces AutoComplete Web Service
	private class PlacesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... place) {
			// For storing data from web service
			String data = "";
			

			String key = "";
			if(spotasManager.getServerKey()!=null && spotasManager.getServerKey().length()>0)
			{
				key = "key="+spotasManager.getServerKey();
			}
			else {
				key = "key="+VariableConstants.serverapikey;
			}

			String input="";
			
			try {
				input = "input=" + URLEncoder.encode(place[0], "utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}		

			String types = "establishment|geocode&location="+currentLatitude+","+currentLongitude+"&radius=500po&language=en";

			// Building the parameters to the web service
			String parameters = input+"&"+types+"&"+key;
			
			// Output format
			String output = "json";
			
			// Building the url to the web service
			String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

			try{
				// Fetching the data from web service in background
				data = downloadUrl(url);
			}catch(Exception e){
               // Log.d("Background Task",e.toString());
			}
			return data;		
		}
		
		@Override
		protected void onPostExecute(String result) {			
			super.onPostExecute(result);

			// Creating ParserTask
			parserTask = new ParserTask(PLACES);
			
			// Starting Parsing the JSON string returned by Web Service
			parserTask.execute(result);
		}		
	}
	
	/** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>
    {
    	JSONObject jObject;
    	
    	int parserType = 0;
    	
    	public ParserTask(int type){
    		this.parserType = type;
    	}
		@Override
		protected List<HashMap<String, String>> doInBackground(String... jsonData) 
		{			
			List<HashMap<String, String>> places = null;
            
			try {
				
				if(jsonData[0]!=null)
				{
					jObject = new JSONObject(jsonData[0]);
					switch(parserType){
		        	case PLACES :
		        		PlaceJSONParser placeJsonParser = new PlaceJSONParser();
			            // Getting the parsed data as a List construct
		        		places = placeJsonParser.parse(jObject);
			            break;
		        	case PLACES_DETAILS :      	            	
		            	PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
		            	// Getting the parsed data as a List construct
		            	places = placeDetailsJsonParser.parse(jObject);
				}
				
	        	}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//Utilities.printLog("ParserTask Exception: "+e.toString());
			}
            
            return places;
		}
		
		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) 
		{
			progressBar.setVisibility(View.GONE);

				switch(parserType)
				{
				case PLACES :
					
					if(result!=null)
					{

		                reference_id_list.clear();
		                address_list.clear();
		                
		                for(int i=0;i<result.size();i++)
		                {
		                	address_list.add(result.get(i).get("description"));
		                	reference_id_list.add(result.get(i).get("reference"));
		                }
		                

						AddressAdapterNew adapter=new AddressAdapterNew(SearchAddressGooglePlacesActivity.this, address_list);
						searchAddressListview.setAdapter(adapter);
					}
					
					break;
				case PLACES_DETAILS :

					if(result!=null && result.size()>0)
					{
						double latitude = Double.parseDouble(result.get(0).get("lat"));
						double longitude = Double.parseDouble(result.get(0).get("lng"));	

						 
						   if(spotasManager.getSavingsearchedadress().equals("Homefragment"))
						   { 
							   dbPickup.addPickupLocation("",
									  address_list.get(clicked_index),
									 ""+latitude
									,""+longitude);
							}
							else if(spotasManager.getSavingsearchedadress().equals("CurrentLocation"))
							{
								 dbDrop.addDropLocation("",
										 address_list.get(clicked_index),
									 ""+latitude
									,""+longitude);
							}
						 
						 Intent returnIntent = new Intent();
						 returnIntent.putExtra("SearchAddress",address_list.get(clicked_index));
						 returnIntent.putExtra("ADDRESS_NAME","");
						 returnIntent.putExtra("LATITUDE_SEARCH",""+latitude);
						 returnIntent.putExtra("LONGITUDE_SEARCH",""+longitude);
						 setResult(RESULT_OK,returnIntent);     
						 finish();
					}
					else if(dbLocations.size()>0)
					{
					     Intent returnIntent = new Intent();
						 returnIntent.putExtra("LATITUDE_SEARCH",dbLocations.get(clicked_index).getLatitude());
						 returnIntent.putExtra("LONGITUDE_SEARCH",dbLocations.get(clicked_index).getLongitude());
						 returnIntent.putExtra("SearchAddress",dbLocations.get(clicked_index).getFormattedAddress());
						 returnIntent.putExtra("ADDRESS_NAME",dbLocations.get(clicked_index).getAddressName());
						 setResult(RESULT_OK,returnIntent);  
						 finish();
				   }
					break;						
				}	
		}			
    }    
    
    @Override
    public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.anim_three, R.anim.anim_four);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return true;
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();

		if(locationManager!=null)
			locationManager.removeUpdates(this);
	}

	private void CurrentLocation()
	{
		final ProgressDialog pDialog = new ProgressDialog(this);
		pDialog.setMessage(getResources().getString(R.string.loading));
		pDialog.setCancelable(true);
		pDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						double currentLatitude = 0, currentLongitude = 0;
						Location gpsLocation = getCurrentLocation(LocationManager.GPS_PROVIDER);
						if(gpsLocation != null) {
							currentLatitude = gpsLocation.getLatitude();
							currentLongitude = gpsLocation.getLongitude();
						} else {
							Location nwLocation = getCurrentLocation(LocationManager.NETWORK_PROVIDER);
							if (nwLocation != null) {
								currentLatitude = nwLocation.getLatitude();
								currentLongitude = nwLocation.getLongitude();
							}
						}

						if (currentLatitude == 0.0 || currentLongitude == 0.0) {

							if(pDialog!=null && pDialog.isShowing())
							{
								pDialog.dismiss();
							}

							showSettingsAlert();
						} else {

							if(pDialog!=null && pDialog.isShowing())
							{
								pDialog.dismiss();
							}

							spotasManager.setCurrentlatitude("" + currentLatitude);
							spotasManager.setCurrentlongitude("" + currentLongitude);

							Intent intent = new Intent();
							intent.putExtra("LATITUDE_SEARCH",String.valueOf(currentLatitude));
							intent.putExtra("LONGITUDE_SEARCH", String.valueOf(currentLongitude));
							intent.putExtra("CURRENT", true);
							setResult(RESULT_OK, intent);
							VariableConstants.isUserwantscurrentLocation=true;
							finish();
						}
					}
				});
			}
		}).start();
	}

	private Location getCurrentLocation(String provider)
	{
		Location location;
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(provider)) {
			locationManager.requestLocationUpdates(provider, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, SearchAddressGooglePlacesActivity.this);
			if(locationManager != null) {
				location = locationManager.getLastKnownLocation(provider);
				return location;
			}
		}
		return null;
	}

	private void  showSettingsAlert()
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		// Setting Dialog Title
		alertDialog.setTitle("GPS Settings");
		alertDialog.setCancelable(false);

		// Setting Dialog Message
		alertDialog.setMessage("Please help us determine your location to show businesess near you. Click on Settings and turn on. Thanks");

		// On pressing Settings button
		alertDialog.setPositiveButton("Search", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog,int which)
			{
				dialog.dismiss();
			}
		});

		// on pressing cancel button
		alertDialog.setNegativeButton("Settings", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();

				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			}
		});

		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}
}