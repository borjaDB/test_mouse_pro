package org.example;

import org.example.config.AppConfig;
import org.example.model.MovementType;
import org.example.service.MouseMoverLogic;
import org.example.service.MouseMoverService;
import org.example.util.EventLogger;
import org.example.util.TimeFormatter;

import javax.swing.*;
import java.awt.*;

import static javax.swing.UIManager.setLookAndFeel;

/**
 * Panel principal con todas las funcionalidades implementadas.
 */
public class TimerPanel extends JPanel {
    private final JButton startButton = new JButton("Inicio");
    private final JButton pauseButton = new JButton("Pausa");
    private final JButton stopButton = new JButton("Parar");
    private final MouseMoverService moverService;
    private final EventLogger logger;
    private final AppConfig appConfig;

    private volatile long startTime = 0;
    private volatile long elapsedTime = 0;
    private volatile boolean running = false;
    private volatile boolean paused = false;
    private Thread workerThread = null;

    private final Color darkBg = new Color(40, 40, 40);
    private final Color darkCompBg = new Color(60, 60, 60);
    private final Color darkFg = Color.LIGHT_GRAY;
    private final Color lightBg = Color.WHITE;
    private final Color lightCompBg = new Color(240, 240, 240);
    private final Color lightFg = Color.DARK_GRAY;
    private boolean isDark = true;

    // Labels
    private final JLabel fechaLabel;
    private final JLabel horaLabel;
    private final JLabel estadoLabel;
    private final JLabel tiempoLabel;
    private final JLabel modoLabel;
    private final JLabel separadorVertical;

    // Menú items
    private final JMenuItem oscuroItem;
    private final JMenuItem claroItem;
    private final JMenuItem estiloWindows;
    private final JMenuItem estiloNimbus;
    private final JMenuItem estiloMetal;
    private final JMenuItem modoBajoItem;
    private final JMenuItem modoMedioItem;
    private final JMenuItem modoAltoItem;
    private final JMenuBar menuBar;

    public TimerPanel(MouseMoverService moverService, EventLogger logger) {
        this.moverService = moverService;
        this.logger = logger;
        this.appConfig = new AppConfig();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // Panel superior
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setOpaque(false);
        datePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        fechaLabel = new JLabel(TimeFormatter.getFormattedDate());
        fechaLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        datePanel.add(fechaLabel, BorderLayout.WEST);

        horaLabel = new JLabel(TimeFormatter.getFormattedTime());
        horaLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        datePanel.add(horaLabel, BorderLayout.EAST);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(datePanel, BorderLayout.NORTH);

        // Panel de información
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(true);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Estado del Sistema",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP
            ),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(3, 0, 3, 0);

        // Estado y modo en la misma línea
        JPanel estadoModoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        estadoModoPanel.setOpaque(false);

        estadoLabel = new JLabel("Apagado", SwingConstants.CENTER);
        modoLabel = new JLabel("Modo: Medio", SwingConstants.CENTER);
        separadorVertical = new JLabel(" | ");

        estadoModoPanel.add(estadoLabel);
        estadoModoPanel.add(separadorVertical);
        estadoModoPanel.add(modoLabel);

        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(estadoModoPanel, gbc);

        JSeparator separator = new JSeparator();
        separator.setPreferredSize(new Dimension(200, 1));
        gbc.gridy = 1;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(separator, gbc);

        tiempoLabel = new JLabel("Tiempo: 00:00:00", SwingConstants.CENTER);
        gbc.gridy = 2;
        gbc.insets = new Insets(3, 0, 3, 0);
        gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(tiempoLabel, gbc);

        infoPanel.add(contentPanel, BorderLayout.CENTER);
        topPanel.add(infoPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Panel central
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setPreferredSize(new Dimension(0, 30));
        add(centerPanel, BorderLayout.CENTER);

        // Panel de botones
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 3));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        Dimension buttonSize = new Dimension(90, 35);
        startButton.setPreferredSize(buttonSize);
        pauseButton.setPreferredSize(buttonSize);
        stopButton.setPreferredSize(buttonSize);

        bottomPanel.add(startButton);
        bottomPanel.add(pauseButton);
        bottomPanel.add(stopButton);
        add(bottomPanel, BorderLayout.SOUTH);

        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);

        // Timer de actualización en tiempo real
        Timer swingTimer = new Timer(500, e -> {
            fechaLabel.setText(TimeFormatter.getFormattedDate());
            horaLabel.setText(TimeFormatter.getFormattedTime());

            long currentTime = paused ? elapsedTime : (running ? System.currentTimeMillis() - startTime : elapsedTime);
            tiempoLabel.setText("Tiempo: " + TimeFormatter.formatElapsedTime(currentTime));

            if (running) {
                estadoLabel.setText(paused ? "Pausado" : getEstadoActual());
                Color pausedColor = isDark ? new Color(100, 149, 237) : new Color(0, 0, 180);
                Color movementColor = isDark ? Color.ORANGE : new Color(200, 100, 0);
                Color automaticColor = isDark ? Color.GREEN : new Color(0, 130, 0);
                estadoLabel.setForeground(paused ? pausedColor :
                    (getEstadoActual().equals("En movimiento") ? movementColor : automaticColor));
            } else {
                estadoLabel.setText("Apagado");
                estadoLabel.setForeground(Color.GRAY);
            }

            // Actualizar modo en tiempo real
            actualizarLabelModo();
        });
        swingTimer.start();

        // Listeners de botones
        startButton.addActionListener(e -> {
            if (!running) {
                running = true;
                paused = false;
                startTime = System.currentTimeMillis();
                moverService.start();
                workerThread = new Thread(moverService);
                workerThread.start();
                startButton.setEnabled(false);
                pauseButton.setEnabled(true);
                stopButton.setEnabled(true);
                logger.logEvent("Inicio del proceso automático");
            }
        });

        pauseButton.addActionListener(e -> {
            if (running) {
                paused = !paused;
                if (paused) {
                    elapsedTime = System.currentTimeMillis() - startTime;
                    moverService.pause();
                    pauseButton.setText("Reanudar");
                    logger.logEvent("Proceso pausado");
                } else {
                    startTime = System.currentTimeMillis() - elapsedTime;
                    moverService.resume();
                    pauseButton.setText("Pausa");
                    logger.logEvent("Proceso reanudado");
                }
            }
        });

        stopButton.addActionListener(e -> {
            if (running) {
                running = false;
                paused = false;
                moverService.stop();
                elapsedTime = System.currentTimeMillis() - startTime;
                startButton.setEnabled(true);
                pauseButton.setEnabled(false);
                pauseButton.setText("Pausa");
                stopButton.setEnabled(false);
                logger.logEvent("Proceso detenido");
            }
        });

        // MENÚ
        menuBar = new JMenuBar();
        UIManager.put("Menu.cancelMode", "immediate");

        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem salirItem = new JMenuItem("Salir");
        salirItem.addActionListener(e -> {
            appConfig.saveConfig();
            System.exit(0);
        });
        menuArchivo.add(salirItem);

        // Menú Ver
        JMenu menuVer = new JMenu("Ver");
        JMenuItem registroItem = new JMenuItem("Ver registros");
        registroItem.addActionListener(ev -> showLogWindow());
        menuVer.add(registroItem);

        // Menú Configuración
        JMenu menuConfig = new JMenu("Configuración");
        oscuroItem = new JMenuItem("Tema oscuro");
        claroItem = new JMenuItem("Tema claro");

        oscuroItem.addActionListener(ev -> changeTheme(true));
        claroItem.addActionListener(ev -> changeTheme(false));

        menuConfig.add(oscuroItem);
        menuConfig.add(claroItem);

        // Submenú de estilos
        JMenu estiloMenu = new JMenu("Estilo");
        estiloWindows = new JMenuItem("Windows");
        estiloNimbus = new JMenuItem("Nimbus");
        estiloMetal = new JMenuItem("Metal");

        estiloWindows.addActionListener(ev -> changeLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"));
        estiloNimbus.addActionListener(ev -> changeLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"));
        estiloMetal.addActionListener(ev -> changeLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"));

        estiloMenu.add(estiloWindows);
        estiloMenu.add(estiloNimbus);
        estiloMenu.add(estiloMetal);
        menuConfig.add(estiloMenu);

        // Submenú de modo de movimiento CON DESCRIPCIONES
        JMenu modoMenu = new JMenu("Modo de movimiento");
        modoBajoItem = new JMenuItem("<html><b>Bajo</b> - Movimiento mínimo e imperceptible (2px)</html>");
        modoMedioItem = new JMenuItem("<html><b>Medio</b> - Movimiento equilibrado y suave (8px)</html>");
        modoAltoItem = new JMenuItem("<html><b>Alto</b> - Movimiento notorio y frecuente (18px)</html>");

        modoBajoItem.addActionListener(ev -> cambiarModoMovimiento(MovementType.LOW));
        modoMedioItem.addActionListener(ev -> cambiarModoMovimiento(MovementType.MEDIUM));
        modoAltoItem.addActionListener(ev -> cambiarModoMovimiento(MovementType.HIGH));

        modoMenu.add(modoBajoItem);
        modoMenu.add(modoMedioItem);
        modoMenu.add(modoAltoItem);
        menuConfig.add(modoMenu);

        menuBar.add(menuArchivo);
        menuBar.add(menuVer);
        menuBar.add(menuConfig);

        // Configuración inicial
        SwingUtilities.invokeLater(() -> {
            loadSavedConfiguration();
            setTheme(appConfig.isDarkTheme());
            updateFonts();

            MovementType savedMovementType = appConfig.getMovementType();
            moverService.setMovementType(savedMovementType);
            actualizarLabelModo();

            updateMenuMarkers();

            Container parent = getTopLevelAncestor();
            if (parent instanceof JFrame frame) {
                frame.setJMenuBar(menuBar);
                configurarCierreMenusConESC(frame);
            }
        });
    }

    private String getEstadoActual() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        if (pointerInfo == null) return "Sin puntero";

        Point current = pointerInfo.getLocation();
        Point lastUserPosition = moverService.getLastUserPosition();

        if (lastUserPosition != null && new MouseMoverLogic().shouldSkipCycle(lastUserPosition, current, 10)) {
            return "En movimiento";
        }
        return "Automático";
    }

    private void changeTheme(boolean dark) {
        isDark = dark;
        appConfig.setDarkTheme(dark);
        appConfig.saveConfig();
        setTheme(dark);
        updateMenuMarkers();
    }

    private void setTheme(boolean dark) {
        isDark = dark;
        Color bg = isDark ? darkBg : lightBg;
        Color compBg = isDark ? darkCompBg : lightCompBg;
        Color fg = isDark ? darkFg : lightFg;

        setBackground(bg);
        updateComponentColors(this, bg, compBg, fg);

        if (separadorVertical != null) {
            separadorVertical.setForeground(fg);
        }

        actualizarLabelModo();
        repaint();
        revalidate();
    }

    private void updateComponentColors(Container container, Color bg, Color compBg, Color fg) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel panel) {
                panel.setBackground(panel.isOpaque() ? compBg : bg);
                if (panel instanceof Container) {
                    updateComponentColors(panel, bg, compBg, fg);
                }
            } else if (comp instanceof JLabel label) {
                label.setForeground(fg);
                label.setBackground(compBg);
            } else if (comp instanceof JButton button) {
                button.setBackground(compBg);
                button.setForeground(fg);
            }

            if (comp instanceof Container container1) {
                updateComponentColors(container1, bg, compBg, fg);
            }
        }
    }

    private void changeLookAndFeel(String laf) {
        try {
            setLookAndFeel(laf);
            SwingUtilities.updateComponentTreeUI(SwingUtilities.getWindowAncestor(this));
            updateFonts();
            appConfig.setLookAndFeel(laf);
            appConfig.saveConfig();
            updateMenuMarkers();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // LookAndFeel no disponible
        }
    }

    private void loadSavedConfiguration() {
        try {
            String savedLookAndFeel = appConfig.getLookAndFeel();
            setLookAndFeel(savedLookAndFeel);
        } catch (Exception e) {
            try {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // Usar Metal por defecto
            }
        }
    }

    private void updateFonts() {
        Font labelFont = UIManager.getFont("Label.font");
        if (fechaLabel != null) fechaLabel.setFont(labelFont.deriveFont(Font.BOLD, 14f));
        if (horaLabel != null) horaLabel.setFont(labelFont.deriveFont(Font.BOLD, 14f));
        if (estadoLabel != null) estadoLabel.setFont(labelFont.deriveFont(Font.PLAIN, 14f));
        if (tiempoLabel != null) tiempoLabel.setFont(labelFont.deriveFont(Font.BOLD, 18f));
        if (modoLabel != null) modoLabel.setFont(labelFont.deriveFont(Font.BOLD, 14f));
    }

    private void configurarCierreMenusConESC(JFrame frame) {
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0);
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "CLOSE_MENUS");
        frame.getRootPane().getActionMap().put("CLOSE_MENUS", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                MenuSelectionManager.defaultManager().clearSelectedPath();
            }
        });
    }

    private void cambiarModoMovimiento(MovementType tipo) {
        moverService.setMovementType(tipo);
        appConfig.setMovementType(tipo);
        appConfig.saveConfig();
        actualizarLabelModo();
        updateMenuMarkers();
        logger.logEvent("Modo de movimiento cambiado a: " + tipo.getDisplayName());
    }

    private void actualizarLabelModo() {
        MovementType currentType = moverService.getMovementType();
        modoLabel.setText("Modo: " + currentType.getDisplayName());

        Color color = switch (currentType) {
            case LOW -> new Color(100, 200, 100);
            case MEDIUM -> isDark ? new Color(100, 149, 237) : Color.BLUE;
            case HIGH -> new Color(255, 165, 0);
        };
        modoLabel.setForeground(color);
    }

    private void updateMenuMarkers() {
        oscuroItem.setText(appConfig.isDarkTheme() ? "● Tema oscuro" : "Tema oscuro");
        claroItem.setText(appConfig.isDarkTheme() ? "Tema claro" : "● Tema claro");

        String currentLaF = appConfig.getLookAndFeel();
        estiloWindows.setText(currentLaF.contains("windows") ? "● Windows" : "Windows");
        estiloNimbus.setText(currentLaF.contains("nimbus") ? "● Nimbus" : "Nimbus");
        estiloMetal.setText(currentLaF.contains("metal") ? "● Metal" : "Metal");

        MovementType currentType = moverService.getMovementType();
        modoBajoItem.setText(currentType == MovementType.LOW ?
            "<html>● <b>Bajo</b> - Movimiento mínimo e imperceptible (2px)</html>" :
            "<html><b>Bajo</b> - Movimiento mínimo e imperceptible (2px)</html>");
        modoMedioItem.setText(currentType == MovementType.MEDIUM ?
            "<html>● <b>Medio</b> - Movimiento equilibrado y suave (8px)</html>" :
            "<html><b>Medio</b> - Movimiento equilibrado y suave (8px)</html>");
        modoAltoItem.setText(currentType == MovementType.HIGH ?
            "<html>● <b>Alto</b> - Movimiento notorio y frecuente (18px)</html>" :
            "<html><b>Alto</b> - Movimiento notorio y frecuente (18px)</html>");
    }

    private void showLogWindow() {
        JTextArea registroArea = new JTextArea(10, 40);
        registroArea.setEditable(false);

        Color bg = isDark ? darkBg : lightBg;
        Color fg = isDark ? darkFg : lightFg;
        Color compBg = isDark ? darkCompBg : lightCompBg;

        registroArea.setBackground(bg);
        registroArea.setForeground(fg);
        registroArea.setCaretColor(fg);

        for (String event : logger.getEvents()) {
            registroArea.append(event + "\n");
        }

        JScrollPane registroScroll = new JScrollPane(registroArea);
        registroScroll.getViewport().setBackground(bg);
        registroScroll.setBackground(compBg);
        registroScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JFrame registroFrame = new JFrame("Registro de eventos (Tiempo Real)");
        registroFrame.setSize(500, 250);
        registroFrame.getContentPane().setBackground(bg);
        registroFrame.add(registroScroll);
        registroFrame.setLocationRelativeTo(null);

        final int[] lastEventCount = {logger.getEvents().size()};
        Timer updateTimer = new Timer(1000, e -> {
            java.util.List<String> currentEvents = logger.getEvents();
            if (currentEvents.size() > lastEventCount[0]) {
                for (int i = lastEventCount[0]; i < currentEvents.size(); i++) {
                    registroArea.append(currentEvents.get(i) + "\n");
                }
                lastEventCount[0] = currentEvents.size();
                SwingUtilities.invokeLater(() -> registroArea.setCaretPosition(registroArea.getDocument().getLength()));
            }
        });

        registroFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                updateTimer.start();
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                updateTimer.stop();
            }
        });

        registroFrame.setVisible(true);
    }

    public void saveConfigOnExit() {
        appConfig.saveConfig();
    }
}

