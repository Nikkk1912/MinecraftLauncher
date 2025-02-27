package org.mine.launcher.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.mine.launcher.util.api.AssetsClient;
import org.mine.launcher.util.handlers.AssetsHandler;
import org.mine.launcher.util.handlers.LibrariesHandler;
import org.mine.launcher.util.handlers.VersionJarHandler;
import org.mine.launcher.util.api.VersionClient;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class InstallationService {

    private final VersionClient versionClient;
    private final ConfigService configService;
    private final AssetsClient assetsClient;

    public InstallationService(VersionClient versionClient, ConfigService configService, AssetsClient assetsClient) {
        this.versionClient = versionClient;
        this.configService = configService;
        this.assetsClient = assetsClient;
    }

    public String installVersion (String version, boolean reinstallFlag) {
        JsonNode versionJson = versionClient.getVersionJson(version);
        JsonNode assetsIndexJson = assetsClient.getAssetsIndexJson(versionJson);
        installAssets(assetsIndexJson, reinstallFlag);
        installLibraries(versionJson, reinstallFlag);
        installJar(versionJson);
        return version + " installed.";
    }

    private void installLibraries(JsonNode versionJson, boolean reinstallFlag) {
        Path librariesPath = configService.getLauncherFolderPath().resolve(".minecraft").resolve("libraries");
        LibrariesHandler.handleLibraries(librariesPath, reinstallFlag, versionJson);
    }

    private void installJar(JsonNode versionJson) {
        Path versionsPath = configService.getLauncherFolderPath().resolve(".minecraft").resolve("versions");
        VersionJarHandler.handleVersionJar(versionsPath, versionJson);
    }

    private void installAssets(JsonNode assetsIndexJson, boolean reinstallFlag) {
        Path assetsPath = configService.getLauncherFolderPath().resolve(".minecraft").resolve("assets");
        AssetsHandler.handleAssets(assetsPath, reinstallFlag, assetsIndexJson);
    }
}
