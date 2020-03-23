package mapgenerator.domain;

public class Tile {

    private int elevation;
    private String color;

    public Tile(int elevation) {
        this.elevation = elevation;
    }

    public int getElevation() {
        return elevation;
    }

    public String getColor() {
        if (elevation < 3) {
            return "rgb(133, 200, 255)";
        } else {
            return "rgb(51, 104, 1)";
        }
    }
}
