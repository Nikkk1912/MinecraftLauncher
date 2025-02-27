package org.mine.launcher.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.mine.launcher.util.api.AssetsClient;
import org.mine.launcher.util.api.JvmClient;
import org.mine.launcher.util.handlers.AssetsHandler;
import org.mine.launcher.util.handlers.JvmHandler;
import org.mine.launcher.util.handlers.LibrariesHandler;
import org.mine.launcher.util.handlers.VersionJarHandler;
import org.mine.launcher.util.api.VersionClient;
import org.mine.launcher.util.jsonParsers.VersionJsonParser;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class InstallationService {

    private final VersionClient versionClient;
    private final ConfigService configService;
    private final AssetsClient assetsClient;
    private final JvmClient jvmClient;
    private final ExecutorService executor;

    public InstallationService(VersionClient versionClient, ConfigService configService, AssetsClient assetsClient, JvmClient jvmClient) {
        this.versionClient = versionClient;
        this.configService = configService;
        this.assetsClient = assetsClient;
        this.jvmClient = jvmClient;
        this.executor = Executors.newFixedThreadPool(10);
    }

    public String installVersion(String version, boolean reinstallFlag) {
        JsonNode versionJson = versionClient.getVersionJson(version);
        JsonNode assetsIndexJson = assetsClient.getAssetsIndexJson(versionJson);
        JsonNode jvmManifestJson = jvmClient.getJvmJson();

        List<Future<?>> futures = new ArrayList<>();

        futures.add(executor.submit(() -> installJava(jvmManifestJson, VersionJsonParser.getJavaComponent(versionJson))));
        futures.add(executor.submit(() -> installAssets(assetsIndexJson, reinstallFlag)));
        futures.add(executor.submit(() -> installLibraries(versionJson, reinstallFlag)));
        futures.add(executor.submit(() -> installJar(versionJson)));

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

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

    private void installJava(JsonNode jvmJson, String javaType) {
        Path jvmFolderPath = configService.getLauncherFolderPath().resolve("jvm");

        Path result = JvmHandler.getJava(jvmFolderPath, jvmJson, javaType);
        System.out.println("New Java in here: " + result.toString());
    }
}
