package edu.wpi.first.talontrack;

import javafx.scene.input.DataFormat;

/**
 * Custom data formats for talontrack.
 */
public final class DataFormats {

  public static final String APP_PREFIX = "talontrack";

  /**
   * Data format for dragging waypoints.
   */
  public static final DataFormat WAYPOINT = new DataFormat(APP_PREFIX + "/waypoint");

  /**
   * Data format for dragging control vectors.
   */
  public static final DataFormat CONTROL_VECTOR = new DataFormat(APP_PREFIX + "/control-vector");

  /**
   * Data format for dragging headings.
   */
  public static final DataFormat HEADING = new DataFormat(APP_PREFIX + "/heading");

  /**
   * Data format for dragging spline.
   */
  public static final DataFormat SPLINE = new DataFormat(APP_PREFIX + "/spline");

  private DataFormats() {
    throw new UnsupportedOperationException("This is a utility class");
  }

}
