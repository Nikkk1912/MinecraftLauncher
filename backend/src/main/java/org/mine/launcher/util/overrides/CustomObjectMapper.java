package org.mine.launcher.util.overrides;

import com.mashape.unirest.http.ObjectMapper;

public class CustomObjectMapper implements ObjectMapper {
    private final com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper;

    public CustomObjectMapper() {
        this.jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
    }


    @Override
    public <T> T readValue(String s, Class<T> aClass) {
        try {
            return jacksonObjectMapper.readValue(s, aClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String writeValue(Object o) {
        try {
            return jacksonObjectMapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
