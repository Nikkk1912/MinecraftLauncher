package org.mine.launcher.service;

import org.mine.launcher.util.CommandConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class ExecuteService {

    private final CommandConstructor commandConstructor;

    public ExecuteService(CommandConstructor commandConstructor) {
        this.commandConstructor = commandConstructor;
    }

    public void executeCommand(String versionNum, String playerName) {
        try {
            // Launch the command in CMD
            Process process = Runtime.getRuntime().exec(commandConstructor.assembleLaunchCommand(versionNum, playerName));

            // Capture and display any output from the command
            new Thread(() -> captureOutput(process.getInputStream())).start();
            new Thread(() -> captureOutput(process.getErrorStream())).start();

            // Wait for the command to finish
            int exitCode = process.waitFor();
            System.out.println("Minecraft launched with exit code: " + exitCode);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to capture and display output from the command
    private void captureOutput(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
