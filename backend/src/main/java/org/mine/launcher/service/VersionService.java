package org.mine.launcher.service;

import org.mine.launcher.exceptions.NoInstalledVersionException;
import org.mine.launcher.util.api.VersionManifestClient;
import org.mine.launcher.util.jsonParsers.VersionManifestJsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class VersionService {

    private final ConfigService configService;
    private final VersionManifestClient versionManifestClient;
    private static final Logger logger = LoggerFactory.getLogger(VersionService.class);

    public VersionService(ConfigService configService, VersionManifestClient versionManifestClient) {
        this.configService = configService;
        this.versionManifestClient = versionManifestClient;
    }

    public List<String> getInstalledVersions() {
        String versionsPath = configService.getVersionsDirectory().toString();
        List<String> versions = new ArrayList<>();

        File versionsFolder = new File(versionsPath);

        if (versionsFolder.exists() && versionsFolder.isDirectory()) {
            String[] directories = versionsFolder.list((current, name) -> new File(current, name).isDirectory());

            if (directories != null) {
                return new ArrayList<>(Arrays.asList(directories));
            } else {
                logger.warn("No folders found in the 'versions' directory.");
                throw new NoInstalledVersionException();
            }
        } else {
            logger.error("The specified path does not exist or is not a directory.");
            return versions;
        }
    }

    public List<String> getAllVersions() {
        return VersionManifestJsonParser.parseVersionManifest(versionManifestClient.getVersionManifestJson());
    }
}
