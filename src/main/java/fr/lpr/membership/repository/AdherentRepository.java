package fr.lpr.membership.repository;

import fr.lpr.membership.domain.Adherent;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Adherent entity.
 */
public interface AdherentRepository extends JpaRepository<Adherent,Long> {

}
