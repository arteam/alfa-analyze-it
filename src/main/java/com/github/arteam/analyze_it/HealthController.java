package com.github.arteam.analyze_it;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/admin/health")
    public StatusResponse health() {
        return ImmutableStatusResponse.builder()
                .status("UP")
                .build();
    }
}
