package edu.wpi.first.talontrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wpi.first.talontrack.global.CurrentSelections;
import edu.wpi.first.talontrack.global.PathExports;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;

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

  List<TextField> controls;
  List<TextField> controlsPDoubleOnly;
  List<TextField> controlsDoubleOnly;
  List<TextField> controlsIntOnly;
  List<TextField> controlsStrOnly;
  List<TextField> pS;
  List<Label> lS;
  int i = 0;
  int k = 0;
  private final String directory = ProjectPreferences.getInstance().getDirectory() + "/Instances/";
  ChangeListener<CommandTemplate> tempListener = (observable, oldValue, newValue) -> {
    setTemp(newValue);
  };
  ChangeListener<CommandInstance> instListener = (observable, oldValue, newValue) -> {
    setInst(newValue);
  };
  ChangeListener<String> startListener, finishListener, nameListener;
  TreeItem<String> root = new TreeItem<>();

  @FXML
  private void initialize() {
    controls = List.of(p1, p2, p3, p4, p5, p6, start, finish, pointName);
    controls.forEach(c -> c.setDisable(true));
    controlsPDoubleOnly = List.of(start, finish);
    controlsDoubleOnly = new ArrayList<TextField>();
    controlsIntOnly = new ArrayList<TextField>();
    controlsStrOnly = new ArrayList<TextField>();
    pS = List.of(p1, p2, p3, p4, p5, p6);
    lS = List.of(l1, l2, l3, l4, l5, l6);
    startListener = setUpGenericTextFieldStart(start);
    finishListener = setUpGenericTextFieldFinish(finish);
    nameListener = setUpGenericTextFieldString(pointName);
    controlsPDoubleOnly.forEach(c -> {
      c.setTextFormatter(FxUtils.onlyPositiveDoubleText());
    });
  }

  public void setRoot(TreeItem<String> s) {
    root = s;
  }

  public void bindToCommand(ObservableValue<CommandTemplate> comTemp, FieldDisplayController controller) {
    ObservableValue<CommandInstance> comInst = CurrentSelections.curInstProperty();
    comInst.removeListener(instListener);
    start.textProperty().removeListener(startListener);
    finish.textProperty().removeListener(finishListener);
    pointName.textProperty().removeListener(nameListener);
    comTemp.addListener(tempListener);
    setTemp(comTemp.getValue());
  }

  public void bindToInstance(ObservableValue<CommandInstance> comInst, FieldDisplayController controller) {
    ObservableValue<CommandTemplate> comTemp = CurrentSelections.curCommandTemplateProperty();
    comTemp.removeListener(tempListener);
    setInst(comInst.getValue());
    comInst.addListener(instListener);
    start.textProperty().addListener(startListener);
    finish.textProperty().addListener(finishListener);
    pointName.textProperty().addListener(nameListener);
    enableSaving(comInst);
  }

  private void setTemp(CommandTemplate m) {
    unFormatFields();
    start.clear();
    finish.clear();
    controls.forEach(c -> c.setDisable(true));
    i = 0;
    pointName.setText(m.getName().substring(0, m.getName().length() - 5));
    m.getParameterMap().forEach((a, b) -> {
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
    i = 0;
  }

  private void setInst(CommandInstance m) {
    unFormatFields();
    controls.forEach(c -> c.setDisable(false));
    start.setText(String.valueOf(m.getStart()));
    finish.setText(String.valueOf(m.getFinish()));
    i = 0;
    pointName.setText(m.getName().substring(0, m.getName().length() - 5));
    m.getMap().forEach((a, b) -> {
      if (a != null && b != null) {
        lS.get(i).setVisible(true);
        pS.get(i).setVisible(true);
        lS.get(i).setText(a);
        pS.get(i).setText(b.toString());
      }
      i++;
    });
    while (i != 6) {
      lS.get(i).setVisible(false);
      pS.get(i).setVisible(false);
      i++;
    }
    i = 0;
    m.getParent().getParameterMap().forEach((a, b) -> {
      if (a != null && b != null) {
        if (b.equals("String")) {
          controlsStrOnly.add(pS.get(i));
        } else if (b.equals("double")) {
          controlsDoubleOnly.add(pS.get(i));
        } else if (b.equals("int")) {
          controlsIntOnly.add(pS.get(i));
        }
      }
      i++;
    });
    i = 0;
    formatFields();
  }

  private ChangeListener<String> setUpGenericTextFieldStart(TextField t) {
    ChangeListener<String> l = (observable, oldValue, newValue) -> {
      boolean validText = !("").equals(newValue);
      if ((validText && !(Double.parseDouble(t.getText()) <= PathExports.getAutonLenSeconds())) || (validText
          && !(Double.parseDouble(t.getText()) <= Double.parseDouble(finish.getText())))) {
        t.setText(oldValue);
      }
    };
    return l;
  }

  private ChangeListener<String> setUpGenericTextFieldFinish(TextField t) {
    ChangeListener<String> l = (observable, oldValue, newValue) -> {
      boolean validText = !("").equals(newValue);
      if (validText && !(Double.parseDouble(t.getText()) <= PathExports.getAutonLenSeconds()) || (validText
          && !(Double.parseDouble(t.getText()) >= Double.parseDouble(start.getText())))) {
        t.setText(oldValue);
      }
    };
    return l;
  }

  private ChangeListener<String> setUpGenericTextFieldString(TextField t) {
    ChangeListener<String> l = (observable, oldValue, newValue) -> {
      boolean validText = true;
      if (validText) {
        t.setText(newValue);
      }
    };
    return l;
  }

  private void enableSaving(ObservableValue<CommandInstance> inst) {
    // Save values when out of focus
    controls
        .forEach(textField -> {
          textField.setOnKeyReleased(event -> {
            if (!textField.getText().equals("") && inst.getValue() != null) {
              inst.getValue().setStart(Double.parseDouble(start.getText()));
              inst.getValue().setFinish(Double.parseDouble(finish.getText()));
              inst.getValue().setValMap(convertTextFieldsToList());
              SaveManager.getInstance().addChangeInst(inst.getValue());
              SaveManager.getInstance().saveInst(inst.getValue());
            }
            event.consume();
          });

          textField.setOnMouseClicked(event -> {
            if (!textField.getText().equals("") && inst.getValue() != null) {
              inst.getValue().setStart(Double.parseDouble(start.getText()));
              inst.getValue().setFinish(Double.parseDouble(finish.getText()));
              inst.getValue().setValMap(convertTextFieldsToList());
              SaveManager.getInstance().addChangeInst(inst.getValue());
              SaveManager.getInstance().saveInst(inst.getValue());
            }
            if (!(pointName.getText() + ".inst").equals(inst.getValue().getName())) {
              Alert alert = new Alert(Alert.AlertType.INFORMATION);
              FxUtils.applyDarkMode(alert);
              alert.setHeaderText("Click Enter");
              alert.setContentText("Click enter to save name");
              alert.show();
            }
          });

          textField.setOnKeyPressed(event -> {
            if (!textField.getText().equals("") && inst.getValue() != null && event.getCode().equals(KeyCode.ENTER)) {
              MainIOUtil.renameInst(directory, inst.getValue(),
                  pointName.getText() + ".inst");
              inst.getValue().setName(pointName.getText() + ".inst");
              MainIOUtil.setupItemsInDirectory2(directory, root);
            }
          });
        });
  }

  private void formatFields() {
    controlsDoubleOnly.forEach(n -> {
      n.setTextFormatter(FxUtils.onlyDoubleText());
    });
    controlsIntOnly.forEach(b -> {
      b.setTextFormatter(FxUtils.onlyIntegerText());
    });
    controlsStrOnly.forEach(v -> {
      v.setTextFormatter(FxUtils.onlyStringText());
    });
  }

  private void unFormatFields() {
    controlsDoubleOnly.forEach(n -> {
      n.setTextFormatter(null);
    });
    controlsIntOnly.forEach(b -> {
      b.setTextFormatter(null);
    });
    controlsStrOnly.forEach(v -> {
      v.setTextFormatter(null);
    });
    controlsDoubleOnly.clear();
    controlsIntOnly.clear();
    controlsStrOnly.clear();
  }

  private Map<String, Object> convertTextFieldsToList() {
    Map<String, Object> map = new HashMap<>();
    pS.forEach(t -> {
      if (controlsDoubleOnly.contains(t)) {
        map.put(lS.get(k).getText(), Double.parseDouble(t.getText()));
      } else if (controlsIntOnly.contains(t)) {
        map.put(lS.get(k).getText(), Integer.parseInt(t.getText()));
      } else if (controlsStrOnly.contains(t)) {
        map.put(lS.get(k).getText(), t.getText());
      }
      k++;
    });
    k = 0;
    return map;
  }
}
