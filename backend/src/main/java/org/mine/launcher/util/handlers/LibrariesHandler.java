package org.mine.launcher.util.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import org.mine.launcher.util.FileDownloader;
import org.mine.launcher.util.OsDetector;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipFile;

@Component
public class LibrariesHandler {
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void handleLibraries(Path librariesPath, boolean isReload, JsonNode versionJson) {
        File libFolder = librariesPath.toFile();
        if (!libFolder.exists()) {
            libFolder.mkdirs();
        }

        Path binFolder = librariesPath.getParent().resolve("bin");
        File binFolderFile = binFolder.toFile();
        if (!binFolderFile.exists()) {
            binFolderFile.mkdirs();
        }

        JsonNode libraries = versionJson.get("libraries");
        if (libraries == null) {
            System.err.println("No libraries found in the provided JSON.");
            return;
        }

        String osName = OsDetector.detectPlatform();

        for (JsonNode lib : libraries) {
            if (!isAllowed(lib, osName)) {
                continue;
            }

            JsonNode downloadsNode = lib.path("downloads");
            JsonNode artifact = downloadsNode.path("artifact");
            JsonNode classifiers = downloadsNode.path("classifiers");

            if (artifact.isMissingNode() && classifiers.isMissingNode()) {
                System.out.println("Skipping library (No artifact or classifiers): " + lib);
                continue;
            }

            if (!artifact.isMissingNode()) {
                String path = artifact.get("path").asText();
                String url = artifact.get("url").asText();
                long size = artifact.get("size").asLong();

                File targetFile = new File(libFolder, path);

                if (isReload || !targetFile.exists() || targetFile.length() != size) {
                    CompletableFuture.runAsync(() -> FileDownloader.downloadFile(url, targetFile), executor);
                }
            }

            if (!classifiers.isMissingNode()) {
                String nativeClassifier = getNativeClassifierForOS(osName);
                JsonNode nativeLibrary = classifiers.path(nativeClassifier);

                if (!nativeLibrary.isMissingNode()) {
                    String nativePath = nativeLibrary.get("path").asText();
                    String nativeUrl = nativeLibrary.get("url").asText();
                    long nativeSize = nativeLibrary.get("size").asLong();

                    File nativeTargetFile = new File(libFolder, nativePath);

                    if (isReload || !nativeTargetFile.exists() || nativeTargetFile.length() != nativeSize) {
                        CompletableFuture.runAsync(() -> {
                            downloadAndExtractNativeLibraries(nativeUrl, binFolder, nativePath);
                        }, executor);
                    }
                } else {
                    System.out.println("No native library found for " + osName);
                }
            }
        }
        System.out.println("Libraries downloading complete");
    }

    private static boolean isAllowed(JsonNode lib, String osName) {
        JsonNode rules = lib.get("rules");
        if (rules == null) {
            return true;
        }
        for (JsonNode rule : rules) {
            if ("allow".equals(rule.get("action").asText())) {
                JsonNode os = rule.get("os");
                if (os != null && os.has("name")) {
                    String requiredOs = os.get("name").asText();
                    if (!osName.contains(requiredOs)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static String getNativeClassifierForOS(String osName) {
        return switch (osName) {
            case "windows-x64" -> "natives-windows";
            case "mac-os", "mac-os-arm64" -> "natives-osx";
            case "linux-x64", "linux-i386" -> "natives-linux";
            default -> "natives-linux";
        };
    }

    private static void downloadAndExtractNativeLibraries(String url, Path binFolder, String nativeLibPath) {
        File nativeLibJar = new File(binFolder.toFile(), nativeLibPath);

        FileDownloader.downloadFile(url, nativeLibJar);

        try (ZipFile zipFile = new ZipFile(nativeLibJar)) {
            zipFile.stream()
                    .filter(entry -> !entry.isDirectory() && isNativeLibrary(entry.getName()))
                    .forEach(entry -> {
                        try {
                            File extractedFile = new File(binFolder.toFile(), entry.getName());
                            extractedFile.getParentFile().mkdirs();
                            try (FileOutputStream fos = new FileOutputStream(extractedFile);
                                 InputStream is = zipFile.getInputStream(entry)) {
                                is.transferTo(fos);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isNativeLibrary(String fileName) {
        return fileName.endsWith(".dll") || fileName.endsWith(".so") || fileName.endsWith(".dylib");
    }
}
