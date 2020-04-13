/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapgenerator.domain;

import org.junit.Before;
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
        this.landTile = new Tile(20);
    }

    @Test
    public void seaTileIsBlue() {
        assertEquals("rgb(0,115,196)", seaTile.getColor());
    }

    @Test
    public void landTileIsYellow() {
        assertEquals("rgb(155,210,75)", landTile.getColor());
    }

    @Test
    public void terrainReturnsZero() {
        assertEquals(0, landTile.getTerrain());
    }
    
    @Test
    public void unassignedBordersReturnFalse() {
        assertEquals(false, landTile.getLeftBorder());
        assertEquals(false, landTile.getTopBorder());
    }
    
    public void assignedBordersReturnTrue() {
        seaTile.setLeftBorder();
        seaTile.setTopBorder();
        assertEquals(true, seaTile.getLeftBorder());
        assertEquals(true, seaTile.getTopBorder());
    }
}
