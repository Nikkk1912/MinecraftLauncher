package org.mine.launcher.util.readers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class VersionReader {
    public static String getVersionFromJson (String path) {
        String version = "";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(path));

            JsonNode idNode = rootNode.path("id");
            version = idNode.asText();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return version;
    }
}