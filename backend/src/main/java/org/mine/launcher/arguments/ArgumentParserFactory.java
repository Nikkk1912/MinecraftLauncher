package org.mine.launcher.arguments;

import com.fasterxml.jackson.databind.JsonNode;

public class ArgumentParserFactory {
    public static ArgumentParser getParser(JsonNode versionJson) {
        if (versionJson.has("minecraftArguments")) {
            return new LegacyArgumentParser();
        } else if (versionJson.has("arguments")) {
            return new ModernArgumentParser();
        }
        return new NoArgumentParser();
    }
}
