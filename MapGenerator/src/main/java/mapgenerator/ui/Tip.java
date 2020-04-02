/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapgenerator.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;

/**
 *
 * @author otsohelos
 */
class Tip {

    private Label label;
    private Tooltip tooltip;

    public Tip(String text) {
        this.label = new Label("?");
        label.setFont(new Font("Arial", 16));
        label.setPadding(new Insets(0, 10, 10, 10));
        this.tooltip = new Tooltip(text);
        tooltip.setFont(new Font("Arial", 14));
        Tooltip.install(label, tooltip);
    }

    public Label getTip() {
        return this.label;
    }
}
