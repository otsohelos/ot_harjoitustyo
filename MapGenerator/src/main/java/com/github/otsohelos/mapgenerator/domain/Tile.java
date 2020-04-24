package com.github.otsohelos.mapgenerator.domain;

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
                // unassigned terrain is black
                // this should never happen except in testing
                return "rgb(0,0,0)";
        }
    }

    public void setLeftBorder() {
        leftBorder = !leftBorder;
    }

    public void setTopBorder() {
        topBorder = !topBorder;
    }

    public boolean getTopBorder() {
        return topBorder;
    }

    public boolean getLeftBorder() {
        return leftBorder;
    }

    public void setRainfall(int rainfall) {
        this.rainfall = rainfall;
    }

    public int getRainfall() {
        return this.rainfall;
    }

    public void assignTerrain() {
        if (elevation < multiplier * 3) {
            // it's water
            terrain = 1;
        } else if (elevation > multiplier * 12.5 || rainfall < 5) {
            // very high or dry squares are always desert
            terrain = 4;
        } else if (elevation < multiplier * 7) {
            // lowland
            if (rainfall > 9) {
                // wetlands
                terrain = 2;
            } else {
                // grassland
                terrain = 3;
            }
        } else if (elevation < multiplier * 11) {
            // mid land
            if (rainfall < 7) {
                // grassland
                terrain = 3;
            } else if (rainfall < 11) {
                // forest
                terrain = 5;
            } else {
                // wetlands
                terrain = 2;
            }
        } else {
            // highland
            if (rainfall > 8) {
                // grassland
                terrain = 3;
            } else {
                // desert
                terrain = 4;
            }
        }
    }

    public void setRiver() {
        isRiver = true;
    }

    public boolean isRiver() {
        return this.isRiver;
    }

}
