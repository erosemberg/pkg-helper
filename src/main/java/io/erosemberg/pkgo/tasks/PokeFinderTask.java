package io.erosemberg.pkgo.tasks;

import POGOProtos.Data.PokemonDataOuterClass;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.encounter.DiskEncounterResult;
import com.pokegoapi.api.map.pokemon.encounter.EncounterResult;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;
import io.erosemberg.pkgo.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Erik Rosemberg
 * @since 8/10/16
 */
public class PokeFinderTask implements Runnable {

    private PokemonGo go;

    public PokeFinderTask(PokemonGo go) {
        this.go = go;
    }

    @Override
    public void run() {
        try {
            List<CatchablePokemon> catchable = go.getMap().getCatchablePokemon();

            if (catchable.size() > 0) {
                Log.debug("Found " + catchable.size() + " catchable pokemons.");
                CatchablePokemon max = catchable.stream().findFirst().get();

                if (max == null) {
                    return;
                }

                Log.green("Found a " + max.getPokemonId());
                EncounterResult result = max.encounterPokemon();
                if (result.wasSuccessful()) {
                    PokemonDataOuterClass.PokemonData data = result.getPokemonData();
                    Log.blue("Encountered a " + data.getPokemonId() + " with " + data.getCp() + "CP");

                    CatchResult catchResult = max.catchPokemonBestBallToUse(result, new ArrayList<>(), -1, 1);

                    if (!catchResult.isFailed()) {
                        int xp = catchResult.getXpList().stream().mapToInt(Integer::intValue).sum();
                        int stardust = catchResult.getStardustList().stream().mapToInt(Integer::intValue).sum();
                        Log.green("Caught a " + data.getPokemonId() + " with " + data.getCp() + "CP " + (result instanceof DiskEncounterResult ? "(lured)" : "(wild)") + ", earned " + xp + "xp and " + stardust + " stardust!");
                    } else {
                        Log.red("Failed to catch " + data.getPokemonId() + ", " + catchResult.getStatus());
                    }
                } else {
                    Log.red("Failed to encounter " + max.getPokemonId() + ", " + result.getStatus());
                }
            }
        } catch (LoginFailedException | RemoteServerException | NoSuchItemException e) {
            Log.red("An error occurred: " + e.getMessage());
        }
    }
}
