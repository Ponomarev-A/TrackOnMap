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
	private GoogleMap mMap;
	private Track mTrack = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition cameraPosition) {
				if (mTrack != null) {
					drawTrack();
				}

				// Remove listener to prevent position reset on camera move.
				mMap.setOnCameraChangeListener(null);
			}
		});

		new DownloadJSON() {
			@Override
			protected void onPostExecute(String s) {
				mTrack = FetchData.getTrackFromJson(s);
				drawTrack();
			}
		}.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();

		if (id == R.id.action_infoTrack) {
			if (mTrack != null) {
				InfoFragment infoFragment = new InfoFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable(KEY_TRACK, mTrack);

				infoFragment.setArguments(bundle);
				infoFragment.show(getFragmentManager(), TAG_INFO_FRAGMENT);
			}
			else {
				// TODO: show Toast with "Track not loaded" label
			}
		}

		return super.onOptionsItemSelected(item);
	}

	private void drawTrack() {
		if (mMap == null || mTrack == null) {
			return;
		}

		// Get point and draw line with coordinates (prevPoint(lat, lng), nextPoint(lat, lng)))
		List<TrackPoint> points = mTrack.getPoints();
		TrackPoint startPoint = points.get(0);
		TrackPoint endPoint = points.get(points.size() - 1);

		TrackPoint prevPoint = startPoint;

		for (TrackPoint point : points) {
			mMap.addPolyline(new PolylineOptions().add(prevPoint.getLatLng()).add(point.getLatLng()).color(getLineColor(prevPoint, point)));
			prevPoint = point;
		}

		LatLngBounds.Builder builder = LatLngBounds.builder().include(startPoint.getLatLng()).include(endPoint.getLatLng());

		mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 30));
	}

	private int getLineColor(TrackPoint prevPoint, TrackPoint nextPoint) {
		final int speed = Math.max(prevPoint.getSpeed(), nextPoint.getSpeed());

		if (speed <= COLOR_GREEN_SPEED_KM_H) {
			return Color.GREEN;
		}
		else if (speed <= COLOR_YELLOW_SPEED_KM_H) {
			return Color.YELLOW;
		}
		else {
			return Color.RED;
		}
	}

	public static class InfoFragment extends DialogFragment {
		private Track mTrack;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			Bundle arguments = getArguments();

			if (arguments != null) {
				mTrack = (Track)arguments.getSerializable(KEY_TRACK);
			}

			builder.setTitle(getString(R.string.dialog_title)).setMessage(getInfoTrack()).setPositiveButton(getString(R.string.dialog_button_title), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});

			return builder.create();
		}

		private String getInfoTrack() {
			SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy", Locale.US);

			return String.format(getString(R.string.format_info_track), mTrack.getLength() / 1000f,  // convert to kilometers
					mTrack.getMaxSpeed(), dateFormat.format(new Date(mTrack.getStartTime())), timeFormat.format(new Date(mTrack.getStartTime())), timeFormat.format(new Date(mTrack.getEndTime())));
		}
	}

	private class DownloadJSON extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			// Mock data load from local file
			return loadFromFile();
		}

		private String loadFromFile() {
			String json = null;
			InputStream is = null;

			try {
				is = getAssets().open("track.json");

				byte[] buffer = new byte[is.available()];
				is.read(buffer);

				json = new String(buffer, "UTF-8");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				if (is != null) {
					try {
						is.close();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			return json;
		}
	}
}

