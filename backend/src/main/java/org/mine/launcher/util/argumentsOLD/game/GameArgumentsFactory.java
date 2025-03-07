package org.mine.launcher.util.argumentsOLD.game;

import com.fasterxml.jackson.databind.JsonNode;
import org.mine.launcher.util.argumentsOLD.game.specificArgumentsImpl.GameArguments_1_21_4;
import org.mine.launcher.util.argumentsOLD.game.specificArgumentsImpl.GameArguments_1_5_2;

import java.nio.file.Path;

public class GameArgumentsFactory {
    public static GameArgumentsProvider getGameArguments(JsonNode versionJson, Path gameDir) {
        if (versionJson.has("minecraftArguments")) {
            return new GameArguments_1_5_2(versionJson, gameDir);
        } else {
            return new GameArguments_1_21_4(versionJson, gameDir);
        }
    }
}
