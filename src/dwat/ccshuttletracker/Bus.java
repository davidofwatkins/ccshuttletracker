package dwat.ccshuttletracker;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * The Bus class contains all details for a specific bus, including
 * name, location, direction, speed, etc.
 * 
 * @author David Watkins
 * @version 2.1.1
 * @since 9/20/13
 */
public class Bus {

	private String name;
	private int id;
	private GregorianCalendar lastUpdated;
	private String unitOperator;
	private double latitude;
	private double longitude;
	private int knots;
	private int directionInt;
	private String direction;
	
	/**
	 * Instantiate a Bus object
	 * 
	 * @param id The new Bus's ID
	 * @param name The new Bus's name
	 * @param lastUpdated The last date and time of the Bus's reported update
	 * @param unitOperator The bus operator
	 * @param latitude The latitude of the Bus's location
	 * @param longitude The longitude of the Bus's location
	 * @param knots The last known speed of the Bus in knots
	 * @param directionInt The last known direction of the Bus as an interger
	 * @param direction The last known direction of the Bus
	 */
	public Bus(int id, String name, GregorianCalendar lastUpdated, String unitOperator, double latitude, double longitude, int knots, int directionInt, String direction) {
		this.id = id;
		this.name = name;
		this.lastUpdated = lastUpdated;
		this.unitOperator = unitOperator;
		this.latitude = latitude;
		this.longitude = longitude;
		this.knots = knots;
		this.directionInt = directionInt;
		this.direction = direction;
	}
	
	//Getters:
	public String getName() {
		return name;
	}
	public int getId() {
		return id;
	}
	public String getUnitOperator() {
		return unitOperator;
	}
	
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public int getKnots() {
		return knots;
	}
	public int getMPH() {
		return (int) (knots * 1.15);
	}
	public String getDirection() {
		return direction;
	}
	public int getDirectionInt() {
		return directionInt;
	}
	public GregorianCalendar getLastUpdated() {
		return lastUpdated;
	}
	/**
	 * Detect if a Bus is active. A Bus is active if it is within the borders of
	 * Burlington/Winooski and if it has reported a location within the past 20
	 * minutes.
	 * 
	 * @return true if active, false if not
	 */
	public boolean isActive() {
		
		//If the bus is in Burlington
		if (latitude < 44.50238238974582 && longitude < -73.16662788391113 && latitude > 44.446648964675376 && longitude > -73.23537826538086) {
			
			//And if it's a recent bus
			if (Calendar.getInstance().getTimeInMillis() - lastUpdated.getTimeInMillis() < 1200000) //20 minutes
				return true;
			else
				return false;
		}
		else { return false; }
	}
	
	/**
	 * Prints the Bus's data to standard output. This is only used as a
	 * method for troubleshooting problems.
	 */
	public void reportData() {
		System.out.println("Bus Name: " + name);
		System.out.println("Bus ID: " + id);
		System.out.println("Unit Operator: " + unitOperator);
		System.out.println("Latitude: " + latitude);
		System.out.println("Longitude: " + longitude);
		System.out.println("Direction: " + directionInt);
		System.out.println("Knots: " + knots);
		String tempAmPm;
		if (lastUpdated.get(Calendar.AM_PM) == Calendar.AM) { tempAmPm = "AM"; }
		else { tempAmPm = "PM"; }
		System.out.println("Last Updated: " + lastUpdated.get(Calendar.DAY_OF_MONTH) + "/" + (lastUpdated.get(Calendar.MONTH) + 1) + "/" + lastUpdated.get(Calendar.YEAR) + " at " + lastUpdated.get(Calendar.HOUR_OF_DAY) + ":" + lastUpdated.get(Calendar.MINUTE) + " " + tempAmPm);
		System.out.println();
	}
}
