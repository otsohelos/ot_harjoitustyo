package com.github.otsohelos.mapgenerator.domain;

/**
 * Creates a Map and a RiverMaker; acts as intermediary between those and UI.
 *
 * @author otsohelos
 */
public class MapCreator {

    private int height;
    private int width;
    private Map map;
    private RiverMaker riverMaker;

    public MapCreator(int height, int width) {
        this.height = height;
        this.width = width;
    }

    /**
     * Assigns RiverMaker and calls it to make the rivers.
     *
     * @param intArray
     * @param tileArray
     * @return
     */
    public Tile[][] assignRivers(int[][] intArray, Tile[][] tileArray) {
        this.riverMaker = new RiverMaker(intArray, tileArray);
        riverMaker.makeRivers();
        Tile[][] newTileArray = riverMaker.getTileArray();
        return newTileArray;
    }

    /**
     * Removes rivers and assigns new ones.
     * @return new tile array
     */
    public Tile[][] redoRivers() {
        this.riverMaker = new RiverMaker(map.getIntArray(), map.getTileArray());
        riverMaker.resetRivers();
        riverMaker.makeRivers();
        Tile[][] newTileArray = riverMaker.getTileArray();
        return newTileArray;
    }

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
        Tile[][] tileArray = map.getTileArray();
        int[][] intArray = map.getIntArray();
        Tile[][] tileArrayWithRivers = assignRivers(intArray, tileArray);
        return tileArrayWithRivers;
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
     * Checks if there are any rivers.
     *
     * @return
     */
    public boolean checkRivers() {
        return riverMaker.hasRivers();
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
