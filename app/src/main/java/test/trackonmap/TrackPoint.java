package test.trackonmap;

/**
 * Track point information class
 */
public class TrackPoint {

    private final double latitude;
    private final double longitude;
    private final long date;
    private final int speed;

    public TrackPoint(double latitude, double longitude, long date, int speed) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.speed = speed;
    }

}
