package dwat.ccshuttletracker;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Schedule displayer Activity that holds a ViewPager for all schedules
 * 
 * @author David Watkins
 * @version 2.1.1
 * @since 9/20/13
 */
public class Schedules extends ActionBarActivity {
    
	private ActionBar abar;
	
    // FOR SWIPABLE TABS: http://stackoverflow.com/questions/8045154/fragment-in-actionbar-tab-and-viewpager
    // AND: http://developer.android.com/resources/samples/Support13Demos/src/com/example/android/supportv13/app/ActionBarTabsPager.html
    
	/**
	 * Instantiates the ViewPager and TabsAdapter
	 */
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
        ViewPager viewPager = new ViewPager(this);
        viewPager.setId(R.id.scheduleViewPager);
        viewPager.setOffscreenPageLimit(2); //prevents offscreen pages 2 pages away or less from being destroyed
        viewPager.setBackgroundColor(Color.BLACK);
        setContentView(viewPager);
        setTitle("Schedules");
        
        abar = getSupportActionBar(); //would just be getActionBar() if not using android.support.v4 packages
        abar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        abar.setDisplayHomeAsUpEnabled(true);
        
        TabsAdapter tabsAdapter = new TabsAdapter(this, viewPager);
        
        tabsAdapter.addTab(abar.newTab().setText("Spinner"), HTMLDisplayerFragment.class, "file:///android_asset/ccstracker-spinner.html"); //Bundle can be null if the fragment(s) aren't receiving any info
        tabsAdapter.addTab(abar.newTab().setText("Quarry"), HTMLDisplayerFragment.class, "file:///android_asset/ccstracker-quarry.html");
        tabsAdapter.addTab(abar.newTab().setText("Lakeside"), HTMLDisplayerFragment.class, "file:///android_asset/ccstracker-lakeside-gilbane.html");
        
        //Set the current tab, if one has been chosen
        if (savedInstanceState != null) {
            abar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
        else if (getIntent().hasExtra("scheduleId")) {
        	abar.setSelectedNavigationItem(getIntent().getExtras().getInt("scheduleId"));
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getSupportActionBar().getSelectedNavigationIndex());
    }
    
    //Add ViewOnline button to ActionBar
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.schedulesmenu, menu);
        return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewschedulesonline_button:
            	showSchedulesOnline(this);
                break;
            case android.R.id.home:
            	finish(); //close the activity and return to previous (which is the map)
                return true;
        }
        return super.onOptionsItemSelected(item);
	}
    
    /**
     * When the activity closes ("finishes"), report the last viewed schedule ID to a
     * static version of Map. There's probably a better way to do this, but this works
     * for now.
     */
    @Override
    public void finish() {
    	super.finish();
    	Map.setLastViewedScheduleId(abar.getSelectedNavigationIndex());
    }
    
    /**
     * Launch the web browser to display the schedules online
     * @param context The Application context
     */
    public static void showSchedulesOnline(Context context) {
    	final String URL = "http://www.champlain.edu/current-students/campus-services/transportation-and-parking/transportation-services-and-shuttle/shuttle";
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(URL));
		try { context.startActivity(i); }
		catch (ActivityNotFoundException e) { Toast.makeText(context, "Browser not found.", Toast.LENGTH_SHORT).show(); }
    }
}
