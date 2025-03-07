package org.mine.launcher.util.argumentsOLD.game.specificArgumentsImpl;

import com.fasterxml.jackson.databind.JsonNode;
import org.mine.launcher.util.argumentsOLD.game.AbstractGameArguments;

import java.nio.file.Path;
import java.util.List;

public class GameArguments_1_21_4 extends AbstractGameArguments {
    public GameArguments_1_21_4(JsonNode versionJson, Path gameDir) {
        super(versionJson, gameDir);
    }

    @Override
    public List<String> getGameArguments() {
        return super.getGameArguments();
    }
}

