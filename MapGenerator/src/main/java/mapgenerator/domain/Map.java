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

    /*public void randomize() {
        int initialInt = (rnd.nextInt(9) + 1);
        tileArray[0][0] = new Tile(initialInt);
        intArray[0][0] = initialInt;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                //System.out.println("");
                if (i != 0 || j != 0) {
                    boolean aboveIsWater = false;
                    boolean leftIsWater = false;
                    int rndInt = (rnd.nextInt(6) - 2);
                    int above = 4;
                    int left = 4;
                    if (i != 0) {
                        above = tileArray[i - 1][j].getElevation();
                        aboveIsWater = tileArray[i - 1][j].isWater();
                    }
                    //System.out.print(above);
                    if (j != 0) {
                        left = tileArray[i][j - 1].getElevation();
                        leftIsWater = tileArray[i][j - 1].isWater();
                    }
                    //System.out.print(left);
                    int newInt = (above + left + rndInt) / 2;
                    if (newInt > 10) {
                        newInt = 10;
                    }
                    if (newInt < 1) {
                        newInt = 1;
                    }
                    Tile tile = new Tile(newInt);

                    if (leftIsWater != tile.isWater()) {
                        tile.setLeftBorder(1);
                    }
                    if (aboveIsWater != tile.isWater()) {
                        tile.setTopBorder(1);
                    }

                    tileArray[i][j] = tile;
                    intArray[i][j] = newInt;
                }
            }
        }
        System.out.println(Arrays.deepToString(intArray).replaceAll("],", "]," + System.getProperty("line.separator")));
    }*/
    public void assignTiles() {
        this.assignTiles(5);
    }

    public void assignTiles(int range) {
        //this.randomizeSmarter(range);
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

    // testing:
    public void startRecursively() {
        // random spot
        int i = rnd.nextInt(height - (height / 5)) + height / 10;
        int j = rnd.nextInt(width - (width / 5)) + width / 10;
        System.out.println("seed is " + i + ", " + j);
        randomizeRecursively(i, j, 3, 9);
    }

    public void randomizeRecursively(int i, int j, int range) {
        if (i < 0 || j < 0 || i >= height || j >= width || isAssigned(i, j)) {
            return;
        } else {
            randomizeOne(i, j, range);
        }
        randomizeRecursively(i - 1, j, range);
        randomizeRecursively(i, j - i, range);
        randomizeRecursively(i, j + i, range);
        randomizeRecursively(i + 1, j, range);

    }

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

    public void randomizeSmarter() {
        // if no variability number, use 3
        randomizeSmarter(3);
    }

    public void randomizeSmarter(int range) {

        // determine amount of initial free points
        int dropPoints = howManyDropPoints();

        // create array of i + j coordinates of drop points
        int[][] dropPointsArray = randomizeLocationOfDropPoints(dropPoints);

        // assign random heights to dropPointsArray points
        // and put them on the map
        // no zeroes
        for (int k = 0; k < dropPoints; k++) {
            int freeRandomNumber = rnd.nextInt(20) + 1;
            int i = dropPointsArray[k][0];
            int j = dropPointsArray[k][1];
            intArray[i][j] = freeRandomNumber;
        }

        // randomize directions to grow
        int[] directionArray = new int[dropPoints];
        for (int k = 0; k < dropPoints; k++) {
            int direction = rnd.nextInt(8);
            directionArray[k] = direction;
        }
        System.out.println("initial drop points");
        printIntArray();
        // start growing map from drop points
        int[][] dropPointsArray2 = directionalGrow(dropPointsArray, directionArray, range);
        System.out.println("growing directionally round 1");
        printIntArray();

        int[][] dropPointsArray3 = directionalGrow(dropPointsArray2, directionArray, range);

        int[][] dropPointsArray4 = directionalGrow(dropPointsArray3, directionArray, range);
        //System.out.println("");
        //printIntArray();
        int[][] dropPointsArray5 = directionalGrow(dropPointsArray4, directionArray, range);
        int[][] dropPointsArray6 = directionalGrow(dropPointsArray5, directionArray, range);
        int[][] dropPointsArray7 = directionalGrow(dropPointsArray6, directionArray, range);
        System.out.println("grown.");
        printIntArray();
        //System.out.println("growing in 4 directions");
        grow(dropPointsArray, range);

        printIntArray();
        System.out.println("growing in 4 directions");
        grow(dropPointsArray2, range);
        //printIntArray();
        System.out.println("growing in 4 directions");
        grow(dropPointsArray3, range);
        //printIntArray();
        //System.out.println("growing in 4 directions");
        grow(dropPointsArray4, range);
        //printIntArray();
        System.out.println("growing in 4 directions");
        grow(dropPointsArray5, range);
        grow(dropPointsArray6, range);
        grow(dropPointsArray7, range);
        printIntArray();

        System.out.println("growing from center");
        growFromCenter(range);
        printIntArray();
        System.out.println("filling the rest yay");
        fillTheRest(range);
        printIntArray();
    }

    public void fillTheRest(int range) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!isAssigned(i, j)) {
                    randomizeOne(i, j, range);
                }
            }
        }

    }

    public void growFromCenter(int range) {

        // start from center
        int i = this.height / 2;
        int origI = i;
        int j = this.width / 2;
        int origJ = j;

        randomizeOne(i, j, range);

        // south, east and southeast:
        while (i < this.height && j < this.width) {
            i++;
            randomizeOne(i, j, range);
            growOne(i, j, range);
            randomizeOne(i, origJ, range);
            growOne(i, origJ, range);
            j++;

            randomizeOne(i, j, range);
            growOne(i, j, range);
            randomizeOne(origI, j, range);
            growOne(origI, j, range);
        }
        i = this.height / 2;
        j = this.width / 2;

        // north, west and northwest:
        while (i >= 0 && j >= 0) {
            i--;
            randomizeOne(i, j, range);
            growOne(i, j, range);

            randomizeOne(origI, j, range);
            growOne(origI, j, range);
            j--;
            randomizeOne(i, j, range);
            growOne(i, j, range);
            randomizeOne(origI, j, range);
            growOne(origI, j, range);

        }
        i = this.height / 2;
        j = this.width / 2;

        // northeast:
        while (i >= 0 && j < this.width) {
            randomizeOne(i, j, range);
            growOne(i, j, range);
            i--;

            randomizeOne(i, j, range);
            growOne(i, j, range);

            j++;

        }

        i = this.height / 2;
        j = this.width / 2;

        // southwest:
        while (i < this.width && j >= 0) {
            randomizeOne(i, j, range);
            growOne(i, j, range);
            i++;
            randomizeOne(i, j, range);
            growOne(i, j, range);
            j--;
        }
    }

    public void growOne(int i, int j, int range) {
        randomizeOne(i, j - 1, range);
        randomizeOne(i - 1, j, range);
        randomizeOne(i + 1, j, range);
        randomizeOne(i, j + 1, range);
    }

    public void grow(int[][] array, int range) {
        // expand map in four directions 
        for (int k = 0; k < array[0].length; k++) {
            randomizeOne(array[k][0], array[k][1] - 1, range);
            randomizeOne(array[k][0] - 1, array[k][1], range);
            randomizeOne(array[k][0] + 1, array[k][1], range);
            randomizeOne(array[k][0], array[k][1] + 1, range);
        }
    }

    public int[][] directionalGrow(int[][] dropPointsArray, int[] directionArray, int range) {
        // determine direction of grow
        //System.out.println("growing in random directions");
        for (int k = 0; k < dropPointsArray.length; k++) {
            int direction = directionArray[k];
            int newI = dropPointsArray[k][0];
            int newJ = dropPointsArray[k][1];
            //System.out.println("old I " + newI + ", old J " + newJ);

            // determine the coordinates of where to move
            if (direction < 3 && newI > 0) {
                newI--;
            } else if (direction > 4 && newI < height - 1) {
                newI++;
            }
            if ((direction == 0 || direction == 3 || direction == 5) && newJ > 0) {
                newJ--;
            } else if ((direction == 2 || direction == 4 || direction == 7)
                    && newJ < width - 1) {
                newJ++;
            }
            dropPointsArray[k][0] = newI;
            dropPointsArray[k][1] = newJ;
            //System.out.println("new I " + newI + ", new J " + newJ);

            if (intArray[newI][newJ] == 0) {
                randomizeOne(newI, newJ, range);
            }
        }
        return dropPointsArray;
    }

    public void randomizeOne(int i, int j, int range) {
        if (i < 0 || j < 0 || i >= this.height || j >= this.width) {
            return;
        }
        if (isAssigned(i, j)) {
            return;
        }
        if (range % 2 == 0) {
            System.out.println("Warning: range should be an odd number. Range is " + range);
        }

        int highestNeighbor = 0;
        int lowestNeighbor = 100;

        int assignedNeighbors = 0;
        int sumOfNeighbors = 0;

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
            intArray[i][j] = rnd.nextInt(20) + 1;
            System.out.println("free randomization done!");
            return;
        }

        // assign new tile a value within range
        int rndInt = (rnd.nextInt(range) - range / 2);
        //System.out.println(rndInt);
        //System.out.println(assignedNeighbors);
        //System.out.println(sumOfNeighbors);

        int avg = sumOfNeighbors / assignedNeighbors;

        // if two very disparate neighbors
        if (assignedNeighbors == 2 && highestNeighbor - avg > 5) {
            assignedNeighbors = 1;
            int coinToss = rnd.nextInt(2);
            if (coinToss == 0) {
                sumOfNeighbors = highestNeighbor;
            } else {
                sumOfNeighbors = lowestNeighbor;
            }
            //System.out.println("reassigned, sum of neighbors is " + sumOfNeighbors);
        }

        // take out biggest outliers
        if (assignedNeighbors > 2) {
            if (highestNeighbor - avg > 4) {
                sumOfNeighbors = sumOfNeighbors - highestNeighbor;
                assignedNeighbors--;
            }
            if (avg - lowestNeighbor > 4) {
                sumOfNeighbors = sumOfNeighbors - lowestNeighbor;
                assignedNeighbors--;
            }
        }

        //System.out.println("sum of neighbors: " + sumOfNeighbors);
        int newInt = sumOfNeighbors / assignedNeighbors + rndInt;
        // ensure that newInt is positive

        if (newInt < 1) {
            newInt = 1;
        }
        if (newInt > 20) {
            newInt = 20;
        }

        intArray[i][j] = newInt;
        //tileArray[i][j] = new Tile(newInt);
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

    public Tile[][] show() {

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
