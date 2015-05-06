package fr.lpr.membership.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.lpr.membership.domain.Adresse;
import fr.lpr.membership.repository.AdresseRepository;
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
 * REST controller for managing Adresse.
 */
@RestController
@RequestMapping("/api")
public class AdresseResource {

    private final Logger log = LoggerFactory.getLogger(AdresseResource.class);

    @Inject
    private AdresseRepository adresseRepository;

    /**
     * POST  /adresses -> Create a new adresse.
     */
    @RequestMapping(value = "/adresses",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Adresse adresse) throws URISyntaxException {
        log.debug("REST request to save Adresse : {}", adresse);
        if (adresse.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new adresse cannot already have an ID").build();
        }
        adresseRepository.save(adresse);
        return ResponseEntity.created(new URI("/api/adresses/" + adresse.getId())).build();
    }

    /**
     * PUT  /adresses -> Updates an existing adresse.
     */
    @RequestMapping(value = "/adresses",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Adresse adresse) throws URISyntaxException {
        log.debug("REST request to update Adresse : {}", adresse);
        if (adresse.getId() == null) {
            return create(adresse);
        }
        adresseRepository.save(adresse);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /adresses -> get all the adresses.
     */
    @RequestMapping(value = "/adresses",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Adresse> getAll() {
        log.debug("REST request to get all Adresses");
        return adresseRepository.findAll();
    }

    /**
     * GET  /adresses/:id -> get the "id" adresse.
     */
    @RequestMapping(value = "/adresses/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Adresse> get(@PathVariable Long id) {
        log.debug("REST request to get Adresse : {}", id);
        return Optional.ofNullable(adresseRepository.findOne(id))
            .map(adresse -> new ResponseEntity<>(
                adresse,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /adresses/:id -> delete the "id" adresse.
     */
    @RequestMapping(value = "/adresses/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Adresse : {}", id);
        adresseRepository.delete(id);
    }
}
