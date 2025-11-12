package org.example.config;

import org.example.model.MovementType;

import java.io.*;
import java.util.Properties;

/**
 * Gestiona la configuración persistente de la aplicación.
 * Utiliza un archivo de propiedades para guardar preferencias del usuario.
 */
public class AppConfig {
    private static final String CONFIG_FILE = "app_config.properties";
    private static final String THEME_KEY = "theme.dark";
    private static final String STYLE_KEY = "ui.lookAndFeel";
    private static final String MOVEMENT_TYPE_KEY = "movement.type";

    private final Properties properties;

    public AppConfig() {
        properties = new Properties();
        loadConfig();
    }

    private void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Error al cargar configuración: " + e.getMessage());
                setDefaultValues();
            }
        } else {
            setDefaultValues();
        }
    }

    private void setDefaultValues() {
        properties.setProperty(THEME_KEY, "true");
        properties.setProperty(STYLE_KEY, "javax.swing.plaf.metal.MetalLookAndFeel");
        properties.setProperty(MOVEMENT_TYPE_KEY, "MEDIUM");
    }

    public void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Configuración de la aplicación");
        } catch (IOException e) {
            System.err.println("Error al guardar configuración: " + e.getMessage());
        }
    }

    public boolean isDarkTheme() {
        return Boolean.parseBoolean(properties.getProperty(THEME_KEY, "true"));
    }

    public void setDarkTheme(boolean dark) {
        properties.setProperty(THEME_KEY, String.valueOf(dark));
    }

    public String getLookAndFeel() {
        return properties.getProperty(STYLE_KEY, "javax.swing.plaf.metal.MetalLookAndFeel");
    }

    public void setLookAndFeel(String lookAndFeel) {
        properties.setProperty(STYLE_KEY, lookAndFeel);
    }

    public MovementType getMovementType() {
        String typeStr = properties.getProperty(MOVEMENT_TYPE_KEY, "MEDIUM");
        return MovementType.fromString(typeStr);
    }

    public void setMovementType(MovementType type) {
        properties.setProperty(MOVEMENT_TYPE_KEY, type.name());
    }
}

