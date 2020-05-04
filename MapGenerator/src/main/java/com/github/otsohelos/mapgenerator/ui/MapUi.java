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
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
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
            @Override
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

    public void viewAltitudeCanvas(Stage stage, int height, int width, boolean highVariability, boolean coastal, Tile[][] map, MapCreator mapCreator, boolean riversVisible) {
        // base variables
        int squareSize = 4;
        int canvasWidth = width * squareSize;
        int canvasHeight = height * squareSize;

        // main box
        BorderPane altitudeBox = new BorderPane();

        // Canvas and image
        Canvas altitudeCanvas = new Canvas(canvasWidth, canvasHeight);
        Image legend = new Image("altitudelegend.jpg");
        ImageView legendView = new ImageView(legend);

        // paint canvas
        if (riversVisible) {
            paintCanvas(map, altitudeCanvas, squareSize, "rivers");
        } else {
            paintCanvas(map, altitudeCanvas, squareSize, "altitude");
        }
        // sub-boxes
        BorderPane buttonPane = new BorderPane();
        HBox altitudeButtonBox = new HBox();
        VBox legendBox = new VBox();

        // buttons and labels
        Button redoButton = new Button("Redo");
        Button backButton = new Button("Back");

        Button saveAltitudeButton = new Button("Save...");
        Button showRiversButton = new Button("Show rivers");
        Button hideRiversButton = new Button("Hide rivers");
        Button redoRiversButton = new Button("Redo rivers");
        Button showTerrainButton = new Button("Show terrain");
        Pane spacer1 = new Pane();
        Pane spacer2 = new Pane();

        // set sizes and paddings
        altitudeBox.setMinSize(250, 250);
        spacer1.setMinWidth(30);
        spacer2.setMinWidth(30);

        altitudeButtonBox.setPadding(new Insets(0, 10, 10, 10));
        legendBox.setPadding(new Insets(10, 10, 10, 10));
        buttonPane.setPadding(new Insets(10, 10, 0, 0));

        // large button sizes
        showRiversButton.setMinWidth(85);
        hideRiversButton.setMinWidth(85);
        redoRiversButton.setMinWidth(85);
        showTerrainButton.setMinWidth(85);

        // small button sizes
        redoButton.setMinWidth(55);
        backButton.setMinWidth(55);

        // set everything in the right place
        altitudeButtonBox.getChildren().add(backButton);
        altitudeButtonBox.getChildren().add(redoButton);
        altitudeButtonBox.getChildren().add(spacer1);
        if (riversVisible) {
            altitudeButtonBox.getChildren().add(redoRiversButton);
            altitudeButtonBox.getChildren().add(hideRiversButton);
        } else {
            altitudeButtonBox.getChildren().add(showRiversButton);
        }
        altitudeButtonBox.getChildren().add(spacer2);
        altitudeButtonBox.getChildren().add(showTerrainButton);
        buttonPane.setLeft(altitudeButtonBox);
        buttonPane.setRight(saveAltitudeButton);

        legendBox.getChildren().add(legendView);

        altitudeBox.setCenter(altitudeCanvas);
        altitudeBox.setBottom(buttonPane);
        altitudeBox.setRight(legendBox);

        // alert of no riversVisible
        Alert noRiversAlert = new Alert(AlertType.INFORMATION);
        noRiversAlert.setContentText("There are no rivers in this area.");

        // button actions
        showTerrainButton.setOnAction((event3) -> {
            viewTerrainCanvas(stage, height, width, highVariability, coastal, map, mapCreator);
        });
        saveAltitudeButton.setOnAction((event2) -> {
            saveThisViewWithLegend(stage, width * squareSize, height * squareSize, altitudeCanvas, legend, 176);
        });
        backButton.setOnAction((event) -> {
            viewSettings(stage);
        });
        redoButton.setOnAction((event) -> {
            Tile[][] newMap = mapCreator.makeMap(highVariability, coastal);
            viewAltitudeCanvas(stage, height, width, highVariability, coastal, newMap, mapCreator, false);
        });

        showRiversButton.setOnAction((event) -> {
            if (!mapCreator.checkRivers()) {
                viewAltitudeCanvas(stage, height, width, highVariability, coastal, map, mapCreator, true);
                noRiversAlert.show();
            } else {
                viewAltitudeCanvas(stage, height, width, highVariability, coastal, map, mapCreator, true);
            }
        });

        hideRiversButton.setOnAction((event) -> {
            viewAltitudeCanvas(stage, height, width, highVariability, coastal, map, mapCreator, false);
        });

        redoRiversButton.setOnAction((event) -> {
            Tile[][] newMap = mapCreator.redoRivers();
            if (!mapCreator.checkRivers()) {
                viewAltitudeCanvas(stage, height, width, highVariability, coastal, newMap, mapCreator, true);
                noRiversAlert.show();
            } else {
                viewAltitudeCanvas(stage, height, width, highVariability, coastal, newMap, mapCreator, true);
            }
        });

        Scene altitudeView = new Scene(altitudeBox, Color.WHITE);

        // set focus on canvas so no buttons are focused
        altitudeCanvas.requestFocus();

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
        BorderPane terrainButtonBox = new BorderPane();
        VBox sideBox = new VBox();
        HBox rainyDryBox = new HBox();

        // Canvas
        Canvas terrainCanvas = new Canvas(canvasWidth, canvasHeight);

        // buttons & labels
        Button saveTerrainButton = new Button("Save...");
        saveTerrainButton.setAlignment(Pos.CENTER_RIGHT);

        Button altitudeButton = new Button("Altitude");
        String rainfallString = mapCreator.getRainfallString();
        Label rainfallLabel = new Label(rainfallString);
        Label redoTerrainLabel = new Label("Redo rainfall and terrain randomly:");
        Button redoTerrainButton = new Button("Redo");
        Label redoTerrainControlledLabel = new Label("Redo rainfall and terrain\nto be rainy or dry:");
        Button dryButton = new Button("Dry");
        Button rainyButton = new Button("Rainy");

        // images
        Image legend = new Image("legend.jpg");
        ImageView legendView = new ImageView(legend);

        // set sizes and paddings
        terrainBox.setMinSize(250, 250);
        terrainButtonBox.setPadding(new Insets(0, 10, 10, 10));
        sideBox.setPadding(new Insets(0, 10, 10, 10));
        sideBox.setSpacing(10);
        rainyDryBox.setSpacing(10);

        // small button sizes
        redoTerrainButton.setMinWidth(55);
        dryButton.setMinWidth(55);
        rainyButton.setMinWidth(55);
        altitudeButton.setMinWidth(55);
        saveTerrainButton.setMinWidth(55);

        // set everything in the right place
        terrainCanvasBox.getChildren().add(terrainCanvas);
        terrainBox.getChildren().add(terrainCanvasBox);
        sideBox.getChildren().add(rainfallLabel);
        sideBox.getChildren().add(legendView);
        sideBox.getChildren().add(redoTerrainLabel);
        sideBox.getChildren().add(redoTerrainButton);
        rainyDryBox.getChildren().add(rainyButton);
        rainyDryBox.getChildren().add(dryButton);
        sideBox.getChildren().add(redoTerrainControlledLabel);
        sideBox.getChildren().add(rainyDryBox);
        terrainCanvasBox.getChildren().add(sideBox);
        terrainBox.getChildren().add(terrainButtonBox);
        terrainButtonBox.setLeft(altitudeButton);
        terrainButtonBox.setRight(saveTerrainButton);

        // paint canvas    
        paintCanvas(map, terrainCanvas, squareSize, "terrain");

        // button actions
        saveTerrainButton.setOnAction((event2) -> {
            saveThisView(stage, width * squareSize + 190, height * squareSize, terrainCanvas);
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

        rainyButton.setOnAction((event6) -> {
            mapCreator.assignTerrain(true);
            String newRainfallString = mapCreator.getRainfallString();
            rainfallLabel.setText(newRainfallString);
            paintCanvas(map, terrainCanvas, squareSize, "terrain");
        });

        altitudeButton.setOnAction((event) -> {
            viewAltitudeCanvas(stage, height, width, highVariability, coastal, map, mapCreator, false);
        });

        Scene terrainView = new Scene(terrainBox, Color.WHITE);

        terrainCanvas.requestFocus();
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

    public void saveThisViewWithLegend(Stage stage, int imgWidth, int imgHeight, Canvas canvas, Image legend, int legendWidth) {

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
                        color = "rgb(0,95,136)";
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
