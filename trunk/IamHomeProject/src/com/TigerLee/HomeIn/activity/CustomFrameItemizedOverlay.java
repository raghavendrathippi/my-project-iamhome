package com.tigerlee.homein.activity;

import java.lang.reflect.Method; 
import java.util.List;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.MediaController;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.tigerlee.homein.R;


public abstract class CustomFrameItemizedOverlay<item> extends ItemizedOverlay<OverlayItem> {

	private MapView mapView;
	private CustomFrameOverlayView customFrameOverlayView;
	private View clickRegion;
	private int viewOffset;
	final MapController mc;
	
	
	public CustomFrameItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(defaultMarker);
		this.mapView = mapView;
		viewOffset = 0;
		mc = mapView.getController();
	}
	
	
	public void setBalloonBottomOffset(int pixels) {
		viewOffset = pixels;
	}
	

	protected boolean onBalloonTap(int index) {
		return false;
	}


	@Override
	protected final boolean onTap(int index) {
		
		boolean isRecycled;
		final int thisIndex;
		GeoPoint point;
		
		thisIndex = index;
		point = createItem(index).getPoint();
		
		if (customFrameOverlayView == null) {
			customFrameOverlayView = new CustomFrameOverlayView(mapView.getContext(), viewOffset);
			clickRegion = (View) customFrameOverlayView.findViewById(R.id.mapframe_inner_layout);
			isRecycled = false;
		} else {
			isRecycled = true;
		}
	
		customFrameOverlayView.setVisibility(View.GONE);
		
		List<Overlay> mapOverlays = mapView.getOverlays();
		if (mapOverlays.size() > 1) {
			hideOtherBalloons(mapOverlays);
		}
		
		customFrameOverlayView.setData(createItem(index));
		
		MapView.LayoutParams params = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point,
				MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;
		
		setBalloonTouchListener(thisIndex);
		
		customFrameOverlayView.setVisibility(View.VISIBLE);

		if (isRecycled) {
			customFrameOverlayView.setLayoutParams(params);
		} else {
			mapView.addView(customFrameOverlayView, params);
		}
		
		mc.animateTo(point);
		
		return true;
	}
	
	/**
	 * Sets the visibility of this overlay's balloon view to GONE. 
	 */
	private void hideBalloon() {
		if (customFrameOverlayView != null) {
			customFrameOverlayView.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Hides the balloon view for any other BalloonItemizedOverlay instances
	 * that might be present on the MapView.
	 * 
	 * @param overlays - list of overlays (including this) on the MapView.
	 */
	private void hideOtherBalloons(List<Overlay> overlays) {
		
		for (Overlay overlay : overlays) {
			if (overlay instanceof CustomFrameItemizedOverlay<?> && overlay != this) {
				((CustomFrameItemizedOverlay<?>) overlay).hideBalloon();
			}
		}
		
	}
	
	/**
	 * Sets the onTouchListener for the balloon being displayed, calling the
	 * overridden onBalloonTap if implemented.
	 * 
	 * @param thisIndex - The index of the item whose balloon is tapped.
	 */
	private void setBalloonTouchListener(final int thisIndex) {
		
		try {
			@SuppressWarnings("unused")
			Method m = this.getClass().getDeclaredMethod("onBalloonTap", int.class);
			
			clickRegion.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					
					View l =  ((View) v.getParent()).findViewById(R.id.mapframe_main_layout);
					Drawable d = l.getBackground();
					
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						int[] states = {android.R.attr.state_pressed};
						if (d.setState(states)) {
							d.invalidateSelf();
						}
						return true;
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						int newStates[] = {};
						if (d.setState(newStates)) {
							d.invalidateSelf();
						}
						// call overridden method
						onBalloonTap(thisIndex);
						return true;
					} else {
						return false;
					}
					
				}
			});
			
		} catch (SecurityException e) {
			Log.e("BalloonItemizedOverlay", "setBalloonTouchListener reflection SecurityException");
			return;
		} catch (NoSuchMethodException e) {
			// method not overridden - do nothing
			return;
		}

	}
}