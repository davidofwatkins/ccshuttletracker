package dwat.ccshuttletracker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * A FragmentPagerAdapter that also implements ActionBar.TabListener and
 * ViewPager.OnPageChangeListener to enable swipable tabs in the PagerAdapter
 * 
 * @author David Watkins
 * @version 2.1.1
 * @since 9/20/13
 */
public class TabsAdapter extends FragmentPagerAdapter implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
	
	private Context mContext;
	private ActionBar mActionBar;
	private ViewPager mViewPager;
	private ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
	
	/**
	 * A subclass that holds the class for a tab, plus additional information
	 * about said class
	 */
	private class TabInfo {
		private final Class<?> clss;
		private final Bundle args;
		
		TabInfo(Class<?> newClass, Bundle newBundle) {
			clss = newClass;
			args = newBundle;
		}
	}
	
	public TabsAdapter(Activity a, ViewPager p) {
		super(((FragmentActivity)a).getSupportFragmentManager());
		mContext = a;
		mActionBar = ((SherlockFragmentActivity)a).getSupportActionBar(); //Will not need to cast as FragmentActivity when not relying on support packages
		mViewPager = p;
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
	}
	
	//Function called from the Activity
	public void addTab(Tab tab, Class<?> clss, String url) {
		Bundle args = new Bundle();
		args.putString("url", url);
		TabInfo info = new TabInfo(clss, args);
		tab.setTag(info);
		tab.setTabListener(this); //This class implements ActionBar.TabListener
		mTabs.add(info);
		mActionBar.addTab(tab);
		notifyDataSetChanged();
	}
	
	/* PagerAdapter/FragmentPagerAdapter implementation: */
	
	@Override
	public int getCount() { return mTabs.size(); }
	
	@Override
	public Fragment getItem(int position) {
		TabInfo info = mTabs.get(position);
		return Fragment.instantiate(mContext, info.clss.getName(), info.args);
	}
	
	/* OnPageChangeListener implementation: */
	
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
	
	/**
	 * When the user swipes to a page, update the ActionBar/Tabs to show the correct tab
	 */
	@Override
	public void onPageSelected(int position) {
		mActionBar.setSelectedNavigationItem(position);
	}
	
	@Override public void onPageScrollStateChanged(int state) { }
	
	/* TabListener implementation: */
	
	/**
	 * When a tab is selected, update the current position of the ViewPager to
	 * reflect this.
	 */
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		
		Object tag = tab.getTag();
		
		for (int i = 0; i < mTabs.size(); i++) {
			if (mTabs.get(i) == tag) {
				mViewPager.setCurrentItem(i);
			}
		}
		
	}

	@Override public void onTabReselected(Tab tab, FragmentTransaction ft) { }
	@Override public void onTabUnselected(Tab tab, FragmentTransaction ft) { }
}
