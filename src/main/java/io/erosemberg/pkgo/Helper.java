package io.erosemberg.pkgo;

import POGOProtos.Data.PlayerDataOuterClass;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.fort.PokestopLootResult;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.auth.GoogleAutoCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import io.erosemberg.pkgo.tasks.PokeEggHatcher;
import io.erosemberg.pkgo.tasks.PokeFinderTask;
import io.erosemberg.pkgo.tasks.PokeProfileTask;
import io.erosemberg.pkgo.tasks.PokeStopTask;
import io.erosemberg.pkgo.tasks.PokeWalkTask;
import io.erosemberg.pkgo.util.Lat2Long;
import io.erosemberg.pkgo.util.Log;
import okhttp3.OkHttpClient;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static io.erosemberg.pkgo.tasks.PokeWalkTask.shouldWalk;

/**
 * @author Erik Rosemberg
 * @since 8/10/16
 */
public final class Helper {

    private static Helper self = new Helper();

    private final ScheduledExecutorService service;
    private Lat2Long location;
    private PokemonGo go;
    private String user;

    private Helper() {
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
            user = properties.getProperty("email");
            String password = properties.getProperty("password");
            double latitude = Double.valueOf(properties.getProperty("latitude"));
            double longitude = Double.valueOf(properties.getProperty("longitude"));

            this.location = new Lat2Long(latitude, longitude);

            go = new PokemonGo(new GoogleAutoCredentialProvider(client, user, password), client);
            go.setLocation(latitude, longitude, 1);
            Log.green("Logged in successfully...initiating");
            Log.info("====================================================");
            Log.info("PokemonGo Helper version 1.0-ALPHA has initiated.");
            Log.info("to turn on debug, run the jar again with the -d flag");
            Log.info("====================================================");
            Log.info("Showing player profile data...");
            printProfileInfo();

            schedule(new PokeWalkTask(go, location), 0L, 1L, TimeUnit.SECONDS);
            schedule(new PokeFinderTask(go), 0L, 5L, TimeUnit.SECONDS);
            schedule(new PokeStopTask(go), 30L, 30L, TimeUnit.SECONDS);
            schedule(new PokeProfileTask(go), 1L, 1L, TimeUnit.MINUTES);
            schedule(new PokeEggHatcher(go), 0L, 1L, TimeUnit.MINUTES);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    Log.red("Handling shutdown...");
                }
            });
        } catch (LoginFailedException | RemoteServerException e) {
            Log.red(e.getMessage());
            e.printStackTrace();
        }
    }

    public ScheduledFuture<?> schedule(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        Log.debug("Scheduling " + runnable.getClass().getSimpleName() + " at a " + period + " " + unit.name() + " period.");
        return service.scheduleAtFixedRate(runnable, initialDelay, period, unit);
    }

    public void loot(Pokestop closest) throws LoginFailedException, RemoteServerException {
        if (closest.canLoot()) {
            Log.debug("Pausing walking task to loot pokestop...");
            shouldWalk.set(false);
            Log.green("Going to loot pokestop " + closest.getId());
            PokestopLootResult result = closest.loot();
            if (result == null) {
                shouldWalk.set(true);
                return;
            }

            if (result.wasSuccessful()) {
                if (result.getExperience() == 0 && result.getItemsAwarded().isEmpty()) {
                    Log.debug("Found a possible ban, spinning it a lot of times to evade...");
                    IntStream.range(0, 40).forEach(value -> {
                        try {
                            closest.loot();
                        } catch (LoginFailedException | RemoteServerException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    Log.green("Looted pokestop for " + result.getExperience() + " exp and " + result.getItemsAwarded() + ".");
                }
            }

            Log.debug("Resuming walking task...");
            shouldWalk.set(true);
        } else {
            if (PokeWalkTask.target == null) {
                PokeWalkTask.target = closest;
            }
            Log.red("Failed to loot pokestop, reason was " + closest.loot().getResult());
        }
    }

    public void printProfileInfo() {
        try {
            go.getPlayerProfile().updateProfile(); //Force update.
            PlayerProfile profile = go.getPlayerProfile();
            PlayerDataOuterClass.PlayerData data = profile.getPlayerData();
            Log.blue("Name: " + data.getUsername() + ", Email: " + user);
            Log.blue("Team: " + data.getTeam().name() + ", Creation Date: " + new Date(data.getCreationTimestampMs()).toString());
            Log.blue("Level: " + profile.getStats().getLevel() + ", XP: " + profile.getStats().getExperience() + "/" + profile.getStats().getNextLevelXp());
            Log.blue("PokeCoins: " + profile.getCurrency(PlayerProfile.Currency.POKECOIN) + ", Stardust: " + profile.getCurrency(PlayerProfile.Currency.STARDUST) + ", Caught Pokemons: " + profile.getStats().getPokemonsCaptured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Helper getInstance() {
        return self;
    }

}
