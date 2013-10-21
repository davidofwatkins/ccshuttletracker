package dwat.ccshuttletracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;


/**
 * Controls the Google Map that displays BusOverlays
 * 
 * @author David Watkins
 * @version 2.1.1
 * @since 9/20/13
 *
 */
public class Map extends ActionBarActivity {

	private final int REFRESH_INTERVAL_IN_SEC = 5;
	public static final int ZOOMLEVEL_MEDIUM_BUSSES = 17;
	public static final int ZOOMLEVEL_MEDIUM_STOPS = 18;

	private EnhancedMapView mapView;
	private BusManager bm;
	private OverlayManager overlayManager;
	public static ArrayList<Bus> busses;
	private Timer timer;
	private Drawable defaultDrawable;
	private static Context context;
	public final static SimpleDateFormat SDF = new SimpleDateFormat("MMM d, yyyy 'at' h:mm aaa");
	public static final Handler handler = new Handler();
	private boolean fullyResuming;
	private BroadcastReceiver receiver;
	private ProgressDialog loading = null;
	private static int lastViewedScheduleId = -1;
	private ErrorReporter errorReporter;
	private int activeBalloonBusId = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		context = this;
		
		setTitle("Shuttle Tracker");
		//actionBar.setHomeButtonEnabled(false);  //Should work with ICS actionBar (API 14+)
		
		errorReporter = new ErrorReporter(context);
		mapView = (EnhancedMapView) findViewById(R.id.mapview);

		// Set the zoom level and center of map to either the default or the last view (used for orientation change)
		if (savedInstanceState == null || savedInstanceState.getBoolean("updated") == false) {
			mapView.getController().setCenter(new GeoPoint((int) (44.473948151148015 * 1000000), (int) (-73.20428609848022 * 1000000)));
			mapView.getController().setZoom(15);
		}
		else {
			mapView.getController().setCenter(new GeoPoint(savedInstanceState.getInt("lat"), savedInstanceState.getInt("lon")));
			mapView.getController().setZoom(savedInstanceState.getInt("zoom"));
		}
		
		bm = new BusManager(errorReporter);
		
		//Start the ScreenOffListener (to detect when to not showErrorMessage())
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		receiver = new ScreenOffListener(handler, new Runnable() {
			public void run() {
				fullyResuming = false;
			}
		});
		registerReceiver(receiver, filter);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		//If not resuming from screen off, schedule list, or screen orientation
		if (fullyResuming) { errorReporter.reset(); }
		fullyResuming = true;
		startTimer();
	}
	
	/**
	 * Updates the map by refreshing the bus and bus stop overlays from fresh or
	 * cached bus locations.
	 * 
	 * @param reconnect Specify if new or cached bus location data should be used
	 * @param showRefreshMessage Specify if a "Refreshing..." message should be displayed
	 */
	public void updateMap(boolean reconnect, boolean showRefreshMessage) {
		
		if (showRefreshMessage) {
			//The progress dialog must be displayed from the main thread
			handler.post(new Runnable() {
				public void run() {
					errorReporter.reset();
					loading = ProgressDialog.show(context, "", "Loading. Please wait...", true);
					loading.setCanceledOnTouchOutside(true);
				}
			});
		}
		
		setDefaultDrawable();
		
		//(Re)initialize the busses array
		busses = bm.updateBusses(reconnect); // Overwrite the busses array with a fresh set of busses
		
		//Clear all balloons
		if (mapView.getOverlays().size() > 0 && mapView.getOverlays().get(0) instanceof OverlayManager) {
			((OverlayManager) mapView.getOverlays().get(0)).hideAllBalloons(mapView.getOverlays());
		}
		else if (mapView.getOverlays().size() > 0)  {
			Log.e("CCShuttleTracker", "ERROR: cannot retrieve OverlayManager from MapView! (See Map.java)");
		}
		
		//Remove all overlays if there are any (recreate the overlay manager)
		if (mapView.getOverlays().size() > 0) {
			mapView.getOverlays().clear();
		}
		overlayManager = new OverlayManager(this, defaultDrawable, mapView);
		
		//Create Bus Overlays from busses
		ArrayList<BusOverlay> busOverlays = createBusOverlays();
		for (BusOverlay overlay : busOverlays) {
			overlayManager.addOverlay(overlay);
		}
		
		//Create Bus Stop Overlays
		ArrayList<HashMap<String, Object>> stopOverlays = createStopOverlays();
		for (HashMap<String, Object> overlayDetails : stopOverlays) {
			overlayManager.addOverlay((OverlayItem)overlayDetails.get("overlay"), (Drawable)overlayDetails.get("icon"));
		}
		
		//Submit the overlay manager to the map view
		mapView.getOverlays().add(overlayManager);
		
		//Hide loading dialog if present
		handler.post(new Runnable() {
			public void run() {
				if (loading != null) { loading.dismiss(); }
			}
		});
		
		mapView.postInvalidate();
	}
	
	/**
	 * Specifies which version of the bus icon to use based on 
	 * how close the map is zoomed in.
	 */
	private void setDefaultDrawable() {
		
		if (mapView.getZoomLevel() > ZOOMLEVEL_MEDIUM_BUSSES) {
			defaultDrawable = this.getResources().getDrawable(R.drawable.bus_large);
		}
		else if (mapView.getZoomLevel() < ZOOMLEVEL_MEDIUM_BUSSES) {
			defaultDrawable = this.getResources().getDrawable(R.drawable.bus_small);
		}
		else {
			defaultDrawable = this.getResources().getDrawable(R.drawable.bus_med);
		}
	}
	
	//@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewscheduleinfo_button:
            	
            	fullyResuming = false;
            	Intent i = new Intent(this, Schedules.class);
            	if (lastViewedScheduleId != -1) { i.putExtra("scheduleId", lastViewedScheduleId); }
            	startActivity(i);
                break;

            case R.id.mapmode_button:
            	
            	if (mapView.isSatellite() == false) {
            		mapView.setSatellite(true);
            	}
            	else { mapView.setSatellite(false); }
                break;
              
            case R.id.refresh_button:
            	
            	new Thread(new Runnable() {
					public void run() {
						
						try {
							updateMap(true, true);
						}
						catch (Exception e) {
							Log.e("CCShuttleTracker", "Unable to manually refresh map (via refresh button)");
							Toast.makeText(context, "Error refreshing map", Toast.LENGTH_SHORT).show();
						}
					}
            	}).start();
            	break;
            	
            case R.id.center_button:
            	
            	mapView.getController().animateTo(new GeoPoint((int) (44.473948151148015 * 1000000), (int) (-73.20428609848022 * 1000000)));
            	mapView.getController().setZoom(15);
            	break;
            	
			case R.id.about_button:
            	
            	fullyResuming = false;
    			i = new Intent(this, HTMLDisplayer.class);
    			i.putExtra("title", "About Shuttle Tracker");
    			i.putExtra("url", "file:///android_asset/about-ccstracker.html");
    			startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean("updated", true);
		savedInstanceState.putInt("zoom", mapView.getZoomLevel());
		savedInstanceState.putInt("lat", mapView.getMapCenter().getLatitudeE6());
		savedInstanceState.putInt("lon", mapView.getMapCenter().getLongitudeE6());
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onPause() {
		super.onPause();
		timer.cancel();
		try { unregisterReceiver(receiver); }
		catch(Exception e) { Log.w("CCShuttleTracker", "Cannot unregister unregistered receiver"); }
	}

	/**
	 * Starts the timer for updating the map regularly
	 * (used in onCreate and onResume)
	 * 
	 * @see onCreate()
	 * @see onResume()
	 */
	private void startTimer() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				try {
					updateMap(true, false);
					Log.i("CCShuttleTracker", "Successfully refreshing overlays");
				}
				catch(Exception e) { Log.e("CCShuttleTracker", "Could not refresh overlay: " + e); }
			}
		}, 0, REFRESH_INTERVAL_IN_SEC * 1000);
	}
	
	/**
	 * Specify the last schedule ID to be viewed so that the user can
	 * be presented with the same schedule when returning to the schedules
	 * page.
	 * 
	 * @param id The schedule ID number
	 */
	protected static void setLastViewedScheduleId(int id) {
		lastViewedScheduleId = id;
	}
	
	/**
	 * Used to save the currently open balloon on the map (if one is open),
	 * so that it can be restored after updating the map.
	 * 
	 * @param id The ID of the bus with the open balloon. Use -1 if no bus has an open balloon
	 */
	protected void setActiveBalloonOverlayItemId(int id) {
		activeBalloonBusId = id;
	}
	
	/**
	 * Used to restore a balloon for a specific bus on the MapView after
	 * it has been updated.
	 * 
	 * @return the ID of the bus with the open balloon
	 */
	protected int getActiveBalloonOverlayItemId() {
		return activeBalloonBusId;
	}
	
	/**
	 * Used to get the handler for the current instance of Map
	 * @return the handler for the current instance of Map
	 */
	public Handler getUIHandler() {
		return handler;
	}
	
	private ArrayList<BusOverlay> createBusOverlays() {
		
		ArrayList<BusOverlay> overlays = new ArrayList<BusOverlay>();
		
		try {
			for (Bus bus : busses) {
				int busCounter = 0;
				if (bus.isActive()) {
					
					GeoPoint point = new GeoPoint((int) (bus.getLatitude() * 1000000), (int) (bus.getLongitude() * 1000000));
					
					String snippet;
					if (bus.getKnots() != 0) { snippet = "Moving " + bus.getDirection() + " at " + bus.getMPH() + " MPH"; }
					else { snippet = bus.getMPH() + " MPH"; }
					
					BusOverlay overlay = new BusOverlay(busCounter, bus, point, snippet);
					//overlayManager.addOverlay(overlay, activeBalloonBusId);
					overlays.add(overlay);
				}
				busCounter++;
			}
		}
		catch(NullPointerException e) {
			Log.e("CCShuttleTracker", "Unknown error showing busses.");
		}
		
		return overlays;
	}
	
	private ArrayList<HashMap<String, Object>> createStopOverlays() {
		// Create busStop overlays
		HashMap<Double, Double> busStops = new HashMap<Double, Double>(5);
		busStops.put(44.490467, -73.184191); // Spinner
		busStops.put(44.473680, -73.204125); // Campus
		//busStops.put(44.473458, -73.219746); // Perkins
		busStops.put(44.460419, -73.216463); // Gilbane
		busStops.put(44.4666455, -73.1869866); // Quarry
		
		ArrayList<HashMap<String, Object>> overlays = new ArrayList<HashMap<String, Object>>();
		
		for (HashMap.Entry<Double, Double> stop : busStops.entrySet()) {
			
			GeoPoint stopPoint = new GeoPoint((int) (stop.getKey() * 1000000), (int) (stop.getValue() * 1000000));
			OverlayItem overlay = null;

			Drawable stopIcon = null;
			
			//Spinner
			if (stop.getKey() == 44.490467 && stop.getValue() == -73.184191) {
				
				overlay = new OverlayItem(stopPoint, "Spinner Place", "");
				
				if (mapView.getZoomLevel() > ZOOMLEVEL_MEDIUM_STOPS) { stopIcon = this.getResources().getDrawable(R.drawable.spinner_stop_large); } //stopicon_big, > 16
				else if (mapView.getZoomLevel() < ZOOMLEVEL_MEDIUM_STOPS) { stopIcon = this.getResources().getDrawable(R.drawable.spinner_stop_small); } //stopicon_small, < 16
				else { stopIcon = this.getResources().getDrawable(R.drawable.spinner_stop_med); } //stopicon_med	
			}
			
			//Campus
			else if (stop.getKey() == 44.473680 && stop.getValue() == -73.204125) {
				
				overlay = new OverlayItem(stopPoint, "Campus", "");
				
				if (mapView.getZoomLevel() > ZOOMLEVEL_MEDIUM_STOPS) { stopIcon = this.getResources().getDrawable(R.drawable.campus_stop_large); } //stopicon_big, > 16
				else if (mapView.getZoomLevel() < ZOOMLEVEL_MEDIUM_STOPS) { stopIcon = this.getResources().getDrawable(R.drawable.campus_stop_small); } //stopicon_small, < 16
				else { stopIcon = this.getResources().getDrawable(R.drawable.campus_stop_med); } //stopicon_med
			}
			
			//Gilbane
			else if (stop.getKey() == 44.460419 && stop.getValue() == -73.216463) {
				
				overlay = new OverlayItem(stopPoint, "Gilbane Lot & Lakeside Campus", "");
				
				if (mapView.getZoomLevel() > ZOOMLEVEL_MEDIUM_STOPS) { stopIcon = this.getResources().getDrawable(R.drawable.gilbane_stop_large); } //stopicon_big, > 16
				else if (mapView.getZoomLevel() < ZOOMLEVEL_MEDIUM_STOPS) { stopIcon = this.getResources().getDrawable(R.drawable.gilbane_stop_small); } //stopicon_small, < 16
				else { stopIcon = this.getResources().getDrawable(R.drawable.gilbane_stop_med); } //stopicon_med
			}
			
			//Quarry
			else if (stop.getKey() == 44.4666455 && stop.getValue() == -73.1869866) {
				
				overlay = new OverlayItem(stopPoint, "Quarry Hill", "");
				
				if (mapView.getZoomLevel() > ZOOMLEVEL_MEDIUM_STOPS) { stopIcon = this.getResources().getDrawable(R.drawable.quarry_stop_large); } //stopicon_big, > 16
				else if (mapView.getZoomLevel() < ZOOMLEVEL_MEDIUM_STOPS) { stopIcon = this.getResources().getDrawable(R.drawable.quarry_stop_small); } //stopicon_small, < 16
				else { stopIcon = this.getResources().getDrawable(R.drawable.quarry_stop_med); } //stopicon_med
			}
			
			stopIcon.setBounds(0, 0, stopIcon.getIntrinsicWidth(), stopIcon.getIntrinsicHeight());
			
			HashMap<String, Object> overlayDetails = new HashMap<String, Object>();
			overlayDetails.put("overlay", overlay);
			overlayDetails.put("icon", stopIcon);
			overlays.add(overlayDetails);
		}
		
		return overlays;
	}
}