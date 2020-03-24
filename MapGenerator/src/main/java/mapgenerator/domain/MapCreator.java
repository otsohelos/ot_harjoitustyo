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

    private Map map;

    public MapCreator() {
        map = new Map(12, 18);
    }

    public Tile[][] showMap() {
        map.randomize();
        return map.show();
    }

}
