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

    private int height;
    private int width;
    private Map map;
    private boolean terrainAssigned;

    public MapCreator(int height, int width) {
        this.height = height;
        this.width = width;
        this.terrainAssigned = false;
    }

    public Tile[][] showMap() {
        this.map = new Map(height, width);
        map.assignTiles();
        return map.getTileArray();
    }

    public Tile[][] showMap(int variability, boolean coastal) {
        this.map = new Map(height, width);
        map.assignTiles(variability, coastal);
        return map.getTileArray();
    }

    public boolean checkDimensions(int height, int width) {
        if (height < 10 || width < 10 || height > 150 || width > 150) {
            return false;
        }
        return true;
    }

    public boolean assignTerrain() {
        if (!terrainAssigned) {
            terrainAssigned = true;
            map.assignTerrain();
            return false;
        }
        return true;
    }

    public boolean terrainIsAssigned() {
        return this.terrainAssigned;
    }
}
