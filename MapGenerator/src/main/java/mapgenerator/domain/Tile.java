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
        if (elevation < 6) {
            return true;
        }
        return false;
    }

    public String getColor() {
        if (elevation < 2) {
            return "rgb(35, 50, 120)";
        } else if (elevation < 4) {
            return "rgb(67, 127, 255)";
        } else if (elevation < 6) {
            return "rgb(133, 200, 255)";
        } else if (elevation < 10) {
            return "rgb(73, 151, 1)";
        } else if (elevation < 15) {
            return "rgb(75, 99, 4)";
        } else if (elevation < 18) {
            return "rgb(43, 43, 7)";
        } else {
            return "rgb(15, 15, 9)";
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
