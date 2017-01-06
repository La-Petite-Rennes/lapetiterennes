package fr.lpr.membership.service.sale;

import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;

import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.sale.QSale;
import fr.lpr.membership.domain.sale.Sale;
import fr.lpr.membership.repository.sale.SaleRepository;
import fr.lpr.membership.service.sale.event.SaleSavedEvent;

@Service
@Transactional
public class SaleService {

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
		// If the sale is temporary and another one already exists for the same member, merge the sales
		if (!sale.isFinished()) {
			Sale existingSale = findTemporarySaleForMember(sale.getAdherent());
			sale.getSoldItems().forEach(item -> existingSale.addSoldItem(item.getArticle(), item.getQuantity(), item.getPrice()));
			return update(existingSale);
		}
		// Otherwise, create a new one
		else {
			sale.updatedAt(sale.getCreatedAt());
			return save(sale);
		}
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

}
