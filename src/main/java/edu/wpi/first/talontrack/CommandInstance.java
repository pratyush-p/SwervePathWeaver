package edu.wpi.first.talontrack;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public class CommandInstance {

  private CommandTemplate parent;
  private SimpleObjectProperty<String> name = new SimpleObjectProperty<>();
  private SimpleDoubleProperty start = new SimpleDoubleProperty();
  private SimpleDoubleProperty finish = new SimpleDoubleProperty();
  private SimpleObjectProperty<Map<String, Object>> valMap = new SimpleObjectProperty<>();

  public CommandInstance(CommandTemplate parent, String name) {
    this.parent = parent;
    this.name.set(name);
    Map<String, Object> map = new HashMap<>();
    parent.getParameterMap().forEach((a, b) -> {
      if (b.equalsIgnoreCase("String")) {
        map.put(a, "default");
      } else if (b.equalsIgnoreCase("double")) {
        map.put(a, 69.0);
      } else if (b.equalsIgnoreCase("int")) {
        map.put(a, 420);
      }
    });
    valMap.set(map);
  }

  public CommandInstance(CommandTemplate parent, String name, Map<String, Object> valMap, double start, double finish) {
    this.parent = parent;
    this.name.set(name);
    this.valMap.set(valMap);
    this.start.set(start);
    this.finish.set(finish);
  }

  public CommandInstance(CommandInstance dupInst, String name) {
    this.parent = dupInst.getParent();
    this.name.set(name);
    this.valMap.set(dupInst.getMap());
    this.start.set(dupInst.getStart());
    this.finish.set(dupInst.getFinish());
  }

  public String getName() {
    return this.name.get();
  }

  public void setName(String name) {
    this.name.set(name);
  }

  public SimpleObjectProperty<String> nameProperty() {
    return this.name;
  }

  public double getStart() {
    return this.start.get();
  }

  public void setStart(double start) {
    this.start.set(start);
  }

  public SimpleDoubleProperty startProperty() {
    return this.start;
  }

  public double getFinish() {
    return this.finish.get();
  }

  public void setFinish(double finish) {
    this.finish.set(finish);
  }

  public SimpleDoubleProperty finishProperty() {
    return this.finish;
  }

  public CommandTemplate getParent() {
    return this.parent;
  }

  public Map<String, Object> getMap() {
    return this.valMap.get();
  }

  public void setValMap(Map<String, Object> valMap) {
    this.valMap.set(valMap);
  }

  public SimpleObjectProperty<Map<String, Object>> valMapProperty() {
    return this.valMap;
  }

}
