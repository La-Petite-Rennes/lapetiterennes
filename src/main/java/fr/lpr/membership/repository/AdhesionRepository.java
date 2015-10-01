package fr.lpr.membership.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.lpr.membership.domain.Adhesion;

/**
 * Spring Data JPA repository for the Adhesion entity.
 */
public interface AdhesionRepository extends JpaRepository<Adhesion, Long> {

}
