package org.mine.launcher.controllers;

import org.mine.launcher.service.ExecuteService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/execute")
public class ExecuteController {

    private final ExecuteService executeService;

    public ExecuteController(ExecuteService executeService) {
        this.executeService = executeService;
    }

    @PostMapping
    public String runGame(@RequestParam String versionNum, @RequestParam String playerName) {
        executeService.executeCommand(versionNum, playerName);
        return "Game is running";
    }
}
