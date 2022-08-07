package edu.wpi.first.talontrack;

import java.util.HashMap;
import java.util.Map;

public class CommandInstance {

  private CommandTemplate parent;
  private String name;
  private double start;
  private double finish;
  private Map<String, Object> valMap;

  public CommandInstance(CommandTemplate parent, String name) {
    this.parent = parent;
    this.name = name;
    valMap = new HashMap<>();
    parent.getParameterMap().forEach((a, b) -> {
      if (b.equalsIgnoreCase("String")) {
        valMap.put(a, "Undefined");
      } else if (b.equalsIgnoreCase("double")) {
        valMap.put(a, 0.0);
      } else if (b.equalsIgnoreCase("int")) {
        valMap.put(a, 0);
      }
    });
  }

  public CommandInstance(CommandTemplate parent, String name, Map<String, Object> valMap, double start, double finish) {
    this.parent = parent;
    this.name = name;
    this.valMap = valMap;
    this.start = start;
    this.finish = finish;
  }

  public String getName() {
    return name;
  }

  public double getStart() {
    return start;
  }

  public double getFinish() {
    return finish;
  }

  public CommandTemplate getParent() {
    return parent;
  }

  public Map<String, Object> getMap() {
    return valMap;
  }

  public CommandTemplate getTemplate() {
    return parent;
  }
}
