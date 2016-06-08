package test.trackonmap;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Fetch data from JSON file class
 */
public class FetchData {
	private static final String LOG_TAG = FetchData.class.getSimpleName();

	public static Track getTrackFromJson(String jsonStr) {
		// General tracks information
		final String OWM_STATE = "state";
		final String OWM_MESSAGE = "msg";

		// General track information.  Each track info is an element of the "data" array.
		final String OWM_DATA = "data";

		final String OWM_START = "start";
		final String OWM_END = "end";
		final String OWM_LENGTH = "length";
		final String OWM_COUNT = "count";
		final String OWM_MAX_SPEED = "max_speed";

		// Track points information.  Each track point info is an element of the "points" array.
		final String OWM_POINTS = "points";

		final String OWM_LATITUDE = "lat";
		final String OWM_LONGITUDE = "lng";
		final String OWM_DATE = "date";
		final String OWM_SPEED = "speed";

		try {
			JSONObject trackArrayJson = new JSONObject(jsonStr);
			JSONObject trackJson = trackArrayJson.getJSONArray(OWM_DATA).getJSONObject(0);

			long startTime = trackJson.getLong(OWM_START) * 1000;   // convert to Ms
			long endTime = trackJson.getLong(OWM_END) * 1000;
			int length = trackJson.getInt(OWM_LENGTH);
			int count = trackJson.getInt(OWM_COUNT);
			int max_speed = trackJson.getInt(OWM_MAX_SPEED);

			JSONArray pointsArray = trackJson.getJSONArray(OWM_POINTS);

			ArrayList<TrackPoint> points = new ArrayList<>();

			for (int j = 0; j < pointsArray.length(); j++) {
				JSONObject pointJson = pointsArray.getJSONObject(j);

				double latitude = pointJson.getDouble(OWM_LATITUDE);
				double longitude = pointJson.getDouble(OWM_LONGITUDE);
				long date = pointJson.getLong(OWM_DATE);
				int speed = pointJson.getInt(OWM_SPEED);

				points.add(new TrackPoint(latitude, longitude, date, speed));
			}

			Track track = new Track(startTime, endTime, length, count, max_speed, points);
			Log.d(LOG_TAG, "Fetch data form JSON complete. " + track.getCount() + " track points inserted");

			return track;
		}
		catch (JSONException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			e.printStackTrace();
		}

		return null;
	}
}
