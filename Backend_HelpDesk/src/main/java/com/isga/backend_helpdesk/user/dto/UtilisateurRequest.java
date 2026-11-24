package com.isga.backend_helpdesk.user.dto;

import com.isga.backend_helpdesk.shared.enums.RoleUtilisateur;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Étape API : valide les champs modifiables d'un utilisateur sans exposer l'entité. */
public record UtilisateurRequest(
        @NotBlank(message = "Le nom est obligatoire.") @Size(max = 80, message = "Le nom est limité à 80 caractères.") String nom,
        @NotBlank(message = "Le prénom est obligatoire.") @Size(max = 80, message = "Le prénom est limité à 80 caractères.") String prenom,
        @NotBlank(message = "L'adresse e-mail est obligatoire.") @Email(message = "L'adresse e-mail est invalide.") @Size(max = 180, message = "L'adresse e-mail est limitée à 180 caractères.") String email,
        @NotNull(message = "Le rôle est obligatoire.") RoleUtilisateur role,
        Boolean actif
) {}
