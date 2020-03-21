/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.Random;

/**
 *
 * @author otsohelos
 */
public class Map {

    private int height;
    private int width;
    private int[][] mapArray;

    public Map(int height, int width) {
        this.height = height;
        this.width = width;

        this.mapArray = new int[width][height];
    }

    public int[][] show() {
        Random rnd = new Random();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                mapArray[i][j] = (rnd.nextInt(10));
            }
        }

        for (int i = 0; i < width; i++) {
            System.out.println("");
            for (int j = 0; j < height; j++) {

                System.out.print(mapArray[i][j] + " ");
            }
        }
        return mapArray;
    }
}
