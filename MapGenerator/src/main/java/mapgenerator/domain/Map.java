package mapgenerator.domain;

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

    public Map(int height, int width) {
        this.height = height;
        this.width = width;
        this.intArray = new int[height][width];
        this.tileArray = new Tile[height][width];
    }

    public void randomize() {
        Random rnd = new Random();
        int initialInt = (rnd.nextInt(9));
        tileArray[0][0] = new Tile(initialInt);
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
                    if (newInt > 9) {
                        newInt = 9;
                    }
                    if (newInt < 0) {
                        newInt = 0;
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
        printIntArray();
    }

    public void randomizeOne(int i, int j, int seed, int range) {

    }

    public int randomizeSmarter() {

        // determine amount of initial free points
        int dropPoints = howManyDropPoints();
        // create array for first points, with i + j coordinates
        int[][] dropPointsArray = new int[dropPoints][2];

        // randomize location of drop points
        for (int i = 0; i < dropPoints; i++) {
            dropPointsArray[i][0] = i;

        }
        return dropPoints;
    }

    public int howManyDropPoints() {

        int tiles = this.height * this.width;
        Random rnd = new Random();
        // determine amount of initial free points, with randomness :
        int dropPoints = tiles / 40;
        int newSeed = dropPoints / 2;

        // add randomness into number of free points
        if (newSeed > 1) {
            dropPoints = dropPoints + rnd.nextInt(newSeed) - (newSeed / 2);
        }
        // one drop point for tiny maps
        if (dropPoints < 1) {
            dropPoints = 1;
        }
        System.out.println("dropPoints is " + dropPoints);
        return dropPoints;
    }

    public Tile[][] show() {

        return tileArray;
    }

    public void printIntArray() {
        for (int i = 0; i < height; i++) {
            System.out.println("");
            for (int j = 0; j < width; j++) {
                System.out.print(intArray[i][j] + " ");
            }
        }
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
}
