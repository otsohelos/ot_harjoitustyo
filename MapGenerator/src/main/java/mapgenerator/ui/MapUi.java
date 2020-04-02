/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapgenerator.ui;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import javax.swing.SwingUtilities;
import java.io.File;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import mapgenerator.domain.MapCreator;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Paint;
import mapgenerator.domain.Tile;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

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
        settings.setPadding(new Insets(10,10,10,10));
        //settings.setMinWidth(200);
        //settings.setMinHeight(200);
        Label headerLabel = new Label("MapGenerator settings");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        settings.setPadding(new Insets(10,0,10,10));
        
        TextField heightField = new TextField();
        Label heightLabel = new Label("Height");
        TextField widthField = new TextField();
        Label widthLabel = new Label("Width");
        
        Tip sizeTip = new Tip("Values between 20 and 100 get best results.");

        HBox fieldBox = new HBox();
        settings.getChildren().add(headerLabel);
        fieldBox.getChildren().add(heightLabel);
        fieldBox.getChildren().add(heightField);
        fieldBox.getChildren().add(widthLabel);
        fieldBox.getChildren().add(widthField);
        fieldBox.getChildren().add(sizeTip.getTip());
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
        
        Tip varTip = new Tip("How quickly the elevation changes");
        
        variabilityBox.getChildren().add(varTip.getTip());

        settings.getChildren().add(variabilityBox);

        // set tendency towards coast or inland
        HBox islandBox = new HBox();
        RadioButton coastalButton = new RadioButton("Coastal");
        RadioButton inlandButton = new RadioButton("Inland");
        Label islandLabel = new Label("Land type");

        Tip coastTip = new Tip("\"Coastal\" is more likely to have large areas of water on the map than \"Inland\".");
        
        
        islandBox.getChildren().add(islandLabel);
        islandBox.getChildren().add(coastalButton);
        islandBox.getChildren().add(inlandButton);
        islandBox.getChildren().add(coastTip.getTip());

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

            viewMapCanvas(stage, height, width, variability, coastal);
        });
        Scene settingsView = new Scene(settings);
        stage.setScene(settingsView);
        stage.show();
    }

    public void viewMapCanvas(Stage stage, int height, int width, int variability, boolean coastal) {
        MapCreator mapCreator = new MapCreator(height, width);
        Tile[][] map = mapCreator.showMap(variability, coastal);
        int squareSize = 10;
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
                int rectHeight = 10 - top;
                int rectWidth = 10 - left;

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
            viewMapCanvas(stage, height, width, variability, coastal);
        });

        Button backButton = new Button("Back");
        mapButtonBox.getChildren().add(backButton);

        /*Button saveButton = new Button("Save");
        saveButton.setOnAction((event2) -> {
            FileChooser fileChooser = new FileChooser();

            FileChooser.ExtensionFilter extFilter
                    = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);

            //Show save file dialog
            File file = fileChooser.showSaveDialog(stage);
            
            if (file != null) {
                try {
                    WritableImage writableImage = new WritableImage(canvasWidth, canvasHeight);
                    BufferedImage image = new BufferedImage(mapCanvas.getWidth(), mapCanvas.getHeight(), BufferedImage.TYPE_INT_RGB);

                    Graphics2D graphics = (Graphics2D) image.getGraphics();
                    

                    ImageIO.write(SwingFXUtils.fromFXImage(snapshot, "png", file));
                } catch (IOException ex) {
                    Logger.getLogger(JavaFX_DrawOnCanvas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        })*/

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
