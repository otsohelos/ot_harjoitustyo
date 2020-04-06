package mapgenerator.domain;

public class Tile {

    private int elevation;
    private int topBorder;
    private int leftBorder;
    private int terrain;
    private int cutUnit;

    public Tile(int elevation) {
        this.elevation = elevation;
        this.topBorder = 0;
        this.leftBorder = 0;
        this.terrain = 0;
        this.cutUnit = 3;

    }

    public int getTerrain() {
        return terrain;
    }

    public int getElevation() {
        return elevation;
    }

    boolean isWater() {
        if (elevation < cutUnit * 3) {
            return true;
        }
        return false;
    }

    public String getColor() {
        // deep water:
        if (elevation < cutUnit) {
            return "rgb(0,115,196)";
        } else if (elevation < cutUnit * 2) {
            // mid-depth water
            return "rgb(42,149,244)";
        } else if (elevation < cutUnit * 3) {
            // shallow water
            return "rgb(149,205,255)";
        } else if (elevation < cutUnit * 7) {
            // lowland
            return "rgb(215,255,152)";
        } else if (elevation < cutUnit * 9) {
            // low-mid land
            return "rgb(254,255,129)";
        } else if (elevation < cutUnit * 11) {
            // high-mid land
            return "rgb(242,183,43)";
        } else {
            // highland
            return "rgb(156,98,14)";
        }
    }

    public void setLeftBorder(int i) {
        leftBorder = i;
    }

    public void setTopBorder(int i) {
        topBorder = i;
    }

    public int getTopBorder() {
        return topBorder;
    }

    public int getLeftBorder() {
        return leftBorder;
    }

}
