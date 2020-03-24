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
import javafx.scene.layout.Pane;

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
            //mapGrid.setHgap(1);
            //mapGrid.setVgap(1);
            mapGrid.setMinHeight(200);
            mapGrid.setMinWidth(200);
            for (int i = 0; i < map.length; i++) {
                            System.out.println("");

                for (int j = 0; j < map[i].length; j++) {
                    String color = map[i][j].getColor();
                    System.out.print(color + " ");
                    Pane pane = new Pane();
                    pane.setPrefSize(20, 20);
                        Rectangle rectangle = new Rectangle(20, 20, Paint.valueOf(color));
                        rectangle.setX(map[i][j].getTopBorder());
                        rectangle.setY(map[i][j].getLeftBorder());
                        pane.getChildren().add(rectangle);
                  
                    mapGrid.add(pane, i, j);
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
