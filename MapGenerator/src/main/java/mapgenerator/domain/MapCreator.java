/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapgenerator.domain;

/**
 *
 * @author otsohelos
 */
public class MapCreator {

    private Map map;

    public MapCreator() {
        map = new Map(12, 18);
    }

    public MapCreator(int height, int width) {
        map = new Map(height, width);
    }

    public Tile[][] showMap() {
        map.assignTiles();
        return map.getTileArray();
    }

}
