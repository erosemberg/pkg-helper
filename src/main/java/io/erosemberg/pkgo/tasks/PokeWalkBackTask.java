package io.erosemberg.pkgo.tasks;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.google.common.geometry.S2LatLng;
import io.erosemberg.pkgo.Helper;
import io.erosemberg.pkgo.util.Lat2Long;
import io.erosemberg.pkgo.util.Log;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Erik Rosemberg
 * @since 8/11/16
 */
public class PokeWalkBackTask implements Runnable {

    private PokemonGo go;

    private ScheduledFuture<?> self;

    public PokeWalkBackTask(PokemonGo go) {
        this.go = go;
    }

    @Override
    public void run() {
        Lat2Long initial = Helper.getInstance().getStartLocation();
        Log.debug("Initial latitude: " + initial.getLatitude().get() + " initial longitude: " + initial.getLongitude().get());
        Log.debug("Current latitude: " + go.getLatitude() + " current longitude: " + go.getLongitude());
        S2LatLng end = S2LatLng.fromDegrees(initial.getLatitude().get(), initial.getLongitude().get());
        S2LatLng start = S2LatLng.fromDegrees(go.getLatitude(), go.getLongitude());

        S2LatLng difference = end.sub(start);
        double distance = start.getEarthDistance(end);
        Log.debug("Distance to initial: " + distance);
        if (distance <= 2000) {
            return;
        }

        double time = distance / 4; //4 Meters every 200 milliseconds.
        double delta = 200D / 1000D;
        double thing = time / delta;
        final long[] steps = {Math.round(thing)};
        if (steps[0] == 0) {
            return;
        }

        Log.green("Starting walk back task to initial location... ~" + Double.valueOf(distance).intValue() + "m away (ETA: " + Double.valueOf(time).intValue() + "s)");

        self = Helper.getInstance().schedule(new Runnable() {
            long initialSteps = steps[0];
            @Override
            public void run() {
                double lat = difference.latDegrees() / initialSteps;
                double longi = difference.lngDegrees() / initialSteps;

                double lat1 = initial.getLatitude().addAndGet(-lat);
                double longi1 = initial.getLongitude().addAndGet(-longi);

                go.setLocation(lat1, longi1, 1.0);
                S2LatLng current = S2LatLng.fromDegrees(go.getLatitude(), go.getLongitude());
                double d = current.getEarthDistance(end);

                Log.debug("walkback distance = " + d);
                if (d <= 50) {
                    self.cancel(true);
                    PokeWalkTask.shouldWalk.set(true);
                }
            }
        }, 0L, 200L, TimeUnit.MILLISECONDS);
    }
}
