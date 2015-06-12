package fr.lpr.membership.web.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.repository.AdherentRepository;
import fr.lpr.membership.security.AuthoritiesConstants;
import fr.lpr.membership.service.ExportService;
import fr.lpr.membership.web.rest.util.PaginationUtil;

/**
 * REST controller for managing Adherent.
 */
@RestController
@RequestMapping("/api")
public class AdherentResource {

	private final Logger log = LoggerFactory.getLogger(AdherentResource.class);

	@Inject
	private AdherentRepository adherentRepository;

	@Inject
	private ExportService exportService;

	/**
	 * POST /adherents -> Create a new adherent.
	 */
	@RequestMapping(value = "/adherents", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> create(@Valid @RequestBody Adherent adherent) throws URISyntaxException {
		log.debug("REST request to save Adherent : {}", adherent);
		if (adherent.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new adherent cannot already have an ID").build();
		}
		adherentRepository.save(adherent);
		return ResponseEntity.created(new URI("/api/adherents/" + adherent.getId())).build();
	}

	/**
	 * PUT /adherents -> Updates an existing adherent.
	 */
	@RequestMapping(value = "/adherents", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> update(@Valid @RequestBody Adherent adherent) throws URISyntaxException {
		log.debug("REST request to update Adherent : {}", adherent);
		if (adherent.getId() == null) {
			return create(adherent);
		}
		adherentRepository.save(adherent);
		return ResponseEntity.ok().build();
	}

	@Inject
	ObjectMapper objectMapper;

	/**
	 * GET /adherents -> get all the adherents.
	 */
	@RequestMapping(value = "/adherents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Adherent>> getAll(@RequestParam(value = "page", required = false) Integer offset, @RequestParam(value = "per_page",
	required = false) Integer limit) throws URISyntaxException {
		final Page<Adherent> page = adherentRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
		final HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/adherents", offset, limit);

		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	/**
	 * Search /adherents -> get the adherents filtered by name and sorted
	 */
	@RequestMapping(value = "/adherents/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Adherent>> search(@RequestParam(value = "page", required = false) Integer offset, @RequestParam(value = "per_page",
	required = false) Integer limit, @RequestParam(value = "criteria", required = false) String criteria, @RequestParam(value = "sort",
	defaultValue = "id") String sortProperty, @RequestParam(value = "sortOrder", defaultValue = "ASC") String sortOrder) throws URISyntaxException {
		final Sort sort = new Sort(Direction.fromStringOrNull(sortOrder), sortProperty);
		final Pageable pageRequest = PaginationUtil.generatePageRequest(offset, limit, sort);

		Page<Adherent> page = null;
		if (criteria == null || criteria.isEmpty()) {
			page = adherentRepository.findAll(pageRequest);
		} else {
			page = adherentRepository.findByNomLikeOrPrenomLike(criteria, criteria, pageRequest);
		}

		final HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/adherents", offset, limit);

		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	/**
	 * GET /adherents/:id -> get the "id" adherent.
	 */
	@RequestMapping(value = "/adherents/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Adherent> get(@PathVariable Long id) {
		log.debug("REST request to get Adherent : {}", id);
		return Optional.ofNullable(adherentRepository.findOne(id)).map(adherent -> new ResponseEntity<>(adherent, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /adherents/:id -> delete the "id" adherent.
	 */
	@RequestMapping(value = "/adherents/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void delete(@PathVariable Long id) {
		log.debug("REST request to delete Adherent : {}", id);
		adherentRepository.delete(id);
	}

	/**
	 * GET /adherent/export -> Export the adherent
	 */
	@RequestMapping(value = "/adherents/export", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@RolesAllowed(AuthoritiesConstants.ADMIN)
	public void exportAll(@RequestParam(value = "format", defaultValue = ExportService.CSV) String format, HttpServletResponse response) throws IOException {
		exportService.export(format, ImmutableList.of("id", "nom", "prenom"), response);
	}
}
