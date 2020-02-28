package fr.lpr.membership.web.rest.adhesion;

import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.Adhesion;
import fr.lpr.membership.repository.AdherentRepository;
import fr.lpr.membership.repository.AdhesionRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * REST controller for managing Adhesion.
 */
@RestController
@RequestMapping("/adhesions")
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
	@PostMapping
	@Timed
	public ResponseEntity<Void> create(@Validated @RequestBody Adhesion adhesion) throws URISyntaxException {
		log.debug("REST request to save Adhesion : {}", adhesion);
		if (adhesion.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new adhesion cannot already have an ID").build();
		}

		final Adherent adherent = adherentRepository.getOne(adhesion.getAdherent().getId());
		adherent.addAdhesion(adhesion);
		adhesion.setAdherent(adherent);

		adherentRepository.save(adherent);

		return ResponseEntity.created(new URI("/adhesions/" + adhesion.getId())).build();
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
	@PutMapping
	@Timed
	public ResponseEntity<Void> update(@Validated @RequestBody Adhesion adhesion) throws URISyntaxException {
		log.debug("REST request to update Adhesion : {}", adhesion);
		if (adhesion.getId() == null) {
			return create(adhesion);
		}
		adhesionRepository.save(adhesion);
		return ResponseEntity.ok().build();
	}

	/**
	 * GET /adhesions/adherent/:adherentId -&gt; get adhesions of the "adherentId" adherent
	 *
	 * @param adherentId
	 *            the identifier of an adherent
	 * @return the adhesions
	 */
	@GetMapping("/adherent/{adherentId}")
	@Timed
	public ResponseEntity<Set<Adhesion>> getAdherentAdhesions(@PathVariable Long adherentId) {
		log.debug("REST request to get adhesions of Adherent : {}", adherentId);
		return adherentRepository.findById(adherentId)
            .map(adherent -> new ResponseEntity<>(adherent.getAdhesions(), HttpStatus.OK))
			.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * GET /adhesions/:id -&gt; get the "id" adhesion.
	 *
	 * @param id
	 *            the identifier
	 * @return the adhesion
	 */
	@GetMapping("/{id}")
	@Timed
	public ResponseEntity<Adhesion> get(@PathVariable Long id) {
		log.debug("REST request to get Adhesion : {}", id);
		return adhesionRepository.findById(id)
            .map(adhesion -> new ResponseEntity<>(adhesion, HttpStatus.OK))
			.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /adhesions/:id -&gt; delete the "id" adhesion.
	 *
	 * @param id
	 *            the identifier
	 */
	@DeleteMapping("/{id}")
	@Timed
	public void delete(@PathVariable Long id) {
		log.debug("REST request to delete Adhesion : {}", id);
		final Adhesion adhesion = adhesionRepository.getOne(id);
		adhesion.setAdherent(null);
		adhesionRepository.delete(adhesion);
	}
}
