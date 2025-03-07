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
public class VersionJarHandler {
    private static final ExecutorService executor = Executors.newFixedThreadPool(1);

    public static void handleVersionJar(Path versionsPath, JsonNode versionJson) {
        String link = versionJson.get("downloads").path("client").get("url").asText();
        String version = versionJson.get("id").asText();

        File targetFile = versionsPath.resolve(version).resolve(version + ".jar").toFile();

        CompletableFuture.runAsync(() -> FileDownloader.downloadFile(link, targetFile), executor);

        System.out.println("Version downloading complete");
    }
}
