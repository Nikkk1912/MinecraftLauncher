package org.mine.launcher.util.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mine.launcher.util.FileDownloader;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JvmHandler {

    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static Path getJava(Path jvmFolderPath, JsonNode jvmManifestJson, String jvmType) {
        String platform = detectPlatform();
        Path jvmInstallPath = jvmFolderPath.resolve(jvmType);
        Path javaExecutable = jvmInstallPath.resolve("bin/java");

        if (Files.exists(javaExecutable)) {
            return javaExecutable;
        }

        JsonNode platformNode = jvmManifestJson.get(platform);
        if (platformNode == null || !platformNode.has(jvmType)) {
            System.err.println("No JVM type found for platform: " + platform + " and jvmType: " + jvmType);
            return null;
        }

        JsonNode jvmVersions = platformNode.get(jvmType);
        if (jvmVersions.isEmpty()) {
            System.err.println("No JVM versions found for: " + jvmType);
            return null;
        }

        JsonNode latestJvm = jvmVersions.get(jvmVersions.size() - 1);
        JsonNode manifest = latestJvm.get("manifest");
        if (manifest == null || !manifest.has("url")) {
            System.err.println("No manifest URL found for: " + jvmType);
            return null;
        }

        String manifestUrl = manifest.get("url").asText();
//        System.out.println("Downloading manifest: " + manifestUrl);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode manifestJson = objectMapper.readTree(new URL(manifestUrl));
            Files.createDirectories(jvmInstallPath);
            processManifestFiles(manifestJson.get("files"), jvmInstallPath);
        } catch (IOException e) {
            System.err.println("Failed to download or process manifest: " + manifestUrl);
            e.printStackTrace();
            return null;
        }

        System.out.println("jvm manifest downloading complete");
        return javaExecutable;
    }

    private static void processManifestFiles(JsonNode filesNode, Path basePath) {
        if (filesNode == null || !filesNode.isObject()) {
            System.err.println("Invalid files structure in manifest");
            return;
        }

        Iterator<Map.Entry<String, JsonNode>> fields = filesNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String relativePath = entry.getKey();
            JsonNode fileNode = entry.getValue();
            Path targetPath = basePath.resolve(relativePath);

            if (fileNode.has("type") && "directory".equals(fileNode.get("type").asText())) {
                try {
                    Files.createDirectories(targetPath);
//                    System.out.println("Created directory: " + targetPath);
                } catch (IOException e) {
                    System.err.println("Failed to create directory: " + targetPath);
                    e.printStackTrace();
                }
            } else if (fileNode.has("downloads")) {
                JsonNode rawDownload = fileNode.get("downloads").get("raw");
                if (rawDownload != null && rawDownload.has("url")) {
                    String fileUrl = rawDownload.get("url").asText();
                    CompletableFuture.runAsync(() -> FileDownloader.downloadFile(fileUrl, targetPath.toFile()), executor);
                }
            }
        }
        System.out.println("Jvm downloading complete");
    }

    private static String detectPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();

        if (osName.contains("windows")) {
            if (osArch.contains("arm")) {
                return "windows-arm64";
            } else {
                String bits = System.getProperty("sun.arch.data.model");
                if (bits.contains("64")) {
                    return "windows-x64";
                } else {
                    return "windows-86";
                }
            }
        } else if (osName.contains("mac")) {
            if (osArch.contains("arm")) {
                return "mac-os-arm64";
            } else {
                return "mac-os";
            }
        } else if (osName.contains("linux-i386")) {
            return "linux-i386";
        } else if (osName.contains("linux")) {
            return "linux";
        }
        return "linux";
    }
}
