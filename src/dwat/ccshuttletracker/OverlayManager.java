package dwat.ccshuttletracker;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

/**
 * An Itemized Overlay that supports balloons with space for additional
 * overlay details.
 * 
 * @author David Watkins
 * @version 2.1.1
 * @since 9/20/13
 */
public class OverlayManager extends BalloonItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
	private Context context;
	private MapView mapView;
	public boolean finishedThreadUpdate = false;
	
	public OverlayManager(Context newContext, Drawable defaultMarker, MapView mv) {
		  super(boundCenterBottom(defaultMarker), mv);
		  mapView = mv;
		  context = newContext;
	}
	
	/**
	 * @return used (by populate()) to fill an overlay with the appropriate details
	 * @see ItemizedOverlay.populate()
	 */
	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}
	
	/**
	 * @return the size of the ArrayList of OverlayItems
	 */
	@Override
	public int size() {
		return overlays.size();
	}
	
	/**
	 * Adds an overlay to the OverlayManager (typically used for bus overlays)
	 * 
	 * @param overlay The OverlayItem to be added
	 */
	public void addOverlay(BusOverlay overlay) {
		overlays.add(overlay);
	    restoreOpenBalloons(overlays.size() - 1);
	    populate(); //Calls createItem()
	}
	
	/**
	 * Adds an overlay to the OverlayManager with a specified icon
	 * (typically used for bus stop overlays)
	 * 
	 * @param overlay The OverlayItem to be added
	 * @param icon The icon to be applied to this OverlayItem
	 */
	public void addOverlay(OverlayItem overlay, Drawable icon) {
		overlay.setMarker(icon);
		boundCenterBottom(icon);
		overlays.add(overlay);
		restoreOpenBalloons(overlays.size() - 1);		
		populate(); //Calls createItem()
	}
	
	//Close balloons for other overlays when opening a new one
	/**
	 * Closes all other balloons when one is opened
	 */
	@Override
	protected void onBalloonOpen(int index) {
		
		hideOtherBalloons(mapView.getOverlays());
		
		int id;
		
		//Record the bus id for the current bus
		if (overlays.get(index) instanceof BusOverlay) {
			id = ((BusOverlay) overlays.get(index)).getBus().getId();
			((Map) context).setActiveBalloonOverlayItemId(id);
			
			//Set the distance between the BUS overlay icon and the bubble
			if (mapView.getZoomLevel() > Map.ZOOMLEVEL_MEDIUM_BUSSES) { setBalloonBottomOffset(dipToPx(40)); } //large
			else if (mapView.getZoomLevel() < Map.ZOOMLEVEL_MEDIUM_BUSSES) { setBalloonBottomOffset(dipToPx(22)); } //small
			else { setBalloonBottomOffset(dipToPx(30)); } //medium
		}
		//Otherwise, it's a bus stop:
		else {
			id = overlays.get(index).getPoint().getLatitudeE6(); //for stops, the id is simply its LatitudeE6
			((Map) context).setActiveBalloonOverlayItemId(id);
			
			//Set the distance between the STOP overlay icon and the bubble
			if (mapView.getZoomLevel() > Map.ZOOMLEVEL_MEDIUM_STOPS) { setBalloonBottomOffset(dipToPx(32)); } //large
			else if (mapView.getZoomLevel() < Map.ZOOMLEVEL_MEDIUM_STOPS) { setBalloonBottomOffset(dipToPx(15)); } //small
			else { setBalloonBottomOffset(dipToPx(22)); } //medium
		}
	}
	
	/**
	 * Hides all balloons on the MapView
	 * @param overlays A list of balloon overlays to hide
	 */
	protected void hideAllBalloons(final List<Overlay> overlays) {		
		
		((Map)context).getUIHandler().post(new Runnable() {
			public void run() {
				for (Overlay overlay : overlays) {
					if (overlay instanceof BalloonItemizedOverlay<?>) {
						((BalloonItemizedOverlay<?>) overlay).hideBalloon();
						finishedThreadUpdate = true;
					}
				}
			}
		});
		
		//Wait for the handler.post() to finish
		while (finishedThreadUpdate == false) { }
		finishedThreadUpdate = false;
	}
	
	/**
	 * When a balloon is tapped, show an alert with additional
	 * details about other specified Bus
	 * 
	 * @param index The index of the balloon being tapped
	 * @param item the OverlayItem associated with the balloon being tapped
	 */
	@Override
	protected boolean onBalloonTap(int index, OverlayItem item) {
		
		if (item instanceof BusOverlay) {
			
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setTitle(item.getTitle() + ": Details");
			
			Bus bus = ((BusOverlay) overlays.get(index)).getBus();			
			dialog.setMessage("Bus ID: " + bus.getId() +
								"\nLatitude: " + bus.getLatitude() +
								"\nLongitude: " + bus.getLongitude() +
								"\nSpeed: " + bus.getMPH() + " MPH" +
								"\nDirection: " + bus.getDirection() +
								"\nLast Updated: " + Map.SDF.format(bus.getLastUpdated().getTime()));
			
			dialog.setCancelable(true);
			dialog.setPositiveButton("Ok", null);
			dialog.show();
			
			return true;
		}
		else { return false;	}
	}
	
	/**
	 * Re-open the balloons that were open before the Map was updated
	 * @param index The index for the BusOverlay that had an open balloon before the update
	 */
	private void restoreOpenBalloons(final int index) {
		
		//If the item is a bus overlay AND its id matches the previously open bubble's id: open that bubble and clear the 
		if (overlays.get(index) instanceof BusOverlay) {
			
			if (((BusOverlay) overlays.get(index)).getBusId() == ((Map) context).getActiveBalloonOverlayItemId()) {				
				
				//reset the active balloon
				((Map) context).setActiveBalloonOverlayItemId(-1);
				
				//Open the bubble
				((Map) context).getUIHandler().post(new Runnable() {
					public void run() {
						onTap(index, false);
					}
				});
			}
		}
		else if (overlays.get(index).getPoint().getLatitudeE6() == ((Map) context).getActiveBalloonOverlayItemId()) {
			
			//reset the active balloon
			((Map) context).setActiveBalloonOverlayItemId(-1);
			
			//Open the bubble
			((Map) context).getUIHandler().post(new Runnable() {
				public void run() {
					onTap(index, false);
				}
			});
		}
	}
	
	/* The method below, getOnCloseRunnable(), implements a method added to the android-mapviewballoons "extension"
	 * that is used to supply BalloonOverlayView with a runnable to execute when the user
	 * closes a balloon ONLY from the "close" button. It is necessary in order to tell the Map
	 * that there is no longer an active balloon open, as coded below: 
	 */
	
	@Override
	public Runnable getOnCloseRunnable() {
		return new Runnable() {
			public void run() {
				((Map) context).setActiveBalloonOverlayItemId(-1);
			}
		};
	}
	/**
	 * Converts density-independent pixels to pixels
	 * @param dip The density-independent pixel value to be converted
	 * @return the pixel value of the provided DIPs
	 */
	private int dipToPx(int dip) {
		Resources r = context.getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
	}
	
	@Override
	protected BalloonOverlayView<OverlayItem> createBalloonOverlayView() {
		// use custom balloon view:
		return new BalloonView<OverlayItem>(getMapView().getContext(), this, getBalloonBottomOffset());
	}
}
