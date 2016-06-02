package test.trackonmap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int COLOR_GREEN_SPEED_KM_H = 60;
    private static final int COLOR_YELLOW_SPEED_KM_H = 120;
    private static final String KEY_TRACK = "INFO_TRACK";
    private static final String TAG_INFO_FRAGMENT = "infoTrack";

    private GoogleMap map;
    private Track track = null;
    private DownloadJSON downloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Start async downloader task
        downloader = new DownloadJSON();
        downloader.execute();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (track != null) {
                    drawTrackOnMap(track, map);
                }
                // Remove listener to prevent position reset on camera move.
                map.setOnCameraChangeListener(null);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_infoTrack) {
            InfoFragment infoFragment = new InfoFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_TRACK, track);

            infoFragment.setArguments(bundle);
            infoFragment.show(getFragmentManager(), TAG_INFO_FRAGMENT);
        }
        return super.onOptionsItemSelected(item);
    }

    private void drawTrackOnMap(Track track, GoogleMap map) {
        // Get point and draw line with coordinates (prevPoint(lat, lng), nextPoint(lat, lng)))
        List<TrackPoint> points = track.getPoints();
        TrackPoint startPoint = points.get(0);
        TrackPoint endPoint = points.get(points.size() - 1);

        TrackPoint prevPoint = startPoint;

        for (TrackPoint point : points) {
            map.addPolyline(new PolylineOptions()
                    .add(prevPoint.getLatLng())
                    .add(point.getLatLng())
                    .color(getLineColor(prevPoint, point))
            );
            prevPoint = point;
        }

        LatLngBounds.Builder builder = LatLngBounds.builder()
                .include(startPoint.getLatLng())
                .include(endPoint.getLatLng());

        map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 30));
    }

    private int getLineColor(TrackPoint prevPoint, TrackPoint nextPoint) {
        int speed = Math.max(prevPoint.getSpeed(), nextPoint.getSpeed());

        if (speed <= COLOR_GREEN_SPEED_KM_H) {
            return Color.GREEN;
        } else if (speed <= COLOR_YELLOW_SPEED_KM_H) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }

    public static class InfoFragment extends DialogFragment {
        private Track track;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            Bundle arguments = getArguments();
            if (arguments != null) {
                track = (Track) arguments.getSerializable(KEY_TRACK);
            }

            builder.setTitle(getString(R.string.dialog_title))
                    .setMessage(getInfoTrack())
                    .setPositiveButton(getString(R.string.dialog_button_title), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            return builder.create();
        }

        private String getInfoTrack() {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy", Locale.ENGLISH);

            return String.format(getString(R.string.format_info_track),
                    track.getLength() / 1000f,  // convert to kilometers
                    track.getMaxSpeed(),
                    dateFormat.format(new Date(track.getStartTime())),
                    timeFormat.format(new Date(track.getStartTime())),
                    timeFormat.format(new Date(track.getEndTime()))
            );
        }
    }

    private class DownloadJSON extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // Mock data load from local file
            return loadFromFile();
        }

        @Override
        protected void onPostExecute(String s) {
            track = FetchData.getTrackFromJson(s);
        }

        private String loadFromFile() {

            String json = null;

            try {
                InputStream is = getAssets().open("track.json");

                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                is.close();

                json = new String(buffer, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return json;
        }
    }
}

