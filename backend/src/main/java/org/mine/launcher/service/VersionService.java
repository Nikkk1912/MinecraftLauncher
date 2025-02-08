package org.mine.launcher.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.mine.launcher.util.jsonParsers.VersionManifestParser;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class VersionService {

    private static final String VERSION_MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";

    private final ConfigService configService;

    public VersionService(ConfigService configService) {
        this.configService = configService;
    }

    public List<String> getInstalledVersions() {
        String versionsPath = configService.getSetting("minecraft_home") + "\\versions";

        List<String> versions = new ArrayList<>();

        File versionsFolder = new File(versionsPath);

        if (versionsFolder.exists() && versionsFolder.isDirectory()) {
            String[] directories = versionsFolder.list((current, name) -> new File(current, name).isDirectory());

            if (directories != null) {
                return List.of(directories);
            }
            // TODO create error handling
            else {
                System.out.println("No folders found in the 'versions' directory.");
            }
        } else {
            System.out.println("The specified path does not exist or is not a directory.");
        }
        return versions;
    }

    public List<String> getAllVersions() {
        return VersionManifestParser.parseVersionManifest(fetchAvailableMinecraftVersions());
    }

    private JsonNode fetchAvailableMinecraftVersions(){
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(VERSION_MANIFEST_URL)
                    .asJson();
            return jsonResponse.getBody();
        } catch (UnirestException uni) {
            uni.printStackTrace();
        }
        return null;
    }
}
