package edu.wpi.first.pathweaver;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

import javax.measure.Unit;
import javax.measure.quantity.Length;

/**
 * The Waypoint class represents a point on the field. This class
 * follows WPILib convention, with X being the long side of the field,
 * and Y being the short side.
 *
 * Viewed from the screen, Y should increase as one moves up the screen, and X
 * should increase as one moves left.
 */
public class Waypoint {
	private static final double SIZE = 30.0;
	private static final double ICON_X_OFFSET = (SIZE * 3D / 5D) / 16.5;

	private final DoubleProperty x = new SimpleDoubleProperty();
	private final DoubleProperty y = new SimpleDoubleProperty();
	private final DoubleProperty tangentX = new SimpleDoubleProperty();
	private final DoubleProperty tangentY = new SimpleDoubleProperty();
	private final DoubleProperty headingX = new SimpleDoubleProperty();
	private final DoubleProperty headingY = new SimpleDoubleProperty();
	private final BooleanProperty lockTangent = new SimpleBooleanProperty();
	private final BooleanProperty lockHeading = new SimpleBooleanProperty();
	private final BooleanProperty reversed = new SimpleBooleanProperty();
	private final StringProperty name = new SimpleStringProperty("");

	private final Line tangentLine;
	private final Line headingLine;
	private final Polygon icon;
	private final Rectangle robot;

	private Field field;

	/**
	 * Creates Waypoint object containing javafx circle.
	 *
	 * @param position
	 *                      x and y coordinates in {@link Waypoint} convention
	 * @param tangentVector
	 *                      tangent vector in user set units
	 * @param fixedAngle
	 *                      If the angle the of the waypoint should be fixed. Used
	 *                      for first
	 *                      and last waypoint
	 */
	public Waypoint(Point2D position, Point2D tangentVector, Point2D headingVector, boolean fixedAngle, boolean reverse) {
		lockTangent.set(fixedAngle);
		lockHeading.set(true);
		reversed.set(reverse);
		setCoords(position);

		field = ProjectPreferences.getInstance().getField();
		var values = ProjectPreferences.getInstance().getValues();

		robot = new Rectangle(values.getBumperLength() * field.getScale(), values.getBumperWidth() * field.getScale());
		setupRobot();

		icon = new Polygon(0.0, SIZE / 3, SIZE, 0.0, 0.0, -SIZE / 3);
		setupIcon();

		tangentLine = new Line();
		tangentLine.getStyleClass().add("tangent");
		tangentLine.startXProperty().bind(x);
		tangentLine.startYProperty().bind(y.negate());
		setTangent(tangentVector);
		tangentLine.endXProperty().bind(Bindings.createObjectBinding(() -> getTangentX() + getX(), tangentX, x));
		tangentLine.endYProperty().bind(Bindings.createObjectBinding(() -> -getTangentY() + -getY(), tangentY, y));
		tangentLine.setSmooth(true);

		headingLine = new Line();
		headingLine.getStyleClass().add("heading");
		headingLine.startXProperty().bind(x);
		headingLine.startYProperty().bind(y.negate());
		setHeading(headingVector);
		headingLine.endXProperty().bind(Bindings.createObjectBinding(() -> getHeadingX() + getX(), headingX, x));
		headingLine.endYProperty().bind(Bindings.createObjectBinding(() -> -getHeadingY() + -getY(), headingY, y));
		headingLine.setSmooth(true);

	}

	public void enableSubchildSelector(int i) {
		// FxUtils.enableSubchildSelector(this.robot, i);
		FxUtils.enableSubchildSelector(this.icon, i);
		// getRobot().applyCss();
		getIcon().applyCss();
	}

	private void setupIcon() {
		icon.setLayoutX(-(icon.getLayoutBounds().getMaxX() + icon.getLayoutBounds().getMinX()) / 2 - ICON_X_OFFSET);
		icon.setLayoutY(-(icon.getLayoutBounds().getMaxY() + icon.getLayoutBounds().getMinY()) / 2);

		icon.translateXProperty().bind(x);
		icon.translateYProperty().bind(y.negate());
		FxUtils.applySubchildClasses(this.icon);
		this.icon.rotateProperty()
				.bind(Bindings.createObjectBinding(
						() -> getHeading() == null ? 0.0 : Math.toDegrees(Math.atan2(-getHeadingY(), getHeadingX())),
						headingX, headingY));
		icon.getStyleClass().add("waypoint");
	}

	private void setupRobot() {
		robot.setLayoutX(-(robot.getLayoutBounds().getMaxX() + robot.getLayoutBounds().getMinX()) / 2);
		robot.setLayoutY(-(robot.getLayoutBounds().getMaxY() + robot.getLayoutBounds().getMinY()) / 2);

		robot.translateXProperty().bind(x);
		robot.translateYProperty().bind(y.negate());
		FxUtils.applySubchildClasses(this.robot);
		this.robot.rotateProperty()
				.bind(Bindings.createObjectBinding(
						() -> getHeading() == null ? 0.0 : Math.toDegrees(Math.atan2(-getHeadingY(), getHeadingX())),
						headingX, headingY));
		robot.getStyleClass().add("robot");
		robot.setSmooth(true);
		robot.setStrokeLineJoin(StrokeLineJoin.ROUND);
		robot.setOpacity(0.33);
	}

	/**
	 * Convenience function for math purposes.
	 *
	 * @param other
	 *              The other Waypoint.
	 *
	 * @return The coordinates of this Waypoint relative to the coordinates of
	 *         another Waypoint.
	 */
	public Point2D relativeTo(Waypoint other) {
		return new Point2D(this.getX() - other.getX(), this.getY() - other.getY());
	}

	public boolean isLockTangent() {
		return lockTangent.get();
	}

	public BooleanProperty lockTangentProperty() {
		return lockTangent;
	}

	public void setLockTangent(boolean lockTangent) {
		this.lockTangent.set(lockTangent);
	}

	public boolean isLockHeading() {
		return lockHeading.get();
	}

	public boolean isReversed() {
		return reversed.get();
	}

	public BooleanProperty reversedProperty() {
		return reversed;
	}

	public void setReversed(boolean reversed) {
		this.reversed.set(reversed);
	}

	public Line getTangentLine() {
		return tangentLine;
	}

	public Point2D getTangent() {
		return new Point2D(tangentX.get(), tangentY.get());
	}

	public void setTangent(Point2D tangent) {
		this.tangentX.set(tangent.getX());
		this.tangentY.set(tangent.getY());
	}

	public double getTangentX() {
		return tangentX.get();
	}

	public double getTangentY() {
		return tangentY.get();
	}

	public void setTangentX(double tangentX) {
		this.tangentX.set(tangentX);
	}

	public void setTangentY(double tangentY) {
		this.tangentY.set(tangentY);
	}

	public Line getHeadingLine() {
		return headingLine;
	}

	public Point2D getHeading() {
		return new Point2D(headingX.get(), headingY.get());
	}

	public void setHeading(Point2D heading) {
		this.headingX.set(heading.getX());
		this.headingY.set(heading.getY());
	}

	public double getHeadingX() {
		return headingX.get();
	}

	public double getHeadingY() {
		return headingY.get();
	}

	public void setHeadingX(double headingX) {
		this.headingX.set(headingX);
	}

	public void setHeadingY(double headingY) {
		this.headingY.set(headingY);
	}

	public Polygon getIcon() {
		return icon;
	}

	public Rectangle getRobot() {
		return robot;
	}

	public double getX() {
		return x.get();
	}

	public DoubleProperty xProperty() {
		return x;
	}

	public void setX(double x) {
		this.x.set(x);
	}

	public double getY() {
		return y.get();
	}

	public DoubleProperty yProperty() {
		return y;
	}

	public void setY(double y) {
		this.y.set(y);
	}

	public Point2D getCoords() {
		return new Point2D(getX(), getY());
	}

	public void setCoords(Point2D coords) {
		setX(coords.getX());
		setY(coords.getY());
	}

	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public DoubleProperty tangentXProperty() {
		return tangentX;
	}

	public DoubleProperty tangentYProperty() {
		return tangentY;
	}

	public DoubleProperty headingXProperty() {
		return headingX;
	}

	public DoubleProperty headingYProperty() {
		return headingY;
	}

	/**
	 * Converts the unit system of a this Waypoint.
	 *
	 * @param from
	 *             Unit to convert from.
	 * @param to
	 *             Unit to convert to.
	 */
	public void convertUnit(Unit<Length> from, Unit<Length> to) {
		var converter = from.getConverterTo(to);
		x.set(converter.convert(x.get()));
		y.set(converter.convert(y.get()));
		tangentX.set(converter.convert(tangentX.get()));
		tangentY.set(converter.convert(tangentY.get()));
		headingX.set(converter.convert(headingX.get()));
		headingY.set(converter.convert(headingY.get()));
	}

	public Waypoint copy() {
		return new Waypoint(getCoords(), getTangent(), getHeading(), isLockTangent(), isReversed());
	}

	@Override
	public String toString() {
		return String.format("%s (%f,%f), (%f,%f), (%f,%f), %b %b", getName(), getX(), getY(), getTangentX(), getTangentY(),
				getHeadingX(), getHeadingY(), isLockTangent(), isReversed());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		Waypoint point = (Waypoint) o;

		return x.get() == point.x.get() && y.get() == point.y.get() && tangentX.get() == point.tangentX.get()
				&& tangentY.get() == point.tangentY.get() && name.get().equals(point.name.get())
				&& isLockTangent() == point.isLockTangent() && isReversed() == point.isReversed();
	}
}
