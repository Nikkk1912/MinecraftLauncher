package org.mine.launcher.util.MojangEndpointsParsers;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

public class VersionManifestParser {
    public static final String OTHERS_REGEX = ".*(?:pre|rc|potato|infinite|Pre-Release|old alpha).*";
    private static final String VERSION_MANIFEST_V2_URL = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
    private static final int DAYS_THRESHOLD = 14; // 2 weeks
    private static final String MANIFEST_FILE_NAME = "version_manifest_v2.json";
    private static Path configFolder;
    private static Path jsonPath;

    static {
        findConfigFolderPath();
        jsonPath = configFolder.resolve(MANIFEST_FILE_NAME);
    }

    public static Map<String, String> getVersionsAndUrlsMap(boolean includeSnapshots, boolean includeOthers) {
        checkVersionManifest();

        Map<String, String> versionsMap = new LinkedHashMap<>();

        Pattern pattern = Pattern.compile(OTHERS_REGEX);

        JsonNode manifest = readJson(jsonPath);
        JsonNode versionsNode = manifest.path("versions");

        for (JsonNode versionNode : versionsNode) {
            String versionId = versionNode.path("id").asText();
            String versionUrl = versionNode.path("url").asText();

            if (includeOthers && pattern.matcher(versionId).matches()) {
                versionsMap.put(versionId, versionUrl);
            }
            if (includeSnapshots && versionNode.path("type").asText().equals("snapshot")) {
                versionsMap.put(versionId, versionUrl);
            }
            if (versionNode.path("type").asText().equals("release")) {
                versionsMap.put(versionId, versionUrl);
            }
        }
        return versionsMap;
    }

    public static List<String> getVersionList(boolean includeSnapshots, boolean includeOthers) {
        checkVersionManifest();

        List<String> versionsList = new ArrayList<>();

        String regex = ".*(?:pre|rc|potato|infinite|Pre-Release|old alpha).*";
        Pattern pattern = Pattern.compile(regex);

        JsonNode manifest = readJson(jsonPath);
        JsonNode versionsNode = manifest.path("versions");

        for (JsonNode versionNode : versionsNode) {
            String versionId = versionNode.path("id").asText();

            if (includeOthers && pattern.matcher(versionId).matches()) {
                versionsList.add(versionId);
            }
            if (includeSnapshots && versionNode.path("type").asText().equals("snapshot")) {
                versionsList.add(versionId);
            }
            if (versionNode.path("type").asText().equals("release")) {
                versionsList.add(versionId);
            }
        }
        return versionsList;
    }

    private static void checkVersionManifest() {
        try {
            if (checkIfVersionManifestPresent()) {
                if (!checkIfVersionManifestFresh()) {
                    System.out.println("Version manifest is good and present");
                } else {
                    System.out.println("Version manifest is old. Getting new...");
                    downloadJson(VERSION_MANIFEST_V2_URL, MANIFEST_FILE_NAME);
                }
            } else {
                System.out.println("No version manifest( Will get new one...");
                downloadJson(VERSION_MANIFEST_V2_URL, MANIFEST_FILE_NAME);
            }
        } catch (IOException e) {
            System.out.println("Got issues with downloading...");
            e.printStackTrace();
        }
    }

    private static void findConfigFolderPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            configFolder = Paths.get(System.getenv("APPDATA"), "MyLauncher");
        } else {
            configFolder = Paths.get(System.getProperty("user.home"), ".config", "MyLauncher");
        }
    }

    private static boolean checkIfVersionManifestPresent() {
        Path jsonPath = configFolder.resolve("version_manifest_v2.json");
        return Files.exists(jsonPath) && !Files.isDirectory(jsonPath);
    }

    private static boolean checkIfVersionManifestFresh() {
        Path jsonPath = configFolder.resolve("version_manifest_v2.json");
        return !isFileOlderThanTwoWeeks(jsonPath);
    }

    private static boolean isFileOlderThanTwoWeeks(Path filePath) {
        try {
            if (Files.exists(filePath)) {
                FileTime lastModifiedTime = Files.getLastModifiedTime(filePath);
                Instant fileInstant = lastModifiedTime.toInstant();
                Instant twoWeeksAgo = Instant.now().minus(DAYS_THRESHOLD, ChronoUnit.DAYS);

                return !fileInstant.isBefore(twoWeeksAgo); // if older than 2 weeks - redownload
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // If file doesn't exist - redownload
    }

    private static String downloadFileContent(String urlString) throws IOException {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }
        return result.toString();
    }

    private static void saveJson(Object data, Path filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

            Files.createDirectories(filePath.getParent());
            writer.writeValue(filePath.toFile(), data);

            System.out.println("Saving JSON to: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save JSON file.");
        }
    }

    private static void downloadJson(String url, String fileName) throws IOException {
        String jsonContent = downloadFileContent(url);
        ObjectMapper mapper = new ObjectMapper();
        Object jsonObject = mapper.readTree(jsonContent);

        Path filePath = configFolder.resolve(fileName);
        saveJson(jsonObject, filePath);
    }

    public static JsonNode readJson(Path filePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(filePath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to read JSON from file: " + filePath);
            return null;
        }
    }
}