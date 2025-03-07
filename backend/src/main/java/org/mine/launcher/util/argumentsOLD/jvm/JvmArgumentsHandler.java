package org.mine.launcher.util.argumentsOLD.jvm;

import com.fasterxml.jackson.databind.JsonNode;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JvmArgumentsHandler {
    public static List<String> getJvmArguments(JsonNode versionJson, Path nativesDir, String launcherName, String launcherVersion, String classpath) {
        if (!versionJson.has("arguments")) {
            return new ArrayList<>();
        }

        List<String> args = new ArrayList<>();
        JsonNode jvmArgs = versionJson.path("arguments").path("jvm");

        for (JsonNode arg : jvmArgs) {
            if (arg.isTextual()) {
                args.add(arg.asText());
            } else if (arg.has("rules")) {
                if (isAllowed(arg.get("rules"))) {
                    if (arg.get("value").isArray()) {
                        for (JsonNode value : arg.get("value")) {
                            args.add(value.asText());
                        }
                    } else {
                        args.add(arg.get("value").asText());
                    }
                }
            }
        }

        args.add("-Djava.library.path=" + nativesDir.toAbsolutePath());
        args.add("-Dminecraft.launcher.brand=" + launcherName);
        args.add("-Dminecraft.launcher.version=" + launcherVersion);
        args.add("-cp");
        args.add(classpath);

        return args;
    }

    private static boolean isAllowed(JsonNode rules) {
        for (JsonNode rule : rules) {
            if ("allow".equals(rule.path("action").asText())) {
                JsonNode os = rule.get("os");
                if (os != null) {
                    String requiredOs = os.get("name").asText();
                    if (!System.getProperty("os.name").toLowerCase().contains(requiredOs)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
