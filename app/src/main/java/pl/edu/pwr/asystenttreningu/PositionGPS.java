package pl.edu.pwr.asystenttreningu;

/**
 * Created by michalos on 30.09.14.
 */
public class PositionGPS {

    private double Latitude;
    private double Longitude;
    private long Time;

    public PositionGPS(double latitude, double longitude, long time) {
        Latitude = latitude;
        Longitude = longitude;
        Time = time;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }
}
