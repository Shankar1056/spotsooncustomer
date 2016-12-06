package com.threembed;

/**
 * Created by bala on 3/12/15.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.spotsoon.customer.R;
import com.spotsoon.customer.SpotAsap;
import com.utility.SpotasManager;
import com.utility.Utility;
import com.utility.VariableConstants;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class
MainActivityNew extends AppCompatActivity implements OnItemClickListener{

	private ArrayList<NavigationItem> Items = new ArrayList<NavigationItem>();
	private int currentTabStatus = -1;
	private DrawerLayout Drawer;                                  // Declaring DrawerLayout
	private boolean doubleBackToExitPressedOnce = false,hasHomeValues;
	private String homeResponse;
	private BroadcastReceiver broadcastReceiver;
	private boolean isInternet = false;
	private SpotasManager session;
	private static  boolean home = true;
	private static android.support.v4.app.FragmentManager fragmentManager;


	//Shankar Kumar
	//private Tracker mTracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(MainActivityNew.this, new Crashlytics());
		Utility.statusbar(MainActivityNew.this);
		setContentView(R.layout.activity_main_new);

//		AnalyticsApplication application = (AnalyticsApplication) getApplication(); //Shankar Kumar
//		mTracker = application.getDefaultTracker();

		Toolbar mToolbar = (Toolbar)findViewById(R.id.tool_bar);
		setSupportActionBar(mToolbar);

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setTitle("");
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		fragmentManager = getSupportFragmentManager();
		Intent intent = getIntent();
		if(intent!=null && intent.hasExtra("HAS_VALUE"))
		{
			hasHomeValues = intent.getBooleanExtra("HAS_VALUE", false);
			if(hasHomeValues)
			{
				homeResponse = intent.getStringExtra("VALUE");
			//	Log.i("Spotsoon","homeResponse="+homeResponse);
			}
		}

		/* Assinging the toolbar object ot the view
    	   and setting the the Action bar to our toolbar
		 */
		 session = new SpotasManager(MainActivityNew.this);

		//{"New order","Records","Manage My Drivers","Settings"};
		Items.add(new NavigationItem(session.getFname(), session.getEmailId(),0, true));
		Items.add(new NavigationItem("Home", "", R.drawable.customer_home_slider_home_btn, false));
		Items.add(new NavigationItem("My Orders", "", R.drawable.customer_home_slider_my_order_btn, false));
		Items.add(new NavigationItem("Invite Friends to SpotSoon", "", R.drawable.customer_home_slider_share_btn, false));
		Items.add(new NavigationItem(getResources().getString(R.string.help), "", R.drawable.customer_home_slider_support_btn, false));
		Items.add(new NavigationItem("About", "", R.drawable.customer_home_slider_home_btn, false));
		Items.add(new NavigationItem("Rate Us", "", R.drawable.customer_home_slider_share_btn, false));
		Items.add(new NavigationItem("Suggest a Business", "", R.drawable.customer_home_slider_suggest_provider_btn, false));
		//Items.add(new NavigationItem("Log OUT", "", R.drawable.customer_home_slider_suggest_provider_btn, false));

		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);

		ListView listView = (ListView)findViewById(R.id.listItems);

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		//int height = metrics.heightPixels;

		DrawerLayout.LayoutParams list = (DrawerLayout.LayoutParams) listView.getLayoutParams();
		list.width = width - 100;
		listView.setLayoutParams(list);

		ItemAdapter itemAdapter = new ItemAdapter(this,R.layout.item_row,Items);
		
		listView.setAdapter(itemAdapter);
		listView.setOnItemClickListener(this);

		Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
		ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.drawer_open,R.string.drawer_close){
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				// code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
				// open I am not going to put anything here)
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				// Code here will execute once drawer is closed
			}

		}; // Drawer Toggle Object Made
		Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
		mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State



		if(VariableConstants.isFromnotification)
		{
			VariableConstants.isFromnotification = false;

			getSupportActionBar().setCustomView(R.layout.toolbar_menu);
			getSupportActionBar().setTitle("My Orders");

			Fragment fragment = new MyOrdersNew();
			if (fragment != null)
			{
				android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
				android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.container, fragment);
				fragmentTransaction.commit();
				setTitle(getResources().getString(R.string.app_name));
				home = false;
			}
		}
		else {//default as home page

			Fragment fragment = new HomeFragmentNew();
			if (fragment != null)
			{
				android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
				android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.container, fragment);
				fragmentTransaction.commit();
				setTitle(getResources().getString(R.string.app_name));
				home = true;
			} else {
				// error in creating fragment
			}
		}


		//Shankar Kumar
		/*mTracker.send(new HitBuilders.EventBuilder()
				.setCategory("Action")
				.setAction("MainActivity")
				.build());*/


	}



	@Override
	protected void onResume() {
		super.onResume();
//		mTracker.setScreenName("Splash and Login" + "Splash and Login");
//		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Drawer.closeDrawers();
		Fragment fragment = null;
		switch(position)
		{
			case 0:
				currentTabStatus=0;
				home = false;
				getSupportActionBar().setTitle("My Profile");
				fragment = new ProfileFragment();

				break;
			case 1:
				currentTabStatus=1;
				home = true;
				getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
				fragment = new HomeFragmentNew();

				Bundle bundle = new Bundle();
				bundle.putBoolean("HAS_VALUE", hasHomeValues);
				if(hasHomeValues)
				{
					bundle.putString("VALUE", homeResponse);
				}

				fragment.setArguments(bundle);

				break;
			 case 2:
                currentTabStatus=2;
				 home = false;
				getSupportActionBar().setCustomView(R.layout.toolbar_menu);
                getSupportActionBar().setTitle("My Orders");
                fragment = new MyOrdersNew();

                break;

            case 3:
                currentTabStatus=3;
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.putExtra(Intent.EXTRA_TEXT, "Check out SpotSoon andriod billing app. Download it from " +"https://t.co/LDXLXfYaLL" );//Check out SpotSoon andriod shopping app. Download it from   spManager.getCoupon()
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT, "Check out SpotSoon App");
				startActivity(Intent.createChooser(intent, "Invite Friends"));

                break;
            case 4:
                currentTabStatus=4;
				home = false;
				getSupportActionBar().setCustomView(R.layout.toolbar_menu);
                getSupportActionBar().setTitle(R.string.help);
                fragment = new SupportFragment();
                break;
            case 5:
                currentTabStatus=5;
				home = false;
				getSupportActionBar().setCustomView(R.layout.toolbar_menu);
				getSupportActionBar().setTitle("About");
				fragment = new AboutFragment();

                break;
			case 6:
				currentTabStatus=6;
				rateUsPopup();

				break;

			case 7:
				currentTabStatus=7;
				home = false;
				getSupportActionBar().setCustomView(R.layout.toolbar_menu);
				getSupportActionBar().setTitle(R.string.suggest_provider);
				fragment = new suggest_provider();

				break;
			case 8:
				currentTabStatus=8;
				session.setIsLogin(false);
				Intent splash = new Intent(MainActivityNew.this, SpotAsap.class);
				splash.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(splash);
				finish();
				overridePendingTransition(R.anim.anim_three, R.anim.anim_four);

				break;

		default:
			 currentTabStatus=0;
			home = true;
			 getSupportActionBar().setCustomView(R.layout.toolbar_menu);
			 getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
			 fragment = new HomeFragmentNew();
			break;
		}

		if (fragment != null)
        {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commit();
            setTitle(getResources().getString(R.string.app_name));
        } else {
            // error in creating fragment
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}


	public class ItemAdapter extends ArrayAdapter<NavigationItem> {

		// declaring our ArrayList of items
		private ArrayList<NavigationItem> objects;

		public ItemAdapter(Context context, int textViewResourceId, ArrayList<NavigationItem> objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
		}


		@Override
		public int getCount() {
			return this.objects.size();
		}

		public View getView(int position, View convertView, ViewGroup parent){
			// assign the view we are converting to a local variable
			View v = convertView;
			NavigationItem item = objects.get(position);
			// first check to see if the view is null. if so, we have to inflate it.
			// to inflate it basically means to render, or show, the view.
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				if(item.isHeader)
				{
					v = inflater.inflate(R.layout.menu_header, null);

					/*DisplayMetrics metrics = getResources().getDisplayMetrics();
					int width = metrics.widthPixels;
					int height = metrics.heightPixels;

					LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height/4);
					v.setLayoutParams(llparams);*/

				}
				else
				{
					v = inflater.inflate(R.layout.item_row, null);
				}
			}

			if(item.isHeader)
			{
				TextView userName =(TextView)v.findViewById(R.id.name);
                userName.setText(item.getName());
			}
			else
			{
				TextView textView =(TextView)v.findViewById(R.id.rowText);
				textView.setText(item.getName());
				ImageView imageView = (ImageView)v.findViewById(R.id.rowIcon);
				imageView.setBackgroundResource(item.resourceId);
			}

			return v;
		}
	}

	/**
	 * <h1>Rate us popup</h1>
	 */
	private void rateUsPopup() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View dialogView = inflater.inflate(R.layout.rate_us_popup, null);
		LinearLayout meh = (LinearLayout) dialogView.findViewById(R.id.ll_meh);
		LinearLayout lovedIt = (LinearLayout) dialogView.findViewById(R.id.ll_loved_it);
		builder.setView(dialogView);
		final AlertDialog alertDialog = builder.create();

		meh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				openFeedBackPopup();
			}
		});

		lovedIt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				openRateAppPopUp();
			}
		});

		alertDialog.show();
	}


	private void openRateAppPopUp() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View dialogView = inflater.inflate(R.layout.rate_app_pop_up, null);
		TextView nope = (TextView) dialogView.findViewById(R.id.tv_nope);
		TextView sure = (TextView) dialogView.findViewById(R.id.tv_sure);
		builder.setView(dialogView);
		final AlertDialog alertDialog = builder.create();

		nope.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});

		sure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				final String appPackage = getPackageName();

				try{
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+appPackage)));
				}
				catch (android.content.ActivityNotFoundException e)
				{
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+appPackage)));
				}
			}
		});

		alertDialog.show();

	}


	private void openFeedBackPopup() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View dialogView = inflater.inflate(R.layout.feedback_popup, null);
		TextView nope = (TextView) dialogView.findViewById(R.id.tv_nope);
		TextView sure = (TextView) dialogView.findViewById(R.id.tv_sure);
		builder.setView(dialogView);
		final AlertDialog alertDialog = builder.create();

		nope.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});

		sure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				openMailBox();
			}
		});

		alertDialog.show();
	}


	private void openMailBox() {
		//
		Intent emailIntent =  new Intent (Intent.ACTION_SEND);
		emailIntent.setType("text/plain");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@spotsoon.com"});
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Customer Feedback");
		startActivity(emailIntent);
	}

	@Override
	public void onBackPressed()
	{
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;

		if (home) {
			Toast.makeText(MainActivityNew.this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
		}
		else {
			setTitle(getResources().getString(R.string.app_name));
			//getSupportActionBar().setCustomView(R.string.app_name);
			Fragment fragment = new HomeFragmentNew();
			android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.container, fragment);
			fragmentTransaction.commit();
			home = true;
		}

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000);
	}
}