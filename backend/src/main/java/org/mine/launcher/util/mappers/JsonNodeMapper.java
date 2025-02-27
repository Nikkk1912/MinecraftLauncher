package org.mine.launcher.util.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonNodeMapper {

    public static com.fasterxml.jackson.databind.JsonNode UnirestToJacksonJsonNode(com.mashape.unirest.http.JsonNode unirestJsonNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(unirestJsonNode.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while converting unirest json node to jackson json node");
            return null;
        }
    }
}
