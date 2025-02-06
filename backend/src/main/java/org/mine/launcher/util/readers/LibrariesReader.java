package org.mine.launcher.util.readers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class LibrariesReader {

    public static ArrayList<String> readJson(String path) {

        ArrayList<String> libraryPaths = new ArrayList<>();
        try {
            ObjectMapper  objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(path));

            libraryPaths = getLibraryPaths(rootNode);
//            System.out.println("Library Paths:");
//            libraryPaths.forEach(System.out::println);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return libraryPaths;
    }

    private static ArrayList<String> getLibraryPaths(JsonNode rootNode) {
        ArrayList<String> paths = new ArrayList<>();

        JsonNode librariesNode = rootNode.path("libraries");
        if (librariesNode.isArray()) {
            for (JsonNode libraryNode : librariesNode) {

                if (isAllowedOnWindows(libraryNode)) {

                    JsonNode downloadsNode = libraryNode.path("downloads").path("artifact");
                    if (downloadsNode.isObject()) {
                        JsonNode pathNode = downloadsNode.path("path");
                        if (pathNode.isTextual()) {
                            paths.add("C:Users/moska/AppData/Roaming/.minecraft/libraries/" + pathNode.asText());
                        }
                    }
                }
            }
        }
        return paths;
    }

    private static boolean isAllowedOnWindows(JsonNode libraryNode) {
        JsonNode rulesNode = libraryNode.path("rules");
        if (rulesNode.isArray()) {
            for (JsonNode rule : rulesNode) {
                String action = rule.path("action").asText();
                JsonNode osNode = rule.path("os");
                String osName = osNode.path("name").asText();

                if ("allow".equals(action) && ("windows".equals(osName) || osName.isEmpty())) {
                    return true;
                }

                if ("disallow".equals(action) && "windows".equals(osName)) {
                    return false;
                }
            }
        }
        return true;
    }
}

