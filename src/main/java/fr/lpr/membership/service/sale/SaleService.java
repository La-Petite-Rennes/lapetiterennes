package fr.lpr.membership.service.sale;

import java.util.List;
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

import com.google.common.collect.Lists;

import fr.lpr.membership.domain.sale.QSale;
import fr.lpr.membership.domain.sale.Sale;
import fr.lpr.membership.domain.sale.SoldItem;
import fr.lpr.membership.repository.sale.SaleRepository;
import fr.lpr.membership.service.sale.event.SaleSavedEvent;
import fr.lpr.membership.service.sale.event.SaleUpdatedEvent;
import fr.lpr.membership.service.stock.StockQuantityChangedEvent;
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
		sale.updatedAt(sale.getCreatedAt());
		Sale savedSale = save(sale);

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
		Sale existingSale = saleRepository.findOne(sale.getId());
		if (existingSale == null) {
			// FIXME Exception
			return null;
		}

		// TODO Code à placer dans l'entité Sale ?
		for (SoldItem item : sale.getSoldItems()) {
			// Add new sold item to the sale
			if (item.getId() == null) {
				existingSale.getSoldItems().add(item);
				eventPublisher.publishEvent(StockQuantityChangedEvent.fromSale(item.getArticle(), item.getQuantity()));
			} else {
				// FIXME Changer le type d'exception
				SoldItem existingItem = existingSale.getSoldItems().stream()
						.filter(i -> i.getId().equals(item.getId()))
						.findFirst()
						.orElseThrow(() -> new RuntimeException());

				if (existingItem.getPrice() != item.getPrice()) {
					existingSale.addSoldItem(item.getArticle(), item.getQuantity(), item.getPrice());
					eventPublisher.publishEvent(StockQuantityChangedEvent.fromSale(item.getArticle(), item.getQuantity()));
				} else {
					int quantityDiff = existingItem.changeQuantity(item.getQuantity());
					eventPublisher.publishEvent(StockQuantityChangedEvent.fromSale(item.getArticle(), quantityDiff));
				}
			}
		}

		existingSale.updatedAt(DateTime.now());
		eventPublisher.publishEvent(new SaleUpdatedEvent(existingSale));
		return saleRepository.save(existingSale);
	}

	private Sale save(Sale sale) {
		Sale savedSale = saleRepository.save(sale);
		eventPublisher.publishEvent(new SaleSavedEvent(savedSale));
		return savedSale;
	}

	public Page<Sale> history(Integer offset, Integer limit) {
		final Sort sort = new Sort(Direction.DESC, "createdAt");
		final Pageable pageRequest = PaginationUtil.generatePageRequest(offset, limit, sort);

		return saleRepository.findAll(pageRequest);
	}

	public List<Sale> getTemporarySales() {
		return Lists.newArrayList(saleRepository.findAll(QSale.sale.finished.isFalse()));
	}

}
