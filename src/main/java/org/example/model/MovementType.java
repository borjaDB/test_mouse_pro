package org.example.model;

/**
 * Enum que representa los diferentes tipos de movimiento del ratón.
 */
public enum MovementType {
    LOW("Bajo"),
    MEDIUM("Medio"),
    HIGH("Alto");

    private final String displayName;

    MovementType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static MovementType fromString(String text) {
        for (MovementType type : MovementType.values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        return MEDIUM;
    }
}

