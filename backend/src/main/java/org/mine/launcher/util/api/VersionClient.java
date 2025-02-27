package org.mine.launcher.util.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.mine.launcher.service.ConfigService;
import org.mine.launcher.util.jsonParsers.VersionManifestJsonParser;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class VersionClient {

    private final Path versionsFolderPath;
    private final ObjectMapper objectMapper;
    private final VersionManifestClient versionManifestClient;

    public VersionClient(ConfigService configService, ObjectMapper objectMapper, VersionManifestClient versionManifestClient) {
        this.objectMapper = objectMapper;
        this.versionManifestClient = versionManifestClient;
        this.versionsFolderPath = configService.getLauncherFolderPath().resolve(".minecraft").resolve("versions");
    }

    public JsonNode getVersionJson(String version) {
        if (isVersionJsonPresent(version)) {
            return readVersionJson(version);
        } else {
            return downloadVersionJson(version);
        }
    }

    private JsonNode downloadVersionJson(String version) {
        String apiLink = VersionManifestJsonParser.getVersionLinkByVersionNumber(version, versionManifestClient.getVersionManifestJson());
        File versionJson = versionsFolderPath.resolve(version).resolve(version + ".json").toFile();

        try {
            HttpResponse<String> jsonResponse = Unirest.get(apiLink).asString();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse.getBody());

            if (!versionJson.getParentFile().exists()) {
                versionJson.getParentFile().mkdirs();
            }

            objectMapper.writeValue(versionJson, jsonNode);
            return jsonNode;
        } catch (UnirestException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JsonNode readVersionJson(String version) {
        File versionJson = versionsFolderPath.resolve(version).resolve(version + ".json").toFile();
        try {
            return objectMapper.readTree(versionJson);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isVersionFolderExists(String version) {
        Path versionFolderPath = versionsFolderPath.resolve(version);
        return Files.exists(versionFolderPath) && Files.isDirectory(versionFolderPath);
    }

    private boolean isVersionJsonPresent(String version) {
        Path versionJsonPath = versionsFolderPath.resolve(version).resolve(version + ".json");
        return Files.exists(versionJsonPath) && Files.isRegularFile(versionJsonPath);
    }
}
