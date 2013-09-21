package dwat.ccshuttletracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Manages all Bus objects and is responsible for retrieving location data
 * from shuttle.champlain.edu
 * 
 * @author David Watkins
 * @version 2.1.1
 * @since 9/20/13
 */
public class BusManager {

	private String unparsedString;
	private String cachedUnparsedString;
	private boolean reconnect;
	private ErrorReporter reporter;

	public BusManager(ErrorReporter r) {
		reporter = r;
	}
	
	/**
	 * Downloads raw data from the server and creates the necessary Buses from it
	 * 
	 * @param reconnect Specifies if new Bus data should be downloaded from the server
	 * @return the new Buses containing the updated data
	 */
	public ArrayList<Bus> updateBusses(Boolean reconnect) {
		this.reconnect = reconnect;
		unparsedString = getRawData();
		
		ArrayList<Bus> buses = null;
		int i = 1;
		while (i <= 3) { //try this three times in case it doesn't work at first
			try {
				if (reconnect) { buses = generateBuses(unparsedString); }
				else { buses = generateBuses(cachedUnparsedString); }
				break;
			}
			catch (JSONException e) {
				Log.e("CCShuttleTracker", "Error parsing JSON data");
				if (i == 3) return buses; //return empty busses array to avoid crash
			}
			catch (ParseException e) {
				Log.e("CCShuttleTracker", "Error parsing shuttle time");
				if (i == 3) return buses; //return empty busses array to avoid crash
			}
			catch (NullPointerException e) {
				Log.e("CCShuttleTracker", "Unknown error (Null pointer)");
				if (i == 3) return buses; //return empty busses array to avoid crash
			}
		}
		
		if (buses.size() == 0)
			reporter.reportNoShuttles();
		else
			reporter.resetShuttleMessages();
		
		return buses;
	}
	/**
	 * Downloads raw JSON data from the server
	 * 
	 * @return raw data from server
	 */
	private String getRawData() {
		
		if (reconnect == true) {
			
			try {
				
				URL url = new URL("http://shuttle.champlain.edu/shuttledata");
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				
				String returnString = in.readLine();
				cachedUnparsedString = returnString;
				
				reporter.resetConnectionMessages();
				
				return returnString;
			}
			catch (IOException e) {
				
				//need to set cached string to none too, so that the map
				//doesn't show old bus locations when it has no connection
				cachedUnparsedString = ""; 
				
				// Make error message that says it can't reach shuttle.champlain.edu
				reporter.reportNoConnection();
				
				return "";
			}
			catch(Exception e) {
				Log.w("CCShuttleTracker", "Unknown error obtaining raw data");
				return "";
			}
		}
		else { // if reconnect == false, don't connect to server for new coords, just used last received
			
			if (cachedUnparsedString != "" && cachedUnparsedString != null) {
				unparsedString = cachedUnparsedString;
				return unparsedString;
			}
			else {
				return "";
			}
		}
	}
	
	/**
	 * Converts string of JSON data into list of Buses
	 * 
	 * @param jsonString The raw data from server to be used in creating the Buses
	 * @return A list of Buses filled with the appropriate information
	 * @throws JSONException
	 * @throws ParseException
	 * @throws NullPointerException
	 */
	private ArrayList<Bus> generateBuses(String jsonString) throws JSONException, ParseException, NullPointerException {
		
		ArrayList<Bus> newBuses = new ArrayList<Bus>();
		
		JSONArray busesData = new JSONArray(jsonString);
		
		for (int i = 0; i < busesData.length(); i++) {
			JSONObject busData = (JSONObject) busesData.get(i);
			
			//Turn Time into a GregorianCalendar
			String lastUpdatedString = busData.getString("Date_Time_ISO");
			SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ss.SSSSSSSZ", Locale.US);
			Date parsed = df.parse(lastUpdatedString);
			GregorianCalendar lastUpdated = new GregorianCalendar();
			lastUpdated.setTime(parsed);
			
			int directionInt = busData.getInt("Direction");
			String direction;
			
			//Determine the "direction" (N/E/S/W) from directionInt
			if (directionInt >= 337 || directionInt < 22) { direction = "north"; }
			else if (directionInt >= 22 && directionInt < 67) { direction = "NE"; }
			else if (directionInt >= 67 && directionInt < 112) { direction = "east"; }
			else if (directionInt >= 112 && directionInt < 157) { direction = "SE"; }
			else if (directionInt >= 157 && directionInt < 202) { direction = "south"; }
			else if (directionInt >= 202 && directionInt < 247) { direction = "SW"; }
			else if (directionInt >= 247 && directionInt < 292) { direction = "west"; }
			else if (directionInt >= 292 && directionInt < 337) { direction = "NW"; }
			else { direction = "N/A"; }
			
			Bus newBus = new Bus(
					busData.getInt("UnitID"),
					busData.getString("Unit_Name"),
					lastUpdated,
					busData.getString("Unit_Operator"),
					busData.getDouble("Lat"),
					busData.getDouble("Lon"),
					busData.getInt("Knots"),
					busData.getInt("Direction"),
					direction
				);
			
			newBuses.add(newBus);
		}
		
		return newBuses;
	}
}
