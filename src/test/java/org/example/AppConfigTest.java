package org.example;

import org.example.config.AppConfig;
import org.example.model.MovementType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    @Test
    void testDefaultValues() {
        AppConfig config = new AppConfig();
        // El tema puede ser true o false dependiendo de la configuración guardada
        assertNotNull(config.getLookAndFeel());
        assertNotNull(config.getMovementType());
    }

    @Test
    void testSetAndGetDarkTheme() {
        AppConfig config = new AppConfig();
        config.setDarkTheme(false);
        assertFalse(config.isDarkTheme());

        config.setDarkTheme(true);
        assertTrue(config.isDarkTheme());
    }

    @Test
    void testSetAndGetMovementType() {
        AppConfig config = new AppConfig();

        config.setMovementType(MovementType.LOW);
        assertEquals(MovementType.LOW, config.getMovementType());

        config.setMovementType(MovementType.HIGH);
        assertEquals(MovementType.HIGH, config.getMovementType());
    }

    @Test
    void testSetAndGetLookAndFeel() {
        AppConfig config = new AppConfig();
        String testLaF = "javax.swing.plaf.nimbus.NimbusLookAndFeel";

        config.setLookAndFeel(testLaF);
        assertEquals(testLaF, config.getLookAndFeel());
    }
}

