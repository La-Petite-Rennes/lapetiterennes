package fr.lpr.membership.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import fr.lpr.membership.domain.sale.Sale;
import fr.lpr.membership.service.sale.event.AbstractSaleEvent;
import fr.lpr.membership.web.rest.dto.SaleDTO;
import fr.lpr.membership.web.rest.dto.mapper.SaleMapper;

@Component
public class SaleModificationListener {

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private SaleMapper saleMapper;

	@TransactionalEventListener
	public void handleSaleEvent(AbstractSaleEvent event) {
		Sale sale = event.getSale();

		if (!sale.isFinished()) {
			SaleDTO dto = saleMapper.saleToSaleDto(sale);
			this.template.convertAndSend("/topic/temporarySales", dto);
		}
	}

}
