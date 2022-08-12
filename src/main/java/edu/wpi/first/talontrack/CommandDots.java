package edu.wpi.first.talontrack;

import java.util.List;

import edu.wpi.first.talontrack.PratsTrajectoryStuff.PratsTrajectoryInstance;
import edu.wpi.first.talontrack.ProjectPreferences.Values;
import edu.wpi.first.talontrack.global.PathExports;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CommandDots {

  private Field field;
  private double startTime;
  private double finishTime;
  private Circle startCircle;
  private Circle finishCircle;
  private List<PratsTrajectoryInstance> trajs;
  private CommandInstance inst;
  private Values vals;
  double fieldHeight;

  private DoubleProperty startX = new SimpleDoubleProperty();
  private DoubleProperty startY = new SimpleDoubleProperty();
  private BooleanProperty startVis = new SimpleBooleanProperty();
  private DoubleProperty finishX = new SimpleDoubleProperty();
  private DoubleProperty finishY = new SimpleDoubleProperty();
  private BooleanProperty finishVis = new SimpleBooleanProperty();

  public CommandDots() {
  }

  private void setupStartCircle() {
    startCircle.centerXProperty().bind(startX);
    startCircle.centerYProperty().bind(startY);
    startCircle.visibleProperty().bind(startVis);
  }

  private void setupFinishCircle() {
    finishCircle.centerXProperty().bind(finishX);
    finishCircle.centerYProperty().bind(finishY);
    finishCircle.visibleProperty().bind(finishVis);
  }

  private void setupListeners() {
    startVis.set(true);
    finishVis.set(true);
    for (int k = 0; k < trajs.size(); k++) {
      if ((startTime < trajs.get(k).getFinishTime())
          && (startTime >= trajs.get(k).getStartTime())) {
        startX.set(trajs.get(k).getPratsTrajRaw().sample((startTime -
            trajs.get(k).getStartTime())).poseMeters.getX());
        startY.set(fieldHeight - trajs.get(k).getPratsTrajRaw().sample(
            (startTime - trajs.get(k).getStartTime())).poseMeters.getY());
        startVis.set(true);
      }
      if ((finishTime < trajs.get(k).getFinishTime())
          && (finishTime >= trajs.get(k).getStartTime())) {
        finishX
            .set(trajs.get(k).getPratsTrajRaw().sample((finishTime -
                trajs.get(k).getStartTime())).poseMeters.getX());
        finishY.set(fieldHeight - trajs.get(k).getPratsTrajRaw().sample(
            (finishTime - trajs.get(k).getStartTime())).poseMeters.getY());
        finishVis.set(true);
      }
    }
  }

  public Group getGroup() {
    return new Group(startCircle, finishCircle);
  }

  public Circle getStartCircle() {
    return startCircle;
  }

  public Circle getFinishCircle() {
    return finishCircle;
  }

  public void setInst(CommandInstance inst) {
    this.inst = inst;
    field = ProjectPreferences.getInstance().getField();
    fieldHeight = field.getRealLength().getValue().doubleValue();
    vals = ProjectPreferences.getInstance().getValues();
    trajs = PathExports.getTrajectories();
    startTime = inst.getStart();
    finishTime = inst.getFinish();
    startCircle = new Circle(.762 / 4, Color.CYAN);
    finishCircle = new Circle(.762 / 4, Color.NAVY);
    startCircle.setSmooth(true);
    startCircle.setStroke(Color.BLACK);
    startCircle.setStrokeWidth(0.033);
    finishCircle.setSmooth(true);
    finishCircle.setStroke(Color.BLACK);
    finishCircle.setStrokeWidth(0.033);
    setupListeners();
    setupStartCircle();
    setupFinishCircle();
  }
}
