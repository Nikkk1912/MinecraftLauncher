package org.mine.launcher.controllers;

import org.mine.launcher.service.ConfigService;
import org.mine.launcher.service.LaunchService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/launch")
public class LaunchController {

    private final LaunchService launchService;
    private final ConfigService configService;

    public LaunchController(LaunchService launchService, ConfigService configService) {
        this.launchService = launchService;
        this.configService= configService;
    }

    @PostMapping
    public String runGame(@RequestParam String version,
                          @RequestParam String playerName,
                          @RequestParam(name = "offlineMode", required = false, defaultValue = "true") boolean isOffline) {
        configService.saveSetting("playerName", playerName);
        configService.saveSetting("lastLaunchedVersion", version);
        launchService.launchVersion(version, isOffline);
        return "Game is running";
    }
}
