package org.mine.launcher.controllers;

import org.mine.launcher.service.ConfigService;
import org.mine.launcher.service.ExecuteService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/execute")
public class ExecuteController {

    private final ExecuteService executeService;
    private final ConfigService configService;

    public ExecuteController(ExecuteService executeService, ConfigService configService) {
        this.executeService = executeService;
        this.configService= configService;
    }

    @PostMapping
    public String runGame(@RequestBody Map<String, String> request) {
        String versionNum = request.get("versionNum");
        String playerName = request.get("playerName");
        configService.saveSetting("playerName", playerName);
//        executeService.executeCommand(versionNum, playerName);
        return "Game is running";
    }
}
