package org.mine.launcher.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.mine.launcher.service.ConfigService;
import org.mine.launcher.util.jsonParsers.VersionJsonParser;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassPathBuilder {

    private final ConfigService configService;

    public ClassPathBuilder(ConfigService configService) {
        this.configService = configService;
    }

    public String buildClasspath(JsonNode versionJson) {
        String librariesDir = configService.getLibrariesDir();
        List<String> libraryPaths = VersionJsonParser.getLibraries(versionJson);

        String classpath = libraryPaths.stream()
                .map(path -> new File(librariesDir, path).getAbsolutePath())
                .collect(Collectors.joining(File.pathSeparator));

        String versionJar = configService.getVersionJarFilePath(VersionJsonParser.getVersion(versionJson));

        return "\"" + classpath  + File.pathSeparator + versionJar + "\"" ;
    }
}
