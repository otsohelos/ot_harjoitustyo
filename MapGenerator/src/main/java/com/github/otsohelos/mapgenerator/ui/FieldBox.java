/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.otsohelos.mapgenerator.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 *
 * @author otsohelos
 */
class FieldBox {

    // FieldBox is solely for the purpose of displaying the height and width fields
    private final HBox box;
    private final Label heightLabel;
    private final Label widthLabel;
    private final Tip sizeTip;
    private final TextField heightField;
    private final TextField widthField;

    public FieldBox() {
        this.box = new HBox();
        this.widthLabel = new Label("Width");
        this.heightLabel = new Label("Height");
        this.sizeTip = new Tip("Values between 40 and 150 get best results.");
        this.heightField = new TextField();
        this.widthField = new TextField();
        box.getChildren().add(heightLabel);
        box.getChildren().add(heightField);
        box.getChildren().add(widthLabel);
        box.getChildren().add(widthField);
        box.getChildren().add(sizeTip.getTip());
        box.setPadding(new Insets(0, 10, 0, 10));

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
