/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapgenerator.ui;

import javafx.application.Application;
import javafx.stage.Stage;
import mapgenerator.domain.MapCreator;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Paint;
import mapgenerator.domain.Tile;

/**
 *
 * @author otsohelos
 */
public class MapUi extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("MapGenerator");
        FlowPane settings = new FlowPane();
        Button generateButton = new Button("Generate");
        settings.getChildren().add(generateButton);
        generateButton.setOnAction((event) -> {
            MapCreator mapCreator = new MapCreator();
            Tile[][] map = mapCreator.showMap();
            GridPane mapGrid = new GridPane();
            mapGrid.setHgap(1);
            mapGrid.setVgap(1);
            mapGrid.setMinHeight(200);
            mapGrid.setMinWidth(200);

            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                     String color = map[j][i].getColor();

                      Rectangle green = new Rectangle(25, 25, Paint.valueOf(color));
                      mapGrid.add(green, j, i);
                }
            }

            Scene mapView = new Scene(mapGrid);
            mapView.getStylesheets().add("mapstyle.css");

            stage.setScene(mapView);

        });
        Scene settingsView = new Scene(settings);
        stage.setScene(settingsView);
        stage.show();
    }

//    @Override
//    public void init() {
//        MapCreator mapCreator = new MapCreator();
//
//    }
    public static void main(String[] args) {
        launch(MapUi.class);

    }
}
