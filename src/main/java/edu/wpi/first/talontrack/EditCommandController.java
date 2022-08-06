package edu.wpi.first.talontrack;

import java.util.HashMap;
import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class EditCommandController {
  @FXML
  private Label lStart;
  @FXML
  private Label lFinish;
  @FXML
  private Label l1;
  @FXML
  private Label l2;
  @FXML
  private Label l3;
  @FXML
  private Label l4;
  @FXML
  private Label l5;
  @FXML
  private Label l6;
  @FXML
  private Label lName;
  @FXML
  private TextField start;
  @FXML
  private TextField finish;
  @FXML
  private TextField pointName;
  @FXML
  private TextField p1;
  @FXML
  private TextField p2;
  @FXML
  private TextField p3;
  @FXML
  private TextField p4;
  @FXML
  private TextField p5;
  @FXML
  private TextField p6;

  List<Control> controls;
  List<TextField> pS;
  List<Label> lS;
  int i = 0;

  @FXML
  private void initialize() {
    controls = List.of(p1, p2, p3, p4, p5, p6, start, finish, pointName);
    controls.forEach(c -> c.setDisable(true));
    pS = List.of(p1, p2, p3, p4, p5, p6);
    lS = List.of(l1, l2, l3, l4, l5, l6);
  }

  public void bindToCommand(ObservableValue<CommandTemplate> comTemp, FieldDisplayController controller) {
    comTemp.addListener((observable, oldValue, newValue) -> {
      i = 0;
      pointName.setText(newValue.getName().substring(0, newValue.getName().length() - 5));
      newValue.getParameterMap().forEach((a, b) -> {
        if (a != null && b != null) {
          lS.get(i).setVisible(true);
          pS.get(i).setVisible(true);
          lS.get(i).setText(a);
          pS.get(i).setText(b);
        }
        i++;
      });
      while (i != 6) {
        lS.get(i).setVisible(false);
        pS.get(i).setVisible(false);
        i++;
      }
    });

  }
}
