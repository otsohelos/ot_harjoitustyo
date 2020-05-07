package com.github.otsohelos.mapgenerator.domain;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author otsohelos
 */
public class RiverMakerTest {

    //RiverMaker riverMaker;
    //RiverMaker tinyRiverMaker;
    Map map;
    Map tinyMap;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.map = new Map(80, 100);
        this.tinyMap = new Map(10, 10);
    }

    @Test
    public void findHighestWorks() {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 24; j++) {
                map.setInt(i, j, 1);
            }
        }
        RiverMaker newRiverMaker = new RiverMaker(map.getIntArray(), map.getTileArray());
        int[][] highest = newRiverMaker.findHighest();
        assertEquals(0, highest[highest.length - 1][0]);
        assertEquals(0, highest[highest.length - 1][1]);
    }

    @Test
    public void findHighestSmallWorks() {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 24; j++) {
                map.setInt(i, j, 1);
            }
        }

        map.setInt(0, 4, 5);
        map.setInt(0, 5, 5);
        map.setInt(1, 4, 5);

        RiverMaker newRiverMaker = new RiverMaker(map.getIntArray(), map.getTileArray());
        int[][] highest = newRiverMaker.findHighest();
        int[][] highestSmall = newRiverMaker.findHighestFromArray(highest);
        assertEquals(0, highestSmall[highest.length - 1][0]);
        assertEquals(1, highestSmall[highest.length - 1][1]);
    }

    @Test
    public void directionsWork() {
        RiverMaker riverMaker = new RiverMaker(map.getIntArray(), map.getTileArray());

        int[][] route = {{1, 2}, {1, 3}};
        String[] directions = riverMaker.makeDirections(route, 1);
        assertEquals(2, directions.length);
        assertEquals("e", directions[0]);
    }
}
