// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.talontrack.PratsTrajectoryStuff;

import java.util.Objects;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

/** Represents a transformation for a Pose2d. */
public class PratsTransform2d {
  private final Translation2d m_translation;
  private final Rotation2d m_rotation;
  private final Rotation2d m_tangent;

  /**
   * Constructs the transform that maps the initial pose to the final pose.
   *
   * @param initial The initial pose for the transformation.
   * @param last    The final pose for the transformation.
   */
  public PratsTransform2d(PratsPose2d initial, PratsPose2d last) {
    // We are rotating the difference between the translations
    // using a clockwise rotation matrix. This transforms the global
    // delta into a local delta (relative to the initial pose).
    m_translation = last.getTranslation()
        .minus(initial.getTranslation())
        .rotateBy(initial.getTangent().unaryMinus());

    m_rotation = last.getTangent().minus(initial.getTangent());

    m_tangent = last.getTangent().minus(initial.getTangent());
  }

  /**
   * Constructs a transform with the given translation and rotation components.
   *
   * @param translation Translational component of the transform.
   * @param rotation    Rotational component of the transform.
   */
  public PratsTransform2d(Translation2d translation, Rotation2d rotation, Rotation2d tangent) {
    m_translation = translation;
    m_rotation = rotation;
    m_tangent = tangent;
  }

  /** Constructs the identity transform -- maps an initial pose to itself. */
  public PratsTransform2d() {
    m_translation = new Translation2d();
    m_rotation = new Rotation2d();
    m_tangent = new Rotation2d();
  }

  /**
   * Scales the transform by the scalar.
   *
   * @param scalar The scalar.
   * @return The scaled Transform2d.
   */
  public PratsTransform2d times(double scalar) {
    return new PratsTransform2d(m_translation.times(scalar), m_rotation.times(scalar), m_tangent.times(scalar));
  }

  /**
   * Composes two transformations.
   *
   * @param other The transform to compose with this one.
   * @return The composition of the two transformations.
   */
  public PratsTransform2d plus(PratsTransform2d other) {
    return new PratsTransform2d(new PratsPose2d(), new PratsPose2d().pratsTransformBy(this).pratsTransformBy(other));
  }

  /**
   * Returns the translation component of the transformation.
   *
   * @return The translational component of the transform.
   */
  public Translation2d getTranslation() {
    return m_translation;
  }

  /**
   * Returns the X component of the transformation's translation.
   *
   * @return The x component of the transformation's translation.
   */
  public double getX() {
    return m_translation.getX();
  }

  /**
   * Returns the Y component of the transformation's translation.
   *
   * @return The y component of the transformation's translation.
   */
  public double getY() {
    return m_translation.getY();
  }

  /**
   * Returns the rotational component of the transformation.
   *
   * @return Reference to the rotational component of the transform.
   */
  public Rotation2d getRotation() {
    return m_rotation;
  }

  public Rotation2d getTangent() {
    return m_tangent;
  }

  /**
   * Invert the transformation. This is useful for undoing a transformation.
   *
   * @return The inverted transformation.
   */
  public PratsTransform2d inverse() {
    // We are rotating the difference between the translations
    // using a clockwise rotation matrix. This transforms the global
    // delta into a local delta (relative to the initial pose).
    return new PratsTransform2d(
        getTranslation().unaryMinus().rotateBy(getRotation().unaryMinus()),
        getRotation().unaryMinus(),
        getRotation().unaryMinus());
  }

  @Override
  public String toString() {
    return String.format("Transform2d(%s, %s)", m_translation, m_rotation);
  }

  /**
   * Checks equality between this Transform2d and another object.
   *
   * @param obj The other object.
   * @return Whether the two objects are equal or not.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof PratsTransform2d) {
      return ((PratsTransform2d) obj).m_translation.equals(m_translation)
          && ((PratsTransform2d) obj).m_rotation.equals(m_rotation);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(m_translation, m_rotation);
  }
}
