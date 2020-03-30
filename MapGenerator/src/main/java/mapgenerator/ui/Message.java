/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapgenerator.ui;

import javafx.scene.control.Label;

public class Message {
    private Label label;

    public Message() {
        this.label = new Label("");
    }

    public void setText(String text) {
        label.setText(text);
        
    }
}
