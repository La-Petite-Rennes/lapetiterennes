package fr.lpr.membership.service.sale;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.joda.time.DateTime;
import org.joda.time.YearMonth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.lpr.membership.domain.QAdhesion;
import fr.lpr.membership.domain.sale.QSale;
import fr.lpr.membership.repository.AdhesionRepository;
import fr.lpr.membership.repository.sale.SaleRepository;

@Service
@Transactional(readOnly = true)
public class SaleStatisticsService {

	@Autowired
	private SaleRepository saleRepository;

	@Autowired
	private AdhesionRepository adhesionRepository;

	public SaleStatistics<YearMonth> last12MonthsSales() {
		// Select sales from
		DateTime from = DateTime.now().minusMonths(12).withDayOfMonth(1).withTimeAtStartOfDay();

		Map<YearMonth, List<SalableItem>> soldItemsByMonth =
				StreamSupport
						.stream(saleRepository.findAll(QSale.sale.createdAt.between(from, DateTime.now())).spliterator(), false)
						.flatMap(s -> s.getSoldItems().stream())
						.map(SoldItemProxy::new)
						.collect(Collectors.groupingBy(item -> new YearMonth(item.getSaleDate())));

		Map<YearMonth, List<SalableItem>> adhesionsByMonth =
				StreamSupport
						.stream(adhesionRepository.findAll(
								QAdhesion.adhesion.dateAdhesion.between(from.toLocalDate(), DateTime.now().toLocalDate()))
								.spliterator(), false)
						.map(AdhesionProxy::new)
						.collect(Collectors.groupingBy(a -> new YearMonth(a.getSaleDate())));

		SaleStatistics<YearMonth> itemsByMonth = new SaleStatistics<>();
		itemsByMonth.addItems(soldItemsByMonth);
		itemsByMonth.addItems(adhesionsByMonth);
		return itemsByMonth;
	}

	public void printByMonthAndArticles() {
		SaleStatistics<YearMonth> sales = last12MonthsSales();
		for (YearMonth month : sales.getItemsByPeriod().keySet()) {
			Collection<SalableItem> items = sales.getItemsByPeriod().get(month);
			Map<String, List<SalableItem>> collect = items.stream().collect(Collectors.groupingBy(item -> item.getName()));
		}
	}

}
