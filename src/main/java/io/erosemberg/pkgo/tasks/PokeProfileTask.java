package io.erosemberg.pkgo.tasks;

import POGOProtos.Enums.PokemonIdOuterClass;
import POGOProtos.Networking.Responses.ReleasePokemonResponseOuterClass;
import com.google.common.collect.Maps;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import io.erosemberg.pkgo.Helper;
import io.erosemberg.pkgo.util.Log;
import io.erosemberg.pkgo.util.PokemonUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Erik Rosemberg
 * @since 8/10/16
 */
public class PokeProfileTask implements Runnable {

    private PokemonGo go;

    public PokeProfileTask(PokemonGo go) {
        this.go = go;
    }

    @Override
    public void run() {
        try {
            go.getInventories().updateInventories(true); //Force inventory update.
            Map<PokemonIdOuterClass.PokemonId, Integer> countByPokemon = Maps.newHashMap();
            List<Pokemon> pokemons = go.getInventories().getPokebank().getPokemons();
            Collections.sort(pokemons, (o1, o2) -> Integer.compare(o1.getCp(), o2.getCp()));

            for (Pokemon pokemon : pokemons) {
                countByPokemon.put(pokemon.getPokemonId(), countByPokemon.getOrDefault(pokemon.getPokemonId(), 0) + 1);
                if (countByPokemon.get(pokemon.getPokemonId()) <= 1) {
                    continue;
                }
                if (pokemon.getCp() >= 400) {
                    continue;
                }
                boolean should = !pokemon.isFavorite() || !pokemon.getNickname().isEmpty() || !pokemon.getDeployedFortId().isEmpty();
                if (should) {
                    countByPokemon.put(pokemon.getPokemonId(), countByPokemon.getOrDefault(pokemon.getPokemonId(), 1) - 1);
                    ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result result = pokemon.transferPokemon();
                    if (result != ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result.SUCCESS) {
                        Log.red("Failed to transfer " + pokemon.getPokemonId() + ", " + result);
                    } else {
                        Log.green("Transferred " + pokemon.getPokemonId() + " with " + pokemon.getCp() + "CP and " + PokemonUtil.getIV(pokemon.getProto()) + "IV");
                    }
                }
            }
        } catch (LoginFailedException | RemoteServerException e) {
            e.printStackTrace();
        }
    }
}
