package edu.wpi.first.talontrack.PratsTrajectoryStuff;

import edu.wpi.first.talontrack.global.PathExports;

public class PratsTrajectoryInstance {

  double startTime;
  double finishTime;
  String name;
  PratsTrajectory traj;

  public PratsTrajectoryInstance(PratsTrajectory traj, String name) {
    this.traj = traj;
    this.startTime = 0.0;
    this.finishTime = startTime + traj.getTotalTimeSeconds();
    this.name = name;
    PathExports.addTrajectory(this);
  }

  public double getStartTime() {
    return startTime;
  }

  public void setStartTime(double startTime) {
    this.startTime = startTime;
  }

  public double getFinishTime() {
    return finishTime;
  }

  public void setFinishTime(double finishTime) {
    this.finishTime = finishTime;
  }

  public PratsTrajectory getPratsTrajRaw() {
    return traj;
  }

  public String getName() {
    return name;
  }
}
