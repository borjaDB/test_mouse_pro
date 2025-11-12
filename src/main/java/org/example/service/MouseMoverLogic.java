package org.example.service;

import java.awt.*;
import java.util.Random;

/**
 * Lógica de movimiento del ratón.
 * Proporciona métodos para calcular posiciones y offsets.
 */
public class MouseMoverLogic {
    private final Random random = new Random();

    public boolean shouldSkipCycle(Point lastUserPosition, Point currentPosition, int threshold) {
        return currentPosition.distance(lastUserPosition) > threshold;
    }

    /**
     * Genera un desplazamiento aleatorio pequeño para movimientos sutiles
     */
    public Point generateRandomOffset(int amplitude) {
        int xOffset = random.nextInt(amplitude * 2 + 1) - amplitude;
        int yOffset = random.nextInt(amplitude * 2 + 1) - amplitude;
        return new Point(xOffset, yOffset);
    }

    /**
     * Calcula la posición en una curva sinusoidal para movimientos suaves
     */
    public Point calculateSinusoidalPosition(int startX, int startY, int amplitude, double progress) {
        double offset = Math.sin(Math.PI * progress);
        int x = startX + (int) (amplitude * offset);
        return new Point(x, startY);
    }
}

