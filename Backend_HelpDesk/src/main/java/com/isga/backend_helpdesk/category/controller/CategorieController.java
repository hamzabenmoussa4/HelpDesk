package com.isga.backend_helpdesk.category.controller;

import com.isga.backend_helpdesk.category.dto.CategorieRequest;
import com.isga.backend_helpdesk.category.dto.CategorieResponse;
import com.isga.backend_helpdesk.category.service.CategorieService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Étape API : expose les opérations REST de gestion des catégories.
 * Le contrôleur HTTP délègue les règles métier au service.
 */
@RestController
@RequestMapping("/api/categories")
public class CategorieController {

    private final CategorieService categorieService;

    public CategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    /** GET /api/categories : liste toutes les catégories ou filtre par état actif. */
    @GetMapping
    public List<CategorieResponse> lister(@RequestParam(required = false) Boolean active) {
        return categorieService.lister(active);
    }

    /** GET /api/categories/{id} : renvoie une catégorie précise. */
    @GetMapping("/{id}")
    public CategorieResponse obtenirParId(@PathVariable Long id) {
        return categorieService.obtenirParId(id);
    }

    /** POST /api/categories : valide et crée une catégorie, puis renvoie HTTP 201. */
    @PostMapping
    public ResponseEntity<CategorieResponse> creer(@Valid @RequestBody CategorieRequest demande) {
        CategorieResponse reponse = categorieService.creer(demande);
        URI emplacement = URI.create("/api/categories/" + reponse.id());
        return ResponseEntity.created(emplacement).body(reponse);
    }

    /** PUT /api/categories/{id} : remplace les champs modifiables d'une catégorie. */
    @PutMapping("/{id}")
    public CategorieResponse modifier(
            @PathVariable Long id,
            @Valid @RequestBody CategorieRequest demande) {
        return categorieService.modifier(id, demande);
    }

    /** DELETE /api/categories/{id} : supprime la catégorie si aucune relation ne l'empêche. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        categorieService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
