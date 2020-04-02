/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapgenerator.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import mapgenerator.domain.MapCreator;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import mapgenerator.domain.Tile;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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
        settings.setPadding(new Insets(10, 0, 10, 10));

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

        generateButton.setOnAction((event) -> {
            int height = Integer.valueOf(fieldBox.getHeight());
            int width = Integer.valueOf(fieldBox.getWidth());
            int selectedVariability = varSwitch.getSelected();

            if (width < 5) {
                width = 5;
            }
            if (height < 5) {
                height = 5;
            }

            int variability = 3;

            if (selectedVariability == 2) {
                variability = 5;
            }

            boolean coastal = true;
            int selectedIsland = coastalSwitch.getSelected();

            if (selectedIsland == 2) {
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
