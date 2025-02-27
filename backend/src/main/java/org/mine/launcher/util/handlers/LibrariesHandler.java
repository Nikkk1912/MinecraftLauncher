package org.mine.launcher.util.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import org.mine.launcher.util.FileDownloader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class LibrariesHandler {
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void handleLibraries(Path librariesPath, boolean isReload, JsonNode versionJson) {
        File libFolder = librariesPath.toFile();
        if (!libFolder.exists()) {
            libFolder.mkdirs();
        }

        JsonNode libraries = versionJson.get("libraries");
        if (libraries == null) {
            System.err.println("No libraries found in the provided JSON.");
            return;
        }

        String osName = System.getProperty("os.name").toLowerCase();

        for (JsonNode lib : libraries) {
            if (!isAllowed(lib, osName)) {
                continue;
            }
            JsonNode artifact = lib.path("downloads").path("artifact");
            if (artifact.isMissingNode()) {
                continue;
            }

            String path = artifact.get("path").asText();
            String url = artifact.get("url").asText();
            long size = artifact.get("size").asLong();

            File targetFile = new File(libFolder, path);

            if (isReload || !targetFile.exists() || targetFile.length() != size) {
                CompletableFuture.runAsync(() -> FileDownloader.downloadFile(url, targetFile), executor);
            }
        }
    }

    private static boolean isAllowed(JsonNode lib, String osName) {
        JsonNode rules = lib.get("rules");
        if (rules == null) {
            return true;
        }
        for (JsonNode rule : rules) {
            if ("allow".equals(rule.get("action").asText())) {
                JsonNode os = rule.get("os");
                if (os != null && os.has("name")) {
                    String requiredOs = os.get("name").asText();
                    if (!osName.contains(requiredOs)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
