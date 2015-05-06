package fr.lpr.membership.repository;

import fr.lpr.membership.domain.Adresse;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Adresse entity.
 */
public interface AdresseRepository extends JpaRepository<Adresse,Long> {

}
