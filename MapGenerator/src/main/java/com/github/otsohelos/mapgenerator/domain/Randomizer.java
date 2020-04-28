
package com.github.otsohelos.mapgenerator.domain;

import java.util.Random;

/**
 * Helper class for random operations.
 * @author otsohelos
 */
class Randomizer {

    private Random rnd;

    public Randomizer() {
        this.rnd = new Random();
    }

    /**
     * Returns int under limit.
     * @param limit
     * @return int
     */
    public int randomize(int limit) {
        return rnd.nextInt(limit);
    }

    /**
     * Returns whether a random number is smaller than a given number.
     * @param limit Upper bound for Random
     * @param cut Random number is compared to this
     * @return Boolean
     */
    public boolean isSmaller(int limit, int cut) {
        return rnd.nextInt(limit) < cut;
    }
    
    /**
     * Returns a random number plus specified number
     * @param limit Limit for random number
     * @param addition
     * @return int
     */
    public int randomizePlus(int limit, int addition) {
        return rnd.nextInt(limit) + addition;
    }

}
