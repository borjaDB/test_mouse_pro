package org.example;

import javax.swing.*;
import java.awt.*;

import static javax.swing.UIManager.setLookAndFeel;

public class TimerPanel extends JPanel {
    private final JButton startButton = new JButton("Inicio");
    private final JButton pauseButton = new JButton("Pausa");
    private final JButton stopButton = new JButton("Parar");
    private final JLabel timerLabel = new JLabel("Tiempo: 00:00:00");
    private final JLabel statusLabel = new JLabel("Apagado");
    private final MouseMoverService moverService;
    private EventLogger logger = null;
    private volatile long startTime = 0;
    private volatile long elapsedTime = 0;
    private volatile boolean running = false;
    private volatile boolean paused = false;
    private Thread workerThread = null;
    private Color darkBg = new Color(40, 40, 40);
    private Color darkCompBg = new Color(60, 60, 60);
    private Color darkFg = Color.LIGHT_GRAY;
    private Color lightBg = Color.WHITE;
    private Color lightCompBg = new Color(240, 240, 240);
    private Color lightFg = Color.DARK_GRAY;
    private boolean isDark = true;

    public TimerPanel(MouseMoverService moverService, EventLogger logger) {
        this.moverService = moverService;
        this.logger = logger;
        setLayout(new BorderLayout());
        setBackground(isDark ? darkBg : lightBg);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Panel superior con fecha/hora
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        JLabel fechaLabel = new JLabel(getFechaActual());
        fechaLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        fechaLabel.setForeground(isDark ? darkFg : lightFg);
        fechaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(fechaLabel);

        // Subpanel para modo y tiempo con borde tipo fieldbox
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(true);
        infoPanel.setBackground(isDark ? darkCompBg : lightCompBg);
        infoPanel.setBorder(BorderFactory.createLineBorder(isDark ? Color.LIGHT_GRAY : Color.DARK_GRAY, 2, true));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel estadoLabel = new JLabel("Apagado");
        estadoLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        estadoLabel.setForeground(isDark ? darkFg : lightFg);
        estadoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel tiempoLabel = new JLabel("Tiempo: 00:00:00");
        tiempoLabel.setFont(new Font("Monospaced", Font.BOLD, 22));
        tiempoLabel.setForeground(isDark ? darkFg : lightFg);
        tiempoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(estadoLabel);
        infoPanel.add(tiempoLabel);
        infoPanel.add(Box.createVerticalStrut(6));
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(infoPanel);
        add(topPanel, BorderLayout.NORTH);

        // Panel central vacío (puedes añadir gráficos o mensajes si lo deseas)
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        add(centerPanel, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
        bottomPanel.setOpaque(false);
        bottomPanel.add(startButton);
        bottomPanel.add(pauseButton);
        bottomPanel.add(stopButton);
        add(bottomPanel, BorderLayout.SOUTH);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);

        // Actualización visual y lógica
        Timer swingTimer = new Timer(500, e -> {
            fechaLabel.setText(getFechaActual());
            tiempoLabel.setText("Tiempo: " + formatTime(paused ? elapsedTime : (running ? System.currentTimeMillis() - startTime : elapsedTime)));
            if (running) {
                estadoLabel.setText(paused ? "Pausado" : getEstadoActual());
                estadoLabel.setForeground(paused ? Color.BLUE : (getEstadoActual().equals("En movimiento") ? Color.ORANGE : Color.GREEN));
            } else {
                estadoLabel.setText("Apagado");
                estadoLabel.setForeground(Color.GRAY);
            }
        });
        swingTimer.start();

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

        setTheme(true);

        // Agregar barra de menú estándar arriba
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem salirItem = new JMenuItem("Salir");
        salirItem.addActionListener(e -> System.exit(0));
        menuArchivo.add(salirItem);
        JMenu menuVer = new JMenu("Ver");
        JMenuItem registroItem = new JMenuItem("Ver registros");
        registroItem.addActionListener(ev -> {
            JTextArea registroArea = new JTextArea(10, 40);
            registroArea.setEditable(false);
            Color bg = isDark ? darkBg : lightBg;
            Color fg = isDark ? darkFg : lightFg;
            registroArea.setBackground(bg);
            registroArea.setForeground(fg);
            for (String event : logger.getEvents()) {
                registroArea.append(event + "\n");
            }
            JScrollPane registroScroll = new JScrollPane(registroArea);
            registroScroll.getViewport().setBackground(bg);
            JFrame registroFrame = new JFrame("Registro de eventos");
            registroFrame.setSize(500, 250);
            registroFrame.add(registroScroll);
            registroFrame.getContentPane().setBackground(bg);
            registroFrame.setLocationRelativeTo(null);
            registroFrame.setVisible(true);
        });
        menuVer.add(registroItem);
        JMenu menuConfig = new JMenu("Configuración");
        JMenuItem oscuroItem = new JMenuItem("Tema oscuro");
        JMenuItem claroItem = new JMenuItem("Tema claro");
        oscuroItem.addActionListener(ev -> setTheme(true));
        claroItem.addActionListener(ev -> setTheme(false));
        menuConfig.add(oscuroItem);
        menuConfig.add(claroItem);
        // Submenú de estilos visuales
        JMenu estiloMenu = new JMenu("Estilo");
        JMenuItem estiloWindows = new JMenuItem("Windows");
        JMenuItem estiloNimbus = new JMenuItem("Nimbus");
        JMenuItem estiloMetal = new JMenuItem("Metal");
        estiloWindows.addActionListener(ev -> {
            try {
                setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                     UnsupportedLookAndFeelException e) {
                throw new RuntimeException(e);
            }
        });
        estiloNimbus.addActionListener(ev -> {
            try {
                setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                     UnsupportedLookAndFeelException e) {
                throw new RuntimeException(e);
            }
        });
        estiloMetal.addActionListener(ev -> {
            try {
                setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                     UnsupportedLookAndFeelException e) {
                throw new RuntimeException(e);
            }
        });
        estiloMenu.add(estiloWindows);
        estiloMenu.add(estiloNimbus);
        estiloMenu.add(estiloMetal);
        menuConfig.add(estiloMenu);
        menuBar.add(menuArchivo);
        menuBar.add(menuVer);
        menuBar.add(menuConfig);
        // Insertar la barra de menú en el JFrame principal
        SwingUtilities.invokeLater(() -> {
            Container parent = getTopLevelAncestor();
            if (parent instanceof JFrame frame) {
                frame.setJMenuBar(menuBar);
            }
        });

        // Aplicar el estilo Windows por defecto al iniciar
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            // Si no está disponible, ignora el error y usa el predeterminado
        }
    }

    // Renombrado para evitar conflicto con JPanel
    private void refreshPanel() {
        if (running) {
            long now = System.currentTimeMillis();
            long elapsed = paused ? elapsedTime : (now - startTime);
            timerLabel.setText("Tiempo: " + formatTime(elapsed));
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            if (pointerInfo == null) {
                statusLabel.setText("Sin puntero");
                statusLabel.setForeground(Color.RED);
                return;
            }
            Point current = pointerInfo.getLocation();
            Point lastUserPosition = moverService.getLastUserPosition();
            if (lastUserPosition != null && new MouseMoverLogic().shouldSkipCycle(lastUserPosition, current, 10)) {
                statusLabel.setText("En movimiento");
                statusLabel.setForeground(Color.ORANGE);
            } else {
                statusLabel.setText(paused ? "Pausado" : "Automático");
                statusLabel.setForeground(paused ? Color.BLUE : Color.GREEN);
            }
        } else {
            timerLabel.setText("Tiempo: " + formatTime(elapsedTime));
            statusLabel.setText("Apagado");
            statusLabel.setForeground(Color.GRAY);
        }
    }

    private String formatTime(long ms) {
        long totalSeconds = ms / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void setTheme(boolean dark) {
        isDark = dark;
        Color bg = dark ? darkBg : lightBg;
        Color compBg = dark ? darkCompBg : lightCompBg;
        Color fg = dark ? darkFg : lightFg;
        setBackground(bg);
        timerLabel.setBackground(compBg);
        timerLabel.setForeground(fg);
        statusLabel.setBackground(compBg);
        statusLabel.setForeground(fg);
        startButton.setBackground(compBg);
        startButton.setForeground(fg);
        pauseButton.setBackground(compBg);
        pauseButton.setForeground(fg);
        stopButton.setBackground(compBg);
        stopButton.setForeground(fg);
    }

    private String getFechaActual() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEEE d 'de' MMMM HH:mm");
        return sdf.format(new java.util.Date());
    }
    private String getEstadoActual() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        if (pointerInfo == null) {
            return "Sin puntero";
        }
        Point current = pointerInfo.getLocation();
        Point lastUserPosition = moverService.getLastUserPosition();
        if (lastUserPosition != null && new MouseMoverLogic().shouldSkipCycle(lastUserPosition, current, 10)) {
            return "En movimiento";
        }
        return "Automático";
    }

    // Clase auxiliar para paneles redondeados
    class RoundedPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
        }
    }
}
