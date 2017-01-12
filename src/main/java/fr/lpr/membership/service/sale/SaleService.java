package fr.lpr.membership.service.sale;

import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;

import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.sale.QSale;
import fr.lpr.membership.domain.sale.Sale;
import fr.lpr.membership.repository.sale.SaleRepository;
import fr.lpr.membership.service.sale.event.SaleSavedEvent;
import fr.lpr.membership.web.rest.util.PaginationUtil;

@Service
@Transactional
public class SaleService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SaleService.class);

	@Autowired
	private SaleRepository saleRepository;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	/**
	 * Save a new sale.
	 *
	 * @param sale
	 * @return
	 */
	public Sale newSale(Sale sale) {
		Sale savedSale = null;

		Sale existingSale = findTemporarySaleForMember(sale.getAdherent());

		// If the sale is temporary and another one already exists for the same member, merge the sales
		if (!sale.isFinished() && existingSale != null) {
			sale.getSoldItems().forEach(item -> existingSale.addSoldItem(item.getArticle(), item.getQuantity(), item.getPrice()));
			savedSale = update(existingSale);
		}
		// Otherwise, create a new one
		else {
			sale.updatedAt(sale.getCreatedAt());
			savedSale = save(sale);
		}

		LOGGER.info("Enregistrement d'une vente pour {} : \n\t\t{}", sale.getAdherent().getFullName(),
				sale.getSoldItems().stream().map(item -> item.getQuantity() + " * " + item.getArticle().getName()).collect(Collectors.joining(", \n\t\t")));

		return savedSale;
	}

	/**
	 * Update a sale.
	 *
	 * @param sale
	 * @return
	 */
	public Sale update(Sale sale) {
		sale.updatedAt(DateTime.now());
		return save(sale);
	}

	private Sale save(Sale sale) {
		Sale savedSale = saleRepository.save(sale);
		eventPublisher.publishEvent(new SaleSavedEvent(savedSale));
		return savedSale;
	}

	private Sale findTemporarySaleForMember(Adherent adherent) {
		QSale sale = QSale.sale;

		BooleanBuilder predicate = new BooleanBuilder();
		predicate.and(sale.adherent.id.eq(adherent.getId()));
		predicate.and(sale.finished.isFalse());

		return saleRepository.findOne(predicate);
	}

	public Page<Sale> history(Integer offset, Integer limit) {
		final Sort sort = new Sort(Direction.DESC, "createdAt");
		final Pageable pageRequest = PaginationUtil.generatePageRequest(offset, limit, sort);

		return saleRepository.findAll(pageRequest);
	}

}
