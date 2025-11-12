package org.example.model;

/**
 * Configuración de parámetros para cada tipo de movimiento.
 */
public class MovementConfig {
    private final int amplitude;
    private final int windowMs;
    private final int moveDurationMs;
    private final int steps;
    private final boolean useRandomMovement;

    public MovementConfig(int amplitude, int windowMs, int moveDurationMs, int steps, boolean useRandomMovement) {
        this.amplitude = amplitude;
        this.windowMs = windowMs;
        this.moveDurationMs = moveDurationMs;
        this.steps = steps;
        this.useRandomMovement = useRandomMovement;
    }

    public int getAmplitude() {
        return amplitude;
    }

    public int getWindowMs() {
        return windowMs;
    }

    public int getMoveDurationMs() {
        return moveDurationMs;
    }

    public int getSteps() {
        return steps;
    }

    public boolean isUseRandomMovement() {
        return useRandomMovement;
    }

    public static MovementConfig getConfigForType(MovementType type) {
        return switch (type) {
            case LOW -> new MovementConfig(2, 30_000, 200, 12, true);
            case MEDIUM -> new MovementConfig(8, 10_000, 600, 30, false);
            case HIGH -> new MovementConfig(18, 6_000, 1000, 45, false);
        };
    }
}

