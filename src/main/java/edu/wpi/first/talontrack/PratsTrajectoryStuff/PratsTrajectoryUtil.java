// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.talontrack.PratsTrajectoryStuff;

import edu.wpi.first.math.WPIMathJNI;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.TrajectoryUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class PratsTrajectoryUtil {
  private PratsTrajectoryUtil() {
    throw new UnsupportedOperationException("This is a utility class!");
  }

  /**
   * Creates a trajectory from a double[] of elements.
   *
   * @param elements A double[] containing the raw elements of the trajectory.
   * @return A trajectory created from the elements.
   */
  private static PratsTrajectory createTrajectoryFromElements(double[] elements) {
    // Make sure that the elements have the correct length.
    if (elements.length % 7 != 0) {
      throw new TrajectorySerializationException(
          "An error occurred when converting trajectory elements into a trajectory.");
    }

    // Create a list of states from the elements.
    List<PratsTrajectory.State> states = new ArrayList<>();
    for (int i = 0; i < elements.length; i += 8) {
      states.add(
          new PratsTrajectory.State(
              elements[i],
              elements[i + 1],
              elements[i + 2],
              new PratsPose2d(elements[i + 3], elements[i + 4], new Rotation2d(elements[i + 5]),
                  new Rotation2d(elements[i + 6])),
              elements[i + 7]));
    }
    return new PratsTrajectory(states);
  }

  /**
   * Returns a double[] of elements from the given trajectory.
   *
   * @param trajectory The trajectory to retrieve raw elements from.
   * @return A double[] of elements from the given trajectory.
   */
  private static double[] getElementsFromTrajectory(PratsTrajectory trajectory) {
    // Create a double[] of elements and fill it from the trajectory states.
    double[] elements = new double[trajectory.getStates().size() * 7];

    for (int i = 0; i < trajectory.getStates().size() * 7; i += 7) {
      var state = trajectory.getStates().get(i / 7);
      elements[i] = state.timeSeconds;
      elements[i + 1] = state.velocityMetersPerSecond;
      elements[i + 2] = state.accelerationMetersPerSecondSq;
      elements[i + 3] = state.poseMeters.getX();
      elements[i + 4] = state.poseMeters.getY();
      elements[i + 5] = state.poseMeters.getRotation().getRadians();
      // elements[i + 6] = state.poseMeters.getTangent().getRadians();
      elements[i + 6] = state.curvatureRadPerMeter;
    }
    System.out.println("Trajectory created.");
    return elements;
  }

  /**
   * Imports a Trajectory from a talontrack-style JSON file.
   *
   * @param path The path of the json file to import from
   * @return The trajectory represented by the file.
   * @throws IOException if reading from the file fails.
   */
  public static PratsTrajectory fromPathweaverJson(Path path) throws IOException {
    return createTrajectoryFromElements(WPIMathJNI.fromPathweaverJson(path.toString()));
  }

  /**
   * Exports a Trajectory to a talontrack-style JSON file.
   *
   * @param trajectory The trajectory to export
   * @param path       The path of the file to export to
   * @throws IOException if writing to the file fails.
   */
  public static void toPathweaverJson(PratsTrajectory trajectory, Path path) throws IOException {
    WPIMathJNI.toPathweaverJson(getElementsFromTrajectory(trajectory), path.toString());
  }

  /**
   * Deserializes a Trajectory from talontrack-style JSON.
   *
   * @param json The string containing the serialized JSON
   * @return the trajectory represented by the JSON
   * @throws TrajectorySerializationException if deserialization of the string
   *                                          fails.
   */
  public static PratsTrajectory deserializeTrajectory(String json) {
    return createTrajectoryFromElements(WPIMathJNI.deserializeTrajectory(json));
  }

  /**
   * Serializes a Trajectory to talontrack-style JSON.
   *
   * @param trajectory The trajectory to export
   * @return The string containing the serialized JSON
   * @throws TrajectorySerializationException if serialization of the trajectory
   *                                          fails.
   */
  public static String serializeTrajectory(PratsTrajectory trajectory) {
    return WPIMathJNI.serializeTrajectory(getElementsFromTrajectory(trajectory));
  }

  public static class TrajectorySerializationException extends RuntimeException {
    public TrajectorySerializationException(String message) {
      super(message);
    }
  }
}
