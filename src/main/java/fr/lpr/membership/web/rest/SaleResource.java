package fr.lpr.membership.web.rest;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.YearMonth;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fr.lpr.membership.security.AuthoritiesConstants.ADMIN;
import static fr.lpr.membership.security.AuthoritiesConstants.WORKSHOP_MANAGER;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/sales")
@Timed
@Slf4j
@RequiredArgsConstructor
public class SaleResource {

	private final SaleRepository saleRepository;

	private final SaleService saleService;

	private final SaleMapper saleMapper;

	private final SaleStatisticsService statisticsService;

	private final ExportExcelService exportExcelService;

	@PostMapping(consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> newSale(@RequestBody @Validated SaleDTO saleDTO) throws URISyntaxException {
		if (saleDTO.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new sale cannot already have an ID").build();
		}

		Sale newSale = saleService.newSale(saleMapper.saleDtoToSale(saleDTO));
		return ResponseEntity.created(new URI("/api/sales/" + newSale.getId())).build();
	}

	@PutMapping(consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> updateSale(@RequestBody @Validated SaleDTO saleDTO) throws URISyntaxException {
		if (saleDTO.getId() == null) {
			return newSale(saleDTO);
		}

		saleService.update(saleMapper.saleDtoToSale(saleDTO));
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/statistics/{year}", produces = APPLICATION_JSON_VALUE)
	public SaleStatistics<YearMonth> statistics(@PathVariable Integer year) {
		DateTime from = DateTime.now().withYear(year).withMonthOfYear(1).withDayOfMonth(1).withTimeAtStartOfDay();
		return statisticsService.statsByMonths(from);
	}

	@GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
	@Transactional(readOnly = true)
	public ResponseEntity<SaleDTO> get(@PathVariable Long id) {
		return Optional.ofNullable(saleRepository.getOne(id))
				.map(saleMapper::saleToSaleDto)
				.map(saleDTO -> new ResponseEntity<>(saleDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping(value = "/export", produces = APPLICATION_JSON_VALUE)
	@Timed
	@RolesAllowed({ ADMIN, WORKSHOP_MANAGER })
	public void exportStatistics(HttpServletResponse response) throws IOException {
		response.setHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		exportExcelService.export(response.getOutputStream());
	}

	@DeleteMapping(value = "/{id}")
	@Timed
	@RolesAllowed({ ADMIN, WORKSHOP_MANAGER })
	public void delete(@PathVariable Long id) {
		log.debug("REST request to delete Sale : {}", id);
		saleService.delete(id);
	}

	@GetMapping(value = "/history", produces = APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<SaleDTO>> history(@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException {
		Page<Sale> page = saleService.history(offset, limit);

		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sales/history", offset, limit);
		return new ResponseEntity<>(page.getContent().stream().map(saleMapper::saleToSaleDto).collect(Collectors.toList()), headers, OK);
	}

	@GetMapping(value = "/temporary", produces = APPLICATION_JSON_VALUE)
	@Timed
	public List<SaleDTO> getTemporarySales() {
		return saleService.getTemporarySales().stream().map(saleMapper::saleToSaleDto).collect(Collectors.toList());
	}

}
