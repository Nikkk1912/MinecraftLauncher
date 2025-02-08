package org.mine.launcher.controllers;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.mine.launcher.service.VersionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/version")
public class VersionController {

    private final VersionService versionService;

    public VersionController(VersionService versionService) {
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
