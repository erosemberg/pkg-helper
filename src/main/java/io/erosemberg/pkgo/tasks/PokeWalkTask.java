package io.erosemberg.pkgo.tasks;

import POGOProtos.Map.Fort.FortDataOuterClass;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.MapObjects;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.fort.PokestopLootResult;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.google.common.geometry.S2LatLng;
import io.erosemberg.pkgo.Helper;
import io.erosemberg.pkgo.util.Lat2Long;
import io.erosemberg.pkgo.util.Log;
import org.omg.PortableInterceptor.SUCCESSFUL;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Erik Rosemberg
 * @since 8/10/16
 */
public class PokeWalkTask implements Runnable {

    private PokemonGo go;
    private Lat2Long lat2Long;
    public static AtomicBoolean shouldWalk = new AtomicBoolean(true);

    public static Pokestop target;
    private ScheduledFuture<?> self;

    public PokeWalkTask(PokemonGo go, Lat2Long lat2Long) {
        this.go = go;
        this.lat2Long = lat2Long;
    }

    @Override
    public void run() {
        if (!shouldWalk.get()) {
            return;
        }

        if (target != null) {
            shouldWalk.set(false); //Stop walking here.
            Log.debug("Target was not null..processing...");
            S2LatLng end = S2LatLng.fromDegrees(target.getLatitude(), target.getLongitude());
            S2LatLng start = S2LatLng.fromDegrees(lat2Long.getLatitude().get(), lat2Long.getLongitude().get());
            S2LatLng difference = end.sub(start);
            double distance = start.getEarthDistance(end);
            double time = distance / 2.8;
            double delta = 200D / 1000D;
            double thing = time / delta;
            final long[] steps = {Math.round(thing)};
            if (steps[0] == 0) {
                return;
            }
            Log.debug("Walking to stop in " + steps[0] + " steps.");

            self = Helper.getInstance().schedule(new Runnable() {
                long initialSteps = steps[0];
                @Override
                public void run() {
                    double lat = difference.latDegrees() / initialSteps;
                    double longi = difference.lngDegrees() / initialSteps;

                    double lat1 = lat2Long.getLatitude().addAndGet(lat);
                    double longi1 = lat2Long.getLongitude().addAndGet(longi);

                    go.setLocation(lat1, longi1, 1.0);
                    S2LatLng current = S2LatLng.fromDegrees(go.getLatitude(), go.getLongitude());
                    double d = current.getEarthDistance(end);
                    initialSteps--;
                    Log.debug("Ran, steps = " + initialSteps + ", distance = " + d);
                    if (d < 10) {
                        Log.green("Reached stop before what was calculated!");
                        initialSteps = 0;
                    }

                    if (initialSteps % 20 == 0) {
                        Log.debug("Reaching stop in " + initialSteps + " steps");
                    }
                    if (initialSteps == 0) {
                        Log.green("Reached stop, it will loot soon.");
                        try {
                            Helper.getInstance().loot(target);
                        } catch (LoginFailedException | RemoteServerException e) {
                            e.printStackTrace();
                        }
                        shouldWalk.set(true);
                        target = null;
                        self.cancel(true);
                        return;
                    }
                    shouldWalk.set(false);
                }
            }, 0L, 200L, TimeUnit.MILLISECONDS);
        }

        //Walk randomly around the map to find pokestops.
        double lat = lat2Long.getLatitude().addAndGet(getRandomDirection());
        double longi = lat2Long.getLongitude().addAndGet(getRandomDirection());

        go.setLocation(lat, longi, 1.0);

        Log.debug("Walking to " + lat2Long.toString());
    }

    private double getRandomDirection() {
        return Math.random() * 0.0001 - 0.00005;
    }
}
