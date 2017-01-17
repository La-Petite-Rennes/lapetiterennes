package fr.lpr.membership.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import fr.lpr.membership.service.sale.event.SaleDeletedEvent;

@Component
public class SaleDeletedListener {

	@Autowired
	private SimpMessagingTemplate template;

	@TransactionalEventListener
	public void handleSaleDeleted(SaleDeletedEvent event) {
		this.template.convertAndSend("/topic/deletedSale", event.getSale().getId());
	}

}
