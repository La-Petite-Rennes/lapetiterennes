package fr.lpr.membership.websocket;

import fr.lpr.membership.service.sale.event.SaleDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SaleDeletedListener {

	private final SimpMessagingTemplate template;

	@TransactionalEventListener
	public void handleSaleDeleted(SaleDeletedEvent event) {
		this.template.convertAndSend("/topic/deletedSale", event.getSale().getId());
	}

}
