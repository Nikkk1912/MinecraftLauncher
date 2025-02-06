package org.mine.launcher.util.readers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class AssetIndexReader {
    public static String getAssetIndexFromJson(String path) {
        String assetIndexId = "";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(path));

            JsonNode assetIndexNode = rootNode.path("assetIndex");
            JsonNode assetIndexIdNode = assetIndexNode.path("id");

            assetIndexId = assetIndexIdNode.asText();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return assetIndexId;
    }
}
