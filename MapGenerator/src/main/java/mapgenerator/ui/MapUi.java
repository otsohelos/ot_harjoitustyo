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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Paint;
import mapgenerator.domain.Tile;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

/**
 *
 * @author otsohelos
 */
public class MapUi extends Application {

    @Override
    public void start(Stage stage) {
        viewSettings(stage);
    }

    public void viewSettings(Stage stage) {
        stage.setTitle("MapGenerator");
        VBox settings = new VBox();
        settings.setMinWidth(200);
        settings.setMinHeight(200);
        TextField heightField = new TextField();
        Label heightLabel = new Label("Height");
        TextField widthField = new TextField();
        Label widthLabel = new Label("Width");

        HBox fieldBox = new HBox();
        fieldBox.getChildren().add(heightLabel);
        fieldBox.getChildren().add(heightField);
        fieldBox.getChildren().add(widthLabel);
        fieldBox.getChildren().add(widthField);
        settings.getChildren().add(fieldBox);

        // make variability selection
        HBox variabilityBox = new HBox();
        RadioButton lowVar = new RadioButton("Low");
        RadioButton highVar = new RadioButton("High");

        // select low variability by default
        lowVar.setSelected(true);

        ToggleGroup variabilityGroup = new ToggleGroup();

        highVar.setToggleGroup(variabilityGroup);
        lowVar.setToggleGroup(variabilityGroup);

        Label varLabel = new Label("Variability of elevation");

        variabilityBox.getChildren().add(varLabel);
        variabilityBox.getChildren().add(lowVar);
        variabilityBox.getChildren().add(highVar);

        settings.getChildren().add(variabilityBox);

        // set tendency towards coast or inland
        HBox islandBox = new HBox();
        RadioButton coastalButton = new RadioButton("Coastal");
        RadioButton inlandButton = new RadioButton("Inland");
        Label islandLabel = new Label("Land type");
        Label varInfo = new Label("?");
        varInfo.setFont(new Font("Arial", 24));

        Tooltip varTip = new Tooltip("Coastal is more likely to have large areas of water on the map than Inland.");
        Tooltip.install(varInfo, varTip);

        islandBox.getChildren().add(islandLabel);
        islandBox.getChildren().add(coastalButton);
        islandBox.getChildren().add(inlandButton);
        islandBox.getChildren().add(varInfo);

        ToggleGroup islandGroup = new ToggleGroup();
        coastalButton.setToggleGroup(islandGroup);
        inlandButton.setToggleGroup(islandGroup);

        coastalButton.setSelected(true);

        settings.getChildren().add(islandBox);

        // Generate button
        Button generateButton = new Button("Generate");

        HBox generateBox = new HBox();
        generateBox.getChildren().add(generateButton);
        settings.getChildren().add(generateBox);

        generateButton.setOnAction((event) -> {
            int height = Integer.valueOf(heightField.getText());
            int width = Integer.valueOf(widthField.getText());
            RadioButton selectedVariability
                    = (RadioButton) variabilityGroup.getSelectedToggle();

            if (width < 5) {
                width = 5;
            }
            if (height < 5) {
                height = 5;
            }

            int variability = 3;

            if (selectedVariability == highVar) {
                variability = 5;
            }

            boolean coastal = true;
            RadioButton selectedIsland = (RadioButton) islandGroup.getSelectedToggle();

            if (selectedIsland == inlandButton) {
                coastal = false;
            }

            viewMap(stage, height, width, variability, coastal);
        });
        Scene settingsView = new Scene(settings);
        stage.setScene(settingsView);
        stage.show();
    }

    public void viewMap(Stage stage, int height, int width, int variability, boolean coastal) {
        MapCreator mapCreator = new MapCreator(height, width);
        Tile[][] map = mapCreator.showMap(variability, coastal);
        GridPane mapGrid = new GridPane();
        //mapGrid.setHgap(1);
        //mapGrid.setVgap(1);
        mapGrid.setMinHeight(200);
        mapGrid.setMinWidth(200);
        for (int i = 0; i < map.length; i++) {
            //System.out.println("");
            for (int j = 0; j < map[i].length; j++) {
                String color = map[i][j].getColor();
                //System.out.print(color + " ");
                Pane pane = new Pane();
                pane.setPrefSize(10, 10);
                pane.setMaxSize(10, 10);
                int top = map[i][j].getTopBorder();
                int left = map[i][j].getLeftBorder();
                int rectHeight = 10 - top;
                int rectWidth = 10 - left;
                Rectangle rectangle = new Rectangle(rectWidth, rectHeight, Paint.valueOf(color));
                rectangle.setY(top);
                rectangle.setX(left);
                pane.getChildren().add(rectangle);

                mapGrid.add(pane, j, i);
            }
        }
        FlowPane mapPane = new FlowPane();
        mapPane.getChildren().add(mapGrid);
        Button redoButton = new Button("Redo");
        mapPane.getChildren().add(redoButton);
        redoButton.setOnAction((event) -> {
            viewMap(stage, height, width, variability, coastal);
        });

        Button backButton = new Button("Back");
        mapPane.getChildren().add(backButton);

        backButton.setOnAction((event) -> {
            viewSettings(stage);
        });

        Scene mapView = new Scene(mapPane);
        mapView.getStylesheets().add("mapstyle.css");

        stage.setScene(mapView);
    }

    public static void main(String[] args) {
        launch(MapUi.class);
    }
}
