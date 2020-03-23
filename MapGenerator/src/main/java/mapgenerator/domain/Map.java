/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapgenerator.domain;

import java.util.Random;

/**
 *
 * @author otsohelos
 */
public class Map {

    private int height;
    private int width;
    private int[][] mapArray;
    private Tile[][] tileArray;

    public Map(int height, int width) {
        this.height = height;
        this.width = width;

        this.mapArray = new int[width][height];
        this.tileArray = new Tile[width][height];
    }

    public Tile[][] show() {
        Random rnd = new Random();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rndInt = (rnd.nextInt(10));
                mapArray[i][j] = rndInt;
                tileArray[i][j] = new Tile(rndInt);
            }
        }

        for (int i = 0; i < width; i++) {
            System.out.println("");
            for (int j = 0; j < height; j++) {

                System.out.print(mapArray[i][j] + " ");
            }
        }
        return tileArray;
    }
}
