package org.mine.launcher.util;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LibrariesHandler
{
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void handleLibraries(String librariesPath, boolean isReload, JsonNode versionJson) {
        File libFolder = new File(librariesPath);
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
                CompletableFuture.runAsync(() -> downloadLibrary(url, targetFile), executor);
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

    private static void downloadLibrary(String url, File targetFile) {
        try {
            System.out.println("Downloading: " + url);
            targetFile.getParentFile().mkdirs();
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            try (var inputStream = connection.getInputStream()) {
                Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            System.out.println("Downloaded: " + targetFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to download: " + url);
            e.printStackTrace();
        }
    }
}
