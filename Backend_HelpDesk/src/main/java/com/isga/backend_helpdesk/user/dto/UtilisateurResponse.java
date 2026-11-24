package com.isga.backend_helpdesk.user.dto;

import com.isga.backend_helpdesk.shared.enums.RoleUtilisateur;
import java.time.LocalDateTime;

/** Étape API : représentation JSON publique d'un utilisateur. */
public record UtilisateurResponse(Long id, String nom, String prenom, String email,
                                  RoleUtilisateur role, boolean actif, LocalDateTime dateCreation) {}
