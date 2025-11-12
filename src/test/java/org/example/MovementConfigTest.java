package org.example;

import org.example.model.MovementConfig;
import org.example.model.MovementType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MovementConfigTest {
    @Test
    void testGetConfigForType_Low() {
        MovementConfig config = MovementConfig.getConfigForType(MovementType.LOW);
        assertEquals(2, config.getAmplitude());
        assertEquals(30_000, config.getWindowMs());
        assertEquals(200, config.getMoveDurationMs());
        assertEquals(12, config.getSteps());
        assertTrue(config.isUseRandomMovement());
    }

    @Test
    void testGetConfigForType_Medium() {
        MovementConfig config = MovementConfig.getConfigForType(MovementType.MEDIUM);
        assertEquals(8, config.getAmplitude());
        assertEquals(10_000, config.getWindowMs());
        assertEquals(600, config.getMoveDurationMs());
        assertEquals(30, config.getSteps());
        assertFalse(config.isUseRandomMovement());
    }

    @Test
    void testGetConfigForType_High() {
        MovementConfig config = MovementConfig.getConfigForType(MovementType.HIGH);
        assertEquals(18, config.getAmplitude());
        assertEquals(6_000, config.getWindowMs());
        assertEquals(1000, config.getMoveDurationMs());
        assertEquals(45, config.getSteps());
        assertFalse(config.isUseRandomMovement());
    }

    @Test
    void testConfigValues_LowMovementIsSubtle() {
        MovementConfig lowConfig = MovementConfig.getConfigForType(MovementType.LOW);
        MovementConfig mediumConfig = MovementConfig.getConfigForType(MovementType.MEDIUM);

        assertTrue(lowConfig.getAmplitude() < mediumConfig.getAmplitude(),
            "Low amplitude should be less than medium");
        assertTrue(lowConfig.getWindowMs() > mediumConfig.getWindowMs(),
            "Low window should be longer than medium");
    }

    @Test
    void testConfigValues_HighMovementIsNotable() {
        MovementConfig highConfig = MovementConfig.getConfigForType(MovementType.HIGH);
        MovementConfig mediumConfig = MovementConfig.getConfigForType(MovementType.MEDIUM);

        assertTrue(highConfig.getAmplitude() > mediumConfig.getAmplitude(),
            "High amplitude should be greater than medium");
        assertTrue(highConfig.getWindowMs() < mediumConfig.getWindowMs(),
            "High window should be shorter than medium");
    }
}

