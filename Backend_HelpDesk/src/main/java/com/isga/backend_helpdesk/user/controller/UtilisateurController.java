package com.isga.backend_helpdesk.user.controller;

import com.isga.backend_helpdesk.shared.enums.RoleUtilisateur;
import com.isga.backend_helpdesk.user.dto.*;
import com.isga.backend_helpdesk.user.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

/** Étape API MVC : reçoit les requêtes HTTP et délègue au service utilisateur. */
@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {
    private final UtilisateurService service;
    public UtilisateurController(UtilisateurService service) { this.service = service; }
    @GetMapping public List<UtilisateurResponse> lister(@RequestParam(required=false) Boolean actif, @RequestParam(required=false) RoleUtilisateur role) { return service.lister(actif, role); }
    @GetMapping("/{id}") public UtilisateurResponse obtenir(@PathVariable Long id) { return service.obtenir(id); }
    @PostMapping public ResponseEntity<UtilisateurResponse> creer(@Valid @RequestBody UtilisateurRequest d) {
        UtilisateurResponse r = service.creer(d); return ResponseEntity.created(URI.create("/api/utilisateurs/" + r.id())).body(r);
    }
    @PutMapping("/{id}") public UtilisateurResponse modifier(@PathVariable Long id, @Valid @RequestBody UtilisateurRequest d) { return service.modifier(id, d); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> supprimer(@PathVariable Long id) { service.supprimer(id); return ResponseEntity.noContent().build(); }
}
