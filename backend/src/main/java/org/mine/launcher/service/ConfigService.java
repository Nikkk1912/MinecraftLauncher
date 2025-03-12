package org.mine.launcher.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

@Service
public class ConfigService {
    private final String configFilePath;
    private final String launcherFolderPath;
    private final Properties properties = new Properties();
    private final Logger logger = LoggerFactory.getLogger(ConfigService.class);

    public ConfigService() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            launcherFolderPath = System.getenv("APPDATA") + "\\MyLauncher";
            configFilePath = launcherFolderPath + "\\config.properties";
        } else {
            launcherFolderPath = System.getProperty("user.home") + "/.config/MyLauncher";
            configFilePath = launcherFolderPath + "/config.properties";
        }

        File file = new File(configFilePath);
        file.getParentFile().mkdirs();

        if (file.exists()) {
            loadProperties(file);
        } else {
            createDefaultProperties(file);
        }
    }

    private void loadProperties(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);
        } catch (IOException e) {
            logger.error("Error loading settings file: {}", e.getMessage());
        }
    }

    private void createDefaultProperties(File file) {

        properties.setProperty("maxRam", "4096");
        properties.setProperty("minRam", "2048");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            properties.store(fos, "Default Launcher Configuration");
            logger.info("Created default settings file at: {}", configFilePath);
        } catch (IOException e) {
            logger.error("Error creating settings file: {}", e.getMessage());
        }
    }

    public String getSetting(String key) {
        return properties.getProperty(key);
    }

    public void saveSetting(String key, String value) {
        properties.setProperty(key, value);
        try (FileOutputStream fos = new FileOutputStream(configFilePath)) {
            properties.store(fos, "Launcher Configuration");
        } catch (IOException e) {
            logger.error("Error saving settings: {}", e.getMessage());
        }
    }

    public Path getLauncherFolderPath() {
        return Path.of(launcherFolderPath);
    }

    public Path getGameDirectory() {
        return Path.of(launcherFolderPath).resolve(".minecraft");
    }

    public Path getJvmDirectory() {
        return Path.of(launcherFolderPath).resolve("jvm");
    }

    public Path getAssetsDirectory() {
        return Path.of(launcherFolderPath).resolve(".minecraft").resolve("assets");
    }

    public Path getLibrariesDirectory() {
        return Path.of(launcherFolderPath).resolve(".minecraft").resolve("libraries");
    }

    public Path getVersionsDirectory() {
        return Path.of(launcherFolderPath).resolve(".minecraft").resolve("versions");
    }

    public File getJavaExecutableFilePath(String javaType) {
        return Path.of(launcherFolderPath).resolve("jvm").resolve(javaType).resolve("bin").resolve("java.exe").toFile();
    }

    public File getVersionJarFilePath(String version) {
        return Path.of(launcherFolderPath).resolve(".minecraft").resolve("versions").resolve(version).resolve(version + ".jar").toFile();
    }

    public Path getBinDirectory() {
        return Path.of(launcherFolderPath).resolve(".minecraft").resolve("bin");
    }
}