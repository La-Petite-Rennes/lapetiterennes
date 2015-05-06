package fr.lpr.membership.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.lpr.membership.domain.Coordonnees;
import fr.lpr.membership.repository.CoordonneesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Coordonnees.
 */
@RestController
@RequestMapping("/api")
public class CoordonneesResource {

    private final Logger log = LoggerFactory.getLogger(CoordonneesResource.class);

    @Inject
    private CoordonneesRepository coordonneesRepository;

    /**
     * POST  /coordonneess -> Create a new coordonnees.
     */
    @RequestMapping(value = "/coordonneess",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Coordonnees coordonnees) throws URISyntaxException {
        log.debug("REST request to save Coordonnees : {}", coordonnees);
        if (coordonnees.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new coordonnees cannot already have an ID").build();
        }
        coordonneesRepository.save(coordonnees);
        return ResponseEntity.created(new URI("/api/coordonneess/" + coordonnees.getId())).build();
    }

    /**
     * PUT  /coordonneess -> Updates an existing coordonnees.
     */
    @RequestMapping(value = "/coordonneess",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Coordonnees coordonnees) throws URISyntaxException {
        log.debug("REST request to update Coordonnees : {}", coordonnees);
        if (coordonnees.getId() == null) {
            return create(coordonnees);
        }
        coordonneesRepository.save(coordonnees);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /coordonneess -> get all the coordonneess.
     */
    @RequestMapping(value = "/coordonneess",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Coordonnees> getAll() {
        log.debug("REST request to get all Coordonneess");
        return coordonneesRepository.findAll();
    }

    /**
     * GET  /coordonneess/:id -> get the "id" coordonnees.
     */
    @RequestMapping(value = "/coordonneess/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Coordonnees> get(@PathVariable Long id) {
        log.debug("REST request to get Coordonnees : {}", id);
        return Optional.ofNullable(coordonneesRepository.findOne(id))
            .map(coordonnees -> new ResponseEntity<>(
                coordonnees,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /coordonneess/:id -> delete the "id" coordonnees.
     */
    @RequestMapping(value = "/coordonneess/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Coordonnees : {}", id);
        coordonneesRepository.delete(id);
    }
}
