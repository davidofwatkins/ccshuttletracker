package dwat.ccshuttletracker;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

/**
 * The View to be displayed in balloon overlays
 * 
 * @author David Watkins
 * @version 2.1.1
 * @since 9/20/13
 */
public class BalloonView<Item extends OverlayItem> extends BalloonOverlayView<OverlayItem> {

	private TextView title;
	private TextView snippet;
	private BalloonItemizedOverlay<?> bio;
	private View v;
	private Context context;
	
	public BalloonView(Context context, BalloonItemizedOverlay<?> bio, int balloonBottomOffset) {
		super(context, balloonBottomOffset);
		this.bio = bio;
		this.context = context;
	}
	
	/**
	 * Set up the BalloonView with fields for the Title/Snippet, close button, etc.
	 * 
	 * @param c The application context
	 * @param parent the ViewGroup to inflate the layout into
	 */
	@Override
	protected void setupView(Context c, final ViewGroup parent) {
		
		//inflate the custom layout into parent
		LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = inflater.inflate(R.layout.balloon, parent);
		
		//Set up fields
		title = (TextView) v.findViewById(R.id.balloon_title);
		snippet = (TextView) v.findViewById(R.id.balloon_snippet);
		
		//Implement balloon close
		ImageView close = (ImageView) v.findViewById(R.id.balloon_close);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				parent.setVisibility(GONE);
				bio.getOnCloseRunnable().run();
			}
		});
	}
	
	/**
	 * Updates the BalloonView with the appropriate Title and Snippet
	 * 
	 * @param item The associated OverlayItem for which this balloon is being displayed
	 */
	@Override
	protected void setBalloonData(OverlayItem item, ViewGroup parent) {
		
		//map item data to fields
		title.setText(item.getTitle());
		snippet.setText(item.getSnippet());
		
		//Change the PaddingBottom of the balloon, depending on whether or not it has a snippet
		LinearLayout ll = (LinearLayout) v.findViewById(R.id.balloon_main_layout);
		if (snippet.getText() == "" || snippet.getText() == null) {
			ll.setPadding(ll.getPaddingLeft(), ll.getPaddingTop(), ll.getPaddingRight(), dipToPx(0));
		}
		else {
			ll.setPadding(ll.getPaddingLeft(), ll.getPaddingTop(), ll.getPaddingRight(), dipToPx(20));
		}
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
}
