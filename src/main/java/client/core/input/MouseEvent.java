package client.core.input;

import java.awt.Point;
import lombok.Value;
import lombok.With;

public sealed interface MouseEvent {
  // Common properties all events might need
  Point getPoint();

  default int getX() {
    return getPoint().x;
  }

  default int getY() {
    return getPoint().y;
  }

  MouseEvent withPoint(Point point);

  boolean isConsumed();

  // Different event types as sealed subclasses
  @Value
  @With
  final class Pressed implements MouseEvent {
    Point point;
    int button;
    int clickCount;
    boolean isConsumed;
  }

  @Value
  @With
  final class Released implements MouseEvent {
    Point point;
    int button;
    boolean isConsumed;
  }

  @Value
  @With
  final class Clicked implements MouseEvent {
    Point point;
    int button;
    int clickCount;
    boolean isConsumed;
  }

  @Value
  @With
  final class Moved implements MouseEvent {
    Point point;
    boolean isConsumed;
  }

  @Value
  @With
  final class Dragged implements MouseEvent {
    Point point;
    int button;
    boolean isConsumed;
  }

  @Value
  @With
  final class Entered implements MouseEvent {
    Point point;
    boolean isConsumed;
  }

  @Value
  @With
  final class Exited implements MouseEvent {
    Point point;
    boolean isConsumed;
  }

  @Value
  @With
  final class WheelMoved implements MouseEvent {
    Point point;
    double scrollX;
    double scrollY;
    boolean isConsumed;

    public float getWheelRotation() {
      return (float) scrollY;
    }
  }
}
