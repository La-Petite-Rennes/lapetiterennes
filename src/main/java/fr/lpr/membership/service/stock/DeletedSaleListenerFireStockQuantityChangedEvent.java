package fr.lpr.membership.service.stock;

import static fr.lpr.membership.service.stock.StockQuantityChangedEvent.deletedSale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import fr.lpr.membership.domain.sale.Sale;
import fr.lpr.membership.service.sale.event.SaleDeletedEvent;

@Component
public class DeletedSaleListenerFireStockQuantityChangedEvent {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@EventListener
	public void handleSaleDeletedEvent(SaleDeletedEvent event) {
		Sale sale = event.getSale();
		sale.getSoldItems().forEach(item ->
				eventPublisher.publishEvent(deletedSale(item.getArticle(), item.getQuantity())));
	}

}
