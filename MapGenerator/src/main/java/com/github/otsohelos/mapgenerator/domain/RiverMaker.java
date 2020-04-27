package com.github.otsohelos.mapgenerator.domain;

import java.util.ArrayList;

/**
 *
 * @author otsohelos
 */
public class RiverMaker {
    // this class is seroiusly under construction and doesn't work yet

    private final int[][] intArray;
    private final boolean[][] riverArray;
    private final Tile[][] tileArray;
    private final int height;
    private final int width;
    private ArrayList<Integer> heightsList;
    private final Randomizer rzr;
    private final int[][] localHeights;
    private boolean[][] riverArrayLarge;
    private int[] riversReady;
    private boolean hasRivers;

    public RiverMaker(int[][] intArray, Tile[][] tileArray) {
        this.intArray = intArray;
        this.height = intArray.length;
        this.width = intArray[0].length;
        this.heightsList = new ArrayList<>();
        this.rzr = new Randomizer();
        this.riverArray = new boolean[height][width];
        this.tileArray = tileArray;
        this.localHeights = new int[height / 4][width / 4];
        this.riverArrayLarge = new boolean[height / 4][width / 4];
        this.riversReady = new int[3];
        this.hasRivers = false;

        //System.out.println("local heights:");
        for (int i = 0; i < height / 4; i++) {
            for (int j = 0; j < width / 4; j++) {
                int localHeight = calculateHeight(i * 4, j * 4, i * 4 + 4, j * 4 + 4);
                localHeights[i][j] = localHeight;
                //System.out.print(localHeight + " ");
            }
            //System.out.println("");
        }
    }

    public void makeRivers() {
        // make array of coordinates and heights for highest large quares: i, j, height
        int[][] threeHighestLarge = new int[3][3];

        // divide intArray to 24 * 24 squares and find highest ones
        int heightBy24 = height / 24;
        int widthBy24 = width / 24;

        int smallestHigh = 0;

        for (int i = 0; i < heightBy24; i++) {
            for (int j = 0; j < widthBy24; j++) {
                int localHeight = calculateHeight(i * 24, j * 24, i * 24 + 24, j * 24 + 24);
                //System.out.println("local height for " + i + ", " + j + " is " + localHeight);

                if (localHeight > smallestHigh) {
                    // find smallest of the three peaks, replace it with this one
                    for (int k = 0; k < 3; k++) {
                        if (threeHighestLarge[k][2] == smallestHigh) {
                            threeHighestLarge[k][2] = localHeight;
                            threeHighestLarge[k][0] = i;
                            threeHighestLarge[k][1] = j;
                            smallestHigh = Math.min(threeHighestLarge[0][2], (Math.min(threeHighestLarge[1][2], threeHighestLarge[2][2])));
                            break;
                        }
                    }
                }
            }
        }
        //System.out.println("highest peaks are:");
        for (int i = 0; i < 3; i++) {
            //System.out.println(threeHighestLarge[i][2] + " at " + threeHighestLarge[i][0] + ", " + threeHighestLarge[i][1]);
        }

        int[][] threeHighestSmall = new int[3][3];

        // find highest 4x4 square in each large square
        for (int k = 0; k < 3; k++) {
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    int startI = threeHighestLarge[k][0] * 6 + i;
                    int startJ = threeHighestLarge[k][1] * 6 + j;
                    int localHeight = localHeights[threeHighestLarge[k][0] * 6][threeHighestLarge[k][1] * 6];
                    if (threeHighestSmall[k][2] < localHeight) {
                        threeHighestSmall[k][0] = startI;
                        threeHighestSmall[k][1] = startJ;
                        threeHighestSmall[k][2] = localHeight;
                    }
                }
            }
        }

        //System.out.println("highest small peaks are:");
        for (int i = 0; i < 3; i++) {
            //System.out.println(threeHighestSmall[i][2] + " at " + threeHighestSmall[i][0] + ", " + threeHighestSmall[i][1]);
        }

        for (int i = 0; i < 3; i++) {
            if (threeHighestSmall[i][2] != 0) {
                int[][] route = new int[100][2];
                boolean[][] riverProgression = new boolean[height / 4][width / 4];
                route[0][0] = threeHighestSmall[i][0];
                route[0][1] = threeHighestSmall[i][1];
                riverProgression[route[0][0]][route[0][1]] = true;
                //System.out.println("making route from " + threeHighestSmall[i][0] + ", " + threeHighestSmall[i][1]);
                makeRoute(threeHighestSmall[i][0], threeHighestSmall[i][1], i, route, 0, riverProgression);
            }
        }
    }

    private int calculateHeight(int startI, int startJ, int endI, int endJ) {
        int sum = 0;
        for (int i = startI; i < endI; i++) {
            for (int j = startJ; j < endJ; j++) {
                sum = sum + intArray[i][j];
            }
        }
        return sum;
    }

    public void makeRoute(int i, int j, int startPoint, int[][] route, int routeIndex, boolean[][] riverProgression) {

        // make a maximum of 1 river from each starting point
        if (riversReady[startPoint] > 0) {
            return;
        }

        // set growth direction
        int dir = rzr.randomize(4);
        //System.out.println("first dir: " + dir);
        for (int tries = 0; tries < 4; tries++) {
            // depending on dir, calculate coordinates for where to advance
            int k = i + dir / 2 * (dir % 3 - 1); // produces i plus 0, 0, 1, -1
            int l = j + ((dir + 2) % 4) / 2 * ((dir + 2) % 3 - 1); // produces j plus 1, -1, 0, 0
            dir++;
            if (dir == 4) {
                dir = 0;
            }
            // grow only to squares within bounds that aren't already river
            if (k >= 0 && l >= 0 && k < height / 4 && l < width / 4 && riverProgression[k][l] == false) {
                // grow only downstream-ish
                if (localHeights[i][j] > localHeights[k][l] - 10) {
                    // if we're not in water already then grow recursively
                    if (!(k == 0 || l == 0 || k == height / 4 - 1 || l == width / 4 - 1)) {
                        //System.out.println("growing from " + i + ", " + j + " to " + k + ", " + l);
                        route[routeIndex + 1][0] = k;
                        route[routeIndex + 1][1] = l;
                        riverProgression[k][l] = true;
                        makeRoute(k, l, startPoint, route, routeIndex + 1, riverProgression);
                    } else if (routeIndex > 3) {
                        route[routeIndex + 1][0] = k;
                        route[routeIndex + 1][1] = l;
                        riversReady[startPoint]++;
                        hasRivers = true;
                        makeRiver(route, routeIndex + 1);
                    }
                }
            }
        }
    }

    public void makeRiver(int[][] route, int routeIndex) {
        // determine start point
        int currentK = route[0][0] * 4 + rzr.randomize(4);
        int currentL = route[0][1] * 4 + rzr.randomize(4);

        //System.out.println("river detail:");
        for (int i = 0; i < routeIndex; i++) {
            System.out.println("square: " + route[i][0] + ", " + route[i][1]);
            int kDir = 0;
            int lDir = 0;
            if (i < routeIndex) {
                kDir = route[i + 1][0] - route[i][0];
                lDir = route[i + 1][1] - route[i][1];
            }

            // while we're in this 4x4 square
            while (currentK >= route[i][0] * 4 && currentK < route[i][0] * 4 + 4 && currentL >= route[i][1] * 4 && currentL < route[i][1] * 4 + 4) {
                int whereToGo = rzr.randomize(2);
                if (kDir == 0) {
                    if (rzr.randomize(4) < 1) {
                        currentK = route[i][0] * 4 + 1 + whereToGo;
                    }
                } else {
                    currentK = currentK + kDir;
                }
                if (lDir == 0) {
                    if (rzr.randomize(4) < 1) {
                        currentL = route[i][1] * 4 + 2 - whereToGo;
                    }
                } else {
                    currentL = currentL + lDir;
                }
                riverArray[currentK][currentL] = true;
                if (!tileArray[currentK][currentL].isWater() && currentK >= 0 && currentL >= 0 && currentK < height && currentL < width) {
                    tileArray[currentK][currentL].setRiver();
                } else {
                    return;
                }
                System.out.println(currentK + ", " + currentL);
            }
        }
    }

    public Tile[][] getTileArray() {
        return tileArray;
    }

    public boolean hasRivers() {
        return this.hasRivers;
    }
}
