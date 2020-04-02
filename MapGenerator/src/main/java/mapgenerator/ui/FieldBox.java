/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapgenerator.ui;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 *
 * @author otsohelos
 */
class FieldBox {

    private HBox box;
    private Label heightLabel;
    private Label widthLabel;
    private Tip sizeTip;
    private TextField heightField;
    private TextField widthField;

    public FieldBox() {
        this.box = new HBox();
        this.widthLabel = new Label("Width");
        this.heightLabel = new Label("Height");
        this.sizeTip = new Tip("Values between 20 and 100 get best results.");
        this.heightField = new TextField();
        this.widthField = new TextField();
        box.getChildren().add(heightLabel);
        box.getChildren().add(heightField);
        box.getChildren().add(widthLabel);
        box.getChildren().add(widthField);
        box.getChildren().add(sizeTip.getTip());
    }

    public HBox getBox() {
        return this.box;
    }

    public String getHeight() {
        return heightField.getText();
    }

    public String getWidth() {
        return widthField.getText();
    }

}
