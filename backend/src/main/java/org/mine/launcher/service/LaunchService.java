package org.mine.launcher.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.mine.launcher.arguments.ArgumentParser;
import org.mine.launcher.arguments.ArgumentParserFactory;
import org.mine.launcher.domain.UserData;
import org.mine.launcher.util.ClassPathBuilder;
import org.mine.launcher.util.CommandRunner;
import org.mine.launcher.util.DebugUtil;
import org.mine.launcher.util.ReplacementBuilder;
import org.mine.launcher.util.api.AssetsClient;
import org.mine.launcher.util.api.VersionClient;
import org.mine.launcher.util.jsonParsers.VersionJsonParser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LaunchService {

    private final VersionClient versionClient;
    private final AssetsClient assetsClient;
    private final ConfigService configService;
    private final CommandRunner commandRunner;
    private final ClassPathBuilder classPathBuilder;

    public LaunchService(VersionClient versionClient, AssetsClient assetsClient, ConfigService configService, CommandRunner commandRunner, ClassPathBuilder classPathBuilder) {
        this.versionClient = versionClient;
        this.assetsClient = assetsClient;
        this.configService = configService;
        this.commandRunner = commandRunner;
        this.classPathBuilder = classPathBuilder;
    }

    public void launchVersion(String version, boolean isOffline) {
        JsonNode versionJson = versionClient.getVersionJson(version);

        UserData userData = new UserData(
                configService.getSetting("playerName"),
                "000000000000", //placeholder
                "000000000000", //placeholder
                configService.getGameDirectory(),
                configService.getAssetsDir(),
                VersionJsonParser.getAssetIndex(versionJson),
                VersionJsonParser.getVersion(versionJson),
                "ShedevroLauncher",
                "1"
        );

        String javaRunFile = configService.getJavaRunFile(VersionJsonParser.getJavaComponent(versionJson));

        List<String> command = new ArrayList<>();
        command.add(javaRunFile);
        command.add("-Xms" + configService.getSetting("minRam") + "M"); // Max RAM allocation
        command.add("-Xmx" + configService.getSetting("maxRam") + "M"); // Min RAM allocation
//        command.add("-Djava.library.path=" + configService.getNativesDirectory());
//        command.add("-Djava.library.path=\"\"");
//        command.add("-Dminecraft.launcher.brand=" + userData.getLauncherName());
//        command.add("-Dminecraft.launcher.version=" + userData.getLauncherVersion());
        command.add("-cp");
        command.add(classPathBuilder.buildClasspath(versionJson));
        command.add(VersionJsonParser.getMainClass(versionJson));
//
//        // Get parsed arguments
        ArgumentParser argumentParser = ArgumentParserFactory.getParser(versionJson);
        Map<String, String> replacements = ReplacementBuilder.buildReplacementsMap(versionJson,userData, isOffline);
        List<String> parsedArgs = argumentParser.parseArguments(versionJson, replacements);
        command.addAll(parsedArgs);
//
//        // Add the JAR file
//        command.add("--gameDir");
//        command.add(userData.getGameDirectory());
//        command.add("--assetsDir");
//        command.add(userData.getAssetsDirectory());
//        command.add("--assetIndex");
//        command.add(userData.getAssetsIndex());

        String commandString = String.join(" ", command);
        DebugUtil.logCommand(commandString);
        System.out.println("Launching Minecraft with command: " + commandString);
        commandRunner.runCommand(commandString);

    }
}
