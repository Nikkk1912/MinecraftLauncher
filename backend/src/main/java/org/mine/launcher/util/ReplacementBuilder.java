package org.mine.launcher.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.mine.launcher.domain.UserData;

import java.util.HashMap;
import java.util.Map;

public class ReplacementBuilder {
    public static Map<String, String> buildReplacementsMap(JsonNode versionJson, UserData userData, boolean offlineMode) {
        Map<String, String> replacements = new HashMap<>();

        replacements.put("game_directory", userData.getGameDirectory());
        replacements.put("assets_root", userData.getAssetsDirectory());
        replacements.put("assets_index_name", userData.getAssetsIndex());
//        replacements.put("version_name", userData.getVersionName());
//        replacements.put("launcher_name", userData.getLauncherName());
//        replacements.put("launcher_version", userData.getLauncherVersion());
        replacements.put("user_type", "mojang"); // Default value

        // Offline mode for development purposes
        if (offlineMode) {
            replacements.put("auth_player_name", userData.getPlayerName());
            replacements.put("auth_uuid", "00000000-0000-0000-0000-000000000000");
            replacements.put("auth_access_token", "");
            replacements.put("clientid", "0");
            replacements.put("auth_xuid", "0");
        } else {
            replacements.put("auth_player_name", userData.getPlayerName());
            replacements.put("auth_uuid", userData.getUuid());
            replacements.put("auth_access_token", userData.getAccessToken());
        }
        replacements.put("version_name", versionJson.get("id").asText());
        replacements.put("version_type", versionJson.get("type").asText());


        return replacements;
    }
}
