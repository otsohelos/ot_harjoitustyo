/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.otsohelos.mapgenerator.domain;

import org.junit.After;
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
        //this.riverMaker = new RiverMaker(map.getIntArray(), map.getTileArray());
        //this.tinyRiverMaker = new RiverMaker(tinyMap.getIntArray(), tinyMap.getTileArray());
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

}
