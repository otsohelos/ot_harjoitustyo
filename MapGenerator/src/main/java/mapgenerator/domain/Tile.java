package mapgenerator.domain;

public class Tile {

    private int elevation;
    private int topBorder;
    private int leftBorder;

    public Tile(int elevation) {
        this.elevation = elevation;
        this.topBorder = 0;
        this.leftBorder = 0;
    }

    public int getElevation() {
        return elevation;
    }

    boolean isWater() {
        if (elevation < 3) {
            return true;
        }
        return false;
    }

    public String getColor() {
        if (elevation < 2) {
            return "rgb(67, 127, 255)";
        } else if (elevation < 3) {
            return "rgb(133, 200, 255)";
        } else if (elevation < 5) {
            return "rgb(73, 151, 1)";
        } else {
            return "rgb(66, 88, 4)";
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
