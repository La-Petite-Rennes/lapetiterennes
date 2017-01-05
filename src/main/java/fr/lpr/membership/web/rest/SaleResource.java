package fr.lpr.membership.web.rest;

import java.net.URI;
import java.net.URISyntaxException;

import org.joda.time.DateTime;
import org.joda.time.YearMonth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import fr.lpr.membership.domain.sale.Sale;
import fr.lpr.membership.service.sale.SaleService;
import fr.lpr.membership.service.sale.SaleStatistics;
import fr.lpr.membership.service.sale.SaleStatisticsService;
import fr.lpr.membership.web.rest.dto.SaleDTO;
import fr.lpr.membership.web.rest.dto.mapper.SaleMapper;

@RestController
@RequestMapping("/api/sales")
@Timed
public class SaleResource {

	@Autowired
	private SaleService saleService;

	@Autowired
	private SaleMapper saleMapper;

	@Autowired
	private SaleStatisticsService statisticsService;

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> newSale(@RequestBody @Validated SaleDTO saleDTO) throws URISyntaxException {
		Sale newSale = saleService.newSale(saleMapper.saleDtoToSale(saleDTO));
		return ResponseEntity.created(new URI("/api/sales/" + newSale.getId())).build();
	}

	@RequestMapping(value = "/statistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public SaleStatistics<YearMonth> statistics() {
		DateTime from = DateTime.now().minusMonths(12).withDayOfMonth(1).withTimeAtStartOfDay();
		return statisticsService.statsByMonths(from);
	}

}
