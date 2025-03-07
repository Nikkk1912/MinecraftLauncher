package org.mine.launcher.util;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class CommandRunner {
    public void runCommand(String command) {
        String[] commandArray = command.split(" ");
        ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
        processBuilder.inheritIO();

        try {
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
