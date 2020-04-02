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

/**
 *
 * @author otsohelos
 */
public class TileTest {

    Tile seaTile;
    Tile landTile;

    @Before
    public void setUp() {
        this.seaTile = new Tile(2);
        this.landTile = new Tile(15);
    }

    @Test
    public void seaTileIsBlue() {
        assertEquals("rgb(42,149,244)", seaTile.getColor());
    }

    @Test
    public void landTileIsYellow() {
        assertEquals("rgb(242,183,43)", landTile.getColor());
    }

    @Test
    public void terrainReturnsZero() {
        assertEquals(0, landTile.getTerrain());
    }
    
    @Test
    public void unassignedBordersReturnZero() {
        assertEquals(0, landTile.getLeftBorder());
        assertEquals(0, landTile.getTopBorder());
    }
    
    public void assignedBordersReturnOne() {
        seaTile.setLeftBorder(1);
        seaTile.setTopBorder(1);
        assertEquals(1, seaTile.getLeftBorder());
        assertEquals(1, seaTile.getTopBorder());
    }
}
