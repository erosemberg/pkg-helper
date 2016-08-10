package io.erosemberg.pkgo.tasks;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.NearbyPokemon;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import io.erosemberg.pkgo.util.Log;

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
            List<NearbyPokemon> catchable = go.getMap().getNearbyPokemon();

            if (catchable.size() > 0) {
                Log.green("Found " + catchable.size() + " catchable pokemons.");
                for (NearbyPokemon pokemon : catchable) {
                    Log.debug("Found a nearby pokemon " + pokemon.getPokemonId().name() + ", " + pokemon.getDistanceInMeters() + "m away");
                }
            } else {
                Log.red("Did not find any pokemons to catch :(.");
            }
        } catch (LoginFailedException | RemoteServerException e) {
            e.printStackTrace();
        }
    }
}
