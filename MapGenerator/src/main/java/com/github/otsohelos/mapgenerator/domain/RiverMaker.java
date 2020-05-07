package com.github.otsohelos.mapgenerator.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Determines location of any rivers and places them on the map.
 *
 * @author otsohelos
 */
public class RiverMaker {

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
    private final boolean[][] riversReady;
    private int minRiverLength;

    public RiverMaker(int[][] intArray, Tile[][] tileArray) {
        this.intArray = deepCopy(intArray);
        this.height = intArray.length;
        this.width = intArray[0].length;
        this.rzr = new Randomizer();
        this.riverArray = new boolean[height][width];
        this.tileArray = tileArray;
        this.localHeights = new int[height / 4][width / 4];
        this.hasRivers = false;
        this.howManyStarts = determineStarts(height, width);
        this.routesReady = new int[howManyStarts][2];
        this.routes = new ArrayList<>();
        this.riversReady = new boolean[height / 4][width / 4];
        this.minRiverLength = 5;
        if (width * height < 14000) {
            minRiverLength = 2;
        } else if (width * height < 19000) {
            minRiverLength = 3;
        } else if (width * height < 30000) {
            minRiverLength = 4;
        }

        for (int i = 0; i < height / 4; i++) {
            for (int j = 0; j < width / 4; j++) {
                int localHeight = calculateHeight(i * 4, j * 4, i * 4 + 4, j * 4 + 4);
                localHeights[i][j] = localHeight;
            }
        }
    }

    // Basic river assigning start methods:
    /**
     * Determines how many start points for rivers depending on map size.
     *
     * @param height
     * @param width
     * @return number of start points, at least three
     */
    public final int determineStarts(int height, int width) {

        int starts = (height * width / 10000) * 3;
        if (starts < 3) {
            starts = 3;
        }
        return starts;
    }

    /**
     * Finds start points for rivers and makes rivers starting from those
     * points.
     */
    public void makeRivers() {
        // get array of coordinates and heights for highest large quares: i, j, height
        int[][] highestLarge = findHighest();

        // find highest 4x4 square inside each highest 24x24 square
        int[][] highestSmall = findHighestFromArray(highestLarge);
        makeRoutes(highestSmall);

        // find longest river routes and make rivers
        for (int[][] r : routes) {
            if (r[1][2] == routesReady[r[0][2]][1]) {
                if (riversReady[r[0][0]][r[0][1]] == false) {
                    makeRiver(r, r[1][2]);
                }
                riversReady[r[0][0]][r[0][1]] = true;
            }
        }
    }

    // Route-making methods. These determine a rough route for a river:
    /**
     * Starts recursive making of routes from each starting point.
     *
     * @param highestSmall Array of coordinates of 4x4 squares with highest
     * altitudes
     */
    public void makeRoutes(int[][] highestSmall) {
        // go through each highest 4x4 square and make river from them
        for (int i = 0; i < howManyStarts; i++) {

            int[][] route = new int[100][3];
            boolean[][] riverProgression = new boolean[height / 4][width / 4];
            route[0][0] = highestSmall[i][0];
            route[0][1] = highestSmall[i][1];
            // store start point index in position 0, 2
            route[0][2] = i;
            riverProgression[route[0][0]][route[0][1]] = true;
            advanceRoute(highestSmall[i][0], highestSmall[i][1], route, 0, riverProgression);
        }
    }

    /**
     * Recursively next 4x4 square in route, and when last one is found, adds
     * the route to ArrayList routes.
     *
     * @param i Coordinate
     * @param j Coordinate
     * @param initRoute Route already made
     * @param routeIndex Index of last placed tile in the route
     * @param riverProgression Array of squares already in this route
     */
    private void advanceRoute(int i, int j, int[][] initRoute, int index, boolean[][] riverProgression) {
        int startPoint = initRoute[0][2];
        int[][] deepCopiedRoute = deepCopy(initRoute);
        boolean[][] newRiverProgression = deepCopy(riverProgression);

        // set growth direction
        int dir = rzr.randomize(4);
        for (int tries = 0; tries < 4; tries++) {
            // depending on dir, calculate coordinates for where to advance
            int k = i + dir / 2 * (dir % 3 - 1); // produces i plus 0, 0, 1, -1
            int l = j + ((dir + 2) % 4) / 2 * ((dir + 2) % 3 - 1); // produces j plus 1, -1, 0, 0
            // grow dir but keep it under 4
            dir = (dir + 1) % 4;

            // grow only to squares within bounds, make a maximum of 2 rivers from each starting point
            if (k < 0 || l < 0 || k >= height / 4 || l >= width / 4 || newRiverProgression[k][l] == true || routesReady[startPoint][0] > 2) {
                return;
            } else if (!(k == 0 || l == 0 || k == height / 4 - 1 || l == width / 4 - 1) && index < 49 && !(tileArray[k * 4 + 1][l * 4].isWater() && tileArray[k * 4 + 3][l * 4 + 2].isWater()) && localHeights[i][j] > localHeights[k][l] - 30) {
                growRoute(deepCopiedRoute, newRiverProgression, index, k, l);
            } else if (index > minRiverLength && localHeights[i][j] > localHeights[k][l] - 30 && index + 1 > routesReady[startPoint][1]) {
                // if can't advance and newRoute is longer than 4
                finalizeRoute(deepCopiedRoute, index, startPoint, k, l);
            }
        }
    }

    /**
     * Grows route.
     *
     * @param route
     * @param progression
     * @param index
     * @param k
     * @param l
     */
    public void growRoute(int[][] route, boolean[][] progression, int index, int k, int l) {
        // if we're not in water or on map edge, grow route downstream-ish
        route[index + 1][0] = k;
        route[index + 1][1] = l;
        // mark this square as done so can't return to it
        progression[k][l] = true;
        advanceRoute(k, l, route, index + 1, progression);
    }

    /**
     * Adds final points to route and adds route to routes list.
     *
     * @param route Route so far
     * @param index Index of last point on route
     * @param startPoint which start point did this route originate from
     * @param k Final point coordinate
     * @param l Final point coordinate
     */
    public void finalizeRoute(int[][] route, int index, int startPoint, int k, int l) {

        route[index + 1][0] = k;
        route[index + 1][1] = l;
        routesReady[startPoint][0]++;
        routesReady[startPoint][1] = index + 1;

        route[0][2] = startPoint;
        route[1][2] = index + 2;
        routes.add(route);
    }

    // Math methods:
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
        }
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

    // Actual river making methods:
    /**
     * Takes route and sets appropriate tiles to river in both tileArray and
     * riverArray.
     *
     * @param route List of coordinates
     * @param routeIndex Length of route
     */
    public void makeRiver(int[][] route, int routeIndex) {
        String[] directions = makeDirections(route, routeIndex);
        for (int k = 0; k < directions.length; k++) {
            int i = route[k][0];
            int j = route[k][1];
            if (riversReady[i][j]) {
                return;
            }
            riversReady[i][j] = true;
            if (directions[k].equals("s")) {
                goSouth(i, j);
            } else if (directions[k].equals("n")) {
                goNorth(i, j);
            } else if (directions[k].equals("w")) {
                goWest(i, j);
            } else if (directions[k].equals("e")) {
                goEast(i, j);
            }
        }
    }

    /**
     * Changes tiles to river tiles; advances south.
     *
     * @param i
     * @param j
     */
    public void goSouth(int i, int j) {
        // low-probability extra route
        if (rzr.isSmaller(6, 1)) {
            setRiver(i * 4 + 2, j * 4 + 1);
            setRiver(i * 4 + 3, j * 4);
            setRiver(i * 4 + 4, j * 4 + rzr.randomize(2));
            setRiver(i * 4 + 5, j * 4 + 1);
            return;
        } else if (rzr.isSmaller(2, 1)) {
            // else choose from two options
            setRiver(i * 4 + 2, j * 4 + 1);
            setRiver(i * 4 + 3, j * 4 + 1);
            setRiver(i * 4 + 4, j * 4 + 2);
            setRiver(i * 4 + 5, j * 4 + 1);
        } else {
            setRiver(i * 4 + 2, j * 4 + 1);
            setRiver(i * 4 + 3, j * 4 + 2);
            setRiver(i * 4 + 4, j * 4 + 2);
            setRiver(i * 4 + 5, j * 4 + 1);
        }
    }

    /**
     * Changes tiles to river tiles; advances north.
     *
     * @param i
     * @param j
     */
    public void goNorth(int i, int j) {
        goSouth(i - 1, j);
    }

    /**
     * Changes tiles to river tiles; advances west.
     *
     * @param i
     * @param j
     */
    public void goWest(int i, int j) {
        // low-probability extra route
        if (rzr.isSmaller(6, 1)) {
            setRiver(i * 4 + 2, j * 4 + 1);
            setRiver(i * 4 + 3, j * 4);
            setRiver(i * 4 + 3, j * 4 - 1);
            setRiver(i * 4 + 2, j * 4 - 2);
            setRiver(i * 4 + 1, j * 4 - 3);
        } else if (rzr.isSmaller(2, 1)) {
            // else choose from two options
            setRiver(i * 4 + 2, j * 4 + 1);
            setRiver(i * 4 + 2, j * 4);
            setRiver(i * 4 + 2, j * 4 - 1);
            setRiver(i * 4 + 1, j * 4 - 2);
        } else {
            setRiver(i * 4 + 1, j * 4);
            setRiver(i * 4 + 1, j * 4 - 1);
            setRiver(i * 4 + 2, j * 4 - 2);
            setRiver(i * 4 + 1, j * 4 - 3);
        }
    }

    /**
     * Changes tiles to river tiles; advances east.
     *
     * @param i
     * @param j
     */
    public void goEast(int i, int j) {
        goWest(i, j + 1);
    }

    /**
     * Changes tile in both tileArray and riverArray to river tiles if tile is
     * within bounds and not already water.
     *
     * @param i
     * @param j
     * @return false if tile not within bounds, else true
     */
    public boolean setRiver(int i, int j) {
        if (i >= 0 && i < height && j >= 0 && j < width) {
            riverArray[i][j] = true;
            tileArray[i][j].setRiver(true);
            hasRivers = true;
            return true;
        }
        return false;
    }

    /**
     * Makes array of directions ("s", "n", "e" and "w") for river.
     *
     * @param route
     * @param routeIndex
     * @return array of directions
     */
    public String[] makeDirections(int[][] route, int routeIndex) {
        String[] directions = new String[routeIndex];
        for (int k = 0; k < routeIndex; k++) {
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
        }
        directions[directions.length - 1] = directions[directions.length - 2];
        return directions;
    }

    // Getters:
    public Tile[][] getTileArray() {
        return tileArray;
    }

    public boolean hasRivers() {
        return this.hasRivers;
    }

    // Helper methods: 
    /**
     * Makes a deep copy of a 2D int array.
     *
     * @param original
     * @return
     */
    public final int[][] deepCopy(int[][] original) {
        int[][] copy = new int[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[0].length; j++) {
                copy[i][j] = original[i][j];
            }

        }
        return copy;
    }

    /**
     * Makes a deep copy of a 2D Boolean array.
     *
     * @param original
     * @return
     */
    public boolean[][] deepCopy(boolean[][] original) {
        boolean[][] copy = new boolean[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[0].length; j++) {
                copy[i][j] = original[i][j];
            }

        }
        return copy;
    }

    /**
     * Removes all existing rivers.
     */
    public void resetRivers() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tileArray[i][j].setRiver(false);
            }
        }
    }
}
