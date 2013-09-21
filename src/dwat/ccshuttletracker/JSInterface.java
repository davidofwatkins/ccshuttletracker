package dwat.ccshuttletracker;

import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;

/**
 * A interface for Javascript within WebViews to connect with Android
 * functions.
 * 
 * @author David Watkins
 * @version 2.1.1
 * @since 9/20/13
 */
public class JSInterface {
	
	private Context context;
	
	public JSInterface(Context c) {
		context = c;
	}
	
	/**
	 * @return the devices current orientation as an integer
	 */
	@JavascriptInterface
	public int getOrientation() {
		
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getOrientation();
	}
	
	/**
	 * A way to print to the Logcat from Javascript (used for
	 * debugging)
	 * @param s The string to be logged
	 */
	@JavascriptInterface
	public void printToConsole(String s) {
		Log.i("CCShuttleTracker", s);
	}
}