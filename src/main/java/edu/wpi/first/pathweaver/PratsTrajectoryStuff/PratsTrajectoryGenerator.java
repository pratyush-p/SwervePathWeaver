// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.pathweaver.PratsTrajectoryStuff;

import edu.wpi.first.math.MathSharedStore;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.spline.Spline;
import edu.wpi.first.math.spline.SplineHelper;
import edu.wpi.first.math.spline.SplineParameterizer.MalformedSplineException;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

public final class PratsTrajectoryGenerator {
  private static final PratsTrajectory kDoNothingTrajectory = new PratsTrajectory(
      Arrays.asList(new PratsTrajectory.State()));
  private static BiConsumer<String, StackTraceElement[]> errorFunc;

  /** Private constructor because this is a utility class. */
  private PratsTrajectoryGenerator() {
  }

  private static void reportError(String error, StackTraceElement[] stackTrace) {
    if (errorFunc != null) {
      errorFunc.accept(error, stackTrace);
    } else {
      MathSharedStore.reportError(error, stackTrace);
    }
  }

  /**
   * Set error reporting function. By default, DriverStation.reportError() is
   * used.
   *
   * @param func Error reporting function, arguments are error and stackTrace.
   */
  public static void setErrorHandler(BiConsumer<String, StackTraceElement[]> func) {
    errorFunc = func;
  }

  /**
   * Generates a trajectory from the given waypoints and config. This method uses
   * quintic hermite
   * splines -- therefore, all points must be represented by Pose2d objects.
   * Continuous curvature is
   * guaranteed in this method.
   *
   * @param waypoints List of waypoints..
   * @param config    The configuration for the trajectory.
   * @return The generated trajectory.
   */
  @SuppressWarnings("LocalVariableName")
  public static PratsTrajectory generateTrajectory(List<PratsPose2d> waypoints, PratsTrajectoryConfig config) {
    final var flip = new PratsTransform2d(new Translation2d(), Rotation2d.fromDegrees(180.0),
        Rotation2d.fromDegrees(180.0));

    List<PratsPose2d> newWaypoints = new ArrayList<>();
    if (config.isReversed()) {
      for (PratsPose2d originalWaypoint : waypoints) {
        newWaypoints.add(originalWaypoint.pratsPlus(flip));
      }
    } else {
      newWaypoints.addAll(waypoints);
    }
    // Get the spline points
    List<PratsPoseWithCurvature> points;
    try {
      points = splinePointsFromSplines(PratsSplineHelper.getQuinticSplinesFromWaypoints(newWaypoints), newWaypoints);
    } catch (MalformedSplineException ex) {
      reportError(ex.getMessage(), ex.getStackTrace());
      return kDoNothingTrajectory;
    }

    // Change the points back to their original orientation.
    if (config.isReversed()) {
      for (var point : points) {
        point.poseMeters = point.poseMeters.pratsPlus(flip);
        point.curvatureRadPerMeter *= -1;
      }
    }

    // Generate and return trajectory.
    return PratsTrajectoryParameterizer.timeParameterizeTrajectory(
        points,
        config.getConstraints(),
        config.getStartVelocity(),
        config.getEndVelocity(),
        config.getMaxVelocity(),
        config.getMaxAcceleration(),
        config.isReversed());
  }

  public static List<PratsPoseWithCurvature> finalizedSplinePoints;

  /**
   * Generate spline points from a vector of splines by parameterizing the
   * splines.
   *
   * @param splines The splines to parameterize.
   * @return The spline points for use in time parameterization of a trajectory.
   * @throws MalformedSplineException When the spline is malformed (e.g. has close
   *                                  adjacent points
   *                                  with approximately opposing headings)
   */
  public static List<PratsPoseWithCurvature> splinePointsFromSplines(PratsSpline[] splines, List<PratsPose2d> wps) {
    // Create the vector of spline points.
    var splinePoints = new ArrayList<PratsPoseWithCurvature>();

    // Add the first point to the vector.
    splinePoints.add(splines[0].getPoint(0.0));

    // Iterate through the vector and parameterize each spline, adding the
    // parameterized points to the final vector.
    for (final var spline : splines) {
      var points = PratsSplineParameterizer.parameterize(spline);

      // Append the array of poses to the vector. We are removing the first
      // point because it's a duplicate of the last point from the previous
      // spline.
      splinePoints.addAll(points.subList(1, points.size()));
    }

    int i = 0;
    int x = 0;
    int k = 0;
    int[] wi = new int[500];

    for (final var splinePoint : splinePoints) {
      for (final var wp : wps) {
        if ((round(splinePoint.poseMeters.getTranslation().getX(), 3) == round(wp.getTranslation().getX(), 3)) &&
            (round(splinePoint.poseMeters.getTranslation().getY(), 3) == round(wp.getTranslation().getY(), 3))) {
          splinePoint.poseMeters.setRotation(wp.getRotation());
          wi[x] = i;
          x++;
        }
      }
      splinePoints.set(i, splinePoint);
      i++;
    }

    finalizedSplinePoints = splinePoints;

    for (int b = 0; b < x; b++) {
      System.out.println(wi[b]);
    }

    for (final var splinePointB : splinePoints) {
      PratsPose2d point = splinePointB.poseMeters;
      if (point.getRotation().getRadians() != 4) {

      } else {
        point.setRotation(Rotation2d.fromDegrees(rotationAvg(k, x, wi, i)));
      }
      k++;
    }

    return splinePoints;
  }

  private static double round(double value, int places) {
    if (places < 0)
      throw new IllegalArgumentException();

    BigDecimal bd = BigDecimal.valueOf(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  private static double rotationAvg(int index, int x, int[] wi, int i) {
    int smallerIndex = 0;
    int biggerIndex = 0;
    int indexDifferenceRange = 0;
    int indexDifferencePoint = 0;
    for (int _i = 0; _i < (x - 1); _i++) {
      if (index > wi[_i] && index < wi[_i + 1]) {
        smallerIndex = wi[_i];
        biggerIndex = wi[_i + 1];
        indexDifferenceRange = biggerIndex - smallerIndex;
        indexDifferencePoint = index - smallerIndex;
      }
    }
    double returnVal = finalizedSplinePoints.get(smallerIndex).poseMeters.getRotation().getDegrees();
    double pointOneDeg = finalizedSplinePoints.get(smallerIndex).poseMeters.getRotation().getDegrees();
    double pointTwoDeg = finalizedSplinePoints.get(biggerIndex).poseMeters.getRotation().getDegrees();
    double range = findDegDist(pointOneDeg, pointTwoDeg);
    return returnVal += sCurve(range, indexDifferenceRange, indexDifferencePoint);
  }

  public static double sCurve(double range, double totalDomain, double curDomain) {
    double x = curDomain / totalDomain;
    double k = 4.5;
    // k is the amount of curve desired. k > 0 always. k = 4.5 recommended.
    // A k of 0.0->01 is basically linear. A k larger than 7.75 is too much.
    double y = ((.5 / sCurve2(1, k)) * sCurve2((2 * x) - 1, k)) + .5;
    // Uses sigmoid ease function for easing in and out of rotations for waypoints.
    return y * range;
  }

  public static double sCurve2(double t, double a) {
    return (1 / (1 + (Math.pow(Math.E, (-a * t))))) - 0.5;
  }

  public static double findDegDist(double a, double b) {
    a += 180;
    b += 180;
    double alpha = b - a;
    double beta = b - a + 360;
    double gamma = b - a - 360;
    if (Math.abs(alpha) <= Math.abs(beta) && Math.abs(alpha) <= Math.abs(gamma)) {
      return alpha;
    } else if (Math.abs(beta) <= Math.abs(alpha) && Math.abs(beta) <= Math.abs(gamma)) {
      return beta;
    } else if (Math.abs(gamma) <= Math.abs(beta) && Math.abs(gamma) <= Math.abs(alpha)) {
      return gamma;
    } else {
      return 0.0;
    }
  }

  // Work around type erasure signatures
  @SuppressWarnings("serial")
  public static class ControlVectorList extends ArrayList<Spline.ControlVector> {
    public ControlVectorList(int initialCapacity) {
      super(initialCapacity);
    }

    public ControlVectorList() {
      super();
    }

    public ControlVectorList(Collection<? extends Spline.ControlVector> collection) {
      super(collection);
    }
  }
}
