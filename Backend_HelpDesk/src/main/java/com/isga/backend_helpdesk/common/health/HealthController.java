package com.isga.backend_helpdesk.common.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Contrôleur commun utilisé pour vérifier que l'API HTTP est démarrée. */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    /** Répond à GET /api/health avec l'état courant de l'API. */
    @GetMapping
    public HealthResponse getHealth() {
        return new HealthResponse("UP", "HelpDesk");
    }
}
