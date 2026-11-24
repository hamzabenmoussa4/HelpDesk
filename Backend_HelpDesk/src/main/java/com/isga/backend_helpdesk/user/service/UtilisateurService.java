package com.isga.backend_helpdesk.user.service;

import com.isga.backend_helpdesk.common.exception.DuplicateResourceException;
import com.isga.backend_helpdesk.common.exception.ResourceNotFoundException;
import com.isga.backend_helpdesk.shared.enums.RoleUtilisateur;
import com.isga.backend_helpdesk.user.dto.UtilisateurRequest;
import com.isga.backend_helpdesk.user.dto.UtilisateurResponse;
import com.isga.backend_helpdesk.user.entity.Utilisateur;
import com.isga.backend_helpdesk.user.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Étape métier : centralise les règles de création et de gestion des utilisateurs. */
@Service
@Transactional(readOnly = true)
public class UtilisateurService {
    private final UtilisateurRepository repository;
    public UtilisateurService(UtilisateurRepository repository) { this.repository = repository; }

    /** Liste les utilisateurs, avec filtres facultatifs sur leur état et leur rôle. */
    public List<UtilisateurResponse> lister(Boolean actif, RoleUtilisateur role) {
        List<Utilisateur> resultat = role != null && Boolean.TRUE.equals(actif)
                ? repository.findByRoleAndActifOrderByNomAscPrenomAsc(role, true)
                : actif == null ? repository.findAllByOrderByNomAscPrenomAsc()
                : repository.findByActifOrderByNomAscPrenomAsc(actif);
        if (role != null && !Boolean.TRUE.equals(actif)) resultat = resultat.stream().filter(u -> u.getRole() == role).toList();
        return resultat.stream().map(this::reponse).toList();
    }
    public UtilisateurResponse obtenir(Long id) { return reponse(trouver(id)); }

    /** Crée un utilisateur et refuse les adresses e-mail déjà utilisées. */
    @Transactional
    public UtilisateurResponse creer(UtilisateurRequest d) {
        String email = normaliserEmail(d.email()); verifierEmail(email, null);
        Utilisateur u = new Utilisateur(d.nom().trim(), d.prenom().trim(), email, d.role());
        if (d.actif() != null) u.setActif(d.actif());
        return reponse(repository.save(u));
    }
    /** Modifie un utilisateur en vérifiant l'unicité de son adresse. */
    @Transactional
    public UtilisateurResponse modifier(Long id, UtilisateurRequest d) {
        Utilisateur u = trouver(id); String email = normaliserEmail(d.email()); verifierEmail(email, id);
        u.setNom(d.nom().trim()); u.setPrenom(d.prenom().trim()); u.setEmail(email); u.setRole(d.role());
        if (d.actif() != null) u.setActif(d.actif());
        return reponse(repository.save(u));
    }
    /** Désactive au lieu d'effacer pour conserver les liens des tickets et de l'historique. */
    @Transactional
    public void supprimer(Long id) { Utilisateur u = trouver(id); u.setActif(false); }

    private Utilisateur trouver(Long id) { return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aucun utilisateur ne correspond à l'identifiant " + id + ".")); }
    private void verifierEmail(String email, Long id) {
        boolean existe = id == null ? repository.existsByEmailIgnoreCase(email) : repository.existsByEmailIgnoreCaseAndIdNot(email, id);
        if (existe) throw new DuplicateResourceException("Cette adresse e-mail est déjà utilisée.");
    }
    private String normaliserEmail(String email) { return email.trim().toLowerCase(); }
    private UtilisateurResponse reponse(Utilisateur u) { return new UtilisateurResponse(u.getId(), u.getNom(), u.getPrenom(), u.getEmail(), u.getRole(), u.isActif(), u.getDateCreation()); }
}
