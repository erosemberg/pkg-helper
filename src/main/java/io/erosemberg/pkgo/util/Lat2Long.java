package io.erosemberg.pkgo.util;

import com.google.common.util.concurrent.AtomicDouble;

/**
 * @author Erik Rosemberg
 * @since 8/10/16
 */
public class Lat2Long {

    private AtomicDouble latitude = new AtomicDouble(0.00D);
    private AtomicDouble longitude = new AtomicDouble(0.00D);

    public Lat2Long(double latitude, double longitude) {
        this.latitude.set(latitude);
        this.longitude.set(longitude);
    }

    public AtomicDouble getLatitude() {
        return latitude;
    }

    public AtomicDouble getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return latitude.get() + ", " + longitude.get();
    }
}
