/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapgenerator.domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;

/**
 *
 * @author otsohelos
 */
public class MapTest {

    Map map;
    Map bigMap;
    Map tinyMap;

    @Before
    public void setUp() {
        this.map = new Map(10, 15);
        this.bigMap = new Map(25, 25);
        this.tinyMap = new Map(3, 3);
    }

    @Test
    public void mapReturnsTileArrayOfCorrectSize() {
        Tile[][] tileArray = map.getTileArray();

        assertEquals(10, tileArray.length);
        assertEquals(15, tileArray[0].length);
    }

    @Test
    public void intArrayHasCorrectAmountOfNumbers() {
        map.assignTiles();
        map.getTileArray();
        int[][] intArray = map.getIntArray();

        assertEquals(10, intArray.length);
        assertEquals(15, intArray[0].length);
    }

    @Test
    public void intArrayCreatesSomeNonZeroNumbers() {
        boolean nonZero = false;
        int[][] intArray = map.getIntArray();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 15; j++) {
                if (nonZero) {
                    break;
                }
                if (intArray[i][j] != 0) {
                    nonZero = true;
                    break;
                }
            }
        }

        assertFalse(nonZero);

        map.assignTiles();

        map.getTileArray();
        int[][] intArray2 = map.getIntArray();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 15; j++) {
                if (nonZero) {
                    break;
                }
                if (intArray2[i][j] != 0) {
                    nonZero = true;
                    break;
                }
            }
        }
        assertTrue(nonZero);
    }

    @Test
    public void intArrayNumbersStayWithinBounds() {
        boolean underZero = false;
        boolean overUpperLimit = false;

        map.assignTiles();

        int[][] intArray = map.getIntArray();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 15; j++) {
                if (intArray[i][j] < 0) {
                    underZero = true;
                }
                if (intArray[i][j] > map.getMaxElevation()) {
                    overUpperLimit = true;
                }
            }
        }
        assertFalse(underZero);
        assertFalse(overUpperLimit);

    }

    @Test
    public void isAssignedWorks() {
        assertFalse(tinyMap.isAssigned(1, 1));
        tinyMap.assignTiles();
        assertTrue(tinyMap.isAssigned(1, 1));
    }

    @Test
    public void randomizeOneReturnsValuesInRange() {
        //tinyMap.setInt(0, 0, 0);
        tinyMap.randomizeOne(0, 0);
        //System.out.println("randomized nr is " + tinyMap.getIntArray()[0][0]);
        assertTrue(tinyMap.getIntArray()[0][0] < 40);
        assertTrue(tinyMap.getIntArray()[0][0] > 0);

    }

    @Test
    public void randomizeOneReturnsValuesInRange2() {
        //tinyMap.setInt(0, 0, 0);
        tinyMap.setInt(0, 1, 10);
        tinyMap.setInt(1, 0, 12);
        tinyMap.randomizeOne(0, 0);
        assertTrue(tinyMap.getIntArray()[0][0] < 13);
        assertTrue(tinyMap.getIntArray()[0][0] > 8);
    }

    @Test
    public void assignTilesAssignsTiles() {
        map.assignTiles();
        int elev1 = map.getTile(5, 5).getElevation();
        int elev2 = map.getTile(8, 10).getElevation();
        int elev3 = map.getTile(3, 12).getElevation();

        // since some tiles may be left unassigned
        // but it's extremely unlikely all three will be unassigned
        // assign value someAssigned to signify some of these are assigned
        boolean someAssigned = false;

        if (elev1 > 0 || elev2 > 0 || elev3 > 0) {
            someAssigned = true;
        }
        assertTrue(someAssigned);
    }

}
