package test.trackonmap;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int COLOR_GREEN_SPEED_KM_H = 100;
    private static final int COLOR_YELLOW_SPEED_KM_H = 110;

    private GoogleMap map;
    private Track track = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        track = FetchData.getTrackFromJson(loadJsonFromFile());

    }

    private String loadJsonFromFile() {

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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        drawTrackOnMap(track);
    }

    private void drawTrackOnMap(Track track) {

        // Get point and draw line with coordinates (prevPoint(lat, lng), nextPoint(lat, lng)))
        TrackPoint prevPoint = track.getPoints().get(0);

        for (TrackPoint nextPoint : track.getPoints()) {

            map.addPolyline(new PolylineOptions()
                    .add(prevPoint.getLatLng())
                    .add(nextPoint.getLatLng())
                    .color(getLineColor(prevPoint, nextPoint))
            );

            prevPoint = nextPoint;
        }

        map.moveCamera(CameraUpdateFactory.newLatLng(prevPoint.getLatLng()));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_infoTrack) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getString(R.string.dialog_title))
                    .setMessage(getInfoTrack())
                    .setPositiveButton(getString(R.string.dialog_button_title), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            builder.create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

