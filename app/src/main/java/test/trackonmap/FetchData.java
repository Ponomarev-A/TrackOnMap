package test.trackonmap;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Fetch data from JSON file class
 */
public class FetchData {

    private static final String LOG_TAG = FetchData.class.getSimpleName();

    public static List<Track> getDataFromJson(String jsonStr) {

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
            JSONArray trackArray = trackArrayJson.getJSONArray(OWM_DATA);

            ArrayList<Track> tracks = new ArrayList<>();

            for (int i = 0; i < trackArray.length(); i++) {
                JSONObject trackJson = trackArray.getJSONObject(i);

                long startTime = trackJson.getLong(OWM_START);
                long endTime = trackJson.getLong(OWM_END);
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

                tracks.add(new Track(startTime, endTime, length, count, max_speed, points));
            }

            Log.d(LOG_TAG, "Fetch data form JSON complete. " + tracks.size() + " track(s) inserted");
            return tracks;

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }


}
