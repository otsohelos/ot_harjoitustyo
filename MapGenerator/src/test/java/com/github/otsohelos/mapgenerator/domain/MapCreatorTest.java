/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.otsohelos.mapgenerator.domain;

import com.github.otsohelos.mapgenerator.domain.MapCreator;
import com.github.otsohelos.mapgenerator.domain.Tile;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author otsohelos
 */
public class MapCreatorTest {

    MapCreator mapCreator;
    Tile[][] tileMap;

    @Before
    public void setUp() {
        this.mapCreator = new MapCreator(50, 60);
        this.tileMap = mapCreator.makeMap(false, true);
    }

    @Test
    public void tilesAreAssigned() {
        int elev1 = tileMap[30][30].getElevation();
        int elev2 = tileMap[20][25].getElevation();
        int elev3 = tileMap[10][45].getElevation();

        // since any one of these may be unassigned
        // but it's extremely unlilkely that all of them are
        // let's check that at least one is assigned
        boolean someAssigned = false;
        if (elev1 + elev2 + elev3 > 0) {
            someAssigned = true;
        }

        // let's do it again if we still get false
        if (someAssigned == false) {
            tileMap = mapCreator.makeMap(false, true);
            elev1 = tileMap[30][30].getElevation();
            elev2 = tileMap[20][25].getElevation();
            elev3 = tileMap[10][45].getElevation();
            if (elev1 + elev2 + elev3 > 0) {
                someAssigned = true;
            }
        }
        assertTrue(someAssigned);
    }

    @Test
    public void wrongDimensionsDontWork() {
        MapCreator tooWide = new MapCreator(100, 400);
        MapCreator tooHigh = new MapCreator(400, 100);
        MapCreator tooNarrow = new MapCreator(100, 10);
        MapCreator tooLow = new MapCreator(10, 100);

        assertFalse(tooWide.checkDimensions());
        assertFalse(tooHigh.checkDimensions());
        assertFalse(tooNarrow.checkDimensions());
        assertFalse(tooLow.checkDimensions());
    }

    @Test
    public void goodDimensionsWork() {
        MapCreator good1 = new MapCreator(40, 100);
        MapCreator good2 = new MapCreator(100, 260);

        assertTrue(good1.checkDimensions());
        assertTrue(good2.checkDimensions());
    }

    @Test
    public void assignTerrainAssignsTerrain() {
        mapCreator.assignTerrain();

        // tile terrain color is not black
        assertFalse(tileMap[25][30].getTerrainColor().equals("rgb(0,0,0)"));

        // tile has rgb terrain color
        assertEquals("rgb(", tileMap[25][30].getTerrainColor().substring(0, 4));
    }
    
    @Test
    public void rainfallStringReturnsGoodValues(){
        String rain1 = mapCreator.getRainfallString();
        assertTrue(rain1.matches("This area is (wet|dry).\nAverage rainfall [0-6] out of 6."));
        mapCreator.assignTerrain();
        String rain2 = mapCreator.getRainfallString();
        assertTrue(rain2.matches("This area is (wet|dry).\nAverage rainfall [0-6] out of 6."));
    }

}
