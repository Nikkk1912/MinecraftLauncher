package org.mine.launcher.util.arguments.game.specificArgumentsImpl;

import com.fasterxml.jackson.databind.JsonNode;
import org.mine.launcher.util.arguments.game.AbstractGameArguments;
import org.mine.launcher.util.arguments.game.GameArgumentsProvider;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class GameArguments_1_5_2 implements GameArgumentsProvider {
    private final JsonNode versionJson;
    private final Path gameDir;

    public GameArguments_1_5_2(JsonNode versionJson, Path gameDir) {
        this.versionJson = versionJson;
        this.gameDir = gameDir;
    }

    private String replacePlaceholders(String argument) {
        return argument
                .replace("${auth_player_name}", "Player")
                .replace("${auth_session}", "OfflineSession")
                .replace("${game_directory}", gameDir.toAbsolutePath().toString())
                .replace("${game_assets}", gameDir.resolve("assets").toString());
    }

    @Override
    public List<String> getGameArguments() {
        String rawArgs = versionJson.path("minecraftArguments").asText("");
        return Arrays.asList(replacePlaceholders(rawArgs).split(" "));
    }
}

