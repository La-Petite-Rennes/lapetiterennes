package fr.lpr.membership.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.lpr.membership.domain.Coordonnees;

/**
 * Spring Data JPA repository for the Coordonnees entity.
 */
public interface CoordonneesRepository extends JpaRepository<Coordonnees, Long> {

}
