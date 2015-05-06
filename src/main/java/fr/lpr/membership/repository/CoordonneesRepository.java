package fr.lpr.membership.repository;

import fr.lpr.membership.domain.Coordonnees;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Coordonnees entity.
 */
public interface CoordonneesRepository extends JpaRepository<Coordonnees,Long> {

}
