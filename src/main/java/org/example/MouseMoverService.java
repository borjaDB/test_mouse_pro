package org.example;

import java.awt.*;
import javax.swing.*;
import java.util.Random;

public class MouseMoverService implements Runnable {
    private final MouseMoverLogic logic;
    private final EventLogger logger;
    private volatile boolean running = false;
    private volatile boolean paused = false;
    private volatile Point lastUserPosition = null;
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
                int windowMs = 10_000;
                int moveDurationMs = 600;
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

                logger.logEvent("El proceso automático continúa");
                Point moveStartLocation = MouseInfo.getPointerInfo().getLocation();
                int moveStartX = (int) moveStartLocation.getX();
                int moveStartY = (int) moveStartLocation.getY();

                int amplitude = 8;
                int steps = 30;
                int delay = moveDurationMs / steps;
                for (int i = 0; i < steps; i++) {
                    if (!running) return;
                    if (paused) { Thread.sleep(200); i--; continue; }
                    double progress = (double) i / (steps - 1);
                    double offset = Math.sin(Math.PI * progress);
                    int x = moveStartX + (int) (amplitude * offset);
                    robot.mouseMove(x, moveStartY);
                    Thread.sleep(delay);
                }
                robot.mouseMove(moveStartX, moveStartY);

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
}

