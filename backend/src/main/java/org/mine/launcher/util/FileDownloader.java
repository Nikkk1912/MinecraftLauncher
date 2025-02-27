package org.mine.launcher.util;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileDownloader {

    public static void downloadFile(String url, File targetFile) {
        if (targetFile.exists()) {
//            System.out.println("File already exists, skipping download: " + targetFile.getAbsolutePath());
            return;
        }

        try {
            targetFile.getParentFile().mkdirs();

            // Open connection to the URL
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            try (var inputStream = connection.getInputStream()) {
                Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

//            System.out.println("Downloaded: " + targetFile.getAbsolutePath());

        } catch (IOException e) {
//            System.err.println("Failed to download: " + url);
            e.printStackTrace();

            if (targetFile.exists()) {
                boolean deleted = targetFile.delete();
                if (!deleted) {
                    System.err.println("Failed to delete existing file: " + targetFile.getAbsolutePath());
                }
            }
//            System.out.println("Retrying download: " + url);
            downloadFile(url, targetFile);
        }
    }

}
