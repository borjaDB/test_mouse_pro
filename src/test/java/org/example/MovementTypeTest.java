package org.example;

import org.example.model.MovementType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MovementTypeTest {
    @Test
    void testGetDisplayName() {
        assertEquals("Bajo", MovementType.LOW.getDisplayName());
        assertEquals("Medio", MovementType.MEDIUM.getDisplayName());
        assertEquals("Alto", MovementType.HIGH.getDisplayName());
    }

    @Test
    void testFromString_ValidInputs() {
        assertEquals(MovementType.LOW, MovementType.fromString("LOW"));
        assertEquals(MovementType.MEDIUM, MovementType.fromString("MEDIUM"));
        assertEquals(MovementType.HIGH, MovementType.fromString("HIGH"));
    }

    @Test
    void testFromString_CaseInsensitive() {
        assertEquals(MovementType.LOW, MovementType.fromString("low"));
        assertEquals(MovementType.MEDIUM, MovementType.fromString("medium"));
        assertEquals(MovementType.HIGH, MovementType.fromString("high"));
    }

    @Test
    void testFromString_InvalidInput() {
        assertEquals(MovementType.MEDIUM, MovementType.fromString("invalid"));
        assertEquals(MovementType.MEDIUM, MovementType.fromString(""));
        assertEquals(MovementType.MEDIUM, MovementType.fromString(null));
    }
}

