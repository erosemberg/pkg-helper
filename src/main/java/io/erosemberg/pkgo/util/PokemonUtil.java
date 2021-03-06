package io.erosemberg.pkgo.util;

import POGOProtos.Data.PokemonDataOuterClass;
import com.pokegoapi.api.map.fort.FortDetails;

/**
 * @author Erik Rosemberg
 * @since 8/11/16
 */
public class PokemonUtil {

    public static int getIV(PokemonDataOuterClass.PokemonData pokemon) {
        return pokemon.getIndividualStamina() + pokemon.getIndividualAttack() + pokemon.getIndividualDefense();
    }

    public static int getIVPercent(PokemonDataOuterClass.PokemonData pokemon) {
        int iv = getIV(pokemon);
        return (iv * 100) / 45;
    }

    public static String getName(FortDetails details) {
        return details.getName();
    }

}
