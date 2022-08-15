package edu.wpi.first.talontrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wpi.first.talontrack.PratsTrajectoryStuff.PratsTrajectoryInstance;
import edu.wpi.first.talontrack.global.CurrentSelections;
import edu.wpi.first.talontrack.global.PathExports;
import edu.wpi.first.talontrack.global.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TimelineController {

  @FXML
  private GridPane timelineGrid;
  @FXML
  private VBox topBox;
  @FXML
  private Pane topPane;

  private TitledPane trajInfo;
  private VBox trajBox;

  private HBox box;
  private List<HBox> boxList = new ArrayList<>();
  private List<VBox> allVBoxes = new ArrayList<>();
  private Map<VBox, PratsTrajectoryInstance> trajMap = new HashMap<VBox, PratsTrajectoryInstance>();
  private Map<VBox, CommandInstance> vboxInstanceMap = new HashMap<VBox, CommandInstance>();

  private Background darkBlue;
  private Background mouseOver;
  private Background navy;
  private Background standard;

  private TextField startField;
  private TextField finishField;

  private int i = 0;
  private int traj_index = 0;
  private int inst_index = 0;
  private double subtractionTotal = 0;

  private List<PratsTrajectoryInstance> trajsList;
  private PratsTrajectoryInstance selectedTraj;

  @FXML
  private void initialize() {
    darkBlue = new Background(new BackgroundFill(Color.rgb(28, 28, 28), CornerRadii.EMPTY, Insets.EMPTY));
    mouseOver = new Background(new BackgroundFill(Color.rgb(60, 60, 60), CornerRadii.EMPTY, Insets.EMPTY));
    navy = new Background(new BackgroundFill(Color.rgb(50, 50, 80), new CornerRadii(5), Insets.EMPTY));
    standard = new Background(new BackgroundFill(Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY));
    topBox.setPadding(Insets.EMPTY);
    startField = new TextField();
    finishField = new TextField();
    trajBox = new VBox(5.0, new Label("Start"), startField, new Label("Finish"), finishField);
    trajInfo = new TitledPane("Trajectory Info", trajBox);
    topBox.getChildren().add(trajInfo);
    setupInstListener();
    setupTrajListener();
  }

  public void fillTimeline() {
    trajsList = PathExports.getTrajectories();
    Timeline.getTempList().forEach((c) -> {
      setupInstHeaderBoxes(autoSubstringJavaInst(c.getName()));
      instancesVBox(box, c).forEach(b -> {
        box.getChildren().add(b);
        allVBoxes.add(b);
      });
      timelineGrid.addRow(i, box);
      GridPane.setConstraints(box, 0, i, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS,
          Priority.ALWAYS, new Insets(0, 0, 1, 0));
      i++;
    });

    setupTrajectoryTimeline("Paths");

    boxList.forEach(bl -> {
      bl.setOnMouseEntered(ev -> {
        bl.setBackground(mouseOver);
      });
      bl.setOnMouseExited(ev -> {
        bl.setBackground(darkBlue);
      });
    });

    allVBoxes.forEach(v -> {
      v.setOnMouseEntered(ev -> {
        v.setBackground(navy);
      });
      v.setOnMouseExited(ev -> {
        v.setBackground(standard);
      });
      v.setOnMousePressed(ev -> {
        v.setBackground(standard);
      });
      v.setOnMouseReleased(ev -> {
        v.setBackground(navy);
      });
    });

    vboxInstanceMap.forEach((a, b) -> {
      a.setOnMouseClicked(ev -> {
        CurrentSelections.setCurInst(b);
      });
    });
    i = 0;

    trajMap.forEach((v, t) -> {
      v.setOnMouseClicked(ev -> {
        selectedTraj = t;
        fillTextFieldTraj(t);
      });
    });
  }

  private void fillTextFieldTraj(PratsTrajectoryInstance t) {
    trajInfo.setText(autoSubstringWPILIB(t.getName()));
    startField.setText(String.valueOf(t.getStartTime()));
    finishField.setText(String.valueOf(t.getFinishTime()));
  }

  public Pane getTopPane() {
    return topPane;
  }

  private void setupTrajBoxes(String name) {
    box = new HBox(0);
    box.setPrefWidth(topPane.getWidth());
    box.setMaxHeight(90);
    box.setPrefHeight(topPane.getHeight() / Timeline.getTempList().size());
    box.setBackground(darkBlue);
    boxList.add(box);
    VBox vbox = new VBox();
    vbox.setPrefSize(200, box.getHeight());
    vbox.setBackground(new Background(new BackgroundFill(Color.rgb(33, 33, 33), CornerRadii.EMPTY, Insets.EMPTY)));
    vbox.setBorder(new Border(new BorderStroke(Color.DARKCYAN, BorderStrokeStyle.SOLID, null, null)));
    vbox.setAlignment(Pos.CENTER);
    Label label = new Label(name);
    label.setFont(Font.font(14.0));
    VBox.setVgrow(label, Priority.ALWAYS);
    vbox.getChildren().add(label);
    box.getChildren().add(vbox);
    trajectoriesVBox(box).forEach(b -> {
      box.getChildren().add(b);
      allVBoxes.add(b);
    });
  }

  private void setupInstHeaderBoxes(String name) {
    box = new HBox(0);
    box.setPrefWidth(topPane.getWidth());
    box.setMaxHeight(85);
    box.setPrefHeight(topPane.getHeight() / Timeline.getTempList().size());
    box.setBackground(darkBlue);
    boxList.add(box);
    VBox vbox = new VBox();
    vbox.setPrefSize(200, box.getHeight());
    vbox.setBackground(new Background(new BackgroundFill(Color.rgb(33, 33, 33), CornerRadii.EMPTY, Insets.EMPTY)));
    vbox.setBorder(new Border(new BorderStroke(Color.DARKGREEN, BorderStrokeStyle.SOLID, null, null)));
    vbox.setAlignment(Pos.CENTER);
    Label label = new Label(name);
    label.setFont(Font.font(14.0));
    VBox.setVgrow(label, Priority.ALWAYS);
    vbox.getChildren().add(label);
    box.getChildren().add(vbox);
  }

  private List<VBox> trajectoriesVBox(HBox box) {
    List<VBox> returnList = new ArrayList<>();
    List<PratsTrajectoryInstance> trajs = PathExports.getTrajectories();
    trajs.forEach(t -> {
      VBox v = new VBox();
      v.setPrefSize(secondsToPixels(t.getPratsTrajRaw().getTotalTimeSeconds()), box.getHeight());
      if (traj_index == 0) {
        v.setTranslateX(1 + v.getLayoutBounds().getMinX() + secondsToPixels(t.getStartTime()));
        // System.out.println(t.getPratsTrajRaw().getTotalTimeSeconds());
      } else {
        for (int k = 0; k < traj_index; k++) {
          subtractionTotal += secondsToPixels(trajs.get(k).getPratsTrajRaw().getTotalTimeSeconds());
        }
        v.setTranslateX(1 + v.getLayoutBounds().getMinX() + secondsToPixels(t.getStartTime()) - subtractionTotal);
        subtractionTotal = 0;
      }
      v.setBackground(new Background(new BackgroundFill(Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY)));
      v.setBorder(new Border(new BorderStroke(Color.DARKRED,
          BorderStrokeStyle.SOLID, new CornerRadii(5), null)));
      v.setAlignment(Pos.CENTER);
      Label label = new Label(autoSubstringWPILIB(t.getName()));
      label.setFont(Font.font(14.0));
      VBox.setVgrow(label, Priority.ALWAYS);
      v.getChildren().add(label);
      returnList.add(v);
      traj_index++;
      trajMap.put(v, t);
    });
    traj_index = 0;
    return returnList;
  }

  private List<VBox> instancesVBox(HBox box, CommandTemplate temp) {
    List<VBox> returnList = new ArrayList<>();
    Timeline.getInstsFromTemplate(temp).forEach(t -> {
      VBox v = new VBox();
      v.setPrefSize(secondsToPixels(t.getFinish() - t.getStart()), box.getHeight());
      if (inst_index == 0) {
        v.setTranslateX(1 + v.getLayoutBounds().getMinX() + secondsToPixels(t.getStart()));
        // System.out.println(t.getPratsTrajRaw().getTotalTimeSeconds());
      } else {
        for (int k = 0; k < inst_index; k++) {
          subtractionTotal += secondsToPixels(Timeline.getInstsFromTemplate(temp).get(k).getFinish() - Timeline
              .getInstsFromTemplate(temp).get(k).getStart());
        }
        v.setTranslateX(1 + v.getLayoutBounds().getMinX() + secondsToPixels(t.getStart()) - subtractionTotal);
        subtractionTotal = 0;
      }
      v.setBackground(new Background(new BackgroundFill(Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY)));
      v.setBorder(new Border(new BorderStroke(Color.DARKBLUE,
          BorderStrokeStyle.SOLID, new CornerRadii(5), null)));
      v.setAlignment(Pos.CENTER);
      Label label = new Label(autoSubstringJavaInst(t.getName()));
      label.setFont(Font.font(14.0));
      VBox.setVgrow(label, Priority.ALWAYS);
      v.getChildren().add(label);
      returnList.add(v);
      inst_index++;
      vboxInstanceMap.put(v, t);
    });
    inst_index = 0;
    return returnList;
  }

  private void setupTrajectoryTimeline(String name) {
    setupTrajBoxes(name);
    timelineGrid.addRow(i, box);
    GridPane.setConstraints(box, 0, i, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS,
        Priority.ALWAYS, new Insets(0));
  }

  private double secondsToPixels(double lenSeconds) {
    return (topPane.getWidth() - 200) * (lenSeconds / 15);
  }

  private void setupInstListener() {
    CurrentSelections.curInstProperty().addListener((observable, oldValue, newValue) -> {
      fillTimeline();
      newValue.startProperty().addListener((observableStart, oldValueStart, newValueStart) -> {
        fillTimeline();
      });
      newValue.finishProperty().addListener((observableFinish, oldValueFinish, newValueFinish) -> {
        fillTimeline();
      });
    });
  }

  private void setupTrajListener() {
    startField.setOnKeyReleased(event -> {
      if (!startField.getText().equals("")) {
        selectedTraj.setStartTime(Double.parseDouble(startField.getText()));
        selectedTraj.setFinishTime(
            Double.parseDouble(startField.getText()) + selectedTraj.getPratsTrajRaw().getTotalTimeSeconds());
        fillTimeline();
      }
      event.consume();
    });

    finishField.setOnKeyReleased(event -> {
      if (!finishField.getText().equals("")) {
        selectedTraj.setFinishTime(Double.parseDouble(finishField.getText()));
        selectedTraj.setStartTime(
            Double.parseDouble(finishField.getText()) - selectedTraj.getPratsTrajRaw().getTotalTimeSeconds());
        fillTimeline();
      }
      event.consume();
    });

    startField.setOnMouseClicked(event -> {
      if (!startField.getText().equals("")) {
        selectedTraj.setStartTime(Double.parseDouble(startField.getText()));
        selectedTraj.setFinishTime(
            Double.parseDouble(startField.getText()) + selectedTraj.getPratsTrajRaw().getTotalTimeSeconds());
        fillTimeline();
      }
      event.consume();
    });

    finishField.setOnMouseClicked(event -> {
      if (!finishField.getText().equals("")) {
        selectedTraj.setFinishTime(Double.parseDouble(finishField.getText()));
        selectedTraj.setStartTime(
            Double.parseDouble(finishField.getText()) - selectedTraj.getPratsTrajRaw().getTotalTimeSeconds());
        fillTimeline();
      }
      event.consume();
    });

    startField.setOnKeyPressed(ev -> {
      if (ev.getCode().equals(KeyCode.ENTER)) {
        fillTextFieldTraj(selectedTraj);
      }
    });

    finishField.setOnKeyPressed(ev -> {
      if (ev.getCode().equals(KeyCode.ENTER)) {
        fillTextFieldTraj(selectedTraj);
      }
    });

    trajBox.setOnMouseClicked(ev -> {
      fillTextFieldTraj(selectedTraj);
    });

    topPane.setOnMouseClicked(ev -> {
      fillTextFieldTraj(selectedTraj);
    });
  }

  private String autoSubstringJavaInst(String s) {
    return s.substring(0, (s.length() - 5));
  }

  private String autoSubstringWPILIB(String s) {
    return s.substring(0, (s.length() - 12));
  }
}
