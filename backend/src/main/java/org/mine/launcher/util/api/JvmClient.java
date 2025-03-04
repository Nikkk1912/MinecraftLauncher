package org.mine.launcher.util.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.mine.launcher.service.ConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class JvmClient {

    private final Path jvmPath;
    private final ObjectMapper objectMapper;
    private final String jvmManifestUrl;

    public JvmClient(ConfigService configService, ObjectMapper objectMapper, @Value("${mojang.api.javaManifest}") String jvmManifestUrl) {
        this.jvmPath = configService.getLauncherFolderPath();
        this.objectMapper = objectMapper;
        this.jvmManifestUrl = jvmManifestUrl;
    }

    public JsonNode getJvmJson() {
        File jvmManifestFile = jvmPath.resolve("all.json").toFile();

        if (isJvmManifestPresent(jvmManifestFile) && isJvmManifestFresh(jvmManifestFile)) {
            try {
                return objectMapper.readTree(jvmManifestFile);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return downloadJvmManifest(jvmManifestFile);
        }
    }

    private JsonNode downloadJvmManifest(File manifestFile) {
        try {
            File parentDir = manifestFile.getParentFile();
            if (!parentDir.exists() && !parentDir.mkdirs()) {
                System.err.println("Failed to create directories for manifest file: " + parentDir);
                return null;
            }

            HttpResponse<String> jsonResponse = Unirest.get(jvmManifestUrl).asString();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse.getBody());

            objectMapper.writeValue(manifestFile, jsonNode);
            return jsonNode;
        } catch (UnirestException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isJvmManifestPresent(File manifestFile) {
        return manifestFile.exists();
    }

    private boolean isJvmManifestFresh(File manifestFile) {
        try {
            BasicFileAttributes attributes = Files.readAttributes(manifestFile.toPath(), BasicFileAttributes.class);
            Instant creationTime = attributes.creationTime().toInstant();
            return creationTime.isAfter(Instant.now().minus(30, ChronoUnit.DAYS));
        } catch (IOException e) {
            return false;
        }
    }
}
