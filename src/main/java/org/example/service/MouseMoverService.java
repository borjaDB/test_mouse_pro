package org.example.service;

import org.example.model.MovementConfig;
import org.example.model.MovementType;
import org.example.util.EventLogger;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Servicio que gestiona el movimiento automático del ratón.
 */
public class MouseMoverService implements Runnable {
    private final MouseMoverLogic logic;
    private final EventLogger logger;
    private volatile boolean running = false;
    private volatile boolean paused = false;
    private volatile Point lastUserPosition = null;
    private volatile MovementType currentMovementType = MovementType.MEDIUM;
    private static final int USER_MOVE_THRESHOLD = 10;

    public MouseMoverService(MouseMoverLogic logic, EventLogger logger) {
        this.logic = logic;
        this.logger = logger;
    }

    public void start() { running = true; }
    public void stop() { running = false; }
    public void pause() { paused = true; }
    public void resume() { paused = false; }
    public boolean isRunning() { return running; }
    public boolean isPaused() { return paused; }
    public Point getLastUserPosition() { return lastUserPosition; }

    public MovementType getMovementType() { return currentMovementType; }
    public void setMovementType(MovementType type) {
        this.currentMovementType = type;
        logger.logEvent("Tipo de movimiento cambiado a: " + type.getDisplayName());
    }

    @Override
    public void run() {
        try {
            Robot robot = new Robot();
            Random random = new Random();
            while (running) {
                if (paused) {
                    Thread.sleep(200);
                    continue;
                }

                MovementConfig config = MovementConfig.getConfigForType(currentMovementType);

                int windowMs = config.getWindowMs();
                int moveDurationMs = config.getMoveDurationMs();
                int idleBefore = random.nextInt(windowMs - moveDurationMs + 1);
                int idleAfter = windowMs - moveDurationMs - idleBefore;

                Point beforeWaitPosition = MouseInfo.getPointerInfo().getLocation();
                lastUserPosition = beforeWaitPosition;

                for (int i = 0; i < idleBefore / 100; i++) {
                    if (!running) return;
                    if (paused) { Thread.sleep(200); i--; continue; }
                    Thread.sleep(100);
                    Point current = MouseInfo.getPointerInfo().getLocation();
                    if (logic.shouldSkipCycle(lastUserPosition, current, USER_MOVE_THRESHOLD)) {
                        lastUserPosition = current;
                        logger.logEvent("El usuario ha movido el ratón");
                        Thread.sleep(idleAfter + moveDurationMs);
                        continue;
                    }
                }
                Thread.sleep(idleBefore % 100);

                if (!running) return;
                if (paused) continue;

                Point current = MouseInfo.getPointerInfo().getLocation();
                if (logic.shouldSkipCycle(lastUserPosition, current, USER_MOVE_THRESHOLD)) {
                    lastUserPosition = current;
                    logger.logEvent("El usuario ha movido el ratón");
                    Thread.sleep(idleAfter + moveDurationMs);
                    continue;
                }

                logger.logEvent("El proceso automático continúa (" + currentMovementType.getDisplayName() + ")");

                if (config.isUseRandomMovement()) {
                    executeRandomMovement(robot, config);
                } else {
                    executeSinusoidalMovement(robot, config);
                }

                for (int i = 0; i < idleAfter / 100; i++) {
                    if (!running) return;
                    if (paused) { Thread.sleep(200); i--; continue; }
                    Thread.sleep(100);
                }
                Thread.sleep(idleAfter % 100);
            }
        } catch (AWTException | InterruptedException e) {
            logger.logEvent("Error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeRandomMovement(Robot robot, MovementConfig config) throws InterruptedException {
        Point moveStartLocation = MouseInfo.getPointerInfo().getLocation();
        int moveStartX = (int) moveStartLocation.getX();
        int moveStartY = (int) moveStartLocation.getY();

        int steps = config.getSteps();
        int delay = config.getMoveDurationMs() / steps;

        for (int i = 0; i < steps; i++) {
            if (!running) return;
            if (paused) { Thread.sleep(200); i--; continue; }

            Point offset = logic.generateRandomOffset(config.getAmplitude());
            int x = moveStartX + offset.x;
            int y = moveStartY + offset.y;
            robot.mouseMove(x, y);
            Thread.sleep(delay);
        }
    }

    private void executeSinusoidalMovement(Robot robot, MovementConfig config) throws InterruptedException {
        Point moveStartLocation = MouseInfo.getPointerInfo().getLocation();
        int moveStartX = (int) moveStartLocation.getX();
        int moveStartY = (int) moveStartLocation.getY();

        int steps = config.getSteps();
        int delay = config.getMoveDurationMs() / steps;

        for (int i = 0; i < steps; i++) {
            if (!running) return;
            if (paused) { Thread.sleep(200); i--; continue; }

            double progress = (double) i / (steps - 1);
            Point newPos = logic.calculateSinusoidalPosition(moveStartX, moveStartY, config.getAmplitude(), progress);
            robot.mouseMove(newPos.x, newPos.y);
            Thread.sleep(delay);
        }
        robot.mouseMove(moveStartX, moveStartY);
    }
}

