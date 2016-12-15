package fr.lpr.membership.service.sale;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.joda.time.DateTime;
import org.joda.time.YearMonth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.lpr.membership.domain.sale.QSale;
import fr.lpr.membership.domain.sale.Sale;
import fr.lpr.membership.repository.AdhesionRepository;
import fr.lpr.membership.repository.sale.SaleRepository;

@Service
public class SaleStatisticsService {

	@Autowired
	private SaleRepository saleRepository;

	@Autowired
	private AdhesionRepository adhesionRepository;

	public Map<YearMonth, List<Sale>> last12MonthsSales() {
		// Select sales from
		DateTime from = DateTime.now().minusMonths(12).withDayOfMonth(0).withTimeAtStartOfDay();

		Map<YearMonth, List<Sale>> result =
				StreamSupport
						.stream(saleRepository.findAll(QSale.sale.createdAt.between(from, DateTime.now())).spliterator(), false)
						.collect(Collectors.groupingBy(s -> new YearMonth(s.getCreatedAt())));

		return result;
	}

}
