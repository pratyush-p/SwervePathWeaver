package edu.wpi.first.talontrack.spline;

import java.util.List;

import edu.wpi.first.talontrack.Waypoint;
import edu.wpi.first.talontrack.path.Path;

//Simple functional interface to create a Spline
public interface SplineFactory {
    Spline makeSpline(List<Waypoint> waypoints, Path path);
}
