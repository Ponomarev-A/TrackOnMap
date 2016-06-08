package test.trackonmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Track point information class
 */
public class TrackPoint {
	private final long date;
	private final int speed;
	private final LatLng latLng;

	public TrackPoint(double latitude, double longitude, long date, int speed) {
		latLng = new LatLng(latitude, longitude);
		this.date = date;
		this.speed = speed;
	}

	public long getDate() {
		return date;
	}

	public int getSpeed() {
		return speed;
	}

	public LatLng getLatLng() {
		return latLng;
	}
}
