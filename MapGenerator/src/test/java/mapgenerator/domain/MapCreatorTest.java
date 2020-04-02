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
public class MapCreatorTest {

    MapCreator mapCreator;
    Tile[][] tileMap;

    @Before
    public void setUp() {
        this.mapCreator = new MapCreator(50, 60);
        this.tileMap = mapCreator.showMap(3, true);
    }

    @Test
    public void tilesAreAssigned() {
        int elev1 = tileMap[30][30].getElevation();
        int elev2 = tileMap[20][25].getElevation();
        int elev3 = tileMap[10][45].getElevation();

        // since one of these may be unassigned
        // but it's extremely unlilkely that all of them are
        // let's check that at least one is assigned
        boolean someAssigned = false;
        if (elev1 + elev2 + elev3 > 0) {
            someAssigned = true;
        }

        // let's do it again if we still get false
        if (someAssigned == false) {
            tileMap = mapCreator.showMap(3, true);
            elev1 = tileMap[30][30].getElevation();
            elev2 = tileMap[20][25].getElevation();
            elev3 = tileMap[10][45].getElevation();
            if (elev1 + elev2 + elev3 > 0) {
                someAssigned = true;
            }
        }
        assertTrue(someAssigned);
    }

}
