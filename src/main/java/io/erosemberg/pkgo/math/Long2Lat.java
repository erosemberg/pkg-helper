package io.erosemberg.pkgo.math;

import com.google.common.util.concurrent.AtomicDouble;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Erik Rosemberg
 * @since 8/10/16
 */
public final class Long2Lat {

    private AtomicDouble longitude = new AtomicDouble(0.00D);
    private AtomicDouble latitude = new AtomicDouble(0.00D);

    public Long2Lat(double latitude, double longitude) {
        this.longitude.set(longitude);
        this.latitude.set(latitude);
    }

    //TODO: The rest.
}
