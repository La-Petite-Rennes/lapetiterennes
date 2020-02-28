package fr.lpr.membership.web.rest.sale;

import fr.lpr.membership.domain.sale.Sale;
import fr.lpr.membership.repository.sale.SaleRepository;
import fr.lpr.membership.service.sale.ExportExcelService;
import fr.lpr.membership.service.sale.SaleService;
import fr.lpr.membership.service.sale.SaleStatistics;
import fr.lpr.membership.service.sale.SaleStatisticsService;
import fr.lpr.membership.web.rest.dto.SaleDTO;
import fr.lpr.membership.web.rest.dto.mapper.SaleMapper;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import static fr.lpr.membership.security.AuthoritiesConstants.ADMIN;
import static fr.lpr.membership.security.AuthoritiesConstants.WORKSHOP_MANAGER;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/sales")
@Timed
@Slf4j
@RequiredArgsConstructor
public class SaleResource {

	private final SaleRepository saleRepository;

	private final SaleService saleService;

	private final SaleMapper saleMapper;

	private final SaleStatisticsService statisticsService;

	private final ExportExcelService exportExcelService;

	@PostMapping
	public ResponseEntity<Void> newSale(@RequestBody @Validated SaleDTO saleDTO) throws URISyntaxException {
		if (saleDTO.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new sale cannot already have an ID").build();
		}

		Sale newSale = saleService.newSale(saleMapper.saleDtoToSale(saleDTO));
		return ResponseEntity.created(new URI("/sales/" + newSale.getId())).build();
	}

	@PutMapping
	public ResponseEntity<Void> updateSale(@RequestBody @Validated SaleDTO saleDTO) throws URISyntaxException {
		if (saleDTO.getId() == null) {
			return newSale(saleDTO);
		}

		saleService.update(saleMapper.saleDtoToSale(saleDTO));
		return ResponseEntity.ok().build();
	}

	@GetMapping("/statistics/{year}")
	public SaleStatistics<YearMonth> statistics(@PathVariable Integer year) {
		LocalDateTime from = LocalDateTime.of(year, 1, 1, 0, 0);
		return statisticsService.statsByMonths(from);
	}

	@GetMapping("/{id}")
	@Transactional(readOnly = true)
	public ResponseEntity<SaleDTO> get(@PathVariable Long id) {
		return saleRepository.findById(id)
				.map(saleMapper::saleToSaleDto)
				.map(saleDTO -> new ResponseEntity<>(saleDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/export/{year}")
	@Timed
	@RolesAllowed({ ADMIN, WORKSHOP_MANAGER })
	public void exportStatistics(@PathVariable Integer year, HttpServletResponse response) throws IOException {
        LocalDateTime from = LocalDateTime.of(year, 1, 1, 0, 0);
		response.setHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		exportExcelService.export(from, response.getOutputStream());
	}

	@DeleteMapping("/{id}")
	@Timed
	@RolesAllowed({ ADMIN, WORKSHOP_MANAGER })
	public void delete(@PathVariable Long id) {
		log.debug("REST request to delete Sale : {}", id);
		saleService.delete(id);
	}

	@GetMapping("/history")
	@Timed
    // FIXME Retourner un objet Page
	public ResponseEntity<List<SaleDTO>> history(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable)
    {
		Page<Sale> page = saleService.history(pageable);
		return new ResponseEntity<>(page.getContent().stream().map(saleMapper::saleToSaleDto).collect(Collectors.toList()), OK);
	}

	@GetMapping("/temporary")
	@Timed
	public List<SaleDTO> getTemporarySales() {
		return saleService.getTemporarySales().stream().map(saleMapper::saleToSaleDto).collect(Collectors.toList());
	}
}
