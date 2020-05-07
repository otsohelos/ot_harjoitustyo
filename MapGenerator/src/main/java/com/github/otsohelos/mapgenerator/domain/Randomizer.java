
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
     * Returns random non-negative integer that is under specified limit.
     * @param limit
     * @return
     */
    public int randomize(int limit) {
        return rnd.nextInt(limit);
    }

    /**
     * Returns whether a random nonnegative integer under specified limit is smaller than another specified integer.
     * @param limit Upper bound for Random
     * @param cut Random number is compared to this
     * @return Boolean
     */
    public boolean isSmaller(int limit, int cut) {
        return rnd.nextInt(limit) < cut;
    }
    
    /**
     * Returns a random nonnegative integer under a specified limit plus another specified integer.
     * @param limit Limit for random number
     * @param addition
     * @return int
     */
    public int randomizePlus(int limit, int addition) {
        return rnd.nextInt(limit) + addition;
    }

}
