package org.mine.launcher.util.jsonParsers;

import com.fasterxml.jackson.databind.JsonNode;

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
        try {
            String result = versionJson.get("javaVersion").get("component").asText();
            return result;
        } catch (NullPointerException e) {
            System.err.println("Issue - no java info in version manifest");
            return "java-runtime-delta";
        }
    }

    public static String getJavaVersion(JsonNode versionJson) {
        return versionJson.get("javaVersion").get("majorVersion").asText();
    }

    public static String getMainClass(JsonNode versionJson) {
        return versionJson.get("mainClass").asText();
    }
}
