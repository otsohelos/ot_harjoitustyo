package com.github.otsohelos.mapgenerator.domain;

/**
 *
 * @author otsohelos
 */
public class MapCreator {

    private int height;
    private int width;
    private Map map;

    public MapCreator(int height, int width) {
        this.height = height;
        this.width = width;
    }
    //future feature: rivers
//    public void assignRivers() {
//        map.assignRivers();
//    }

    /**
     * Initializes Map, calls it to make a complete Tile array, gets and returns
     * that array.
     *
     * @param highVariability True = high variability, false = low variability
     * between neighboring squares
     * @param coastal True = coastal, false = inland
     * @return tileArray
     */
    public Tile[][] makeMap(boolean highVariability, boolean coastal) {
        this.map = new Map(height, width);
        map.assignTiles(highVariability, coastal);
        //assignRivers();
        return map.getTileArray();
    }

    /**
     * Check if user-given dimensions are within bounds.
     *
     * @return true or false
     */
    public boolean checkDimensions() {
        if (height < 40 || width < 40 || height > 260 || width > 260) {
            return false;
        }
        return true;
    }

    /**
     * Re-randomizes map terrain.
     */
    public void assignTerrain() {
        map.assignTerrain();
    }

    /**
     * Re-randomizes map terrain as either rainy or dry.
     *
     * @param rainy True = rainy, false = dry
     */
    public void assignTerrain(boolean rainy) {
        map.assignTerrain(rainy);
    }

    /**
     * Gets and returns a String that informs user of how rainy the Map is.
     *
     * @return
     */
    public String getRainfallString() {
        return map.getRainFallString();
    }
}
