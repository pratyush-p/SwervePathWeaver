package edu.wpi.first.talontrack.global;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.talontrack.PratsTrajectoryStuff.PratsTrajectoryInstance;

public class PathExports {

  private static List<PratsTrajectoryInstance> trajs = new ArrayList<PratsTrajectoryInstance>();

  public static void initialize() {
    for (int i = 0; i < trajs.size(); i++) {
      if (i == 0) {
        trajs.get(i).setStartTime(0);
      } else {
        trajs.get(i).setStartTime(trajs.get(i - 1).getFinishTime());
        trajs.get(i)
            .setFinishTime(trajs.get(i - 1).getFinishTime() + trajs.get(i).getPratsTrajRaw().getTotalTimeSeconds());
      }

    }
  }

  public static void addTrajectory(PratsTrajectoryInstance t) {
    trajs.add(t);
  }

  public static List<PratsTrajectoryInstance> getTrajectories() {
    return trajs;
  }

  public static double getAutonLenSeconds() {
    return trajs.get(trajs.size() - 1).getFinishTime();
  }

  public static double getTimeLeft() {
    return 15.0 - getAutonLenSeconds();
  }
}
