package org.mine.launcher.controllers;

import org.mine.launcher.service.InstallationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/install")
public class InstallController {

    private final InstallationService installationService;

    public InstallController(InstallationService installationService) {
        this.installationService = installationService;
    }

    @PostMapping
    public ResponseEntity<String> installVersion(
            @RequestParam String version,
            @RequestParam(name = "reinstall", required = false, defaultValue = "false") boolean reinstall
        ) {
        String response = installationService.installVersion(version, reinstall);
        return ResponseEntity.ok(response);
    }
}
