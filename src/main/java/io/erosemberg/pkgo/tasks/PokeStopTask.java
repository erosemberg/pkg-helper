package io.erosemberg.pkgo.tasks;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.MapObjects;
import com.pokegoapi.api.map.fort.Pokestop;
import io.erosemberg.pkgo.Helper;
import io.erosemberg.pkgo.util.Log;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Erik Rosemberg
 * @since 8/10/16
 */
public class PokeStopTask implements Runnable {

    private PokemonGo go;
    private Cache<String, Long> recentlyLooted = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

    public PokeStopTask(PokemonGo go) {
        this.go = go;
    }

    @Override
    public void run() {
        if (!PokeWalkTask.shouldWalk.get()) {
            return;
        }
        try {
            Log.info("Scanning for pokestops...");
            MapObjects objects = go.getMap().getMapObjects();
            Collection<Pokestop> pokestops = objects.getPokestops().stream().filter(pokestop -> pokestop.getCooldownCompleteTimestampMs() < System.currentTimeMillis()).collect(Collectors.toList());

            if (!pokestops.isEmpty()) {
                Collections.sort(pokestops.stream().collect(Collectors.toList()), (o1, o2) -> Double.compare(o1.getDistance(), o2.getDistance()));
                List<Pokestop> pokestopList = pokestops.stream().collect(Collectors.toList());
                Pokestop pokestop = pokestopList.remove(0);
                if (recentlyLooted.getIfPresent(pokestop.getId()) == null) {
                    recentlyLooted.put(pokestop.getId(), System.currentTimeMillis());
                    Helper.getInstance().loot(pokestop);
                }
            } else {
                Log.debug("No pokestops found nearby...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
