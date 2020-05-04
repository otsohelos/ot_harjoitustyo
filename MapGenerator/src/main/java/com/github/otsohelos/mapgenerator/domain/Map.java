package com.github.otsohelos.mapgenerator.domain;

/**
 * Creates int and Tile maps of region and assigns variables to it.
 *
 * @author otsohelos
 */
public class Map {

    private final int height;
    private final int width;
    private Tile[][] tileArray;
    private final int[][] intArray;
    private Randomizer rzr;
    private final int maxElevation;
    private int islandTendency;
    private int variability;
    //private int madeSmaller;
    private int baseRainfall;
    private int otherPoints;

    public Map(int height, int width) {
        this.height = height;
        this.width = width;
        this.intArray = new int[height][width];
        this.tileArray = new Tile[height][width];
        this.rzr = new Randomizer();
        this.maxElevation = 40;
        this.islandTendency = 3;
        this.variability = 3;
        //this.madeSmaller = 0;
        this.otherPoints = rzr.randomize(3) + 1;
    }

    // Basic methods:
    /**
     * Assigns base variables, assigns altitudes and terrains, makes all tiles.
     *
     * @param highVariability
     * @param coastal
     */
    public void assignTiles(boolean highVariability, boolean coastal) {
        if (!coastal) {
            this.islandTendency = 1;
        }
        // set variability: 3 or 5
        this.variability = 3;
        if (highVariability) {
            variability = 5;
        }
        this.startRecursively();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                makeTile(i, j, intArray[i][j]);
            }
        }
        assignTerrain();
    }

    /**
     * If no parameters, then variability = 3 and coastal = true.
     */
    public void assignTiles() {
        this.assignTiles(false, true);
    }

    /**
     * Randomizes beginning tiles, starts assigning altitudes from there, fills
     * in missed squares.
     */
    public void startRecursively() {
        // randomize central-ish location for start point
        int firstI = rzr.randomizePlus((height / 3), (height / 3));
        int firstJ = rzr.randomizePlus((width / 3), (width / 3));
        //System.out.println("seed is " + firstI + ", " + firstJ);

        // start building map from that point
        int firstPoint = rzr.randomizePlus(maxElevation / 2, (maxElevation / 2 - 5 + variability));
        if (firstPoint > maxElevation) {
            firstPoint = maxElevation;
        }
        intArray[firstI][firstJ] = firstPoint;
        randomizeRecursively(firstI + 1, firstJ, (27 - 4 * variability));

        // on large maps make a few other startpoints
        // also in case of stackoverflow
        if (this.height > 80 || this.width > 80) {
            for (int k = 0; k < otherPoints; k++) {
                makeAnotherPoint(firstI, firstJ);
            }
        }
        fillTheRest();
        fillTheRest();
        //System.out.println("made smaller " + madeSmaller + " times");
    }

    /**
     * Finds new starting location based on the location of the first starting
     * location, then starts randomizing from there.
     */
    public void makeAnotherPoint(int i, int j) {
        // determine startpoint of checking
        int anotherPointI = 0;
        int anotherPointJ = 0;

        // if first startpoint coordinate is small, prefer to start after it
        int whereStartI = Math.min(i / 2, rzr.randomize(height / 4));
        int whereStartJ = Math.min(j / 2, rzr.randomize(width / 4));
        outerLoop:
        for (int k = (height / 4 + height / 4) + whereStartI; k < 3 * height / 4; k++) {
            for (int l = (width / 4 + width / 4) + whereStartJ; l < 3 * width / 4; l++) {
                //System.out.println("checking " + k + ", " + l);
                if (!isAssigned(k, l)) {
                    anotherPointI = k;
                    anotherPointJ = l;
                    //System.out.println("other seed is " + anotherPointI + ", " + anotherPointJ);
                    break outerLoop;
                }
            }
        }
        if (anotherPointI != 0 && anotherPointJ != 0) {
            randomizeRecursively(anotherPointI, anotherPointJ, (27 - 4 * variability));
        }
    }

    /**
     * Fills in unassigned tiles that are surrounded by much higher land.
     */
    public void fillTheRest() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!isAssigned(i, j)) {
                    int sum = neighborsSum(i, j);

                    if (sum > variability * 6) {
                        randomizeRecursively(i, j, 2);
                    }
                }
            }
        }
    }

    /**
     * Creates a tile with given elevation and places it in the tile grid.
     */
    public void makeTile(int i, int j, int elevation) {
        Tile tile = new Tile(elevation);
        // check if above and left tiles have water, make border if needed
        if (i != 0) {
            boolean aboveIsWater = tileArray[i - 1][j].isWater();
            if (aboveIsWater != tile.isWater()) {
                tile.setTopBorder();
            }
        }
        if (j != 0) {
            boolean leftIsWater = tileArray[i][j - 1].isWater();
            if (leftIsWater != tile.isWater() && i != 0) {
                tile.setLeftBorder();
            }
        }
        tileArray[i][j] = tile;
    }

    public void assignTerrain() {
        // assign base rainfall number between 3 and 9
        baseRainfall = islandTendency + 2 + rzr.randomize(5);
        // addition to prevent inland maps from being super dry
        if (islandTendency == 1) {
            baseRainfall++;
        }
        //System.out.println("baserainfall: " + baseRainfall);

        // go through map in 3x3 areas
        int multiplier = 3;
        for (int i = 0; i < height; i = i + multiplier) {
            for (int j = 0; j < width; j = j + multiplier) {
                // check how many water squares in this area
                int howManyWaters = howManyWaters(i, j, multiplier);

                assignTerrainSquares(i, j, howManyWaters, multiplier, baseRainfall);
            }
        }
    }

    // Terrain handling methods:
    /**
     * Randomizes base rainfall number for this Map and rainfall numbers for
     * each square; calls all Tiles to assign a terrain for themselves.
     *
     * @param rainy True means rainy, false means dry
     */
    public void assignTerrain(boolean rainy) {
        if (rainy) {
            // assign base rainfall number: coastal 7-9, inland 5-7
            baseRainfall = islandTendency + 4 + rzr.randomize(3);
        } else {
            // assign base rainfall number: coastal 4-6, inland 2-4
            baseRainfall = islandTendency + 1 + rzr.randomize(3);
        }

        // addition to prevent inland maps from being super dry
        if (islandTendency == 1) {
            baseRainfall++;
        }
        System.out.println("baserainfall: " + baseRainfall);

        // go through map in 3x3 areas
        int multiplier = 3;
        for (int i = 0; i < height; i = i + multiplier) {
            for (int j = 0; j < width; j = j + multiplier) {
                // check how many water squares in this area
                int howManyWaters = howManyWaters(i, j, multiplier);
                assignTerrainSquares(i, j, howManyWaters, multiplier, baseRainfall);

            }
        }
    }

    /**
     * Assigns rainfall for Tiles in a square and sets terrain in Tiles.
     *
     * @param howManyWaters How many water squares in this larger square
     */
    public void assignTerrainSquares(int i, int j, int howManyWaters, int multiplier, int baseRainfall) {
        for (int k = i; k < i + multiplier; k++) {
            for (int l = j; l < j + multiplier; l++) {
                // final rainfall number: proportion of surrounding water tiles times base number 2 to 10
                // plus random number 0 to 4, total rainfall 0 to 10
                int random = rzr.randomize(3);
                double rainfall = 1.0 * (howManyWaters) / (multiplier * multiplier) + baseRainfall + random;

                if (k < height && l < width) {
                    tileArray[k][l].setRainfall((int) rainfall);
                    tileArray[k][l].assignTerrain();
                }
                //System.out.println("rainfall " + rainfall + ", terrain " + tileArray[k][l].getTerrain());
            }
        }
    }

    // Randomization and math methods:
    /**
     * Randomizes given location's elevation, then calls to do the same to one
     * or more neighboring locations; stopWhen variable gets gradually smaller
     * and kills this recursion when it's zero and the square is water.
     */
    public void randomizeRecursively(int i, int j, int stopWhen) {
        if (i < 0 || j < 0 || i >= height || j >= width || isAssigned(i, j) || stopWhen < 1) {
            return;
        }
        randomizeOne(i, j);
        // only stop at the sea
        if (intArray[i][j] < 5) {
            stopWhen = stopWhen - (rzr.randomize(3) / 2);
            //System.out.println("coinToss " + coinToss + ", stopWhen " + stopWhen);
        }
        growRecursively(i, j, stopWhen);
    }

    /**
     * Determines where to continue with elevation assignment and continues it.
     */
    public void growRecursively(int i, int j, int stopWhen) {
        // make array that has random number 0-2 and coordinates of neighbors
        int[][] neighborsWithRandomness = {{rzr.randomize(2), i - 1, j}, {rzr.randomize(2), i + 1, j}, {rzr.randomize(2), i, j - 1}, {rzr.randomize(2), i, j + 1}};
        // make sure that every square tries to grow in at least one direction
        int grown = 0;
        while (grown < 1) {
            for (int k = 0; k < neighborsWithRandomness.length; k++) {
                // continue growing from neighbor if random number is over 0
                if (neighborsWithRandomness[k][0] > 0) {
                    try {
                        randomizeRecursively(neighborsWithRandomness[k][1], neighborsWithRandomness[k][2], stopWhen);
                        grown++;
                    } catch (StackOverflowError soe) {
                        // if stack overflows, make one more starting point
                        otherPoints++;
                        //System.out.println("overflow, starting again");
                    }
                }
            }
            // randomize them again
            for (int k = 0; k < neighborsWithRandomness.length; k++) {
                neighborsWithRandomness[k][0] = rzr.randomize(3);
            }
        }
    }

    /**
     * Randomizes and assigns the elevation of one location.
     *
     * @param i
     * @param j
     */
    public void randomizeOne(int i, int j) {
        int newInt = 0;
        double avg = neighborsAverage(i, j);

        if (avg == 0) {
            newInt = rzr.randomizePlus(maxElevation, 1);
        } else {
            // convert average to int, rounding either up or down
            int intAvg = (int) Math.round(avg);
            // tend toward downhill slopes repending on island tendency and proximity to map edges
            int lowerer = 30;
            if (height - i < 15 || i < 15 || width - j < 15 || i < 15) {
                lowerer = 12;
            } else if (height - i < 22 || i < 22 || width - j < 22 || i < 22) {
                lowerer = 18;
            }
            if (rzr.isSmaller(lowerer, (islandTendency * islandTendency))) {
                //madeSmaller++;
                intAvg--;
            }
            newInt = intAvg + rzr.randomizePlus(variability, (variability / (-2)));
        }
        // ensure that newInt is within bounds
        if (newInt < 1) {
            newInt = 1;
        } else if (newInt > (maxElevation - 1)) {
            newInt = maxElevation - 1;
        }
        intArray[i][j] = newInt;
    }

    /**
     * Calculates the average elevation of the neighbors of a location.
     *
     * @return adjusted average
     */
    public double neighborsAverage(int i, int j) {
        int highestNeighbor = 0;
        int lowestNeighbor = 100;
        int assignedNeighbors = 0;
        int sumOfNeighbors = 0;
        int[][] neighborCoordinates = {{i - 1, j - 1}, {i - 1, j}, {i - 1, j + 1}, {i, j - 1}, {i, j + 1}, {i + 1, j - 1}, {i + 1, j}, {i + 1, j - 1}};

        // check already assigned neighbors' values and calculate their sum
        for (int k = 0; k < neighborCoordinates.length; k++) {
            if (isAssigned(neighborCoordinates[k][0], neighborCoordinates[k][1])) {
                assignedNeighbors++;
                int value = intArray[neighborCoordinates[k][0]][neighborCoordinates[k][1]];
                sumOfNeighbors = sumOfNeighbors + value;
                if (value < lowestNeighbor) {
                    lowestNeighbor = value;
                }
                if (value > highestNeighbor) {
                    highestNeighbor = value;
                }
            }
        }
        // if no neighbors:
        if (assignedNeighbors == 0) {
            return 0;
        }
        // assign new tile a value within range
        if (assignedNeighbors == 1) {
            return sumOfNeighbors;
        }
        double avg = calculateAvg(sumOfNeighbors, assignedNeighbors, highestNeighbor, lowestNeighbor);
        return avg;
    }

    /**
     * Calculates adjusted average of the elevations of neighboring squares.
     */
    private double calculateAvg(int sumOfNeighbors, int assignedNeighbors, int highestNeighbor, int lowestNeighbor) {
        double avg = 1.0 * sumOfNeighbors / assignedNeighbors;
        // check if two very disparate neighbors and randomly pick one or both
        if (assignedNeighbors == 2 && highestNeighbor - avg > 10) {

            int coinToss = rzr.randomize(3);
            if (coinToss == 0) {
                avg = highestNeighbor;
            } else if (coinToss == 1) {
                avg = lowestNeighbor;
            }
        } else if (assignedNeighbors > 2) {
            // take out biggest outliers and replace them with average
            if (highestNeighbor - avg > 8) {
                avg -= (1.0 * highestNeighbor - avg) / (1.0 * assignedNeighbors);
            }
            if (avg - lowestNeighbor > 8) {
                avg += (avg - 1.0 * lowestNeighbor) / (1.0 * assignedNeighbors);
            }
        }
        return avg;
    }

    /**
     * Calculates the sum of the elevations of neighboring squares.
     *
     * @param i Coordinate
     * @param j Coordinate
     * @return Sum
     */
    public int neighborsSum(int i, int j) {
        int sumOfNeighbors = 0;
        int[][] neighborCoordinates = {{i - 1, j - 1}, {i - 1, j}, {i - 1, j + 1}, {i, j - 1}, {i, j + 1}, {i + 1, j - 1}, {i + 1, j}, {i + 1, j - 1}};
        for (int k = 0; k < neighborCoordinates.length; k++) {
            if (isAssigned(neighborCoordinates[k][0], neighborCoordinates[k][1])) {
                sumOfNeighbors = sumOfNeighbors + intArray[neighborCoordinates[k][0]][neighborCoordinates[k][1]];
            }
        }
        return sumOfNeighbors;
    }

    /**
     * Calculates how many water squares there are in a larger square.
     *
     * @param i Coordinate
     * @param j Coordinate
     * @param multiplier Size of this square is multiplier * multiplier
     * @return the number of water tiles
     */
    public int howManyWaters(int i, int j, int multiplier) {
        int waters = 0;
        for (int k = i; k < i + multiplier; k++) {
            for (int l = j; l < j + multiplier; l++) {
                if (k < height && l < width && tileArray[k][l].isWater()) {
                    waters++;
                }
            }
        }
        return waters;
    }

    // Getters:
    /**
     * Checks if location already has an elevation assigned.
     *
     * @param i Coordinate
     * @param j Coordinate
     * @return true or false
     */
    public boolean isAssigned(int i, int j) {
        // is square within bounds?
        if (i < 0 || j < 0 || i >= this.height || j >= this.width) {
            return false;
            // if it is, is it assigned a value?
        } else if (intArray[i][j] > 0) {
            return true;
        }
        return false;
    }

    public Tile[][] getTileArray() {
        return tileArray;
    }

    public int[][] getIntArray() {
        return intArray;
    }

    public int getMaxElevation() {
        return this.maxElevation;
    }

    public String getRainFallString() {
        String wetOrDry = "dry";
        if (baseRainfall > 6) {
            wetOrDry = "wet";
        }
        return ("This area is " + wetOrDry + ".\nAverage rainfall " + (baseRainfall - 3) + " out of 6.");
    }

    //Setter
    public void setInt(int i, int j, int value) {
        System.out.println("Note: the setInt method should only be used for testing purposes");
        intArray[i][j] = value;
    }

    /**
     * Prints the int elevation array for this Map.
     */
    public void printIntArray() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(intArray[i][j] + " ");
            }
        }
        System.out.println("");
    }

    /**
     * Prints terrain number for all squares.
     */
    public void printTerrainArray() {
        for (int i = 0; i < height; i++) {
            System.out.println("");
            for (int j = 0; j < width; j++) {
                System.out.print(tileArray[i][j].getTerrain() + " ");
            }
        }
        System.out.println("");
    }
}
