// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.talontrack.PratsTrajectoryStuff;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.spline.QuinticHermiteSpline;
import edu.wpi.first.math.spline.Spline;
import edu.wpi.first.math.spline.SplineHelper;

import java.util.Arrays;
import java.util.List;

public final class PratsSplineHelper {
  /** Private constructor because this is a utility class. */
  private PratsSplineHelper() {
  }

  /**
   * Returns quintic splines from a set of waypoints.
   *
   * @param waypoints The waypoints
   * @return List of splines.
   */
  public static PratsQuinticHermiteSpline[] getQuinticSplinesFromWaypoints(List<PratsPose2d> waypoints) {
    PratsQuinticHermiteSpline[] splines = new PratsQuinticHermiteSpline[waypoints.size() - 1];
    for (int i = 0; i < waypoints.size() - 1; ++i) {
      var p0 = waypoints.get(i);
      var p1 = waypoints.get(i + 1);

      // This just makes the splines look better.
      final var scalar = 1.2 * p0.getTranslation().getDistance(p1.getTranslation());

      var controlVecA = getQuinticControlVector(scalar, p0);
      var controlVecB = getQuinticControlVector(scalar, p1);

      splines[i] = new PratsQuinticHermiteSpline(controlVecA.x, controlVecB.x, controlVecA.y, controlVecB.y);

    }
    return splines;
  }

  /**
   * Returns a set of quintic splines corresponding to the provided control
   * vectors. The user is
   * free to set the direction of all control vectors. Continuous curvature is
   * guaranteed throughout
   * the path.
   *
   * @param controlVectors The control vectors.
   * @return A vector of quintic hermite splines that interpolate through the
   *         provided waypoints.
   */
  @SuppressWarnings("LocalVariableName")
  public static QuinticHermiteSpline[] getQuinticSplinesFromControlVectors(
      Spline.ControlVector[] controlVectors) {
    QuinticHermiteSpline[] splines = new QuinticHermiteSpline[controlVectors.length - 1];
    for (int i = 0; i < controlVectors.length - 1; i++) {
      var xInitial = controlVectors[i].x;
      var xFinal = controlVectors[i + 1].x;
      var yInitial = controlVectors[i].y;
      var yFinal = controlVectors[i + 1].y;
      splines[i] = new QuinticHermiteSpline(
          xInitial, xFinal,
          yInitial, yFinal);
    }
    return splines;
  }

  private static PratsSpline.ControlVector getQuinticControlVector(double scalar, PratsPose2d point) {
    return new PratsSpline.ControlVector(
        new double[] { point.getX(), scalar * point.getTangent().getCos(), 0.0 },
        new double[] { point.getY(), scalar * point.getTangent().getSin(), 0.0 });
  }
}
