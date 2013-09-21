package dwat.ccshuttletracker;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * An OverlayItem extended to hold information related to a related Bus object
 * 
 * @author David Watkins
 * @version 2.1.1
 * @since 9/20/13
 */
public class BusOverlay extends OverlayItem {

	private int busNum;
	private Bus bus;
	
	/**
	 * Instantiate a new Bus OverlayItem
	 * 
	 * @param newBusNum A numeric identifier for the overlay item
	 * @param newBus A Bus object holding relevant details about the overlay
	 * @param point The location of the overlay
	 * @param snippet The text that will appear on the overlay's balloon
	 */
	public BusOverlay(int newBusNum, Bus newBus, GeoPoint point, String snippet) {
		super(point, newBus.getName(), snippet);
		busNum = newBusNum;
		bus = newBus;
	}
	
	public int getBusNum() {
		return busNum;
	}
	
	public Bus getBus() {
		return bus;
	}
	
	public int getBusId() {
		return bus.getId();
	}

}
