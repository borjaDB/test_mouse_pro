package org.example;

import java.awt.*;

public class MouseMoverLogic {
    public boolean shouldSkipCycle(Point lastUserPosition, Point currentPosition, int threshold) {
        return currentPosition.distance(lastUserPosition) > threshold;
    }
}

