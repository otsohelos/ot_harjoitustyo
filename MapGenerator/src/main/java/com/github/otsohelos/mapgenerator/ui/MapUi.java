/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.otsohelos.mapgenerator.ui;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import com.github.otsohelos.mapgenerator.domain.MapCreator;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import com.github.otsohelos.mapgenerator.domain.Tile;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

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

        // main box
        VBox settings = new VBox();
        settings.setSpacing(10);
        settings.setPadding(new Insets(10, 10, 10, 10));

        Label headerLabel = new Label("MapGenerator settings");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        settings.getChildren().add(headerLabel);

        // dimensions box
        FieldBox fieldBox = new FieldBox();
        settings.getChildren().add(fieldBox.getBox());

        // variability of elevation box
        Switch varSwitch = new Switch("Variability of elevation", "Low", "High", "How quickly the elevation changes");
        settings.getChildren().add(varSwitch.getBox());

        // coast or inland box
        Switch coastalSwitch = new Switch("Land type", "Coastal", "Inland",
                "\"Coastal\" is more likely to have large areas of water on the map than \"Inland\".");
        settings.getChildren().add(coastalSwitch.getBox());

        // Generate button and its box
        HBox generateBox = new HBox();
        Button generateButton = new Button("Generate");
        generateBox.getChildren().add(generateButton);
        settings.getChildren().add(generateBox);

        // alert for wrong dimensions
        Alert dimensionsAlert = new Alert(AlertType.ERROR);

        // event to move to map view
        EventHandler<ActionEvent> moveToMapView = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                int width = -1;
                int height = -1;

                // the following should probably be done in MapCreator
                try {
                    height = Integer.valueOf(fieldBox.getHeight());
                    width = Integer.valueOf(fieldBox.getWidth());
                } catch (Exception exception) {
                    dimensionsAlert.setContentText("Give height and width as integers.\n\n Height and width should be between 40 and 260.");
                    dimensionsAlert.show();
                    return;
                }

                int selectedVariability = varSwitch.getSelected();

                boolean highVariability = false;

                if (selectedVariability == 2) {
                    highVariability = true;
                }

                boolean coastal = true;
                int selectedIsland = coastalSwitch.getSelected();

                if (selectedIsland == 2) {
                    coastal = false;
                }

                MapCreator mapCreator = new MapCreator(height, width);

                if (mapCreator.checkDimensions()) {

                    Tile[][] tileMap = mapCreator.makeMap(highVariability, coastal);

                    viewAltitudeCanvas(stage, height, width, highVariability, coastal, tileMap, mapCreator, false);
                } else {
                    dimensionsAlert.setContentText("Height and width should be between 40 and 260.");
                    dimensionsAlert.show();
                }
            }
        };

        generateButton.setOnAction(moveToMapView);

        Scene settingsView = new Scene(settings);
        stage.setScene(settingsView);
        stage.show();
    }

    public void viewAltitudeCanvas(Stage stage, int height, int width, boolean highVariability, boolean coastal, Tile[][] map, MapCreator mapCreator, boolean rivers) {
        // base variables
        int squareSize = 4;
        int canvasWidth = width * squareSize;
        int canvasHeight = height * squareSize;

        // main box
        VBox altitudeBox = new VBox();

        // Canvas
        Canvas altitudeCanvas = new Canvas(canvasWidth, canvasHeight);

        // paint canvas
        if (rivers) {
            paintCanvas(map, altitudeCanvas, squareSize, "rivers");
        } else {

            paintCanvas(map, altitudeCanvas, squareSize, "altitude");
        }
        // sub-boxes
        HBox altitudeButtonBox = new HBox();

        // buttons and labels
        Button redoButton = new Button("Redo");
        Button backButton = new Button("Back");

        Button saveAltitudeButton = new Button("Save...");
        Button riversButton = new Button("Rivers");
        Button terrainButton = new Button("Terrain");

        // set everything in the right place
        altitudeButtonBox.getChildren().add(backButton);
        altitudeButtonBox.getChildren().add(redoButton);
        altitudeButtonBox.getChildren().add(saveAltitudeButton);
        //altitudeButtonBox.getChildren().add(riversButton);
        altitudeButtonBox.getChildren().add(terrainButton);
        altitudeBox.getChildren().add(altitudeCanvas);
        altitudeBox.getChildren().add(altitudeButtonBox);

        // button actions
        terrainButton.setOnAction((event3) -> {
            viewTerrainCanvas(stage, height, width, highVariability, coastal, map, mapCreator);
        });
        saveAltitudeButton.setOnAction((event2) -> {
            saveThisView(stage, width * squareSize, height * squareSize, altitudeCanvas);
        });
        backButton.setOnAction((event) -> {
            viewSettings(stage);
        });
        redoButton.setOnAction((event) -> {
            Tile[][] newMap = mapCreator.makeMap(highVariability, coastal);
            viewAltitudeCanvas(stage, height, width, highVariability, coastal, newMap, mapCreator, false);
        });

        riversButton.setOnAction((event) -> {
            viewAltitudeCanvas(stage, height, width, highVariability, coastal, map, mapCreator, true);
        });

        Scene altitudeView = new Scene(altitudeBox);
        stage.setScene(altitudeView);
    }

    public void viewTerrainCanvas(Stage stage, int height, int width, boolean highVariability, boolean coastal, Tile[][] map, MapCreator mapCreator) {
        // base variables
        int squareSize = 4;
        int canvasWidth = width * squareSize;
        int canvasHeight = height * squareSize;

        // main box        
        VBox terrainBox = new VBox();

        // sub-boxes
        HBox terrainCanvasBox = new HBox();
        HBox terrainButtonBox = new HBox();
        VBox legendBox = new VBox();
        HBox rainyDryBox = new HBox();

        // Canvas
        Canvas terrainCanvas = new Canvas(canvasWidth, canvasHeight);

        // buttons & labels
        Button saveTerrainButton = new Button("Save...");
        Button altitudeButton = new Button("Altitude");
        String rainfallString = mapCreator.getRainfallString();
        Label rainfallLabel = new Label(rainfallString);
        Label redoTerrainLabel = new Label("Redo rainfall and terrain randomly:");
        Button redoTerrainButton = new Button("Redo");
        Label redoTerrainControlledLabel = new Label("Redo rainfall and terrain to be rainy or dry:");
        Button dryButton = new Button("Dry");
        Button wetButton = new Button("Rainy");

        // images
        Image legend = new Image("legend.jpg");
        ImageView legendView = new ImageView(legend);

        // set everything in the right place
        terrainCanvasBox.getChildren().add(terrainCanvas);
        terrainBox.getChildren().add(terrainCanvasBox);
        legendBox.getChildren().add(rainfallLabel);
        legendBox.getChildren().add(legendView);
        legendBox.getChildren().add(redoTerrainLabel);
        legendBox.getChildren().add(redoTerrainButton);
        rainyDryBox.getChildren().add(wetButton);
        rainyDryBox.getChildren().add(dryButton);
        legendBox.getChildren().add(redoTerrainControlledLabel);
        legendBox.getChildren().add(rainyDryBox);
        terrainCanvasBox.getChildren().add(legendBox);
        terrainBox.getChildren().add(terrainButtonBox);
        terrainButtonBox.getChildren().add(altitudeButton);
        terrainButtonBox.getChildren().add(saveTerrainButton);

        // paint canvas    
        paintCanvas(map, terrainCanvas, squareSize, "terrain");

        // button actions
        saveTerrainButton.setOnAction((event2) -> {
            saveThisView(stage, width * squareSize, height * squareSize, terrainCanvas);
        });
        redoTerrainButton.setOnAction((event4) -> {
            mapCreator.assignTerrain();
            String newRainfallString = mapCreator.getRainfallString();
            rainfallLabel.setText(newRainfallString);
            paintCanvas(map, terrainCanvas, squareSize, "terrain");
        });
        dryButton.setOnAction((event5) -> {
            mapCreator.assignTerrain(false);
            String newRainfallString = mapCreator.getRainfallString();
            rainfallLabel.setText(newRainfallString);
            paintCanvas(map, terrainCanvas, squareSize, "terrain");
        });

        wetButton.setOnAction((event6) -> {
            mapCreator.assignTerrain(true);
            String newRainfallString = mapCreator.getRainfallString();
            rainfallLabel.setText(newRainfallString);
            paintCanvas(map, terrainCanvas, squareSize, "terrain");
        });

        altitudeButton.setOnAction((event) -> {
            viewAltitudeCanvas(stage, height, width, highVariability, coastal, map, mapCreator, false);
        });

        Scene terrainView = new Scene(terrainBox);
        stage.setScene(terrainView);
    }

    public void saveThisView(Stage stage, int imgWidth, int imgHeight, Canvas canvas) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Map");
        FileChooser.ExtensionFilter filter
                = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage(imgWidth, imgHeight);
                canvas.snapshot(null, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
            } catch (IOException e) {
                System.out.println("Error!");
            }
        }
    }

    public void paintCanvas(Tile[][] map, Canvas canvas, int squareSize, String mode) {
        for (int i = 0; i < map.length; i++) {
            //System.out.println("");

            for (int j = 0; j < map[i].length; j++) {
                String color = "";
                if (mode.equals("terrain")) {
                    color = map[i][j].getTerrainColor();
                } else if (mode.equals("altitude")) {
                    color = map[i][j].getColor();
                } else if (mode.equals("rivers")) {
                    if (map[i][j].isRiver()) {
                        color = "rgb(0,0,0)";
                    } else {
                        color = map[i][j].getColor();
                    }
                }

                GraphicsContext gc = canvas.getGraphicsContext2D();

                gc.setFill(Paint.valueOf(color));
                gc.setLineWidth(0);

                gc.fillRect(j * squareSize, i * squareSize, squareSize, squareSize);
                gc.setLineWidth(1);
                gc.setStroke(Color.BLACK);
                // draw borders if there are any
                if (map[i][j].getTopBorder()) {
                    gc.strokeLine(j * squareSize, i * squareSize, j * squareSize + squareSize, i * squareSize);
                }
                if (map[i][j].getLeftBorder()) {
                    gc.strokeLine(j * squareSize, i * squareSize, j * squareSize, i * squareSize + squareSize);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(MapUi.class);
    }
}
