package org.mine.launcher.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class FileDownloader {

    private static final Logger logger = LoggerFactory.getLogger(FileDownloader.class);

    public static void downloadFile(String url, File targetFile) {
        if (targetFile.exists()) {
            return;
        }

        try {
            targetFile.getParentFile().mkdirs();

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            try (var inputStream = connection.getInputStream()) {
                Files.copy(inputStream, targetFile.toPath());
            }

        } catch (IOException e) {
            e.printStackTrace();

            if (targetFile.exists()) {
                boolean deleted = targetFile.delete();
                if (!deleted) {
                    logger.error("Failed to delete existing file: {}", targetFile.getAbsolutePath());
                }
            }
            downloadFile(url, targetFile);
        }
    }
}
