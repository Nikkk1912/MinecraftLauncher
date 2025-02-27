package org.mine.launcher.util.jsonParsers;

import com.fasterxml.jackson.databind.JsonNode;
import org.mine.launcher.exceptions.VersionNotFoundInManifestException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class VersionManifestJsonParser {

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

    public static String getVersionLinkByVersionNumber(String version, JsonNode manifest) {
        String link = null;
        JsonNode versions = manifest.get("versions");

        for (JsonNode versionNode : versions) {
            if (versionNode.path("id").asText().equals(version)) {
                link = versionNode.path("url").asText();
            }
        }

        if (link != null)
        {
            return link;
        } else {
            throw new VersionNotFoundInManifestException(version);
        }
    }
}
