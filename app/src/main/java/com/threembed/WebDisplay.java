package com.threembed;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.spotsoon.customer.R;
import com.utility.Utility;

import io.fabric.sdk.android.Fabric;

public class WebDisplay extends Activity {
	private ProgressBar progress;
	private String title,url;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Fabric.with(WebDisplay.this, new Crashlytics());
		Utility.statusbar(WebDisplay.this);
		setContentView(R.layout.weblink);

		Intent intent=getIntent();
		if(intent!=null)
		{
			
			url=getIntent().getStringExtra("Link");
			title=getIntent().getStringExtra("Title");
			
			Utility.printLog("title value is :: " + title);
			
			Utility.printLog("title link is :: "+url);
			
		}
		
		Utility.printLog("title value is  out side:: "+title);
		//actionBar.setTitle(title);
		
		int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		
		if (actionBarTitleId > 0) 
		{
		    TextView title1 = (TextView) findViewById(actionBarTitleId);
		    
		    if (title1 != null)
		    {
		        title1.setTextColor(Color.rgb(0, 85, 150));
		        
		    }
		}

		WebView	webView = (WebView) findViewById(R.id.webView);
		webView.setWebViewClient(new MyWebViewClient());
		webView.setSaveFromParentEnabled(true);
	
		//webView.set
		
		webView.getSettings().setJavaScriptEnabled(true);
		
		
		
		//webView.getSettings().setUseWideViewPort(true);

		progress = (ProgressBar) findViewById(R.id.progressBar);
		progress.setVisibility(View.GONE);
		
				//String url = urlEditText.getText().toString();
				if (validateUrl(url)) 
				{
					webView.getSettings().setJavaScriptEnabled(true);
					//webView.loadUrl(url);
					webView.loadUrl(url);
				}

	}
	
	private boolean validateUrl(String url)
	{
		return true;
	}


	private class MyWebViewClient extends WebViewClient 
	{	
		 @Override
		    public boolean shouldOverrideUrlLoading(WebView view, String url)
		 {
		        view.loadUrl(url);
		        return true;
		    }

		 @Override
		public void onPageFinished(WebView view, String url)
		 {
			 progress.setVisibility(View.GONE);
			 WebDisplay.this.progress.setProgress(100);
			super.onPageFinished(view, url);
		}

		 @Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		 {
			 progress.setVisibility(View.VISIBLE);
			WebDisplay.this.progress.setProgress(0);
			super.onPageStarted(view, url, favicon);
		}
	}

	public void setValue(int progress)
	{
		this.progress.setProgress(progress);		
	}
	
	 @Override
		public boolean onOptionsItemSelected(MenuItem item)
		{
			switch (item.getItemId()) 
			{
			case android.R.id.home:
			{
				Utility.printLog("it is cmg inside event details");
				finish();
			//	overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
				// NavUtils.navigateUpFromSameTask(this);
				
			}
			
		    	return true;
			}
			return super.onOptionsItemSelected(item);
			
		}
}