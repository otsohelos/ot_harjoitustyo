package com.github.otsohelos.mapgenerator.domain;

/**
 * Basic unit on a Map; holds information about terrain, elevation, and whether
 * it is a river.
 *
 * @author otsohelos
 */
public class Tile {

    private final int elevation;
    private boolean topBorder;
    private boolean leftBorder;
    private int terrain;
    private final int multiplier;
    private int rainfall;
    private boolean isRiver;

    public Tile(int elevation) {
        this.elevation = elevation;
        this.topBorder = false;
        this.leftBorder = false;
        this.terrain = 0;
        this.multiplier = 3;
        this.rainfall = 0;
        this.isRiver = false;
    }

    // Color getters:
    /**
     * Returns basic elevation color.
     *
     * @return RGB color string
     */
    public String getColor() {
        // deep water:
        if (elevation < multiplier) {
            return "rgb(0,115,196)";
        } else if (elevation < multiplier * 2) {
            // mid-depth water
            return "rgb(42,149,244)";
        } else if (elevation < multiplier * 3) {
            // shallow water
            return "rgb(149,205,255)";
        } else if (elevation < multiplier * 7) {
            // lowland
            return "rgb(155,210,75)";
        } else if (elevation < multiplier * 9) {
            // low-mid land
            return "rgb(254,255,129)";
        } else if (elevation < multiplier * 11) {
            // high-mid land
            return "rgb(242,183,43)";
        } else {
            // highland
            return "rgb(156,98,14)";
        }
    }

    // Terrain assigner:
    /**
     * Returns color string for terrain.
     *
     * @return RGB color string
     */
    public String getTerrainColor() {
        switch (terrain) {
            case 1:
                // water
                return "rgb(42,149,244)";
            case 2:
                // wetland
                return "rgb(210,220,229)";
            case 3:
                // grassland
                return "rgb(155,210,75)";
            case 4:
                // desert
                return "rgb(242,183,42)";
            case 5:
                // forest
                return "rgb(80,168,0)";
            default:
                // unassigned terrain is black; this should never happen
                return "rgb(0,0,0)";
        }
    }

    /**
     * Determines the terrain of this tile based on elevation and rainfall.
     */
    public void assignTerrain() {
        if (elevation < multiplier * 3) {
            // it's water
            terrain = 1;
        } else if (elevation > multiplier * 12.5 || rainfall < 6) {
            // very high or dry squares are always desert
            terrain = 4;
        } else if (elevation < multiplier * 7 && rainfall > 9) {
            // lowland: wetlands
            terrain = 2;
        } else if (elevation < multiplier * 7) {
            // lowland: grassland
            terrain = 3;
        } else if (elevation < multiplier * 11 && rainfall < 7) {
            // mid land: grassland
            terrain = 3;
        } else if (elevation < multiplier * 11 && rainfall < 11) {
            // mid land: forest
            terrain = 5;
        } else if (elevation < multiplier * 11) {
            // mid land: wetlands
            terrain = 2;
        } else if (rainfall > 8) {
            // highland: grassland
            terrain = 3;
        } else {
            // desert
            terrain = 4;
        }
    }

    // Setters:
    public void setLeftBorder() {
        leftBorder = !leftBorder;
    }

    public void setTopBorder() {
        topBorder = !topBorder;
    }

    public void setRainfall(int rainfall) {
        this.rainfall = rainfall;
    }

    public void setRiver(boolean value) {
        if (!isWater()) {
            isRiver = value;
        }
    }

    // Basic getters:
    public boolean getTopBorder() {
        return topBorder;
    }

    public boolean getLeftBorder() {
        return leftBorder;
    }

    public int getRainfall() {
        return this.rainfall;
    }

    public int getTerrain() {
        return terrain;
    }

    public int getElevation() {
        return elevation;
    }

    boolean isWater() {
        if (elevation < multiplier * 3) {
            return true;
        }
        return false;
    }

    public boolean isRiver() {
        return this.isRiver;
    }
}
