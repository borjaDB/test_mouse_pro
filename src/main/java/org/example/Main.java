package org.example;

import org.example.service.MouseMoverLogic;
import org.example.service.MouseMoverService;
import org.example.util.EventLogger;

import javax.swing.*;

/**
 * Clase principal de la aplicación.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EventLogger logger = new EventLogger();
            MouseMoverLogic logic = new MouseMoverLogic();
            MouseMoverService moverService = new MouseMoverService(logic, logger);

            JFrame frame = new JFrame("Mouse Mover - Control de Actividad");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(420, 285);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);

            TimerPanel timerPanel = new TimerPanel(moverService, logger);
            frame.setContentPane(timerPanel);

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    timerPanel.saveConfigOnExit();
                }
            });

            frame.setVisible(true);
        });
    }
}