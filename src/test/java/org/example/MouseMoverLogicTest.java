package org.example;

import org.example.service.MouseMoverLogic;
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

    @Test
    void testGenerateRandomOffset_WithinAmplitude() {
        MouseMoverLogic logic = new MouseMoverLogic();
        int amplitude = 5;
        for (int i = 0; i < 100; i++) {
            Point offset = logic.generateRandomOffset(amplitude);
            assertTrue(Math.abs(offset.x) <= amplitude, "X offset should be within amplitude");
            assertTrue(Math.abs(offset.y) <= amplitude, "Y offset should be within amplitude");
        }
    }

    @Test
    void testCalculateSinusoidalPosition_StartPosition() {
        MouseMoverLogic logic = new MouseMoverLogic();
        Point pos = logic.calculateSinusoidalPosition(100, 100, 8, 0.0);
        assertEquals(100, pos.x);
        assertEquals(100, pos.y);
    }

    @Test
    void testCalculateSinusoidalPosition_MidPosition() {
        MouseMoverLogic logic = new MouseMoverLogic();
        Point pos = logic.calculateSinusoidalPosition(100, 100, 8, 0.5);
        assertEquals(108, pos.x); // 100 + 8 * sin(PI * 0.5) = 100 + 8 = 108
        assertEquals(100, pos.y);
    }

    @Test
    void testCalculateSinusoidalPosition_EndPosition() {
        MouseMoverLogic logic = new MouseMoverLogic();
        Point pos = logic.calculateSinusoidalPosition(100, 100, 8, 1.0);
        assertEquals(100, pos.x);
        assertEquals(100, pos.y);
    }
}

