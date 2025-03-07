package org.mine.launcher.util.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import org.mine.launcher.util.FileDownloader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class AssetsHandler {
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private static final String ASSETS_BASE_URL = "https://resources.download.minecraft.net/";
    private static final ConcurrentHashMap<String, Boolean> downloadingAssets = new ConcurrentHashMap<>();

    public static void handleAssets(Path assetsPath, boolean isReload, JsonNode assetsIndexJson) {
        JsonNode objects = assetsIndexJson.get("objects");
        if (objects == null) {
            System.err.println("No objects found in asset index.");
            return;
        }

        for (Map.Entry<String, JsonNode> entry : objects.properties()) {
            JsonNode asset = entry.getValue();
            String hash = asset.get("hash").asText();
            long size = asset.get("size").asLong();

            String folderName = hash.substring(0, 2);
            File assetFile = assetsPath.resolve("objects").resolve(folderName).resolve(hash).toFile();

            if (!isReload && assetFile.exists() && assetFile.length() == size) {
                continue;
            }

            if (downloadingAssets.putIfAbsent(hash, true) == null) {
                String assetUrl = ASSETS_BASE_URL + folderName + "/" + hash;

                CompletableFuture.runAsync(() -> {
                    try {
                        FileDownloader.downloadFile(assetUrl, assetFile);
                    } catch (Exception e) {
                        if (!(e instanceof java.nio.file.FileAlreadyExistsException)) {
                            e.printStackTrace();
                        }
                    } finally {
                        downloadingAssets.remove(hash);
                    }
                }, executor);
            }
        }
        System.out.println("Assets downloading complete");
    }
}
