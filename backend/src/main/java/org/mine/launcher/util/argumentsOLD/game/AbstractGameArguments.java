package org.mine.launcher.util.argumentsOLD.game;

import com.fasterxml.jackson.databind.JsonNode;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AbstractGameArguments implements GameArgumentsProvider {
    protected final JsonNode versionJson;
    protected final Path gameDir;

    public AbstractGameArguments(JsonNode versionJson, Path gameDir) {
        this.versionJson = versionJson;
        this.gameDir = gameDir;
    }

    protected String replacePlaceholders(String argument) {
        return argument
                .replace("${auth_player_name}", "Player")
                .replace("${version_name}", versionJson.get("id").asText())
                .replace("${game_directory}", gameDir.toAbsolutePath().toString())
                .replace("${assets_root}", gameDir.resolve("assets").toString())
                .replace("${assets_index_name}", versionJson.path("assetIndex").path("id").asText())
                .replace("${auth_uuid}", "00000000-0000-0000-0000-000000000000")
                .replace("${auth_access_token}", "null")
                .replace("${clientid}", "null")
                .replace("${auth_xuid}", "null")
                .replace("${user_type}", "mojang")
                .replace("${version_type}", versionJson.path("type").asText("release"));
    }

    protected List<String> processArguments(JsonNode argumentsNode) {
        List<String> args = new ArrayList<>();
        if (argumentsNode != null) {
            for (JsonNode argument : argumentsNode) {
                if (argument.isTextual()) {
                    args.add(replacePlaceholders(argument.asText()));
                } else if (argument.has("rules")) {
                    if (isAllowed(argument.get("rules"))) {
                        if (argument.get("value").isArray()) {
                            for (JsonNode value : argument.get("value")) {
                                args.add(replacePlaceholders(value.asText()));
                            }
                        } else {
                            args.add(replacePlaceholders(argument.get("value").asText()));
                        }
                    }
                }
            }
        }
        return args;
    }

    private boolean isAllowed(JsonNode rules) {
        for (JsonNode rule : rules) {
            if ("allow".equals(rule.path("action").asText())) {
                JsonNode features = rule.get("features");
                if (features != null) {
                    return features.fields().hasNext();
                }
            }
        }
        return true;
    }

    @Override
    public List<String> getGameArguments() {
        return processArguments(versionJson.path("arguments").path("game"));
    }
}
