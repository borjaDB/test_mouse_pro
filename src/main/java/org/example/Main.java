package org.example;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EventLogger logger = new EventLogger();
            MouseMoverLogic logic = new MouseMoverLogic();
            MouseMoverService moverService = new MouseMoverService(logic, logger);
            JFrame frame = new JFrame("Temporizador");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(420, 260);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TimerPanel(moverService, logger));
            frame.setVisible(true);
        });
    }
}