package org.mine.launcher.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DebugUtil {
    public static void logCommand(String commandString) {
        File logFile = new File("D:\\launch_command.txt");
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(System.lineSeparator());
            writer.write(commandString);
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Failed to write launch command to file: " + e.getMessage());
        }
    }
}
