package io.erosemberg.pkgo.tasks;

import com.pokegoapi.api.PokemonGo;

/**
 * @author Erik Rosemberg
 * @since 8/10/16
 */
public class PokeWalkTask implements Runnable {

    private PokemonGo go;

    public PokeWalkTask(PokemonGo go) {
        this.go = go;
    }

    @Override
    public void run() {

    }
}
