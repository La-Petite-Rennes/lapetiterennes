package fr.lpr.membership.service.sale.event;

import fr.lpr.membership.domain.sale.Sale;

public class SaleCreatedEvent extends AbstractSaleEvent {

	public SaleCreatedEvent(Sale sale) {
		super(sale);
	}

}
