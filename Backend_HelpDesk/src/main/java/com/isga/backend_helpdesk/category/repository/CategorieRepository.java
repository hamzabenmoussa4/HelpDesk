package com.isga.backend_helpdesk.category.repository;

import com.isga.backend_helpdesk.category.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Étape persistance : Spring Data JPA fournit les opérations CRUD SQL de base.
 */
public interface CategorieRepository extends JpaRepository<Categorie, Long> {

    /** Recherche un nom sans distinguer les majuscules des minuscules. */
    boolean existsByNomIgnoreCase(String nom);

    /** Vérifie l'unicité du nom en excluant la catégorie modifiée. */
    boolean existsByNomIgnoreCaseAndIdNot(String nom, Long id);

    /** Renvoie toutes les catégories dans un ordre stable pour les listes API. */
    List<Categorie> findAllByOrderByNomAsc();

    /** Filtre les catégories actives ou inactives, triées par nom. */
    List<Categorie> findByActiveOrderByNomAsc(boolean active);
}
