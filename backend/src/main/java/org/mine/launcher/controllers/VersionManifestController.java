package org.mine.launcher.controllers;

import org.mine.launcher.service.VersionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/version")
public class VersionManifestController {

    private final VersionService versionService;

    public VersionManifestController(VersionService versionService) {
        this.versionService = versionService;
    }

    @GetMapping("/installed")
    public List<String> getAllInstalled() {
        return versionService.getInstalledVersions();
    }

    @GetMapping("/all")
    public List<String> getAllVersions() {
        return versionService.getAllVersions();
    }


}
