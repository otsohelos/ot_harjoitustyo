/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapgenerator.domain;

import java.util.Random;

class Randomizer {

    private Random rnd;

    public Randomizer() {
        this.rnd = new Random();
    }

    public int randomize(int limit) {
        return rnd.nextInt(limit);
    }

    public boolean isSmaller(int limit, int cut) {
        return rnd.nextInt(limit) < cut;
    }
    
    public int randomizePlus(int limit, int addition) {
        return rnd.nextInt(limit) + addition;
    }

}
