package dwat.ccshuttletracker;

import com.crashlytics.android.Crashlytics;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Controls the Google Map that displays BusOverlays
 * 
 * @author David Watkins
 * @version 2.1.1
 * @since 9/20/13
 */
public class Map extends ActionBarActivity {

	private final int REFRESH_INTERVAL_IN_SEC = 5;
	public static final int ZOOMLEVEL_LARGE_BUSES = 16;
	public static final double ZOOMLEVEL_SMALL_BUSES = 14.8;
	public static final int ZOOMLEVEL_LARGE_STOPS = 16;
	public static final double ZOOMLEVEL_SMALL_STOPS = 15.36;

	private GoogleMap gmap;
	private BusManager bm;
	private Float previousZoomLevel;
	private ArrayList<Marker> allBusMarkers;
	public static ArrayList<Bus> busses;
	private Timer timer;
	private static Context context;
	public final static SimpleDateFormat SDF = new SimpleDateFormat("MMM d, yyyy 'at' h:mm aaa", Locale.US);
	public static final Handler handler = new Handler();
	private boolean fullyResuming;
	private BroadcastReceiver receiver;
	private ProgressDialog loading = null;
	private static int lastViewedScheduleId = -1;
	private ErrorReporter errorReporter;
	private int activeBalloonBusId = -1;
	
	private final LatLng MAPCENTER = new LatLng(44.473948, -73.204125);
	private final LatLng SPINNER = new LatLng(44.490467, -73.184191);
	private LatLng CAMPUS = new LatLng(44.473680, -73.204125);
	private LatLng GILBANE = new LatLng(44.460419, -73.216463);
	private LatLng QUARRY = new LatLng(44.4666455, -73.1869866);
	private final int DEFAULT_ZOOM = 13;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Boolean debugging = getResources().getBoolean(R.bool.debug);
		if (!debugging) { Crashlytics.start(this); }
		setContentView(R.layout.map);
		context = this;
		
		if (debugging) {
			Log.w("CCShuttleTracker", "Debugging. Crashlytics will not report.");
			Toast.makeText(context, "Debugging", Toast.LENGTH_SHORT).show();
		}
		
		errorReporter = new ErrorReporter(context);
		gmap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		gmap.setPadding(0, 100, 0, 0);

		// Set the zoom level and center of map
		if (savedInstanceState == null || savedInstanceState.getBoolean("updated") == false) {
			gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(MAPCENTER, DEFAULT_ZOOM));
		}
		else {
			LatLng savedCoords = new LatLng(savedInstanceState.getDouble("lat"), savedInstanceState.getDouble("lon"));
			gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(savedCoords, savedInstanceState.getInt("zoom")));
		}
		if (previousZoomLevel == null) { previousZoomLevel = gmap.getCameraPosition().zoom; }
		
		bm = new BusManager(errorReporter);
		allBusMarkers = new ArrayList<Marker>();
		
		//Update markers whenever the map has been zoomed
		gmap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {
				if (previousZoomLevel != null && previousZoomLevel != position.zoom) {
					updateMap(false, false);
				}
			}
		});
		
		//Set up marker pop-up balloons
		gmap.setInfoWindowAdapter(new InfoWindowAdapter() {
			@Override
			public View getInfoWindow(final Marker marker) {
				View popup = getLayoutInflater().inflate(R.layout.balloon, null);
				TextView title = (TextView) popup.findViewById(R.id.balloon_title);
				title.setText(marker.getTitle());
				TextView snippet = (TextView) popup.findViewById(R.id.balloon_snippet);
				snippet.setText(marker.getSnippet());
				
				//Shave off the bottom padding from the bubble if there is no snippet
				if (marker.getSnippet() == "" || marker.getSnippet() == null) {
					LinearLayout bubble = (LinearLayout) popup.findViewById(R.id.balloon_main_layout);
					bubble.setPadding(bubble.getPaddingLeft(), bubble.getPaddingTop(), bubble.getPaddingRight(), 0);
				}
								
				/* Due to restrictions with the Maps API, elements inside balloons can't
				be interacted with without some trickery. Would simply extend MarkerOptions,
				but it's a final class. Probably not worth it for now. */
				
	            return popup;
			}

			@Override
			public View getInfoContents(Marker marker) {
				return null;
			}
		});
		
		//Show more info when the balloon is tapped
		gmap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				if (marker.getSnippet() != "" && marker.getSnippet() != null) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(context);
					dialog.setTitle(marker.getTitle() + ": Details");
					
					for (Bus bus : busses) {
						if (bus.getName().equals(marker.getTitle())) {
							dialog.setMessage("Bus ID: " + bus.getId() +
									"\nLatitude: " + bus.getLatitude() +
									"\nLongitude: " + bus.getLongitude() +
									"\nSpeed: " + bus.getMPH() + " MPH" +
									"\nDirection: " + bus.getDirection() +
									"\nLast Updated: " + Map.SDF.format(bus.getLastUpdated().getTime()));
							dialog.setCancelable(true);
							dialog.setPositiveButton("Ok", null);
							dialog.show();
						}
					}
				}
			}
		});
		
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
		
		//(Re)initialize the busses array
		busses = bm.updateBusses(reconnect); // Overwrite the busses array with a fresh set of busses
		
		handler.post(new Runnable() {
			@Override
			public void run() {
				
				// If there are an equal number of buses to bus markers, just update the appropriate markers
				if (busses.size() == allBusMarkers.size() && previousZoomLevel == gmap.getCameraPosition().zoom) {
					
					float zoomLevel = gmap.getCameraPosition().zoom;
					
					for (Bus bus : busses) {
						for (Marker marker : allBusMarkers) {
							if (bus.getName().equals(marker.getTitle())) {
								marker.setSnippet(bus.generateSnippet());
								marker.setVisible(bus.isActive());
								animateMarker(marker, new LatLng(bus.getLatitude(), bus.getLongitude()));
								break;
							}
						}
					}
					
					previousZoomLevel = zoomLevel;
				}
				//If there are an unequal amount of buses to bus markers, clear the map and redraw everything
				else {
					gmap.clear();
					allBusMarkers.clear();
					
					for (MarkerOptions marker : getStopMarkers()) {
						gmap.addMarker(marker);
					}
					for (MarkerOptions marker : getBusMarkers(busses)) {
						Marker newMarker = gmap.addMarker(marker);
						allBusMarkers.add(newMarker);
					}
				}
				if (loading != null) { loading.dismiss(); }
			}
		});
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

            	navigateToSchedules();
                break;

            case R.id.mapmode_button:
            	
            	if (gmap.getMapType() != GoogleMap.MAP_TYPE_HYBRID) {
            		gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            	}
            	else { gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL); }
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
            	
            	gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(MAPCENTER, DEFAULT_ZOOM));
            	break;
            	
			case R.id.about_button:
            	
				navigateToAbout();
                break;
        }
        return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean("updated", true);
		savedInstanceState.putInt("zoom", (int) gmap.getCameraPosition().zoom);
		savedInstanceState.putDouble("lat", gmap.getCameraPosition().target.latitude);
		savedInstanceState.putDouble("lon", gmap.getCameraPosition().target.longitude);
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
				catch(Exception e) {
					Log.e("CCShuttleTracker", "Could not refresh overlay: " + e);
					e.printStackTrace();
				}
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
	
	/**
	 * Generates a set of MarkerOptions for a set of given Buses to be used
	 * on the map.
	 * 
	 * @param buses An ArrayList of Bus's from which to create the markers
	 * @return an ArrayList of MarkerOptions containing a summary of information for the appropriate bus
	 */
	private ArrayList<MarkerOptions> getBusMarkers(ArrayList<Bus> buses) {
		
		ArrayList<MarkerOptions> markers = new ArrayList<MarkerOptions>();
		float zoomLevel = gmap.getCameraPosition().zoom;
		
		for (Bus bus : buses) {
			markers.add(new MarkerOptions()
				.position(new LatLng(bus.getLatitude(), bus.getLongitude()))
		        .title(bus.getName())
		        .snippet(bus.generateSnippet())
		        .icon(getBusIcon(zoomLevel))
		        .visible(bus.isActive())
			);
		}
		
		previousZoomLevel = zoomLevel;
		return markers;
	}
	
	/**
	 * Calculates the appropriate icon for each bus stop and generates a list
	 * of markers.
	 * 
	 * @return an ArrayList of MarkerOptions for each bus stop
	 */
	private ArrayList<MarkerOptions> getStopMarkers() {
		
		ArrayList<MarkerOptions> markers = new ArrayList<MarkerOptions>();
		float zoomLevel = gmap.getCameraPosition().zoom;
		
		markers.add(new MarkerOptions()
			.position(CAMPUS)
	        .title("Champlain College")
	        .icon(getStopIcon("campus", zoomLevel))
		);
		markers.add(new MarkerOptions()
			.position(SPINNER)
	        .title("Spinner Place")
	        .icon(getStopIcon("spinner", zoomLevel))
		);
		markers.add(new MarkerOptions()
			.position(GILBANE)
	        .title("Gilbane Lot & Lakeside Campus")
	        .icon(getStopIcon("gilbane", zoomLevel))
		);
		markers.add(new MarkerOptions()
			.position(QUARRY)
	        .title("Quarry Hill")
	        .icon(getStopIcon("quarry", zoomLevel))
		);
		
		return markers;
	}
	
	/**
	 * Calculates a bus icon based on the map's current zoom level.
	 * 
	 * @param zoomLevel The map's zoom level from which to calculate the appropriate icon size
	 * @return a bus icon
	 */
	private BitmapDescriptor getBusIcon(float zoomLevel) {
		if (zoomLevel > ZOOMLEVEL_LARGE_BUSES) return BitmapDescriptorFactory.fromResource(R.drawable.bus_large);
		else if (zoomLevel < ZOOMLEVEL_SMALL_BUSES) return BitmapDescriptorFactory.fromResource(R.drawable.bus_small);
		else return BitmapDescriptorFactory.fromResource(R.drawable.bus_med);
	}
	
	/**
	 * Calculates a bus stop icon based on the map's current zoom level
	 * 
	 * @param stopname The unique stop identifier (name) of the requested stop
	 * @param zoomLevel The map's zoom level from which to calculate the appropriate icon size
	 * @return a bus stop icon
	 * @throws NullPointerException
	 */
	private BitmapDescriptor getStopIcon(String stopname, float zoomLevel) throws NullPointerException {
		
		if (stopname.equals("campus")) {
			if (zoomLevel > ZOOMLEVEL_LARGE_STOPS) return BitmapDescriptorFactory.fromResource(R.drawable.campus_stop_large);
			else if (zoomLevel < ZOOMLEVEL_SMALL_STOPS) return BitmapDescriptorFactory.fromResource(R.drawable.campus_stop_small);
			else return BitmapDescriptorFactory.fromResource(R.drawable.campus_stop_med);
		}
		else if (stopname.equals("spinner")) {
			if (zoomLevel > ZOOMLEVEL_LARGE_STOPS) return BitmapDescriptorFactory.fromResource(R.drawable.spinner_stop_large);
			else if (zoomLevel < ZOOMLEVEL_SMALL_STOPS) return BitmapDescriptorFactory.fromResource(R.drawable.spinner_stop_small);
			else return BitmapDescriptorFactory.fromResource(R.drawable.spinner_stop_med);
		}
		else if (stopname.equals("gilbane")) {
			if (zoomLevel > ZOOMLEVEL_LARGE_STOPS) return BitmapDescriptorFactory.fromResource(R.drawable.gilbane_stop_large);
			else if (zoomLevel < ZOOMLEVEL_SMALL_STOPS) return BitmapDescriptorFactory.fromResource(R.drawable.gilbane_stop_small);
			else return BitmapDescriptorFactory.fromResource(R.drawable.gilbane_stop_med);
		}
		else if (stopname.equals("quarry")) {
			if (zoomLevel > ZOOMLEVEL_LARGE_STOPS) return BitmapDescriptorFactory.fromResource(R.drawable.quarry_stop_large);
			else if (zoomLevel < ZOOMLEVEL_SMALL_STOPS) return BitmapDescriptorFactory.fromResource(R.drawable.quarry_stop_small);
			else return BitmapDescriptorFactory.fromResource(R.drawable.quarry_stop_med);
		}
		else {
			throw new NullPointerException("Cannot find stop " + stopname);
		}
	}
	
	/**
	 * Opens the schedules page
	 */
	private void navigateToSchedules() {
		fullyResuming = false;
    	Intent i = new Intent(this, Schedules.class);
    	if (lastViewedScheduleId != -1) { i.putExtra("scheduleId", lastViewedScheduleId); }
    	startActivity(i);
	}
	
	/**
	 * Opens the about page
	 */
	private void navigateToAbout() {
		fullyResuming = false;
		Intent i = new Intent(this, HTMLDisplayer.class);
		i.putExtra("title", "About Shuttle Tracker");
		i.putExtra("url", "file:///android_asset/about-ccstracker.html");
		startActivity(i);
	}
	
	/**
     * Moves a marker to another point on the map (stolen from: http://stackoverflow.com/a/15941069/477632)
     *
     * @param marker the marker to move
     * @param toPosition the new position for the marker to move to
     */
    private void animateMarker(final Marker marker, final LatLng toPosition) {
    	final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = gmap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }
}