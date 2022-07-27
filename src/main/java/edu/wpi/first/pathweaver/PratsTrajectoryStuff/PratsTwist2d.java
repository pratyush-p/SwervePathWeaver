// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.pathweaver.PratsTrajectoryStuff;

import java.util.Objects;

/**
 * A change in distance along a 2D arc since the last pose update. We can use
 * ideas from
 * differential calculus to create new Pose2ds from a PratsTwist2d and vise
 * versa.
 *
 * <p>
 * A Twist can be used to represent a difference between two poses.
 */
@SuppressWarnings("MemberName")
public class PratsTwist2d {
  /** Linear "dx" component. */
  public double dx;

  /** Linear "dy" component. */
  public double dy;

  /** Angular "dtheta" component (radians). */
  public double dtheta;

  public PratsTwist2d() {
  }

  /**
   * Constructs a PratsTwist2d with the given values.
   *
   * @param dx     Change in x direction relative to robot.
   * @param dy     Change in y direction relative to robot.
   * @param dtheta Change in angle relative to robot.
   */
  public PratsTwist2d(double dx, double dy, double dtheta) {
    this.dx = dx;
    this.dy = dy;
    this.dtheta = dtheta;
  }

  @Override
  public String toString() {
    return String.format("PratsTwist2d(dX: %.2f, dY: %.2f, dTheta: %.2f)", dx, dy, dtheta);
  }

  /**
   * Checks equality between this PratsTwist2d and another object.
   *
   * @param obj The other object.
   * @return Whether the two objects are equal or not.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof PratsTwist2d) {
      return Math.abs(((PratsTwist2d) obj).dx - dx) < 1E-9
          && Math.abs(((PratsTwist2d) obj).dy - dy) < 1E-9
          && Math.abs(((PratsTwist2d) obj).dtheta - dtheta) < 1E-9;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(dx, dy, dtheta);
  }
}