package com.github.otsohelos.mapgenerator.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

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
    private final Randomizer rzr;
    private final int[][] localHeights;
    private final int[][] routesReady;
    private boolean hasRivers;
    private final int howManyStarts;
    private ArrayList<int[][]> routes;

    public RiverMaker(int[][] intArray, Tile[][] tileArray) {
        this.intArray = intArray;
        this.height = intArray.length;
        this.width = intArray[0].length;
        this.rzr = new Randomizer();
        this.riverArray = new boolean[height][width];
        this.tileArray = tileArray;
        this.localHeights = new int[height / 4][width / 4];
        this.hasRivers = false;
        this.howManyStarts = 1;
        this.routesReady = new int[howManyStarts][2];
        this.routes = new ArrayList<>();

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

    /**
     * Finds start points for rivers and makes rivers starting from those
     * points.
     */
    public void makeRivers() {
        // get array of coordinates and heights for highest large quares: i, j, height
        int[][] highestLarge = findHighest();

        System.out.println("highest peaks are:");
        for (int i = 0; i < howManyStarts; i++) {
            System.out.println(highestLarge[i][2] + " at " + highestLarge[i][0] + ", " + highestLarge[i][1]);
        }
        // find highest 4x4 square inside each highest 24x24 square
        int[][] highestSmall = findHighestFromArray(highestLarge);

        // go through each highest 4x4 square and make river from them
        for (int i = 0; i < howManyStarts; i++) {
            if (highestSmall[i][2] != 0) {
                int[][] route = new int[100][3];
                boolean[][] riverProgression = new boolean[height / 4][width / 4];
                route[0][0] = highestSmall[i][0];
                route[0][1] = highestSmall[i][1];
                // store start point index in position 0, 2
                route[0][2] = i;
                riverProgression[route[0][0]][route[0][1]] = true;
                //System.out.println("making newRoute from " + highestSmall[i][0] + ", " + highestSmall[i][1]);
                int index = 0;
                //System.out.println("index is " + index);
                makeRoute(highestSmall[i][0], highestSmall[i][1], route, index, riverProgression);
            }
        }
        System.out.println("Routes ready:");
        for (int i = 0; i < howManyStarts; i++) {
            System.out.println("startpoint " + i + ", " + routesReady[i][0] + " rivers, largest index " + routesReady[i][1]);
        }
        boolean[][] riverProgression2 = new boolean[height / 4][width / 4];
        // find longest river routes and make rivers
        System.out.println("Checking routes: ");
        for (int[][] r : routes) {
            System.out.println("startpoint " + r[0][2] + ", length " + r[1][2]);
            if (r[1][2] == routesReady[r[0][2]][1]) {
                System.out.println("Making route: ");
                System.out.println("startpoint " + r[0][2] + ", length " + r[1][2]);
                if (riverProgression2[r[0][0]][r[0][1]] == false) {
                    makeRiver(r, r[1][2]);
                }
                riverProgression2[r[0][0]][r[0][1]] = true;
            }
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (riverArray[i][j] == false) {
                    System.out.print("-");
                } else {
                    System.out.print("*");
                }
            }
            System.out.println("");
        }
    }

    /**
     * Finds 24x24 squarest with highest total elevation.
     *
     * @return howManyStarts x 3 array that has i and j coordinate and sum of
     * elevations for each square.
     */
    public int[][] findHighest() {
        int[][] highestSquares = new int[howManyStarts][3];

        // divide intArray to 24 * 24 squares and find highest ones
        int heightBy24 = height / 24;
        int widthBy24 = width / 24;

        for (int i = 0; i < heightBy24; i++) {
            for (int j = 0; j < widthBy24; j++) {
                int localHeight = calculateHeight(i * 24, j * 24, i * 24 + 24, j * 24 + 24);
                //System.out.println("local height for " + i + ", " + j + " is " + localHeight);

                if (localHeight > highestSquares[0][2]) {
                    highestSquares[0][2] = localHeight;
                    highestSquares[0][0] = i;
                    highestSquares[0][1] = j;
                    // sort so that first height is always smallest
                    Arrays.sort(highestSquares, Comparator.comparingInt(row -> row[2]));
                }
            }
        }
        return highestSquares;
    }

    /**
     * Finds highest 4x4 square inside larger squares, utilizing localHeights
     * array.
     *
     * @param highestLarge The array of larger squares, with coordinates and
     * total elevations
     * @return Array of coordinates and elevations of local highest 4x4 squares
     */
    private int[][] findHighestFromArray(int[][] highestLarge) {
        System.out.println("highest locals:");
        int[][] highestSmall = new int[howManyStarts][3];

        // find highest 4x4 square in each 24x24 square
        for (int k = 0; k < howManyStarts; k++) {
            int startI = highestLarge[k][0] * 6;
            int startJ = highestLarge[k][1] * 6;
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    int localHeight = localHeights[startI + i][startJ + j];
                    if (highestSmall[k][2] < localHeight) {
                        highestSmall[k][0] = startI + i;
                        highestSmall[k][1] = startJ + j;
                        highestSmall[k][2] = localHeight;
                    }
                }
            }
            System.out.println(highestSmall[k][2] + " at " + highestSmall[k][0] + ", " + highestSmall[k][1]);
        }
        //System.out.println("highest small peaks are:");
//        for (int i = 0; i < 3; i++) {
//            System.out.println(highestSmall[i][2] + " at " + highestSmall[i][0] + ", " + highestSmall[i][1]);
//        }
        return highestSmall;

    }

    /**
     * Calculates combined elevation of a rectangle.
     *
     * @param startI Inclusive
     * @param startJ Inclusive
     * @param endI Exclusive
     * @param endJ Exclusive
     * @return sum of heights
     */
    private int calculateHeight(int startI, int startJ, int endI, int endJ) {
        int sum = 0;
        for (int i = startI; i < endI; i++) {
            for (int j = startJ; j < endJ; j++) {
                sum = sum + intArray[i][j];
            }
        }
        return sum;
    }

    private void makeRoute(int i, int j, int[][] initRoute, int routeIndex, boolean[][] riverProgression) {
        int startPoint = initRoute[0][2];
        int[][] deepCopiedRoute = deepCopy(initRoute);
        int index = routeIndex;
        // make a maximum of 2 rivers from each starting point
        if (routesReady[startPoint][0] > 2) {
            return;
        }
        boolean[][] newRiverProgression = deepCopy(riverProgression);

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
            // grow dowstream-ish, only to squares within bounds
            if (k >= 0 && l >= 0 && k < height / 4 && l < width / 4 && newRiverProgression[k][l] == false) {
                if (localHeights[i][j] > localHeights[k][l] - 10) {
                    // if we're not in water or on map edge, grow route
                    if (!(k == 0 || l == 0 || k == height / 4 - 1 || l == width / 4 - 1) && index < 49 && !(tileArray[k * 4 + 1][l * 4].isWater() && tileArray[k * 4 + 3][l * 4 + 2].isWater())) {
                        System.out.println("growing from " + i + ", " + j + " to " + k + ", " + l + ", index " + (index + 1));
                        // add this point to newRoute
                        deepCopiedRoute[index + 1][0] = k;
                        deepCopiedRoute[index + 1][1] = l;

                        // mark this square as done so can't return to it
                        newRiverProgression[k][l] = true;
                        makeRoute(k, l, deepCopiedRoute, index + 1, newRiverProgression);
                    } else if (index > 3) {
                        // if can't advance and newRoute is longer than 3
                        if (index + 1 > routesReady[startPoint][1]) {
                            deepCopiedRoute[index + 1][0] = k;
                            deepCopiedRoute[index + 1][1] = l;
                            routesReady[startPoint][0]++;
                            routesReady[startPoint][1] = index + 1;

                            deepCopiedRoute[0][2] = startPoint;
                            deepCopiedRoute[1][2] = index + 1;
                            System.out.println("adding route from startpoint " + startPoint + ", length " + (index + 1));
                            routes.add(deepCopiedRoute);
                            System.out.println("added route:");
                            for (int p = 0; p < deepCopiedRoute.length; p++) {
                                System.out.println(deepCopiedRoute[p][0] + ", " + deepCopiedRoute[p][1]);
                            }
                            //System.out.println("we now have " + routes.size() + " routes.");
                        }
                        //makeRiver(newRoute, routeIndex + 1);
                    }
                }
            }
        }
    }

    public void makeRiver(int[][] route, int routeIndex) {
        String[] directions = makeDirections(route, routeIndex);
        System.out.println("route to handle:");
        for (int k = 0; k < directions.length; k++) {
            System.out.println(route[k][0] + ", " + route[k][1]);
        }

        int i = 0;
        int j = 0;
        for (int k = 0; k < directions.length; k++) {
            i = route[k][0];
            j = route[k][1];
            switch (directions[k]) {
                case "s":
                    goSouth(i, j);
                    break;
                case "n":
                    goNorth(i, j);
                    break;
                case "w":
                    goWest(i, j);
                    break;
                case "e":
                    goEast(i, j);
                    break;
            }
            System.out.println("going " + directions[k] + " from " + i + ", " + j);
        }

    }

    public void goSouth(int i, int j) {
        // start from below center
        setRiver(i * 4 + 2, j * 4 + 1);
        setRiver(i * 4 + 3, j * 4 + 1);
        setRiver(i * 4 + 4, j * 4 + 2);
        setRiver(i * 4 + 5, j * 4 + 1);
    }

    public void goNorth(int i, int j) {
        goSouth(i + 1, j);
    }

    public void goWest(int i, int j) {
        setRiver(i * 4 + 2, j * 4 + 1);
        setRiver(i * 4 + 2, j * 4);
        setRiver(i * 4 + 2, j * 4 - 1);
        setRiver(i * 4 + 1, j * 4 - 2);
        setRiver(i * 4 + 1, j * 4 - 3);
    }

    public void goEast(int i, int j) {
        goWest(i, j + 1);
    }

    public boolean setRiver(int i, int j) {
        if (i >= 0 && i < height && j >= 0 && j < width) {
            riverArray[i][j] = true;
            tileArray[i][j].setRiver();
            hasRivers = true;
            return true;
        }
        return false;
    }

    public String[] makeDirections(int[][] route, int routeIndex) {
        System.out.println("directions: ");
        String[] directions = new String[routeIndex];
        for (int k = 0; k < routeIndex - 1; k++) {
            int iDir = route[k + 1][0] - route[k][0];
            int jDir = route[k + 1][1] - route[k][1];
            if (iDir == 1) {
                directions[k] = "s";
            } else if (iDir == -1) {
                directions[k] = "n";
            } else if (jDir == 1) {
                directions[k] = "e";
            } else {
                directions[k] = "w";
            }
            System.out.println(route[k][0] + ", " + route[k][1] + " -> " + route[k + 1][0] + ", " + route[k + 1][1]);
            System.out.println(directions[k]);
        }
        directions[directions.length - 1] = directions[directions.length - 2];
        System.out.println(directions[directions.length - 1]);
        return directions;
    }

    public Tile[][] getTileArray() {
        return tileArray;
    }

    public boolean hasRivers() {
        return this.hasRivers;
    }

    public int[][] deepCopy(int[][] original) {
        int[][] copy = new int[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[0].length; j++) {
                copy[i][j] = original[i][j];
            }

        }
        return copy;
    }

    public boolean[][] deepCopy(boolean[][] original) {
        boolean[][] copy = new boolean[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[0].length; j++) {
                copy[i][j] = original[i][j];
            }

        }
        return copy;
    }
}
