package org.mine.launcher.arguments;

import com.fasterxml.jackson.databind.JsonNode;
import org.mine.launcher.util.ArgumentUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModernArgumentParser implements ArgumentParser{
    @Override
    public List<String> parseArguments(JsonNode versionJson, Map<String, String> replacements) {
        List<String> args = new ArrayList<>();
        JsonNode gameArgs = versionJson.path("arguments").path("game");

        for (JsonNode arg : gameArgs) {
            if (arg.isTextual()) {
                args.add(ArgumentUtils.replacePlaceholders(arg.asText(), replacements));
            }
//            else if (arg.has("rules")) {
//                if (isOsRuleSatisfied(arg.get("rules"), replacements)) {
//                    addRuleValues(arg.get("value"), args, replacements);
//                }
//            }
        }
        return args;
    }

//    private boolean isOsRuleSatisfied(JsonNode rules, Map<String, String> replacements) {
//        // Implement rule checking logic (e.g., check OS, features, etc.)
//        return true;
//    }

    private void addRuleValues(JsonNode value, List<String> args, Map<String, String> replacements) {
        if (value.isArray()) {
            for (JsonNode v : value) {
                args.add(ArgumentUtils.replacePlaceholders(v.asText(), replacements));
            }
        } else {
            args.add(ArgumentUtils.replacePlaceholders(value.asText(), replacements));
        }
    }

}
