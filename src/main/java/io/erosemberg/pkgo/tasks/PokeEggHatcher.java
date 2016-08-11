package io.erosemberg.pkgo.tasks;

import POGOProtos.Networking.Responses.UseItemEggIncubatorResponseOuterClass;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.inventory.EggIncubator;
import com.pokegoapi.api.pokemon.EggPokemon;
import com.pokegoapi.api.pokemon.HatchedEgg;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import io.erosemberg.pkgo.util.Log;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Erik Rosemberg
 * @since 8/10/16
 */
public class PokeEggHatcher implements Runnable {

    private PokemonGo go;

    public PokeEggHatcher(PokemonGo go) {
        this.go = go;
    }

    @Override
    public void run() {
        try {
            List<HatchedEgg> eggs = go.getInventories().getHatchery().queryHatchedEggs();
            if (!eggs.isEmpty()) {
                go.getInventories().updateInventories(true);

                for (HatchedEgg egg : eggs) {
                    Pokemon pokemon = go.getInventories().getPokebank().getPokemonById(egg.getId());
                    if (pokemon != null) {
                        Log.green("Hatched a " + pokemon.getPokemonId() + " for " + egg.getExperience() + "XP and " + egg.getStardust() + " stardust and " + egg.getCandy() + " candy");
                    }
                }
            }

            List<EggIncubator> incubators = go.getInventories().getIncubators().stream().filter(eggIncubator -> {
                try {
                    return !eggIncubator.isInUse();
                } catch (LoginFailedException | RemoteServerException e) {
                    e.printStackTrace();
                    return false;
                }
            }).collect(Collectors.toList());
            List<EggPokemon> eggPokemons = go.getInventories().getHatchery().getEggs().stream().filter(eggPokemon -> !eggPokemon.isIncubate()).collect(Collectors.toList());
            if (!incubators.isEmpty() && !eggPokemons.isEmpty()) {
                EggPokemon pokemon = eggPokemons.stream().findFirst().get();
                UseItemEggIncubatorResponseOuterClass.UseItemEggIncubatorResponse.Result result = pokemon.incubate(incubators.stream().findFirst().get());
                if (result == UseItemEggIncubatorResponseOuterClass.UseItemEggIncubatorResponse.Result.SUCCESS) {
                    Log.green("Placed an egg in an incubator, need to walk " + pokemon.getEggKmWalkedTarget() + "km");
                } else {
                    Log.red("Failed to place egg in incubator, " +  result);
                }
            }
        } catch (RemoteServerException | LoginFailedException e) {
            e.printStackTrace();
        }
    }
}
