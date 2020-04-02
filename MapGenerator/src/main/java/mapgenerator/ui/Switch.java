package mapgenerator.ui;

import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

/**
 *
 * @author otsohelos
 */
class Switch {

    private HBox box;
    private ToggleButton button1;
    private ToggleButton button2;
    private ToggleGroup group;
    private Label label;
    private Tip tip;

    public Switch(String labelString, String string1, String string2, String tipString) {
        this.button1 = new ToggleButton(string1);
        this.button2 = new ToggleButton(string2);
        this.group = new ToggleGroup();
        button1.setSelected(true);
        button1.setToggleGroup(group);
        button2.setToggleGroup(group);
        this.label = new Label(labelString);
        this.tip = new Tip(tipString);
        this.box = new HBox();
        box.getChildren().add(label);
        box.getChildren().add(button1);
        box.getChildren().add(button2);
        box.getChildren().add(tip.getTip());
    }

    public HBox getBox() {
        return this.box;
    }

    public int getSelected() {
        ToggleButton selectedButton = (ToggleButton) group.getSelectedToggle();
        if (selectedButton == button1) {
            return 1;
        }
        return 2;
    }
}
