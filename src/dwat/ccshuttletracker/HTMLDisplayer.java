package dwat.ccshuttletracker;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;

/**
 * An Activity that displays a specified webpage in a WebView
 * 
 * @author David Watkins
 * @version 2.1.1
 * @since 9/20/13
 */
public class HTMLDisplayer extends ActionBarActivity {
	
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.basichtmlviewer);
		
		WebView webView = createWebView();
		
		final ActionBar abar = getSupportActionBar(); //would just be getActionBar() if not using android.support.v4 packages
		abar.setDisplayHomeAsUpEnabled(true);
		
		//Implement the given URL and title
		HashMap<String, String> details = getPageDetails();
		setTitle(details.get("title"));
		webView.loadUrl(details.get("url"));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	finish(); //close the activity and return to previous (which is the map)
                return true;
        }
        return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Instantiates the WebView
	 * 
	 * @return a freshly created WebView
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private WebView createWebView() {
		WebView webView = (WebView) findViewById(R.id.basicwebview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		return webView;
	}
	
	/**
	 * @return the title and URL of the webpage sent in the Intent
	 */
	private HashMap<String, String> getPageDetails() {
		String title;
		String URL;
		try {
			title = getIntent().getExtras().getString("title");
			URL = getIntent().getExtras().getString("url");
		}
		catch(Exception e) {
			Log.e("CCShuttleTracker", "Error retrieving URL and/or title from intent.");
			URL = "javascript:alert('Error receiving URL and/or title from intent.')";
			title = "Error";
		}
		
		HashMap<String, String> details = new HashMap<String, String>();
		details.put("title", title);
		details.put("url", URL);
		return details;
		
	}
}
