package org.mine.launcher.util.jsonParsers;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class jvmJsonParser {

    public static List<String> getPlatformsList(JsonNode jvmManifestJson) {
        List<String> platforms = new ArrayList<>();

        Iterator<Map.Entry<String, JsonNode>> fields = jvmManifestJson.fields();

        if (fields.hasNext()) fields.next();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            platforms.add(entry.getKey());
        }
        return platforms;
    }

    public static JsonNode getJvmsNodeByPlatform(String platform, JsonNode jvmManifestJson) {
        return jvmManifestJson.get(platform);
    }
}
