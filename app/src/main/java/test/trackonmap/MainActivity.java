package test.trackonmap;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int COLOR_GREEN_SPEED_KM_H = 100;
    private static final int COLOR_YELLOW_SPEED_KM_H = 110;

    private GoogleMap map;
    private List<Track> trackList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        trackList = FetchData.getDataFromJson(loadJsonFromFile());

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

        for (Track track : trackList) {
            drawTrackOnMap(track);
        }
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
}

