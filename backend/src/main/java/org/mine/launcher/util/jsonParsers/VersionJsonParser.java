package org.mine.launcher.util.jsonParsers;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class VersionJsonParser {

    public static String getAssetIndex(JsonNode versionJson) {
        return versionJson.get("assetIndex").get("id").asText();
    }

    public static String getAssetJsonLink(JsonNode versionJson) {
        return versionJson.get("assetIndex").get("url").asText();
    }

    public static String getJarDownloadLink(JsonNode versionJson) {
        return versionJson.get("downloads").get("client").get("url").asText();
    }

    public static String getJavaComponent(JsonNode versionJson) {
        return versionJson.get("javaVersion").get("component").asText();
    }

    public static String getJavaVersion(JsonNode versionJson) {
        return versionJson.get("javaVersion").get("majorVersion").asText();
    }

    public static String getMainClass(JsonNode versionJson) {
        return versionJson.get("mainClass").asText();
    }
}
