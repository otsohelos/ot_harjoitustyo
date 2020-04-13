package mapgenerator.domain;

import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author otsohelos
 */
public class Map {

    private int height;
    private int width;
    private Tile[][] tileArray;
    private int[][] intArray;
    private Randomizer rzr;
    private int maxElevation;
    private int islandTendency;
    private int variability;
    private int madeSmaller;
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
        this.madeSmaller = 0;
        this.otherPoints = rzr.randomize(3) + 1;
    }

    public void assignTiles() {
        // if no parametres, then variability = 3 and coastal = true
        this.assignTiles(false, true);
    }

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

    public void makeTile(int i, int j, int elevation) {
        Tile tile = new Tile(elevation);
        if (i != 0 || j != 0) {
            //check if above and left tiles have water
            boolean aboveIsWater = false;
            boolean leftIsWater = false;
            if (i != 0) {
                aboveIsWater = tileArray[i - 1][j].isWater();
            }
            if (j != 0) {
                leftIsWater = tileArray[i][j - 1].isWater();
            }

            // if water changes to land or vice versa, make border
            if (leftIsWater != tile.isWater()) {
                tile.setLeftBorder();
            }
            if (aboveIsWater != tile.isWater()) {
                tile.setTopBorder();
            }
        }
        tileArray[i][j] = tile;
    }

    public void startRecursively() {
        // randomize central-ish location for start point
        int i = rzr.randomizePlus((height / 3), (height / 3));
        int j = rzr.randomizePlus((width / 3), (width / 3));
        System.out.println("seed is " + i + ", " + j);

        // start building map from that point
        // high variability maps are likely to start higher
        int firstPoint = rzr.randomizePlus(maxElevation / 2, (maxElevation / 2 - 5 + variability));
        if (firstPoint > maxElevation) {
            firstPoint = maxElevation;
        }
        intArray[i][j] = firstPoint;
        randomizeRecursively(i + 1, j, (27 - 4 * variability));
        //System.out.println("before fillTheRest:");
        //this.printIntArray();

        // on large maps make a few other startpoints
        // also in case of stackoverflow
        if (this.height > 80 || this.width > 80) {
            for (int k = 0; k < otherPoints; k++) {
                makeAnotherPoint(i, j);
            }
        }
        fillTheRest();
        fillTheRest();
        System.out.println("made smaller " + madeSmaller + " times");
    }

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
                    System.out.println("other seed is " + anotherPointI + ", " + anotherPointJ);
                    break outerLoop;
                }
            }
        }
        if (anotherPointI != 0 && anotherPointJ != 0) {
            randomizeRecursively(anotherPointI, anotherPointJ, (27 - 4 * variability));
        }
    }

    // fill in unassigned tiles that are surrounded by much higher land
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

    // randomize recursively with a kill switch:
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
                        randomizeRecursively(neighborsWithRandomness[k][1],
                                neighborsWithRandomness[k][2],
                                stopWhen);
                        grown++;
                    } catch (StackOverflowError soe) {
                        // if stack overflows just mark that down
                        // and make one more starting point
                        otherPoints++;
                        System.out.println("overflow, starting again");

                    }
                    //System.out.println("i is " + i + ", j is " + j);
                    //System.out.println("randomized; grown is " + grown);
                }
            }
            // randomize them again
            for (int k = 0; k < neighborsWithRandomness.length; k++) {
                neighborsWithRandomness[k][0] = rzr.randomize(3);
            }
        }
    }

    public void randomizeOne(int i, int j) {
        if (i < 0 || j < 0 || i >= this.height || j >= this.width || isAssigned(i, j)) {
            return;
        }
        int newInt = 0;

        double avg = neighborsAverage(i, j);

        if (avg == 0) {
            newInt = rzr.randomizePlus(maxElevation, 1);
        } else {

            // convert average to int witn rounding either up or down
            int intAvg = (int) Math.round(avg);

            // tend toward downhill slopes repending on island tendency
            if (rzr.isSmaller(26, (islandTendency * islandTendency))) {
                madeSmaller++;
                intAvg--;
            }

            newInt = intAvg + rzr.randomizePlus(variability, (variability / (-2)));
        }
        // ensure that newInt is within bounds
        if (newInt < 1) {
            newInt = 1;
        } else if (newInt > (maxElevation - 1)) {
            newInt = maxElevation - 1;
            //System.out.println("too large, made smaller");
        }
        intArray[i][j] = newInt;

    }

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

        double avg = 1.0 * sumOfNeighbors / assignedNeighbors;

        // check if two very disparate neighbors and pick one of them or both
        if (assignedNeighbors == 2) {
            if (highestNeighbor - avg > 10) {
                int coinToss = rzr.randomize(3);
                if (coinToss == 0) {
                    assignedNeighbors--;
                    sumOfNeighbors = highestNeighbor;
                } else if (coinToss == 1) {
                    assignedNeighbors--;
                    sumOfNeighbors = lowestNeighbor;
                }
            }
            //System.out.println("reassigned, sum of neighbors is " + sumOfNeighbors);
        } else {
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

    public int neighborsSum(int i, int j) {
        int sumOfNeighbors = 0;

        int[][] neighborCoordinates = {{i - 1, j - 1}, {i - 1, j}, {i - 1, j + 1}, {i, j - 1}, {i, j + 1}, {i + 1, j - 1}, {i + 1, j}, {i + 1, j - 1}};

        for (int k = 0; k < neighborCoordinates.length; k++) {
            if (isAssigned(neighborCoordinates[k][0], neighborCoordinates[k][1])) {
                sumOfNeighbors = sumOfNeighbors
                        + intArray[neighborCoordinates[k][0]][neighborCoordinates[k][1]];
            }
        }
        return sumOfNeighbors;
    }

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

    public void printIntArray(int[][] intArray) {
        for (int i = 0; i < intArray.length; i++) {
            System.out.println("");
            for (int j = 0; j < intArray[0].length; j++) {
                System.out.print(intArray[i][j] + " ");
            }
        }
        System.out.println("");
    }

    public Tile[][] getTileArray() {
        return tileArray;
    }

    public void setInt(int i, int j, int value) {
        System.out.println("Note: the setInt method should only be used for testing purposes");
        intArray[i][j] = value;
    }

    public void printIntArray() {
        for (int i = 0; i < height; i++) {
            System.out.println("");
            for (int j = 0; j < width; j++) {
                System.out.print(intArray[i][j] + " ");
            }
        }
        System.out.println("");
    }

    public int[][] getIntArray() {
        return intArray;
    }

    public void printRainArray() {
        for (int i = 0; i < height; i++) {
            System.out.println("");
            for (int j = 0; j < width; j++) {
                System.out.print(tileArray[i][j].getRainfall() + " ");
            }
        }
        System.out.println("");
    }

    public void printTerrainArray() {
        for (int i = 0; i < height; i++) {
            System.out.println("");
            for (int j = 0; j < width; j++) {
                System.out.print(tileArray[i][j].getTerrain() + " ");
            }
        }
        System.out.println("");
    }

    public Tile getTile(int i, int j) {
        return tileArray[i][j];
    }

    public int getMaxElevation() {
        return this.maxElevation;
    }

    public void assignTerrain() {
        // assign base rainfall number between 3 and 9
        baseRainfall = islandTendency + 2 + rzr.randomize(5);
 
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

    public void assignTerrainSquares(int i, int j, int howManyWaters, int multiplier, int baseRainfall) {

        for (int k = i; k < i + multiplier; k++) {
            for (int l = j; l < j + multiplier; l++) {
                // final rainfall number: proportion of surrounding water tiles times base number 2 to 10
                // plus random number 0 to 4, total rainfall 0 to 10
                int random = rzr.randomize(3);
                double rainfall = 1.0 * (howManyWaters) / (multiplier * multiplier) + baseRainfall + random;
                //System.out.println("how many waters: " + howManyWaters);
                //System.out.println("rainfall proportion: " + (1.0 * howManyWaters / (multiplier * multiplier)));
                //System.out.println("random: " + random);
                //System.out.println("final rainfall: " + rainfall);

                if (k < height && l < width) {
                    tileArray[k][l].setRainfall((int) rainfall);
                    tileArray[k][l].assignTerrain();
                }
                //System.out.println("rainfall " + rainfall + ", terrain " + tileArray[k][l].getTerrain());
            }
        }
    }

    public String getRainFallString() {
        String wetOrDry = "";
        if (baseRainfall > 6) {
            wetOrDry = "wet";
        } else {
            wetOrDry = "dry";
        }
        return ("This area is " + wetOrDry + ".\nAverage rainfall " + (baseRainfall - 3) + " out of 6.");
    }

}
