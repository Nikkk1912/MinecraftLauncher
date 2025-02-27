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
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class VersionManifestClient {

    private final String versionManifestUrl;
    private final ConfigService configService;
    private final ObjectMapper objectMapper;

    public VersionManifestClient(@Value("${mojang.api.versionManifest}") String versionManifestUrl,
                                 ConfigService configService, ObjectMapper objectMapper) {
        this.versionManifestUrl = versionManifestUrl;
        this.configService = configService;
        this.objectMapper = objectMapper;
    }

    public JsonNode getVersionManifestJson() {
        File versionManifestFile = configService.getLauncherFolderPath()
                .resolve("version_manifest.json")
                .toFile();

        if (isVersionManifestPresent(versionManifestFile) && isVersionManifestFresh(versionManifestFile)) {
            try {
                return objectMapper.readTree(versionManifestFile);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return downloadVersionManifest(versionManifestFile);
        }
    }

    private JsonNode downloadVersionManifest(File manifestFile) {
        try {
            HttpResponse<String> jsonResponse = Unirest.get(versionManifestUrl).asString();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse.getBody());
            objectMapper.writeValue(manifestFile, jsonNode);
            return jsonNode;
        } catch (UnirestException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isVersionManifestFresh(File manifestFile) {
        try {
            BasicFileAttributes attributes = Files.readAttributes(manifestFile.toPath(), BasicFileAttributes.class);
            Instant creationTime = attributes.creationTime().toInstant();
            return creationTime.isAfter(Instant.now().minus(30, ChronoUnit.DAYS));
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isVersionManifestPresent(File manifestFile) {
        return manifestFile.exists();
    }
}
