package edu.wpi.first.talontrack.global;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.talontrack.CommandInstance;
import edu.wpi.first.talontrack.CommandTemplate;

public class Timeline {
  private static List<CommandTemplate> tempList;
  private static List<CommandInstance> instList;
  private static double maxTime = 15.0;

  // DONT PAY ATTENTION TO THESE
  private static CommandTemplate returnTemp;
  private static CommandInstance returnInst;

  public static void create(List<CommandTemplate> comTempArr, List<CommandInstance> comInstArr) {
    tempList = comTempArr;
    instList = comInstArr;
  }

  public static List<CommandTemplate> getTempList() {
    return tempList;
  }

  public static List<CommandInstance> getInstList() {
    return instList;
  }

  public static CommandTemplate getTemplateFromName(String name) {
    tempList.forEach(c -> {
      if (name.equals(c.getName())) {
        returnTemp = c;
      }
    });
    return returnTemp;
  }

  public static CommandInstance getInstanceFromName(String name) {
    instList.forEach(c -> {
      if (name.equals(c.getName())) {
        returnInst = c;
      }
    });
    return returnInst;
  }

  public static List<CommandInstance> getInstsFromTemplate(CommandTemplate c) {
    List<CommandInstance> returnList = new ArrayList<>();
    instList.forEach(i -> {
      if (i.getParent().equals(c)) {
        returnList.add(i);
      }
    });
    return returnList;
  }

  public static void setInstList(List<CommandInstance> b) {
    instList = b;
  }
}
