package dwat.ccshuttletracker;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

/**
 * Mostly the same as any other MapView, but this has been modified
 * to provide the following enhancements:
 *  
 * -The map will listen for zoom events so that the overlay icons can be changed (for different sizes)
 * -The map will listen for double taps and zoom in on the point tapped
 * 
 * @author David Watkins
 * @version 2.1.1
 * @since 9/20/13
 */
public class EnhancedMapView extends MapView {

	private int oldZoomLevel;
	private long lastTouchTime = -1;
	private Context context;
	
	public EnhancedMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;		
	}
	public EnhancedMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public EnhancedMapView(Context context, String apiKey) {
		super(context, apiKey);
	}
	
	/**
	 * Adjusts icon sizes depending on MapView zoom level
	 */
	@Override 
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		
		//when zooming in/out of map:
		if (getZoomLevel() != oldZoomLevel) {
			
			//Refresh icons (in order to adjust icon sizes for new zoom level):
			try {
				new Thread(new Runnable() {
					public void run() {
						((Map) context).updateMap(false, false);
					}
				}).start();
			}
			catch(Exception e) { Log.e("CCShuttleTracker", "Could not refresh overlay (from EnhancedMapView): " + e); }
			
			//Close balloons
			for (Overlay overlay : getOverlays()) {
				if (overlay instanceof BalloonItemizedOverlay<?>) {
					((BalloonItemizedOverlay<?>) overlay).hideBalloon();
				}
			}
			
			oldZoomLevel = getZoomLevel();
		}
	}
	
	
	/**
	 * Enables support for double-tapping to zoom
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			
			//Save the current time for next ACTION_DOWN event
			long thisTime = System.currentTimeMillis();
			
			//If a double-tap has occurred
			if (thisTime - lastTouchTime < 250) {
				
				//Zoom out on the location of the tap if two fingers
				if (event.getPointerCount() == 2) {
					this.getController().zoomOutFixing((int) event.getX(), (int) event.getY());
				}
				//Zoom in on the location of the tap if only one finger
				else {
					this.getController().zoomInFixing((int) event.getX(), (int) event.getY());
				}
		        lastTouchTime = -1;
			}
			
			//Not a double-tap
			else {
				lastTouchTime = thisTime;
			}
		}
		
		return super.onInterceptTouchEvent(event);
	}	
}
