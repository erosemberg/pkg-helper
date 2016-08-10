package io.erosemberg.pkgo;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.NearbyPokemon;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.auth.GoogleAutoCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import io.erosemberg.pkgo.tasks.PokeFinderTask;
import io.erosemberg.pkgo.util.Log;
import okhttp3.OkHttpClient;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Erik Rosemberg
 * @since 8/10/16
 */
final class Helper {

    private final ScheduledExecutorService service;

    Helper() {
        service = Executors.newScheduledThreadPool(3, new ThreadFactory() {
            private AtomicInteger counter = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                String name = "Thread " + counter.addAndGet(1);
                Log.debug("Starting " + name);
                return new Thread(r, name);
            }
        });
    }

    void init(Properties properties) {
        OkHttpClient client = new OkHttpClient();
        try {
            Log.green("Starting PokemonGo-Helper by erosemberg (Erik Rosemberg)");
            if (properties == null) {
                Log.red("config.properties does not exist!");
                return;
            }
            String user = properties.getProperty("email");
            String password = properties.getProperty("password");
            double latitude = Double.valueOf(properties.getProperty("latitude"));
            double longitude = Double.valueOf(properties.getProperty("longitude"));

            PokemonGo go = new PokemonGo(new GoogleAutoCredentialProvider(client, user, password), client);
            go.setLocation(latitude, longitude, 1);
            Log.green("Logged in successfully...initiating");
            PlayerProfile profile = go.getPlayerProfile();
            Log.info("====================================================");
            Log.info("PokemonGo Helper version 1.0-ALPHA has initiated.");
            Log.info("to turn on debug, run the jar again with the -d flag");
            Log.info("====================================================");
            //TODO: Show profile input on load.

            schedule(new PokeFinderTask(go), 0L, 5L, TimeUnit.SECONDS);
        } catch (LoginFailedException | RemoteServerException e) {
            Log.red(e.getMessage());
            e.printStackTrace();
        }
    }

    public void schedule(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        Log.debug("Scheduling " + runnable.getClass().getSimpleName() + " at a " + period + " " + unit.name() + " period.");
        service.scheduleAtFixedRate(runnable, initialDelay, period, unit);
    }

}
