package io.erosemberg.pkgo.tasks;

import POGOProtos.Networking.Responses.EvolvePokemonResponseOuterClass;
import POGOProtos.Networking.Responses.UseItemXpBoostResponseOuterClass;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.inventory.ItemBag;
import com.pokegoapi.api.map.pokemon.EvolutionResult;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import io.erosemberg.pkgo.Helper;
import io.erosemberg.pkgo.config.HelperConfig;
import io.erosemberg.pkgo.util.Log;
import io.erosemberg.pkgo.util.PokemonUtil;

import java.util.List;

/**
 * @author Erik Rosemberg
 * @since 8/11/16
 */
public class PokeEvolveTask implements Runnable {

    private PokemonGo go;

    public PokeEvolveTask(PokemonGo go) {
        this.go = go;
    }

    @Override
    public void run() {
        HelperConfig config = Helper.getInstance().getConfig();
        PlayerProfile profile = go.getPlayerProfile();
        try {
            profile.updateProfile();
            ItemBag bag = go.getInventories().getItemBag();
            List<Pokemon> pokemonList = go.getInventories().getPokebank().getPokemons();
            for (Pokemon pokemon : pokemonList) {
                int candies = pokemon.getCandy();
                int needed = pokemon.getCandiesToEvolve();
                if (candies >= needed) {
                    if (config.getMinCPForEvolution() <= pokemon.getCp()) {
                        Log.debug("Found an evolvable pokemon " + pokemon.getPokemonId() + " with " + pokemon.getCp() + "CP and " +  PokemonUtil.getIV(pokemon.getProto()) + "IV.");

                        if (config.isUseLuckyEggForEvolution()) {
                            UseItemXpBoostResponseOuterClass.UseItemXpBoostResponse response = bag.useLuckyEgg();
                            UseItemXpBoostResponseOuterClass.UseItemXpBoostResponse.Result result = response.getResult();

                            if (response.hasAppliedItems()) {
                                Log.green("Using a lucky egg to evolve pokemons!");
                            } else {
                                Log.debug("Failed to use lucky egg " + result);
                            }
                        }

                        Log.debug("Using " + needed + " " + pokemon.getPokemonId() + " candies to evolve with " + pokemon.getCp() + "CP");
                        EvolutionResult result = pokemon.evolve();
                        if (result.isSuccessful()) {
                            Log.green("Evolved " + pokemon.getPokemonId() + " to " + result.getEvolvedPokemon().getPokemonId() + " for " + result.getExpAwarded() + "xp and " + result.getCandyAwarded() + " candies! Evolution has " + result.getEvolvedPokemon().getCp() + "CP and " + PokemonUtil.getIV(result.getEvolvedPokemon().getProto()) + "IV");
                        } else {
                            if (result.getResult() == EvolvePokemonResponseOuterClass.EvolvePokemonResponse.Result.FAILED_INSUFFICIENT_RESOURCES || result.getResult() == EvolvePokemonResponseOuterClass.EvolvePokemonResponse.Result.FAILED_POKEMON_CANNOT_EVOLVE) {
                                return;
                            }
                            Log.red("Failed to evolve " + pokemon.getPokemonId() + " because " + result.getResult());
                        }
                    } else {
                        Log.debug("Not evolving " + pokemon.getPokemonId() + " because CP was lower than " + config.getMinCPForEvolution() + " (" + pokemon.getCp() + ").");
                    }
                }
            }

        } catch (RemoteServerException | LoginFailedException e) {
            e.printStackTrace();
        }
    }
}
