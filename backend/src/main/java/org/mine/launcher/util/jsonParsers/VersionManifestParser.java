package org.mine.launcher.util.jsonParsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class VersionManifestParser {

    public static List<String> parseVersionManifest(com.mashape.unirest.http.JsonNode manifest) {
        List<java.lang.String> versionsList = new ArrayList<>();

        JsonNode root = convertToJacksonJsonNode(manifest);
        assert root != null;
        JsonNode versions = root.path("versions");

        String regex = ".*(?:pre|rc|potato|infinite|Pre-Release).*";
        Pattern pattern = Pattern.compile(regex);

        for (JsonNode versionNode : versions) {
            String versionId = versionNode.path("id").asText();
            if (!pattern.matcher(versionId).matches() && versionNode.path("type").asText().equals("release")) {
                versionsList.add(versionId);
            }
        }

        return versionsList;
    }

    private static com.fasterxml.jackson.databind.JsonNode convertToJacksonJsonNode(com.mashape.unirest.http.JsonNode unirestJsonNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(unirestJsonNode.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
