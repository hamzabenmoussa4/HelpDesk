package com.isga.backend_helpdesk.user.repository;

import com.isga.backend_helpdesk.shared.enums.RoleUtilisateur;
import com.isga.backend_helpdesk.user.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Étape persistance : fournit les requêtes de base et la recherche d'e-mail. */
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    List<Utilisateur> findAllByOrderByNomAscPrenomAsc();
    List<Utilisateur> findByActifOrderByNomAscPrenomAsc(boolean actif);
    List<Utilisateur> findByRoleAndActifOrderByNomAscPrenomAsc(RoleUtilisateur role, boolean actif);
}
