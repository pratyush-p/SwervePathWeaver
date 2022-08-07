package edu.wpi.first.talontrack.global;

import java.util.List;

import edu.wpi.first.talontrack.CommandInstance;
import edu.wpi.first.talontrack.CommandTemplate;
import edu.wpi.first.talontrack.Waypoint;
import edu.wpi.first.talontrack.path.Path;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

/**
 * The class holding the global state for the currently selected objects.
 * Those who would like to remove this should refrain from doing so, as the
 * necessary plumbing is verbose and unwieldy.
 */

public final class CurrentSelections {
    private static SimpleObjectProperty<Waypoint> curSplineStart = new SimpleObjectProperty<>();
    private static SimpleObjectProperty<Waypoint> curSplineEnd = new SimpleObjectProperty<>();
    private static SimpleObjectProperty<Waypoint> curWaypoint = new SimpleObjectProperty<>();
    private static SimpleObjectProperty<Path> curPath = new SimpleObjectProperty<>();
    private static SimpleObjectProperty<CommandTemplate> curCommandTemp = new SimpleObjectProperty<>();
    private static SimpleObjectProperty<List<CommandTemplate>> curCommandTempArr = new SimpleObjectProperty<>();
    private static SimpleObjectProperty<ObservableList<Path>> curPathList = new SimpleObjectProperty<>();
    private static SimpleObjectProperty<CommandInstance> curInst = new SimpleObjectProperty<>();

    private CurrentSelections() {
        throw new UnsupportedOperationException("This class holds global state!");
    }

    public static Waypoint getCurSplineStart() {
        return curSplineStart.get();
    }

    public static SimpleObjectProperty<Waypoint> curSegmentStartProperty() {
        return curSplineStart;
    }

    public static void setCurSplineStart(Waypoint curSplineStart) {
        CurrentSelections.curSplineStart.set(curSplineStart);
    }

    public static Waypoint getCurSplineEnd() {
        return curSplineEnd.get();
    }

    public static SimpleObjectProperty<Waypoint> curSegmentEndProperty() {
        return curSplineEnd;
    }

    public static void setCurSplineEnd(Waypoint curSplineEnd) {
        CurrentSelections.curSplineEnd.set(curSplineEnd);
    }

    public static Waypoint getCurWaypoint() {
        return curWaypoint.get();
    }

    public static SimpleObjectProperty<Waypoint> curWaypointProperty() {
        return curWaypoint;
    }

    public static void setCurWaypoint(Waypoint curWaypoint) {
        CurrentSelections.curWaypoint.set(curWaypoint);
    }

    public static Path getCurPath() {
        return curPath.get();
    }

    public static SimpleObjectProperty<Path> curPathProperty() {
        return curPath;
    }

    public static void setCurPath(Path curPath) {
        CurrentSelections.curPath.set(curPath);
    }

    public static CommandTemplate getCurCommandTemplate() {
        return curCommandTemp.get();
    }

    public static SimpleObjectProperty<CommandTemplate> curCommandTemplateProperty() {
        return curCommandTemp;
    }

    public static void setCurCommandTemplate(CommandTemplate curCommandTemplate) {
        CurrentSelections.curCommandTemp.set(curCommandTemplate);
    }

    public static List<CommandTemplate> getCurCommandTemplateArr() {
        return curCommandTempArr.get();
    }

    public static SimpleObjectProperty<List<CommandTemplate>> curCommandTemplatePropertyArr() {
        return curCommandTempArr;
    }

    public static void setCurCommandTemplateArr(List<CommandTemplate> curCommandTemplateArr) {
        CurrentSelections.curCommandTempArr.set(curCommandTemplateArr);
    }

    public static ObservableList<Path> getCurPathlist() {
        return curPathList.get();
    }

    public static SimpleObjectProperty<ObservableList<Path>> curPathlistProperty() {
        return curPathList;
    }

    public static void setCurPathlist(ObservableList<Path> curPathlist) {
        CurrentSelections.curPathList.set(curPathlist);
    }

    public static CommandInstance getCurInst() {
        return curInst.get();
    }

    public static SimpleObjectProperty<CommandInstance> curInstProperty() {
        return curInst;
    }

    public static void setCurInst(CommandInstance curInst) {
        CurrentSelections.curInst.set(curInst);
    }

}
