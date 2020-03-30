/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapgenerator.domain;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
    public void howManyDropPointsWorks() {

        assertTrue(map.howManyDropPoints() == 3);
        int bigMapDropPoints = bigMap.howManyDropPoints();
        assertTrue(bigMapDropPoints < 19);
        assertTrue(bigMapDropPoints > 11);
        int bigMapDropPoints2 = bigMap.howManyDropPoints();
        assertTrue(bigMapDropPoints2 < 19);
        assertTrue(bigMapDropPoints2 > 11);
        int bigMapDropPoints3 = bigMap.howManyDropPoints();
        assertTrue(bigMapDropPoints3 < 19);
        assertTrue(bigMapDropPoints3 > 11);
        assertTrue(tinyMap.howManyDropPoints() == 1);
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
        boolean overNine = false;

        map.assignTiles();

        map.getTileArray();
        int[][] intArray = map.getIntArray();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 15; j++) {
                if (intArray[i][j] < 0) {
                    underZero = true;
                }
                if (intArray[i][j] > 9) {
                    overNine = true;
                }
            }
        }

        assertFalse(underZero);
        assertFalse(overNine);

    }

    @Test
    public void randomizeLocationOfDropPointsWorks() {
        assertTrue(bigMap.randomizeLocationOfDropPoints(12).length == 12);
    }

    @Test
    public void randomizeSmarterWorks() {
        map.randomizeSmarter();
    }

    @Test
    public void isAssignedWorks() {
        assertFalse(tinyMap.isAssigned(0, 0));
        tinyMap.assignTiles();
        assertTrue(tinyMap.isAssigned(1, 1));
    }

    @Test
    public void randomizeOneReturnsValuesInRange() {
        tinyMap.randomizeOne(0, 0, 3);
        assertEquals(1, tinyMap.getIntArray()[0][0]);

    }

    @Test
    public void randomizeOneReturnsValuesInRange2() {
        tinyMap.setInt(0, 1, 10);
        tinyMap.setInt(1, 0, 12);
        tinyMap.randomizeOne(0, 0, 3);
        assertTrue(tinyMap.getIntArray()[0][0] < 13);
        assertTrue(tinyMap.getIntArray()[0][0] > 9);
    }
    
    @Test
    public void assignTilesAssignsTiles() {
        map.assignTiles();
        int elev1 = map.getTile(0, 0).getElevation();
        int elev2 = map.getTile(8, 10).getElevation();
        int elev3 = map.getTile(9, 14).getElevation();
        
        assertTrue(elev1 > 0);
        assertTrue(elev2 > 0);
        assertTrue(elev2 > 0);
    }
    
}
