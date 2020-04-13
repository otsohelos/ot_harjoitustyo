/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapgenerator.ui;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import mapgenerator.domain.MapCreator;
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
import mapgenerator.domain.Tile;
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
        VBox settings = new VBox();
        settings.setSpacing(10);
        settings.setPadding(new Insets(10, 10, 10, 10));
        Label headerLabel = new Label("MapGenerator settings");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        settings.getChildren().add(headerLabel);

        FieldBox fieldBox = new FieldBox();

        settings.getChildren().add(fieldBox.getBox());

        // set variability of elevation
        Switch varSwitch = new Switch("Variability of elevation", "Low", "High", "How quickly the elevation changes");
        settings.getChildren().add(varSwitch.getBox());

        // set tendency towards coast or inland
        Switch coastalSwitch = new Switch("Land type", "Coastal", "Inland",
                "\"Coastal\" is more likely to have large areas of water on the map than \"Inland\".");

        settings.getChildren().add(coastalSwitch.getBox());

        // Generate button
        Button generateButton = new Button("Generate");

        HBox generateBox = new HBox();
        generateBox.getChildren().add(generateButton);
        settings.getChildren().add(generateBox);
        Alert dimensionsAlert = new Alert(AlertType.ERROR);

        EventHandler<ActionEvent> moveToMapView = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                int width = -1;
                int height = -1;
                try {
                    height = Integer.valueOf(fieldBox.getHeight());
                    width = Integer.valueOf(fieldBox.getWidth());
                } catch (Exception exception) {
                    dimensionsAlert.setContentText("Give height and width as integers.\n\n Height and width should be between 10 and 200.");
                    dimensionsAlert.show();
                    return;
                }
                int selectedVariability = varSwitch.getSelected();

                int variability = 3;

                if (selectedVariability == 2) {
                    variability = 5;
                }

                boolean coastal = true;
                int selectedIsland = coastalSwitch.getSelected();

                if (selectedIsland == 2) {
                    coastal = false;
                }

                MapCreator mapCreator = new MapCreator(height, width);

                if (mapCreator.checkDimensions(height, width)) {

                    Tile[][] map = mapCreator.showMap(variability, coastal);

                    viewAltitudeCanvas(stage, height, width, variability, coastal, map, mapCreator);
                } else {
                    dimensionsAlert.setContentText("Height and width should be between 10 and 200.");
                    dimensionsAlert.show();
                }
            }
        };

        generateButton.setOnAction(moveToMapView);

        Scene settingsView = new Scene(settings);
        stage.setScene(settingsView);
        stage.show();
    }

    public void viewAltitudeCanvas(Stage stage, int height, int width, int variability, boolean coastal, Tile[][] map, MapCreator mapCreator) {
        int squareSize = 4;
        int canvasWidth = width * squareSize;
        int canvasHeight = height * squareSize;
        Canvas altitudeCanvas = new Canvas(canvasWidth, canvasHeight);

        for (int i = 0; i < map.length; i++) {
            //System.out.println("");

            for (int j = 0; j < map[i].length; j++) {
                String color = map[i][j].getColor();

                GraphicsContext gc = altitudeCanvas.getGraphicsContext2D();

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
        //FlowPane mapPane = new FlowPane();
        VBox altitudeBox = new VBox();
        altitudeBox.getChildren().add(altitudeCanvas);
        HBox altitudeButtonBox = new HBox();
        Button redoButton = new Button("Redo");
        altitudeButtonBox.getChildren().add(redoButton);
        redoButton.setOnAction((event) -> {
            Tile[][] newMap = mapCreator.showMap(variability, coastal);
            viewAltitudeCanvas(stage, height, width, variability, coastal, newMap, mapCreator);
        });

        Button backButton = new Button("Back");
        altitudeButtonBox.getChildren().add(backButton);

        Button saveAltitudeButton = new Button("Save...");

        saveAltitudeButton.setOnAction((event2) -> {
            saveThisView(stage, width * squareSize, height * squareSize, altitudeCanvas);
        });

        Button terrainButton = new Button("Terrain");

        terrainButton.setOnAction((event3) -> {
            //mapCreator.assignTerrain();
            viewTerrainCanvas(stage, height, width, variability, coastal, map, mapCreator);

        });

        altitudeButtonBox.getChildren().add(saveAltitudeButton);
        altitudeButtonBox.getChildren().add(terrainButton);

        altitudeBox.getChildren().add(altitudeButtonBox);

        backButton.setOnAction((event) -> {
            viewSettings(stage);
        });

        Scene altitudeView = new Scene(altitudeBox);
        altitudeView.getStylesheets().add("mapstyle.css");

        stage.setScene(altitudeView);
    }

    public void viewTerrainCanvas(Stage stage, int height, int width, int variability, boolean coastal, Tile[][] map, MapCreator mapCreator) {

        int squareSize = 4;
        int canvasWidth = width * squareSize;
        int canvasHeight = height * squareSize;
        Canvas terrainCanvas = new Canvas(canvasWidth, canvasHeight);
        Button saveTerrainButton = new Button("Save...");

        saveTerrainButton.setOnAction((event2) -> {
            saveThisView(stage, width * squareSize, height * squareSize, terrainCanvas);
        });
        VBox terrainBox = new VBox();
        HBox terrainCanvasBox = new HBox();
        terrainCanvasBox.getChildren().add(terrainCanvas);
        terrainBox.getChildren().add(terrainCanvasBox);
        HBox terrainButtonBox = new HBox();
        Scene terrainView = new Scene(terrainBox);

        Button altitudeButton = new Button("Altitude");
        for (int i = 0; i < map.length; i++) {
            //System.out.println("");

            for (int j = 0; j < map[i].length; j++) {
                String color = map[i][j].getTerrainColor();

                GraphicsContext gc = terrainCanvas.getGraphicsContext2D();

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
        String rainfallString = mapCreator.getRainfallString();
        Label rainfallLabel = new Label(rainfallString);

        Image legend = new Image("legend.jpg");
        ImageView legendView = new ImageView(legend);

        VBox legendBox = new VBox();
        legendBox.getChildren().add(rainfallLabel);
        legendBox.getChildren().add(legendView);

        Label redoTerrainLabel = new Label("Redo rainfall and terrain:");
        Button redoTerrainButton = new Button("Redo");
        legendBox.getChildren().add(redoTerrainLabel);
        legendBox.getChildren().add(redoTerrainButton);

        redoTerrainButton.setOnAction((event4) -> {
            mapCreator.assignTerrain();
            String newRainfallString = mapCreator.getRainfallString();
            rainfallLabel.setText(newRainfallString);
            for (int i = 0; i < map.length; i++) {
                //System.out.println("");

                // this is a repeptitive piece of code, maybe refactor
                for (int j = 0; j < map[i].length; j++) {
                    String color = map[i][j].getTerrainColor();

                    GraphicsContext gc = terrainCanvas.getGraphicsContext2D();

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

        });

        terrainCanvasBox.getChildren().add(legendBox);

        terrainBox.getChildren().add(terrainButtonBox);

        terrainButtonBox.getChildren().add(altitudeButton);
        terrainButtonBox.getChildren().add(saveTerrainButton);
        altitudeButton.setOnAction((event) -> {
            viewAltitudeCanvas(stage, height, width, variability, coastal, map, mapCreator);
        });
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

    public static void main(String[] args) {
        launch(MapUi.class);
    }
}
