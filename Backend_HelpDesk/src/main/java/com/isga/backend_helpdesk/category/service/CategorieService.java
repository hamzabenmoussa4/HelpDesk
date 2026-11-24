package com.isga.backend_helpdesk.category.service;

import com.isga.backend_helpdesk.category.dto.CategorieRequest;
import com.isga.backend_helpdesk.category.dto.CategorieResponse;
import com.isga.backend_helpdesk.category.entity.Categorie;
import com.isga.backend_helpdesk.category.repository.CategorieRepository;
import com.isga.backend_helpdesk.common.exception.DuplicateResourceException;
import com.isga.backend_helpdesk.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Étape métier : applique les règles de catégories entre le contrôleur et la base.
 */
@Service
@Transactional(readOnly = true)
public class CategorieService {

    private final CategorieRepository categorieRepository;

    public CategorieService(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    /** Liste les catégories ; le filtre active est facultatif. */
    public List<CategorieResponse> lister(Boolean active) {
        List<Categorie> categories = active == null
                ? categorieRepository.findAllByOrderByNomAsc()
                : categorieRepository.findByActiveOrderByNomAsc(active);
        return categories.stream().map(this::versReponse).toList();
    }

    /** Recherche une catégorie par son identifiant ou signale une 404. */
    public CategorieResponse obtenirParId(Long id) {
        return versReponse(trouverEntite(id));
    }

    /** Valide l'unicité puis crée une catégorie active par défaut. */
    @Transactional
    public CategorieResponse creer(CategorieRequest demande) {
        String nomNormalise = normaliserNom(demande.nom());
        verifierNomDisponible(nomNormalise, null);

        Categorie categorie = new Categorie(nomNormalise, demande.description());
        if (demande.active() != null) {
            categorie.setActive(demande.active());
        }
        return versReponse(categorieRepository.save(categorie));
    }

    /** Met à jour les champs fournis et conserve l'état actif si celui-ci est omis. */
    @Transactional
    public CategorieResponse modifier(Long id, CategorieRequest demande) {
        Categorie categorie = trouverEntite(id);
        String nomNormalise = normaliserNom(demande.nom());
        verifierNomDisponible(nomNormalise, id);

        categorie.setNom(nomNormalise);
        categorie.setDescription(demande.description());
        if (demande.active() != null) {
            categorie.setActive(demande.active());
        }
        return versReponse(categorieRepository.save(categorie));
    }

    /** Supprime la catégorie ; PostgreSQL bloque l'action si des tickets l'utilisent. */
    @Transactional
    public void supprimer(Long id) {
        Categorie categorie = trouverEntite(id);
        categorieRepository.delete(categorie);
    }

    /** Charge l'entité à l'intérieur du service pour centraliser le cas 404. */
    private Categorie trouverEntite(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucune catégorie ne correspond à l'identifiant " + id + "."));
    }

    /** Empêche les doublons sans différencier les majuscules des minuscules. */
    private void verifierNomDisponible(String nom, Long idExclu) {
        boolean existeDeja = idExclu == null
                ? categorieRepository.existsByNomIgnoreCase(nom)
                : categorieRepository.existsByNomIgnoreCaseAndIdNot(nom, idExclu);
        if (existeDeja) {
            throw new DuplicateResourceException(
                    "Une catégorie portant le nom « " + nom + " » existe déjà.");
        }
    }

    /** Enlève les espaces autour du nom avant validation et sauvegarde. */
    private String normaliserNom(String nom) {
        return nom == null ? null : nom.trim();
    }

    /** Expose uniquement les champs prévus dans la réponse JSON. */
    private CategorieResponse versReponse(Categorie categorie) {
        return new CategorieResponse(
                categorie.getId(), categorie.getNom(), categorie.getDescription(), categorie.isActive());
    }
}
