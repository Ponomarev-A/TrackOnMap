package test.trackonmap;

import java.util.List;

/**
 * Track information class
 */
public class Track {
    private final long startTime;
    private final long endTime;
    private final int length;
    private final int max_speed;

    private int count;
    private List<TrackPoint> points;

    public Track(long startTime, long endTime, int length, int count, int max_speed, List<TrackPoint> points) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.length = length;
        this.count = count;
        this.max_speed = max_speed;
        this.points = points;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public int getLength() {
        return length;
    }

    public int getMax_speed() {
        return max_speed;
    }

    public int getCount() {
        return count;
    }

    public List<TrackPoint> getPoints() {
        return points;
    }
}
