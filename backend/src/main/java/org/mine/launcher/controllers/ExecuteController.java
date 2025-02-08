package org.mine.launcher.controllers;

import org.mine.launcher.service.ExecuteService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/execute")
public class ExecuteController {

    private final ExecuteService executeService;

    public ExecuteController(ExecuteService executeService) {
        this.executeService = executeService;
    }

    @PostMapping
    public String runGame(@RequestBody Map<String, String> request) {
        String versionNum = request.get("versionNum");
        String playerName = request.get("playerName");
        executeService.executeCommand(versionNum, playerName);
        return "Game is running";
    }
}
