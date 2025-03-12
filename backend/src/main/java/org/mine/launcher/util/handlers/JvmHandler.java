package org.mine.launcher.util.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mine.launcher.util.FileDownloader;
import org.mine.launcher.util.OsDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JvmHandler {

    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private static final Logger logger = LoggerFactory.getLogger(JvmHandler.class);

    public static Path getJava(Path jvmFolderPath, JsonNode jvmManifestJson, String jvmType) {
        String platform = OsDetector.detectPlatform();
        Path jvmInstallPath = jvmFolderPath.resolve(jvmType);
        Path javaExecutable = jvmInstallPath.resolve("bin/java");

        if (Files.exists(javaExecutable)) {
            return javaExecutable;
        }

        JsonNode platformNode = jvmManifestJson.get(platform);
        if (platformNode == null || !platformNode.has(jvmType)) {
            logger.error("No JVM type found for platform: {} and jvmType: {}", platform, jvmType);
            return null;
        }

        JsonNode jvmVersions = platformNode.get(jvmType);
        if (jvmVersions.isEmpty()) {
            logger.error("No JVM versions found for: {}", jvmType);
            return null;
        }

        JsonNode latestJvm = jvmVersions.get(jvmVersions.size() - 1);
        JsonNode manifest = latestJvm.get("manifest");
        if (manifest == null || !manifest.has("url")) {
            logger.error("No manifest URL found for: {}", jvmType);
            return null;
        }

        String manifestUrl = manifest.get("url").asText();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode manifestJson = objectMapper.readTree(new URL(manifestUrl));
            Files.createDirectories(jvmInstallPath);
            processManifestFiles(manifestJson.get("files"), jvmInstallPath);
        } catch (IOException e) {
            logger.error("Failed to download or process manifest: {}", manifestUrl);
            e.printStackTrace();
            return null;
        }

        logger.info("Jvm manifest downloading complete");
        return javaExecutable;
    }

    private static void processManifestFiles(JsonNode filesNode, Path basePath) {
        if (filesNode == null || !filesNode.isObject()) {
            logger.error("Invalid files structure in manifest");
            return;
        }

        Iterator<Map.Entry<String, JsonNode>> fields = filesNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String relativePath = entry.getKey();
            JsonNode fileNode = entry.getValue();
            Path targetPath = basePath.resolve(relativePath);

            if (fileNode.has("type") && "directory".equals(fileNode.get("type").asText())) {
                try {
                    Files.createDirectories(targetPath);
                } catch (IOException e) {
                    System.err.println("Failed to create directory: " + targetPath);
                    e.printStackTrace();
                }
            } else if (fileNode.has("downloads")) {
                JsonNode rawDownload = fileNode.get("downloads").get("raw");
                if (rawDownload != null && rawDownload.has("url")) {
                    String fileUrl = rawDownload.get("url").asText();
                    CompletableFuture.runAsync(() -> FileDownloader.downloadFile(fileUrl, targetPath.toFile()), executor);
                }
            }
        }
        logger.info("Jvm downloading complete");
    }
}
