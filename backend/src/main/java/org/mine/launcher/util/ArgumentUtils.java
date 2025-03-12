package org.mine.launcher.util;

import java.util.Map;

public class ArgumentUtils {
    public static String replacePlaceholders(String argument, Map<String, String> replacements) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            argument = argument.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return argument;
    }
}
