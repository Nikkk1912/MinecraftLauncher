package org.mine.launcher.arguments;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

public interface ArgumentParser {
    List<String> parseArguments(JsonNode versionJson, Map<String, String> replacements);
}
