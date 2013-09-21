package dwat.ccshuttletracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

/**
 * A small class used by the Map to detect when the screen is turned off so that
 * the "No Connection" and/or "No Busses" message will not be
 * displayed when turning the screen back on.
 * 
 * @author David Watkins
 * @version 2.1.1
 * @since 9/20/13
 */
public class ScreenOffListener extends BroadcastReceiver {

	private Handler mapHandler;
	private Runnable callback;
	
	/**
	 * Initialize the ScreenOffListener
	 * 
	 * @param h The handler for the current Map
	 * @param r A callback Runnable to be performed after the screen is off
	 */
	public ScreenOffListener(Handler h, Runnable r) {
		mapHandler = h;
		callback = r;
	}
	
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			mapHandler.post(callback);
		}
	}

}
