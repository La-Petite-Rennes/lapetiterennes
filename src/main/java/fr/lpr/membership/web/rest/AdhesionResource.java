package fr.lpr.membership.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.Adhesion;
import fr.lpr.membership.repository.AdherentRepository;
import fr.lpr.membership.repository.AdhesionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * REST controller for managing Adhesion.
 */
@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class AdhesionResource {

	private final AdhesionRepository adhesionRepository;

	private final AdherentRepository adherentRepository;

	/**
	 * POST /adhesions -&gt; Create a new adhesion.
	 *
	 * @param adhesion
	 *            the adhesion
	 * @return result of the creation
	 * @throws URISyntaxException
	 *             if uri cannot be built
	 */
	@RequestMapping(value = "/adhesions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> create(@Valid @RequestBody Adhesion adhesion) throws URISyntaxException {
		log.debug("REST request to save Adhesion : {}", adhesion);
		if (adhesion.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new adhesion cannot already have an ID").build();
		}

		final Adherent adherent = adherentRepository.findOne(adhesion.getAdherent().getId());
		adherent.addAdhesion(adhesion);
		adhesion.setAdherent(adherent);

		adherentRepository.save(adherent);

		return ResponseEntity.created(new URI("/api/adhesions/" + adhesion.getId())).build();
	}

	/**
	 * PUT /adhesions -&gt; Updates an existing adhesion.
	 *
	 * @param adhesion
	 *            the adhesion
	 * @return result of the update
	 * @throws URISyntaxException
	 *             if uri cannot be built
	 */
	@RequestMapping(value = "/adhesions", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> update(@Valid @RequestBody Adhesion adhesion) throws URISyntaxException {
		log.debug("REST request to update Adhesion : {}", adhesion);
		if (adhesion.getId() == null) {
			return create(adhesion);
		}
		adhesionRepository.save(adhesion);
		return ResponseEntity.ok().build();
	}

	/**
	 * GET /adhesions -&gt; get all the adhesions.
	 *
	 * @return all adhesions
	 */
	@RequestMapping(value = "/adhesions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<Adhesion> getAll() {
		log.debug("REST request to get all Adhesions");
		return adhesionRepository.findAll();
	}

	/**
	 * GET /adhesions/adherent/:adherentId -&gt; get adhesions of the "adherentId" adherent
	 *
	 * @param adherentId
	 *            the identifier of an adherent
	 * @return the adhesions
	 */
	@RequestMapping(value = "/adhesions/adherent/{adherentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Set<Adhesion>> getAdherentAdhesions(@PathVariable Long adherentId) {
		log.debug("REST request to get adhesions of Adherent : {}", adherentId);
		return Optional.ofNullable(adherentRepository.findOne(adherentId)).map(adherent -> new ResponseEntity<>(adherent.getAdhesions(), HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * GET /adhesions/:id -&gt; get the "id" adhesion.
	 *
	 * @param id
	 *            the identifier
	 * @return the adhesion
	 */
	@RequestMapping(value = "/adhesions/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Adhesion> get(@PathVariable Long id) {
		log.debug("REST request to get Adhesion : {}", id);
		return Optional.ofNullable(adhesionRepository.findOne(id)).map(adhesion -> new ResponseEntity<>(adhesion, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /adhesions/:id -&gt; delete the "id" adhesion.
	 *
	 * @param id
	 *            the identifier
	 */
	@RequestMapping(value = "/adhesions/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void delete(@PathVariable Long id) {
		log.debug("REST request to delete Adhesion : {}", id);
		final Adhesion adhesion = adhesionRepository.findOne(id);
		adhesion.setAdherent(null);
		adhesionRepository.delete(adhesion);
	}
}
