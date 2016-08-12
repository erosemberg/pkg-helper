package io.erosemberg.pkgo.tasks;

import POGOProtos.Data.PokemonDataOuterClass;
import POGOProtos.Networking.Responses.CatchPokemonResponseOuterClass;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.encounter.DiskEncounterResult;
import com.pokegoapi.api.map.pokemon.encounter.EncounterResult;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;
import io.erosemberg.pkgo.Helper;
import io.erosemberg.pkgo.util.Log;
import io.erosemberg.pkgo.util.PokemonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Erik Rosemberg
 * @since 8/10/16
 */
public class PokeFinderTask implements Runnable {

    private PokemonGo go;
    private AtomicInteger softBanConuter = new AtomicInteger(0);
    private boolean shouldFind = true;

    public PokeFinderTask(PokemonGo go) {
        this.go = go;
    }

    @Override
    public void run() {
        if (!shouldFind) return;
        try {
            Log.info("Scanning for catchable pokemons...");
            List<CatchablePokemon> catchable = go.getMap().getCatchablePokemon();

            if (catchable.size() > 0) {
                Log.debug("Found " + catchable.size() + " catchable pokemons.");
                for (CatchablePokemon max : catchable) {
                    Log.green("Found a " + max.getPokemonId());
                    EncounterResult result = max.encounterPokemon();
                    if (result.wasSuccessful()) {
                        PokemonDataOuterClass.PokemonData data = result.getPokemonData();
                        Log.blue("Encountered a " + data.getPokemonId() + " with " + data.getCp() + "CP and " + PokemonUtil.getIV(data) + "IV");

                        CatchResult catchResult = max.catchPokemonBestBallToUse(result, new ArrayList<>(), -1, 1);

                        if (!catchResult.isFailed()) {
                            softBanConuter.set(0);
                            int xp = catchResult.getXpList().stream().mapToInt(Integer::intValue).sum();
                            int stardust = catchResult.getStardustList().stream().mapToInt(Integer::intValue).sum();
                            Log.green("Caught a " + data.getPokemonId() + " with " + data.getCp() + "CP " + (result instanceof DiskEncounterResult ? "(lured)" : "(wild)") + ", earned " + xp + "xp and " + stardust + " stardust!");
                        } else {
                            if (catchResult.getStatus() == CatchPokemonResponseOuterClass.CatchPokemonResponse.CatchStatus.CATCH_FLEE) {
                                int counter = softBanConuter.addAndGet(1);
                                if (counter >= 5) {
                                    shouldFind = false;
                                    Log.red("Possible soft-ban met, stopping the finder task for 2 minutes...");
                                    Helper.getInstance().scheduleLater(() -> {
                                        shouldFind = true;
                                        softBanConuter.set(0);
                                    }, 2, TimeUnit.MINUTES);
                                }
                            }
                            Log.red("Failed to catch " + data.getPokemonId() + ", " + catchResult.getStatus());
                        }
                    } else {
                        Log.red("Failed to encounter " + max.getPokemonId() + ", " + result.getStatus());
                    }
                }
            }
        } catch (LoginFailedException | RemoteServerException | NoSuchItemException ignored) {
        }
    }
}
