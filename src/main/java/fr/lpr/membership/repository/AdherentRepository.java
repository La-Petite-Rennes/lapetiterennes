package fr.lpr.membership.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import fr.lpr.membership.domain.Adherent;

/**
 * Spring Data JPA repository for the Adherent entity.
 */
public interface AdherentRepository extends JpaRepository<Adherent, Long> {

	Page<Adherent> findByNomContainingOrPrenomContainingAllIgnoreCase(String nom, String prenom, Pageable pageable);

}
