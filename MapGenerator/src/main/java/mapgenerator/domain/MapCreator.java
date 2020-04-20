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
    //private boolean terrainAssigned;

    public MapCreator(int height, int width) {
        this.height = height;
        this.width = width;
        //this.terrainAssigned = false;
    }

    public Tile[][] makeMap(boolean highVariability, boolean coastal) {
        this.map = new Map(height, width);
        map.assignTiles(highVariability, coastal);
        return map.getTileArray();
    }

    public boolean checkDimensions() {
        if (height < 40 || width < 40 || height > 260 || width > 260) {
            return false;
        }
        return true;
    }

    public void assignTerrain() {
        map.assignTerrain();
    }

    public void assignTerrain(boolean rainy) {
        map.assignTerrain(rainy);
    }

    public String getRainfallString() {
        return map.getRainFallString();
    }
}
