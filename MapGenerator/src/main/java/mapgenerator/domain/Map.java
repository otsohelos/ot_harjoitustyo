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

    public Map(int height, int width) {
        this.height = height;
        this.width = width;
        this.intArray = new int[height][width];
        this.tileArray = new Tile[height][width];
        this.rzr = new Randomizer();
        this.maxElevation = 40;
        this.islandTendency = 2;
        this.variability = 3;
    }

    public void assignTiles() {
        // if no parametres, then variability = 3 and coastal = true
        this.assignTiles(3, true);
    }

    public void assignTiles(int variabilityValue, boolean coastal) {
        if (!coastal) {
            this.islandTendency = 1;
        }
        this.variability = variabilityValue;
        if (variability % 2 == 0) {
            // warn for even variabilities
            System.out.println("Warning: variability should optimally be an odd number. Variability is " + variability);
        }

        this.startRecursively();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                makeTile(i, j, intArray[i][j]);
            }
        }
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
                tile.setLeftBorder(1);
            }
            if (aboveIsWater != tile.isWater()) {
                tile.setTopBorder(1);
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
        intArray[i][j] = rzr.randomizePlus(maxElevation / 2, (maxElevation / 2 - 2));
        randomizeRecursively(i + 1, j, 18);
        //System.out.println("before fillTheRest:");
        //this.printIntArray();

        //on large maps do another startpoint
        if (this.height > 8 || this.width > 8) {
            makeAnotherPoint(i, j);
        }
        fillTheRest();
        //System.out.println("after:");
        //this.printIntArray();
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
        System.out.println("other seed is " + anotherPointI + ", " + anotherPointJ);
        if (anotherPointI != 0 && anotherPointJ != 0) {
            randomizeRecursively(anotherPointI, anotherPointJ, 18);
        }
    }

    // fill in unassigned tiles that are surrounded by much higher land
    public void fillTheRest() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!isAssigned(i, j)) {
                    int sum = neighborsSum(i, j);

                    if (sum > variability * 7) {
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
        int[][] neighborsWithRandomness = {{rzr.randomize(3), i - 1, j}, {rzr.randomize(3), i + 1, j}, {rzr.randomize(3), i, j - 1}, {rzr.randomize(3), i, j + 1}};
        // make sure that every square tries to grow in at least one direction
        int grown = 0;
        while (grown < 1) {
            for (int k = 0; k < neighborsWithRandomness.length; k++) {
                // continue growing from neighbor if random number is over 0
                if (neighborsWithRandomness[k][0] > 0) {
                    randomizeRecursively(neighborsWithRandomness[k][1],
                            neighborsWithRandomness[k][2],
                            stopWhen);
                    grown++;
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

        double avg = neighborsAverage(i, j);

        if (avg == 0) {
            intArray[i][j] = rzr.randomizePlus(maxElevation, 1);
            return;
        }

        // convert average to int witn rounding either up or down
        int intAvg = (int) Math.round(avg);

        // tend toward downhill slopes repending on island tendency
        if (rzr.isSmaller(9, islandTendency)) {
            intAvg--;
        }

        int newInt = intAvg + rzr.randomizePlus(variability, (variability / (-2)));

        // ensure that newInt is within bounds
        if (newInt < 1) {
            newInt = 1;
        } else if (newInt > (maxElevation - 1)) {
            newInt = maxElevation - 1;
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
        if (assignedNeighbors == 2 && highestNeighbor - avg > 10) {
            int coinToss = rzr.randomize(3);
            if (coinToss == 0) {
                assignedNeighbors--;
                sumOfNeighbors = highestNeighbor;
            } else if (coinToss == 1) {
                assignedNeighbors--;
                sumOfNeighbors = lowestNeighbor;
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
        System.out.println("Warning: the setInt method should only be used for testing purposes");
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

    public Tile getTile(int i, int j) {
        return tileArray[i][j];
    }

    public int getMaxElevation() {
        return this.maxElevation;
    }

}
