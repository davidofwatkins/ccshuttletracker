package dwat.ccshuttletracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * A Fragment that displays a specific webpage inside a WebView
 * 
 * @author David Watkins
 * @version 2.1.1
 * @since 9/20/13
 */
public class HTMLDisplayerFragment extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		EnhancedWebView webView = createWebView(getPageURL());
		return webView;
	}
	
	/**
	 * Creates a WebView from a specified URL
	 * @param url The webpage to display inside the WebView
	 * @return a new webpage
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private EnhancedWebView createWebView(String url) {
		EnhancedWebView webView = new EnhancedWebView(getActivity());
		webView.setBackgroundColor(Color.BLACK); //black
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new JSInterface(getActivity().getApplicationContext()), "Android");
		webView.loadUrl(url);
		
		//Fix white space at right of screen
	    webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
	    
		return webView;
	}
	
	/**
	 * Extract the URL to be used for the WebView from the Intent
	 * @return the URL provided in the Intent
	 */
	private String getPageURL() {
		String URL;
		try { URL = getArguments().getString("url"); }
		catch(Exception e) {
			Log.e("CCShuttleTracker", "Could not get URL for the Fragment");
			URL = "javascript:alert('Could not load URL')";
		}
		return URL;
	}
	
	/**
	 * A custom WebView that listens for scroll events
	 */
	protected class EnhancedWebView extends WebView {

		public EnhancedWebView(Context context) {
			super(context);
		}
		
		@Override
		public void onScrollChanged(int l, int t, int oldl, int oldt) {
			super.onScrollChanged(l, t, oldl, oldt);
		}

	}
}
