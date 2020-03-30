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
    private Random rnd;

    public Map(int height, int width) {
        this.height = height;
        this.width = width;
        this.intArray = new int[height][width];
        this.tileArray = new Tile[height][width];
        this.rnd = new Random();

    }

    public void assignTiles() {
        this.assignTiles(3);
    }

    public void assignTiles(int range) {
        //this.randomizeSmarter(range);
        this.startRecursively(range);
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

    public void startRecursively(int range) {
        // random spot, central-ish
        int i = rnd.nextInt(height / 3) + height / 3;
        int j = rnd.nextInt(width / 3) + width / 3;
        System.out.println("seed is " + i + ", " + j);
        intArray[i][j] = rnd.nextInt(10) + 9;
        randomizeRecursively(i + 1, j, range, 9);
        //this.printIntArray();
    }

    public void startRecursively(int range, int islandTendency) {
        int i = rnd.nextInt(height / 3) + height / 3;
        int j = rnd.nextInt(width / 3) + width / 3;
        System.out.println("seed is " + i + ", " + j);
        intArray[i][j] = rnd.nextInt(10) + 9;
        randomizeRecursively(i + 1, j, 3, 9);
        fillTheRest(3);
    }
    
        public void fillTheRest(int range) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!isAssigned(i, j)) {
                    double avg = neighborsAverage(i, j);
                    
                    if (avg > range) {
                    randomizeOne(i, j, range, avg);}
                }
            }
        }

    }

    /*public void randomizeRecursively(int i, int j, int range) {
        if (i < 0 || j < 0 || i >= height || j >= width || isAssigned(i, j)) {
            return;
        } else {
            randomizeOne(i, j, range);
        }
        randomizeRecursively(i - 1, j, range);
        randomizeRecursively(i, j - i, range);
        randomizeRecursively(i, j + i, range);
        randomizeRecursively(i + 1, j, range);
    } */
    // this one has a kill switch:
    public void randomizeRecursively(int i, int j, int range, int stopWhen) {
        if (i < 0 || j < 0 || i >= height || j >= width || isAssigned(i, j) || stopWhen < 1) {
            return;
        }
        randomizeOne(i, j, range);
        // only stop at the sea
        if (intArray[i][j] < 5) {
            int coinToss = rnd.nextInt(3);
            stopWhen = stopWhen - coinToss / 2;
            //System.out.println("coinToss " + coinToss + ", stopWhen " + stopWhen);
        }
        int one = rnd.nextInt(3);
        int two = rnd.nextInt(3);
        int three = rnd.nextInt(3);
        int four = rnd.nextInt(3);

        if (one > 0) {
            randomizeRecursively(i - 1, j, range, stopWhen);
        }
        if (two > 0) {
            randomizeRecursively(i + 1, j, range, stopWhen);
        }
        if (three > 0) {
            randomizeRecursively(i, j - 1, range, stopWhen);
        }
        if (four > 0) {
            randomizeRecursively(i, j + 1, range, stopWhen);
        }
    }

    public void randomizeOne(int i, int j, int range) {
        if (i < 0 || j < 0 || i >= this.height || j >= this.width) {
            return;
        }
        if (isAssigned(i, j)) {
            return;
        }
        // warn for even ranges
        if (range % 2 == 0) {
            System.out.println("Warning: range should optimally be an odd number. Range is " + range);
        }
        
        double avg = neighborsAverage(i, j);

        if (avg == 0) {
            intArray[i][j] = rnd.nextInt(20) + 1;
            System.out.println("free randomization done!");
            return;
        }

        // convert average to int
        int intAvg = (int) Math.round(avg);
        
        // tend toward downhill slopes
        int coinToss = rnd.nextInt(5);
        if (coinToss < 1) {
            intAvg--;
        }

        int rndInt = (rnd.nextInt(range) - range / 2);

        //System.out.println("sum of neighbors: " + sumOfNeighbors);
        int newInt = intAvg + rndInt;
        // ensure that newInt is within bounds

        if (newInt < 1) {
            newInt = 1;
        }
        if (newInt > 20) {
            newInt = 20;
        }

        intArray[i][j] = newInt;
        //tileArray[i][j] = new Tile(newInt);
    }
    
    // randomize one when neighbors' average is already known
    public void randomizeOne(int i, int j, int range, double avg) {
           int intAvg = (int) Math.round(avg);
        
        // tend toward downhill slopes
        int coinToss = rnd.nextInt(5);
        if (coinToss < 1) {
            intAvg--;
        }

        int rndInt = (rnd.nextInt(range) - range / 2);

        //System.out.println("sum of neighbors: " + sumOfNeighbors);
        int newInt = intAvg + rndInt;
        // ensure that newInt is within bounds

        if (newInt < 1) {
            newInt = 1;
        }
        if (newInt > 20) {
            newInt = 20;
        }
        intArray[i][j] = newInt;

    }

    public double neighborsAverage(int i, int j) {
        int highestNeighbor = 0;
        int lowestNeighbor = 100;

        int assignedNeighbors = 0;
        int sumOfNeighbors = 0;

        // check already assigned neighbors' values and calculate their sum
        if (isAssigned(i - 1, j - 1)) {
            assignedNeighbors++;
            sumOfNeighbors = sumOfNeighbors + intArray[i - 1][j - 1];
            if (intArray[i - 1][j - 1] < lowestNeighbor) {
                lowestNeighbor = intArray[i - 1][j - 1];
            }
            if (intArray[i - 1][j - 1] > highestNeighbor) {
                highestNeighbor = intArray[i - 1][j - 1];
            }
        }
        if (isAssigned(i - 1, j)) {
            assignedNeighbors++;
            sumOfNeighbors = sumOfNeighbors + intArray[i - 1][j];
            if (intArray[i - 1][j] < lowestNeighbor) {
                lowestNeighbor = intArray[i - 1][j];
            }
            if (intArray[i - 1][j] > highestNeighbor) {
                highestNeighbor = intArray[i - 1][j];
            }
        }
        if (isAssigned(i - 1, j + 1)) {
            assignedNeighbors++;
            sumOfNeighbors = sumOfNeighbors + intArray[i - 1][j + 1];
            if (intArray[i - 1][j + 1] < lowestNeighbor) {
                lowestNeighbor = intArray[i - 1][j + 1];
            }
            if (intArray[i - 1][j + 1] > highestNeighbor) {
                highestNeighbor = intArray[i - 1][j + 1];
            }
        }
        if (isAssigned(i, j - 1)) {
            assignedNeighbors++;
            sumOfNeighbors = sumOfNeighbors + intArray[i][j - 1];
            if (intArray[i][j - 1] < lowestNeighbor) {
                lowestNeighbor = intArray[i][j - 1];
            }
            if (intArray[i][j - 1] > highestNeighbor) {
                highestNeighbor = intArray[i][j - 1];
            }
        }
        if (isAssigned(i, j + 1)) {
            assignedNeighbors++;
            sumOfNeighbors = sumOfNeighbors + intArray[i][j + 1];
            if (intArray[i][j + 1] < lowestNeighbor) {
                lowestNeighbor = intArray[i][j + 1];
            }
            if (intArray[i][j + 1] > highestNeighbor) {
                highestNeighbor = intArray[i][j + 1];
            }
        }
        if (isAssigned(i + 1, j - 1)) {
            assignedNeighbors++;
            sumOfNeighbors = sumOfNeighbors + intArray[i + 1][j - 1];
            if (intArray[i + 1][j - 1] < lowestNeighbor) {
                lowestNeighbor = intArray[i + 1][j - 1];
            }
            if (intArray[i + 1][j - 1] > highestNeighbor) {
                highestNeighbor = intArray[i + 1][j - 1];
            }
        }
        if (isAssigned(i + 1, j)) {
            assignedNeighbors++;
            sumOfNeighbors = sumOfNeighbors + intArray[i + 1][j];
            if (intArray[i + 1][j] < lowestNeighbor) {
                lowestNeighbor = intArray[i + 1][j];
            }
            if (intArray[i + 1][j] > highestNeighbor) {
                highestNeighbor = intArray[i + 1][j];
            }
        }
        if (isAssigned(i + 1, j + 1)) {
            assignedNeighbors++;
            sumOfNeighbors = sumOfNeighbors + intArray[i + 1][j + 1];
            if (intArray[i + 1][j + 1] < lowestNeighbor) {
                lowestNeighbor = intArray[i + 1][j + 1];
            }
            if (intArray[i + 1][j + 1] > highestNeighbor) {
                highestNeighbor = intArray[i + 1][j + 1];
            }
        }

        // if no neighbors:
        if (assignedNeighbors == 0) {
            return 0;
        }

        // assign new tile a value within range
        if (assignedNeighbors == 1) {
            return 1.0 * sumOfNeighbors;
        }

        //System.out.println(rndInt);
        //System.out.println(assignedNeighbors);
        //System.out.println(sumOfNeighbors);
        double avg = 1.0 * sumOfNeighbors / assignedNeighbors;

        // check if two very disparate neighbors and pick one of them or both
        if (assignedNeighbors == 2 && highestNeighbor - avg > 5) {
            int coinToss = rnd.nextInt(3);
            if (coinToss == 0) {
                assignedNeighbors--;
                sumOfNeighbors = highestNeighbor;
            } else if (coinToss == 1) {
                assignedNeighbors--;
                sumOfNeighbors = lowestNeighbor;
            }
            //System.out.println("reassigned, sum of neighbors is " + sumOfNeighbors);
        } // take biggest outliers closer to average
        else if (assignedNeighbors > 2) {
            if (highestNeighbor - avg > 4) {
                sumOfNeighbors = sumOfNeighbors - highestNeighbor + (int) avg;
            }
            if (avg - lowestNeighbor > 4) {
                sumOfNeighbors = sumOfNeighbors - lowestNeighbor + (int) avg;
            }

            avg = 1.0 * sumOfNeighbors / assignedNeighbors;
        }
        return avg;
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

    public int[][] randomizeLocationOfDropPoints(int dropPoints) {
        int[][] dropPointsArray = new int[dropPoints][2];

        // randomize location of drop points
        for (int i = 0; i < dropPoints; i++) {
            dropPointsArray[i][0] = rnd.nextInt(height);
            dropPointsArray[i][1] = rnd.nextInt(width);
        }
        return dropPointsArray;
    }

    public int howManyDropPoints() {

        int tiles = this.height * this.width;
        // determine amount of initial free points, with randomness :
        int dropPoints = tiles / 50;
        int newSeed = dropPoints / 2;

        // add randomness into number of free points
        if (newSeed > 1) {
            dropPoints = dropPoints + rnd.nextInt(newSeed) - (newSeed / 2);
        }
        // one drop point for tiny maps
        if (dropPoints < 1) {
            dropPoints = 1;
        }
        //System.out.println("dropPoints is " + dropPoints);
        return dropPoints;
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

//        public void printIntArrayWithBorders() {
//        for (int i = 0; i < height; i++) {
//            System.out.println("");
//            for (int j = 0; j < width; j++) {
//                System.out.print(intArray[i][j]);
//                System.out.print(tileArray[i][j].getTopBorder());
//                System.out.print(tileArray[i][j].getLeftBorder());
//                System.out.print("(" + i + j + ") ");
//            }
//        }
//    }
    public int[][] getIntArray() {
        return intArray;
    }

    public Tile getTile(int i, int j) {
        return tileArray[i][j];
    }

}
