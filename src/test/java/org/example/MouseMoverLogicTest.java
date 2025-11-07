package org.example;

import org.junit.jupiter.api.Test;
import java.awt.Point;
import static org.junit.jupiter.api.Assertions.*;

class MouseMoverLogicTest {
    @Test
    void testShouldSkipCycle_NoMovement() {
        MouseMoverLogic logic = new MouseMoverLogic();
        Point last = new Point(100, 100);
        Point current = new Point(100, 100);
        assertFalse(logic.shouldSkipCycle(last, current, 10));
    }

    @Test
    void testShouldSkipCycle_SmallMovement() {
        MouseMoverLogic logic = new MouseMoverLogic();
        Point last = new Point(100, 100);
        Point current = new Point(105, 105);
        assertFalse(logic.shouldSkipCycle(last, current, 10));
    }

    @Test
    void testShouldSkipCycle_LargeMovement() {
        MouseMoverLogic logic = new MouseMoverLogic();
        Point last = new Point(100, 100);
        Point current = new Point(120, 120);
        assertTrue(logic.shouldSkipCycle(last, current, 10));
    }
}

