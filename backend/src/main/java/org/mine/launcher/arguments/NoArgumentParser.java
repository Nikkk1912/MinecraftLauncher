package org.mine.launcher.arguments;

import com.fasterxml.jackson.databind.JsonNode;
import org.mine.launcher.util.ArgumentUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NoArgumentParser implements ArgumentParser{
    @Override
    public List<String> parseArguments(JsonNode versionJson, Map<String, String> replacements) {
        List<String> arguments = new ArrayList<>();

        arguments.add("--username");
        arguments.add(ArgumentUtils.replacePlaceholders("${auth_player_name}", replacements));
        arguments.add("--version");
        arguments.add(ArgumentUtils.replacePlaceholders("${version_name}", replacements));
        arguments.add("--gameDir");
        arguments.add(ArgumentUtils.replacePlaceholders("${game_directory}", replacements));
        arguments.add("--assetsDir");
        arguments.add(ArgumentUtils.replacePlaceholders("${assets_root}", replacements));
        arguments.add("--assetIndex");
        arguments.add(ArgumentUtils.replacePlaceholders("${assets_index_name}", replacements));
        arguments.add("--uuid");
        arguments.add(ArgumentUtils.replacePlaceholders("${auth_uuid}", replacements));
        arguments.add("--accessToken");
        arguments.add(ArgumentUtils.replacePlaceholders("${auth_access_token}", replacements));
        arguments.add("--userType");
        arguments.add(ArgumentUtils.replacePlaceholders("${user_type}", replacements));
        arguments.add("--versionType");
        arguments.add(ArgumentUtils.replacePlaceholders("${version_type}", replacements));

        return arguments;
    }
}
