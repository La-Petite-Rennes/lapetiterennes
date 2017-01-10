package fr.lpr.membership.service.stock;

import static fr.lpr.membership.service.stock.StockQuantityChangedEvent.fromSale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import fr.lpr.membership.domain.sale.Sale;
import fr.lpr.membership.service.sale.event.SaleSavedEvent;

@Component
public class SavedSaleListenerFireStockQuantityChangedEvent {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@EventListener
	public void handleSaleSavedEvent(SaleSavedEvent event) {
		Sale sale = event.getSale();
		sale.getSoldItems().forEach(item -> eventPublisher.publishEvent(fromSale(item.getArticle(), item.getQuantity())));
	}

}
