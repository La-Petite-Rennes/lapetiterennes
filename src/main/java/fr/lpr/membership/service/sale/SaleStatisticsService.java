package fr.lpr.membership.service.sale;

import com.querydsl.core.BooleanBuilder;
import fr.lpr.membership.domain.QAdhesion;
import fr.lpr.membership.domain.sale.QSale;
import fr.lpr.membership.repository.AdhesionRepository;
import fr.lpr.membership.repository.sale.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SaleStatisticsService {

	private final SaleRepository saleRepository;

	private final AdhesionRepository adhesionRepository;

	public SaleStatistics<YearMonth> statsByMonths(LocalDateTime from) {
        LocalDateTime to = from.plusYears(1);

		// Search finished sales created after 'from' date
		BooleanBuilder salePredicate = new BooleanBuilder();
		salePredicate.and(QSale.sale.createdAt.between(from, to));
		salePredicate.and(QSale.sale.finished.isTrue());

		Map<YearMonth, List<SalableItem>> soldItemsByMonth =
				StreamSupport
						.stream(saleRepository.findAll(salePredicate).spliterator(), false)
						.flatMap(s -> s.getSoldItems().stream())
						.map(SoldItemProxy::new)
						.collect(Collectors.groupingBy(item -> YearMonth.from(item.getSaleDate())));

		// Search adhesions created after 'from' date
		Map<YearMonth, List<SalableItem>> adhesionsByMonth =
				StreamSupport
						.stream(adhesionRepository.findAll(
								QAdhesion.adhesion.dateAdhesion.between(from.toLocalDate(), to.toLocalDate()))
								.spliterator(), false)
						.map(AdhesionProxy::new)
						.collect(Collectors.groupingBy(a -> YearMonth.from(a.getSaleDate())));

		SaleStatistics<YearMonth> itemsByMonth = new SaleStatistics<>();
		itemsByMonth.addItems(soldItemsByMonth);
		itemsByMonth.addItems(adhesionsByMonth);
		return itemsByMonth;
	}

}
