package com.github.otsohelos.mapgenerator.domain;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author otsohelos
 */
public class RiverMap {
    // this class is seroiusly under construction and doesn't work yet

    private int[][] intArray;
    private boolean[][] riverArray;
    private Tile[][] tileArray;
    private int height;
    private int width;
    private ArrayList<Integer> heightsList;
    private Randomizer rzr;

    public RiverMap(int[][] intArray, Tile[][] tileArray) {
        this.intArray = intArray;
        this.height = intArray.length;
        this.width = intArray[0].length;
        this.heightsList = new ArrayList<>();
        this.rzr = new Randomizer();
        this.riverArray = new boolean[height][width];
        this.tileArray = tileArray;
    }

    public void makeRivers() {
        int[][] localHeights = this.calculateLocalHeights();
        Collections.sort(heightsList);

        int highestPoint = heightsList.get(heightsList.size() - 1);

        int[][] route = new int[50][2];

        for (int i = 0; i < localHeights.length; i++) {
            for (int j = 0; j < localHeights[0].length; j++) {
                if (localHeights[i][j] == highestPoint) {
                    int k = rzr.randomize(10) + i * 10;
                    int l = rzr.randomize(10) + j * 10;
                    makeRoute(k, l, route, 0);
                }
            }
        }
// print river map:
//        for (int i = 0; i < height; i++) {
//            System.out.println("");
//            for (int j = 0; j < width; j++) {
//                if (riverArray[i][j] == false) {
//                    System.out.print("0 ");
//                } else {
//                    System.out.print("x ");
//                }
//            }
//        }
    }

    public void makeRoute(int i, int j, int[][] route, int routeIndex) {
        // try to grow several times
        int tries = 0;
        while (tries < 10) {
            // set growth direction
            int k = rzr.randomizePlus(3, i - 1);
            int l = rzr.randomizePlus(3, j - 1);
//            System.out.println("k " + k + ", l " + l);

            // grow only downstream-ish
            if (k >= 0 && l >= 0 && k < height && l < width && intArray[i][j] > intArray[k][l] + 2 && !(k == i && l == j)) {

                // if we're not in water already then grow recursively
                if (!tileArray[k][l].isWater()) {
                    route[routeIndex + 1][0] = k;
                    route[routeIndex + 1][1] = l;
                    routeIndex++;
                    makeRoute(k, l, route, routeIndex);
                } else {
                    System.out.println("profit!");
                    route[routeIndex + 1][0] = k;
                    route[routeIndex + 1][1] = l;
                    makeRiver(route, routeIndex);
                    break;
                }
            } else {
                tries++;
            }
        }
        System.out.println("failed :(");
    }

    public void makeRiver(int[][] route, int routeIndex) {
        System.out.println("yeah");

        for (int i = 0; i <= routeIndex; i++) {
            tileArray[route[i][0]][route[i][1]].setRiver();
        }
    }

    public int[][] calculateLocalHeights() {
        // go through map in 10x10 squares to find local peaks
        int tenthOfHeight = height / 10;
        int tenthOfWidth = width / 10;

        int[][] localHeights = new int[tenthOfHeight][tenthOfWidth];

        for (int i = 0; i < tenthOfHeight; i++) {
            for (int j = 0; j < tenthOfWidth; j++) {
                localHeights[i][j] = calculateLocalHeight(i, j);
                //System.out.print(localHeights[i][j] + "   ");
            }
            //System.out.println("");
        }

        return localHeights;
    }

    public int calculateLocalHeight(int i, int j) {
        int sum = 0;
        for (int k = i * 10; k < i * 10 + 10; k = k + 2) {
            for (int l = j * 10; l < j * 10 + 10; l = l + 2) {
                //System.out.println("k " + k + ", l " + l);
                sum = sum + intArray[k][l];
            }
        }
        this.heightsList.add(sum);
        return sum;
    }

    public void makeSpringHere(int i, int j, int localAvg) {
        System.out.println("making spring: " + i + ", " + j);
        boolean succeeded = false;
        while (!succeeded) {
            int k = rzr.randomize(10) + i * 10;
            int l = rzr.randomize(10) + j * 10;

            if (intArray[k][l] > localAvg) {
                System.out.println("starting river from " + k + ", " + l);
                System.out.println("elevation is " + intArray[k][l]);
                tileArray[k][l].setRiver();
                riverArray[k][l] = true;
                //System.out.println("succeeded!");
                succeeded = true;
                growRiver(k, l);
            }
        }

    }

    public void growRiver(int i, int j) {
        // try to grow several times
        int tries = 0;
        while (tries < 10) {
            // set growth direction
            int k = rzr.randomizePlus(3, i - 1);
            int l = rzr.randomizePlus(3, j - 1);
            System.out.println("k " + k + ", l " + l);

            // grow only downstream
            if (k >= 0 && l >= 0 && k < height && l < width && intArray[i][j] >= intArray[k][l] + 1) {

                // if we're not in water already then grow recursively
                if (!tileArray[k][l].isWater() && !tileArray[k][l].isRiver()) {
                    tileArray[k][l].setRiver();
                    riverArray[k][l] = true;
                    System.out.println("yay");
                    growRiver(k, l);
                    break;
                }
            } else {
                tries++;
            }
        }
    }

    public Tile[][] getTileArray() {
        return tileArray;
    }

}
