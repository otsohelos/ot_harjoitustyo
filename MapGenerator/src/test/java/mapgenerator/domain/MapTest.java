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

    @Before
    public void setUp() {
        this.map = new Map(10, 15);
    }

    @Test
    public void mapReturnsTileArrayOfCorrectSize() {
        Tile[][] tileArray = map.show();

        assertEquals(10, tileArray.length);
        assertEquals(15, tileArray[0].length);
    }

    @Test
    public void intArrayHasCorrectAmountOfNumbers() {
        map.randomize();
        map.show();
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

        map.randomize();

        map.show();
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

        map.randomize();

        map.show();
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

}
