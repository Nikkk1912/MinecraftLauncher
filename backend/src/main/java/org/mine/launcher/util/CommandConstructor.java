package org.mine.launcher.util;

import org.mine.launcher.service.ConfigService;
import org.mine.launcher.util.readers.AssetIndexReader;
import org.mine.launcher.util.readers.LibrariesReader;
import org.mine.launcher.util.readers.MainClassReader;
import org.mine.launcher.util.readers.VersionReader;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CommandConstructor {

    private final ConfigService configService;

    public CommandConstructor(ConfigService configService) {
        this.configService = configService;
    }


    public static final String COMMAND_TEMPLATE = "\"%s\" -Xmx4G -Xms2G -cp \"%s\" %s -Djava.library.path=\"\" --username %s --version %s --gameDir \"%s\" --assetsDir \"%s\" --assetIndex %s --accessToken offline";
    // JavaPath maxRam minRam -cp classPath mainClass playerName version gameDir assetsPath assetsIndex



    public String assembleLaunchCommand(String versionNum, String playerName) {
        String gameDir = configService.getSetting("minecraft_home");

        String jsonPath = gameDir + "\\versions\\" + versionNum + "\\" + versionNum + ".json";

        String assetsPath = gameDir + "\\assets";

        String mainClassPath = MainClassReader.getMainClassFromJson(jsonPath);
        String version = VersionReader.getVersionFromJson(jsonPath);
        String assetIndex = AssetIndexReader.getAssetIndexFromJson(jsonPath);

        ArrayList<String> classPath = LibrariesReader.readJson(jsonPath);
        classPath.add(gameDir + "/versions/" + versionNum + "/" + versionNum + ".jar");
        String classPathString = String.join(";", classPath);

        String javaPath = configService.getSetting("minecraft_java");

        String command = String.format(
                COMMAND_TEMPLATE,
                javaPath, classPathString, mainClassPath, playerName, version, gameDir, assetsPath, assetIndex
        );

        return command;
    }
}
