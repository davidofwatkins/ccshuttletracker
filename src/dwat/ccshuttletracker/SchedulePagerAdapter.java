package dwat.ccshuttletracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.webkit.WebView;

/**
 * A modified PagerAdapter for use with the shuttle schedules Activity
 * 
 * @author David Watkins
 * @version 2.1.1
 * @since 9/20/13
 */
public class SchedulePagerAdapter extends PagerAdapter {
	
	/* Guides and examples for implementing tabbed PagerAdapters:
	 * 
	 * For tabs! http://thepseudocoder.wordpress.com/2011/10/13/android-tabs-viewpager-swipe-able-tabs-ftw/
	 * Scrolling tabs: http://java.dzone.com/articles/scrolling-tabs-android
	 * Code adapted from http://android-developers.blogspot.com/2011/08/horizontal-view-swiping-with-viewpager.html
	 * Navigation Tabs: http://developer.android.com/guide/topics/ui/actionbar.html#Tabs
	 */
	
	private Context activityContext;
	private int totalViews;
	
	public SchedulePagerAdapter(Context c, int totalViews) {
		activityContext = c;
		this.totalViews = totalViews;
	}
	
	/**
     * Returns the total number of Views in the PagerAdapter
     */
    @Override
    public int getCount() {
        return totalViews;
    }
    
    /**
     * Creates and adds a new View into the ScheduleViewPager
     * @param collection the View to be added to the ViewPager
     * @param position The position in the pager to place the View (and
     * therefore which View to add)
     * @return the View added into the ViewPager
     */
	@Override
    public View instantiateItem(View collection, int position) {
    	
    	String url = null;
    	
    	switch (position) {
    		case 0:
    			url = "file:///android_asset/ccstracker-spinner.html";
    			break;
    		case 1:
    			url = "file:///android_asset/ccstracker-quarry.html";
    			break;
    		case 2:
    			url = "file:///android_asset/ccstracker-lakeside-gilbane.html";
    			break;
    	}
        
    	WebView webView = createWebView(url);
    	
    	//Add this view to the ViewPager (entire collection of pages):
        ((ViewPager) collection).addView(webView, 0);
        
        return webView;
    }
    
	/**
	 * Removes a page from the ViewPager once the user has swiped to another
	 * @param collection The container ViewPager
	 * @param position The position number to be removed
	 * @param view The view to be removed
	 */
    @Override
    //Removes a page from the ViewPager collection once the user has swiped it away
    public void destroyItem(View collection, int position, Object view) {
        ((ViewPager) collection).removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }
    
    /**
     * Initializes a WebView containing the specified schedule
     * 
     * @param url The internal URL of the schedule to be displayed
     * @return a WebView containing a shuttle schedule webpage
     */
    @SuppressLint("SetJavaScriptEnabled")
    private WebView createWebView(String url) {
    	WebView webView = new WebView(activityContext);
    	webView.getSettings().setJavaScriptEnabled(true);
    	webView.loadUrl(url);
    	
    	//Add javascript interface for webpages to use for detecting screen orientation
    	webView.addJavascriptInterface(new JSInterface(activityContext), "Android");
    	
    	//Fix white space at right of screen
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        
        return webView;
    }

    @Override
    public Parcelable saveState() { return null; }
    public void finishUpdate(View container) { } //Fires once the user has finished swiping (or the view has been swiped off?)
	public void restoreState(Parcelable arg0, ClassLoader arg1) { }
	public void startUpdate(View arg0) { }

}
