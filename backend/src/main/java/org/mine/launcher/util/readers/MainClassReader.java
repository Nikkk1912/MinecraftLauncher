package org.mine.launcher.util.readers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class MainClassReader {

    public static String getMainClassFromJson(String path) {
        String mainClassPath = "";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(path));

            JsonNode mainClassNode = rootNode.path("mainClass");
            mainClassPath = mainClassNode.asText();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return mainClassPath;
    }
}
