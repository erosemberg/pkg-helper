package io.erosemberg.pkgo.tasks;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.MapObjects;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.fort.PokestopLootResult;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.util.SystemTimeImpl;
import io.erosemberg.pkgo.Helper;
import io.erosemberg.pkgo.util.Log;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.erosemberg.pkgo.tasks.PokeWalkTask.shouldWalk;

/**
 * @author Erik Rosemberg
 * @since 8/10/16
 */
public class PokeStopTask implements Runnable {

    private PokemonGo go;
    private Cache<String, Long> recentlyLooted = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.MINUTES).build();

    public PokeStopTask(PokemonGo go) {
        this.go = go;
    }

    @Override
    public void run() {
        try {
            Log.debug("Checking for pokestops...");
            MapObjects objects = go.getMap().getMapObjects();
            Collection<Pokestop> pokestops = objects.getPokestops();
            if (!pokestops.isEmpty()) {
                Collections.sort(pokestops.stream().collect(Collectors.toList()), (o1, o2) -> Double.compare(o1.getDistance(), o2.getDistance()));
                Pokestop closest = pokestops.stream().findFirst().get();
                if (recentlyLooted.getIfPresent(closest.getId()) == null) {
                    recentlyLooted.put(closest.getId(), System.currentTimeMillis());
                    Helper.getInstance().loot(closest);
                } else {
                    Log.debug("Refound a pokestop that had already been found within 2 minutes.");
                }
            } else {
                Log.debug("No pokestops found nearby...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
