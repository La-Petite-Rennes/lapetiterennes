package fr.lpr.membership.web.rest;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
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
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;

import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.repository.AdherentRepository;
import fr.lpr.membership.repository.SearchAdherentRepository;
import fr.lpr.membership.security.AuthoritiesConstants;
import fr.lpr.membership.service.AdherentService;
import fr.lpr.membership.service.ExportService;
import fr.lpr.membership.service.ImportService;
import fr.lpr.membership.web.rest.dto.ExportRequest;
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
	private SearchAdherentRepository searchAdherentRepository;

	@Inject
	private AdherentService adherentService;

	@Inject
	private ExportService exportService;

	@Inject
	private ImportService importService;

	/**
	 * POST /adherents -&gt; Create a new adherent.
	 *
	 * @param adherent
	 *            the adherent
	 * @return result of the creation
	 * @throws URISyntaxException
	 *             if uri cannot be built
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
	 * PUT /adherents -&gt; Updates an existing adherent.
	 *
	 * @param adherent
	 *            the adherent
	 * @return result of the update
	 * @throws URISyntaxException
	 *             if uri cannot be built
	 */
	@RequestMapping(value = "/adherents", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> update(@Valid @RequestBody Adherent adherent) throws URISyntaxException {
		log.debug("REST request to update Adherent : {}", adherent);
		if (adherent.getId() == null) {
			return create(adherent);
		}
		adherent.setAdhesions(adherentRepository.findOne(adherent.getId()).getAdhesions());
		adherentRepository.save(adherent);
		return ResponseEntity.ok().build();
	}

	/**
	 * GET /adherents -&gt; get all the adherents.
	 *
	 * @param offset
	 *            the offset
	 * @param limit
	 *            max number of adherents
	 * @return the adherents
	 * @throws URISyntaxException
	 *             if uris cannot be built
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
	 * Search /adherents -&gt; get the adherents filtered by name and sorted
	 *
	 * @param offset
	 *            the offset
	 * @param limit
	 *            max number of adherents
	 * @param criteria
	 *            the criteria
	 * @param sortProperty
	 *            property to sort on
	 * @param sortOrder
	 *            the sort order
	 * @return the adherents matching the search
	 * @throws URISyntaxException
	 *             if uris cannot be built
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
			page = searchAdherentRepository.findAdherentByName(criteria, pageRequest);
		}

		final HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/adherents", offset, limit);

		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	/**
	 * GET /adherents/:id -&gt; get the "id" adherent.
	 *
	 * @param id
	 *            the identifier
	 * @return the adherent
	 */
	@RequestMapping(value = "/adherents/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Adherent> get(@PathVariable Long id) {
		log.debug("REST request to get Adherent : {}", id);
		return Optional.ofNullable(adherentRepository.findOne(id)).map(adherent -> new ResponseEntity<>(adherent, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /adherents/:id -&gt; delete the "id" adherent.
	 *
	 * @param id
	 *            the identifier
	 */
	@RequestMapping(value = "/adherents/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.WORKSHOP_MANAGER})
	public void delete(@PathVariable Long id) {
		log.debug("REST request to delete Adherent : {}", id);
		adherentRepository.delete(id);
	}

	/**
	 * GET /adherents/export -&gt; Export the adherents
	 *
	 * @param request
	 *            the json payload request
	 * @param response
	 *            the http response
	 * @throws Exception
	 *             if an error occurs
	 */
	@RequestMapping(value = "/adherents/export", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@RolesAllowed(AuthoritiesConstants.ADMIN)
	public void exportAll(@RequestBody ExportRequest request, HttpServletResponse response) throws Exception {
		final List<String> properties = new ArrayList<>();
		for (final Entry<String, Boolean> entry : request.getProperties().entrySet()) {
			if (entry.getValue()) {
				properties.add(entry.getKey());
			}
		}

		exportService.export(request.getFormat(), properties, request.getAdhesionState(), response);
	}

	/**
	 * GET /adherents/import -&gt; Import the adherents
	 *
	 * @param file
	 *            the file to import
	 * @throws Exception
	 *             if an error occurs
	 */
	@RequestMapping(value = "/adherents/import", method = RequestMethod.POST)
	@Timed
	@RolesAllowed(AuthoritiesConstants.ADMIN)
	public void importAdherents(@RequestParam("file") MultipartFile file) throws Exception {
		if (!file.isEmpty()) {
			try (InputStream inputStream = file.getInputStream()) {
				importService.importCsv(inputStream);
			}
		}
	}

	/**
	 * POST /adherents/reminderEmail/:id -&gt; Send a reminder email to an adherent
	 *
	 * @param adherentId
	 *            the adherent identifier
	 * @return Http status
	 * @throws Exception
	 *             if an error occurs
	 */
	@RequestMapping(value = "/adherents/reminderEmail/{adherentId}", method = RequestMethod.POST)
	@RolesAllowed(AuthoritiesConstants.ADMIN)
	public ResponseEntity<Void> remindesEmail(@PathVariable("adherentId") Long adherentId) throws Exception {
		final Adherent adherent = adherentRepository.findOne(adherentId);

		if (adherent != null) {
			adherentService.sendMail(adherent);
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

}
