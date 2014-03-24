package dwat.ccshuttletracker;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * Manages error messages sent from the application related to shuttle
 * and connection availability.
 * 
 * @author David Watkins
 * @version 2.1.1
 * @since 9/20/13
 */
public class ErrorReporter {
	
	private Context mContext;
	private Handler mHandler;
	private Boolean showShuttleErrors;
	private Boolean showConnectionErrors;
	
	/**
	 * Instantiates a new ErrorReporter, used to manage connection
	 * and shuttle errors on the map.
	 * @param context The application context
	 */
	public ErrorReporter(Context context) {	
		showShuttleErrors = true;
		showConnectionErrors = true;
		mContext = context;
		mHandler = ((Map) mContext).getUIHandler();
	}
	
	/**
	 * Reset ErrorReporter so that all errors will display when necessary
	 */
	public void reset() {
		showShuttleErrors = true;
		showConnectionErrors = true;
	}
	
	/**
	 * Reset ErrorReporter so that shuttle-related error messages will display when necessary
	 */
	public void resetShuttleMessages() {
		showShuttleErrors = true;
	}
	
	/**
	 * Reset ErrorReporter so that connection-related error messages will display when necessary
	 */
	public void resetConnectionMessages() {
		showConnectionErrors = true;
	}
	
	/**
	 * Show error message reporting that no shuttles are active
	 */
	public void reportNoShuttles() {
		
		if (showShuttleErrors) {
			
			//Must show Toasts on the UI thread, otherwise Android might yell at you!
			mHandler.post(new Runnable() {
				public void run() {
					Toast.makeText(mContext, "No Active Shuttles", Toast.LENGTH_LONG).show();
				}
			});
			
			Log.w("CCShuttleTracker", "No Active Buses");
			showShuttleErrors = false;
		}
	}
	
	/**
	 * Show error message reporting that the network connection is unavailable
	 */
	public void reportNoConnection() {
		
		if (showConnectionErrors) {
			
			mHandler.post(new Runnable() {
				public void run() {
					Toast.makeText(mContext, "Cannot connect to shuttle.champlain.edu", Toast.LENGTH_LONG).show();
				}
			});
			
			Log.e("CCShuttleTracker", "Cannot connect to shuttle.champlain.edu");
			showShuttleErrors = false;
			showConnectionErrors = false;
		}
	}

}
