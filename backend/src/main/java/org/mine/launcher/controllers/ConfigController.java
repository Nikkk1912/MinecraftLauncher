package org.mine.launcher.controllers;

import org.mine.launcher.service.ConfigService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping("/{key}")
    public String getSetting(@PathVariable String key) {
        return configService.getSetting(key);
    }

    @PostMapping
    public String saveSetting(@RequestParam String key, @RequestParam String value) {
        configService.saveSetting(key, value);
        return "Setting saved: " + key + " = " + value;
    }
}
