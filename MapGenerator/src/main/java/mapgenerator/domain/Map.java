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

    public void randomizeSmaller() {
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
