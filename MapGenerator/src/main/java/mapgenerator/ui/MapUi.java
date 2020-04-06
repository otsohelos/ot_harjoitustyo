/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapgenerator.ui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import mapgenerator.domain.MapCreator;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
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
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javafx.embed.swing.SwingFXUtils;

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
                    dimensionsAlert.setContentText("Give height and width as integers.\n\n Height and width should be between 10 and 150.");
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

                    viewMapCanvas(stage, height, width, variability, coastal, map, mapCreator);
                } else {
                    dimensionsAlert.setContentText("Height and width should be between 10 and 150.");
                    dimensionsAlert.show();
                }
            }
        };

        generateButton.setOnAction(moveToMapView);

        Scene settingsView = new Scene(settings);
        stage.setScene(settingsView);
        stage.show();
    }

    public void viewMapCanvas(Stage stage, int height, int width, int variability, boolean coastal, Tile[][] map, MapCreator mapCreator) {
        int squareSize = 5;
        int canvasWidth = width * squareSize;
        int canvasHeight = height * squareSize;
        Canvas mapCanvas = new Canvas(canvasWidth, canvasHeight);

        for (int i = 0; i < map.length; i++) {
            //System.out.println("");

            for (int j = 0; j < map[i].length; j++) {
                String color = map[i][j].getColor();
                //System.out.print(color + " ");
                //Rectangle rectangle = new Rectangle(rectSize, rectSize, Paint.valueOf(color));

                int top = map[i][j].getTopBorder();
                int left = map[i][j].getLeftBorder();
                int rectHeight = squareSize - top;
                int rectWidth = squareSize - left;

                GraphicsContext gc = mapCanvas.getGraphicsContext2D();

                gc.setFill(Paint.valueOf(color));
                gc.setLineWidth(0);

                gc.fillRect(j * squareSize + left, i * squareSize + top, rectWidth, rectHeight);

                // System.out.println("rectangle, height " + rectHeight + ", width " + rectWidth + ", color " + color);
            }
        }
        //FlowPane mapPane = new FlowPane();
        VBox mapBox = new VBox();
        mapBox.getChildren().add(mapCanvas);
        HBox mapButtonBox = new HBox();
        Button redoButton = new Button("Redo");
        mapButtonBox.getChildren().add(redoButton);
        redoButton.setOnAction((event) -> {
            Tile[][] newMap = mapCreator.showMap(variability, coastal);
            viewMapCanvas(stage, height, width, variability, coastal, newMap, mapCreator);
        });

        Button backButton = new Button("Back");
        mapButtonBox.getChildren().add(backButton);

        Button saveButton = new Button("Save...");

        saveButton.setOnAction((event2) -> {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Map");
            FileChooser.ExtensionFilter filter
                    = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
            fileChooser.getExtensionFilters().add(filter);
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                try {
                    WritableImage writableImage = new WritableImage(width * squareSize, height * squareSize);
                    mapCanvas.snapshot(null, writableImage);
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(renderedImage, "png", file);
                } catch (IOException e) {
                    System.out.println("Error!");
                }
            }

        });

        Button terrainButton = new Button("Terrain");
        terrainButton.setOnAction((event3) -> {
            mapCreator.assignTerrain();

            for (int i = 0; i < map.length; i++) {
                //System.out.println("");

                for (int j = 0; j < map[i].length; j++) {
                    String color = map[i][j].getTerrainColor();
                    //System.out.print(color + " ");
                    //Rectangle rectangle = new Rectangle(rectSize, rectSize, Paint.valueOf(color));

                    int top = map[i][j].getTopBorder();
                    int left = map[i][j].getLeftBorder();
                    int rectHeight = squareSize - top;
                    int rectWidth = squareSize - left;

                    GraphicsContext gc = mapCanvas.getGraphicsContext2D();

                    gc.setFill(Paint.valueOf(color));
                    gc.setLineWidth(0);

                    gc.fillRect(j * squareSize + left, i * squareSize + top, rectWidth, rectHeight);

                    // System.out.println("rectangle, height " + rectHeight + ", width " + rectWidth + ", color " + color);
                }
            }
            //FlowPane mapPane = new FlowPane();
        });
        mapButtonBox.getChildren().add(saveButton);
        mapButtonBox.getChildren().add(terrainButton);

        mapBox.getChildren().add(mapButtonBox);

        backButton.setOnAction((event) -> {
            viewSettings(stage);
        });

        Scene mapView = new Scene(mapBox);
        mapView.getStylesheets().add("mapstyle.css");

        stage.setScene(mapView);

    }

    public static void main(String[] args) {
        launch(MapUi.class);
    }
}
