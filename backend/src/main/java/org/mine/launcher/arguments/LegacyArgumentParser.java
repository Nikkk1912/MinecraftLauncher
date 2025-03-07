package org.mine.launcher.arguments;

import com.fasterxml.jackson.databind.JsonNode;
import org.mine.launcher.util.ArgumentUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LegacyArgumentParser implements ArgumentParser{
    @Override
    public List<String> parseArguments(JsonNode versionJson, Map<String, String> replacements) {
        List<String> args = new ArrayList<>();
        String rawArgs = versionJson.get("minecraftArguments").asText("");
        for (String arg : rawArgs.split(" ")) {
            args.add(ArgumentUtils.replacePlaceholders(arg, replacements));
        }
        return args;
    }
}
