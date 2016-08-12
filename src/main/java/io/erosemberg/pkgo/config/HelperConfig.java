package io.erosemberg.pkgo.config;

import io.erosemberg.pkgo.util.Config;
import io.erosemberg.pkgo.util.annotation.ConfigPath;

/**
 * @author Erik Rosemberg
 * @since 8/11/16
 */
@SuppressWarnings("all")
@ConfigPath("settings.json")
public class HelperConfig extends Config {

    private String email = "change@me.com";
    private String password = "changeme";

    private double latitude = 0.0D;
    private double longitude = 0.0D;
    private double minDistanceToLootPokestop = 10.0D;

    private int minNearbyPokemonsForIncense = 3;
    private boolean useIncense = false;
    private boolean evolvePokemons = false;
    private boolean useLuckyEggForEvolution = false;
    private int minCPForEvolution = 400;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getMinDistanceToLootPokestop() {
        return minDistanceToLootPokestop;
    }

    public int getMinNearbyPokemonsForIncense() {
        return minNearbyPokemonsForIncense;
    }

    public boolean isUseIncense() {
        return useIncense;
    }

    public boolean isEvolvePokemons() {
        return evolvePokemons;
    }

    public boolean isUseLuckyEggForEvolution() {
        return useLuckyEggForEvolution;
    }

    public int getMinCPForEvolution() {
        return minCPForEvolution;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
