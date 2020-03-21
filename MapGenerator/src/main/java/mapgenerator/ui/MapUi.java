/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapgenerator.ui;

import javafx.application.Application;
import javafx.stage.Stage;
import domain.MapCreator;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

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
            int[][] map = mapCreator.showMap();
            FlowPane mapFlow = new FlowPane();
            for (int i = 0; i < map.length; i++) {
                VBox newBox = new VBox();
                for (int j = 0; j < map[i].length; j++) {
                    String string = String.valueOf(map[j][i]);
                    newBox.getChildren().add(new Label(string));
                }
                mapFlow.getChildren().add(newBox);
            }

            Scene mapView = new Scene(mapFlow);
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
