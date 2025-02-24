package org.mine.launcher.util.jsonParsers;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class VersionManifestParser {

    public static List<String> parseVersionManifest(JsonNode manifest) {
        List<String> versionsList = new ArrayList<>();

        if (manifest == null || !manifest.has("versions")) {
            return versionsList;
        }

        JsonNode versions = manifest.get("versions");
        String regex = ".*(?:pre|rc|potato|infinite|Pre-Release).*";
        Pattern pattern = Pattern.compile(regex);

        for (JsonNode versionNode : versions) {
            String versionId = versionNode.path("id").asText();
            if (!pattern.matcher(versionId).matches() && "release".equals(versionNode.path("type").asText())) {
                versionsList.add(versionId);
            }
        }

        return versionsList;
    }
}
