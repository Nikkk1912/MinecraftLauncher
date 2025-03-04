package org.mine.launcher.util.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mine.launcher.service.ConfigService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
public class AssetsClient {
    private final Path assetsIndexesFolderPath;
    private final ObjectMapper objectMapper;

    public AssetsClient(ConfigService configService, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.assetsIndexesFolderPath = configService.getLauncherFolderPath().resolve(".minecraft").resolve("assets").resolve("indexes");
    }

    public JsonNode getAssetsIndexJson(JsonNode versionJson) {
        String assetId = versionJson.get("assets").asText();
        String assetIndexUrl = versionJson.get("assetIndex").get("url").asText();

        Path assetIndexFile = assetsIndexesFolderPath.resolve(assetId + ".json");
        if (Files.exists(assetIndexFile)) {
            return readAssetIndex(assetIndexFile);
        } else {
            return downloadAssetIndex(assetIndexFile, assetIndexUrl);
        }
    }

    private JsonNode readAssetIndex(Path assetIndexFile) {
        try {
            return objectMapper.readTree(assetIndexFile.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read asset index file: " + assetIndexFile, e);
        }
    }

    private JsonNode downloadAssetIndex(Path assetIndexFile, String assetIndexUrl) {
        try {
            Files.createDirectories(assetsIndexesFolderPath);
            System.out.println("Downloading asset index: " + assetIndexUrl);

            HttpURLConnection connection = (HttpURLConnection) new URL(assetIndexUrl).openConnection();
            connection.setRequestMethod("GET");

            try (var inputStream = connection.getInputStream()) {
                Files.copy(inputStream, assetIndexFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return objectMapper.readTree(assetIndexFile.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to download asset index from: " + assetIndexUrl, e);
        }
    }
}
