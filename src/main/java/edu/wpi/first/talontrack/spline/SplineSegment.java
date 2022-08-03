package edu.wpi.first.talontrack.spline;

import edu.wpi.first.talontrack.Waypoint;
import edu.wpi.first.talontrack.global.CurrentSelections;
import edu.wpi.first.talontrack.path.Path;
import javafx.scene.shape.Polyline;

public class SplineSegment {
    private final Polyline line = new Polyline();
    private Waypoint start;
    private Waypoint end;

    public SplineSegment(Waypoint start, Waypoint end, Path path) {
        this.start = start;
        this.end = end;
        line.setOnDragDetected(event -> {
            CurrentSelections.setCurSplineStart(this.start);
            CurrentSelections.setCurSplineEnd(this.end);
            CurrentSelections.setCurPath(path);
        });

        line.setOnMouseClicked(event -> {
            CurrentSelections.setCurPath(path);
            event.consume();
        });
    }

    public Polyline getLine() {
        return line;
    }

    public Waypoint getStart() {
        return start;
    }

    public Waypoint getEnd() {
        return end;
    }

    public void setStart(Waypoint start) {
        this.start = start;
    }

    public void setEnd(Waypoint end) {
        this.end = end;
    }
}
