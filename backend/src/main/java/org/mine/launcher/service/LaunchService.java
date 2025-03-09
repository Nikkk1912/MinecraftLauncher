package org.mine.launcher.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.mine.launcher.arguments.ArgumentParser;
import org.mine.launcher.arguments.ArgumentParserFactory;
import org.mine.launcher.domain.UserData;
import org.mine.launcher.util.ClassPathBuilder;
import org.mine.launcher.util.CommandRunner;
import org.mine.launcher.util.DebugUtil;
import org.mine.launcher.util.ReplacementBuilder;
import org.mine.launcher.util.api.VersionClient;
import org.mine.launcher.util.jsonParsers.VersionJsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LaunchService {

    private final VersionClient versionClient;
    private final ConfigService configService;
    private final CommandRunner commandRunner;
    private final ClassPathBuilder classPathBuilder;
    private static final Logger logger = LoggerFactory.getLogger(LaunchService.class);

    public LaunchService(VersionClient versionClient, ConfigService configService, CommandRunner commandRunner, ClassPathBuilder classPathBuilder) {
        this.versionClient = versionClient;
        this.configService = configService;
        this.commandRunner = commandRunner;
        this.classPathBuilder = classPathBuilder;
    }

    public void launchVersion(String version, boolean isOffline) {
        JsonNode versionJson = versionClient.getVersionJson(version);

        UserData userData = new UserData(
                configService.getSetting("playerName"),
                "0",
                "0",
                configService.getGameDirectory().toString(),
                configService.getAssetsDirectory().toString(),
                VersionJsonParser.getAssetIndex(versionJson),
                VersionJsonParser.getVersion(versionJson),
                "Shedevro Launcher",
                "1"
        );

        String javaRunFile = configService.getJavaExecutableFilePath(VersionJsonParser.getJavaComponent(versionJson)).toString();
        logger.debug("Java run file: {}", javaRunFile);

        List<String> command = new ArrayList<>();
        command.add(javaRunFile);
        command.add("-Xms" + configService.getSetting("minRam") + "M");
        command.add("-Xmx" + configService.getSetting("maxRam") + "M");
        command.add("-Djava.library.path=" + configService.getBinDirectory());

        command.add("-cp");
        command.add(classPathBuilder.buildClasspath(versionJson));
        command.add(VersionJsonParser.getMainClass(versionJson));

        ArgumentParser argumentParser = ArgumentParserFactory.getParser(versionJson);
        Map<String, String> replacements = ReplacementBuilder.buildReplacementsMap(versionJson,userData, isOffline);
        List<String> parsedArgs = argumentParser.parseArguments(versionJson, replacements);
        command.addAll(parsedArgs);

        String commandString = String.join(" ", command);
        DebugUtil.logCommand(commandString);
        logger.debug("Launching Minecraft with command: {}", commandString);
        commandRunner.runCommand(commandString);
    }
}
