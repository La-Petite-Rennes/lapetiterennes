package fr.lpr.membership.web.rest;
import static fr.lpr.membership.security.AuthoritiesConstants.ADMIN;
import static fr.lpr.membership.security.AuthoritiesConstants.WORKSHOP_MANAGER;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.YearMonth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import fr.lpr.membership.domain.sale.Sale;
import fr.lpr.membership.repository.sale.SaleRepository;
import fr.lpr.membership.service.sale.ExportExcelService;
import fr.lpr.membership.service.sale.SaleService;
import fr.lpr.membership.service.sale.SaleStatistics;
import fr.lpr.membership.service.sale.SaleStatisticsService;
import fr.lpr.membership.web.rest.dto.SaleDTO;
import fr.lpr.membership.web.rest.dto.mapper.SaleMapper;
import fr.lpr.membership.web.rest.util.PaginationUtil;

@RestController
@RequestMapping("/api/sales")
@Timed
public class SaleResource {

	private final Logger log = LoggerFactory.getLogger(SaleResource.class);

	@Autowired
	private SaleRepository saleRepository;

	@Autowired
	private SaleService saleService;

	@Autowired
	private SaleMapper saleMapper;

	@Autowired
	private SaleStatisticsService statisticsService;

	@Autowired
	private ExportExcelService exportExcelService;

	@RequestMapping(method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> newSale(@RequestBody @Validated SaleDTO saleDTO) throws URISyntaxException {
		if (saleDTO.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new sale cannot already have an ID").build();
		}

		Sale newSale = saleService.newSale(saleMapper.saleDtoToSale(saleDTO));
		return ResponseEntity.created(new URI("/api/sales/" + newSale.getId())).build();
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> updateSale(@RequestBody @Validated SaleDTO saleDTO) throws URISyntaxException {
		if (saleDTO.getId() == null) {
			return newSale(saleDTO);
		}

		saleService.update(saleMapper.saleDtoToSale(saleDTO));
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = "/statistics/{year}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public SaleStatistics<YearMonth> statistics(@PathVariable Integer year) {
		DateTime from = DateTime.now().withYear(year).withMonthOfYear(1).withDayOfMonth(1).withTimeAtStartOfDay();
		return statisticsService.statsByMonths(from);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	@Transactional(readOnly = true)
	public ResponseEntity<SaleDTO> get(@PathVariable Long id) {
		return Optional.ofNullable(saleRepository.getOne(id))
				.map(saleMapper::saleToSaleDto)
				.map(saleDTO -> new ResponseEntity<>(saleDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(value = "/export/{year}", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
	@Timed
	@RolesAllowed({ ADMIN, WORKSHOP_MANAGER })
	public void exportStatistics(@PathVariable Integer year, HttpServletResponse response) throws IOException {
        DateTime from = DateTime.now().withYear(year).withMonthOfYear(1).withDayOfMonth(1).withTimeAtStartOfDay();
		response.setHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		exportExcelService.export(from, response.getOutputStream());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@Timed
	@RolesAllowed({ ADMIN, WORKSHOP_MANAGER })
	public void delete(@PathVariable Long id) {
		log.debug("REST request to delete Sale : {}", id);
		saleService.delete(id);
	}

	@RequestMapping(value = "/history", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<SaleDTO>> history(@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException {
		Page<Sale> page = saleService.history(offset, limit);

		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sales/history", offset, limit);
		return new ResponseEntity<>(page.getContent().stream().map(saleMapper::saleToSaleDto).collect(Collectors.toList()), headers, OK);
	}

	@RequestMapping(value = "/temporary", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	@Timed
	public List<SaleDTO> getTemporarySales() {
		return saleService.getTemporarySales().stream().map(saleMapper::saleToSaleDto).collect(Collectors.toList());
	}

}
