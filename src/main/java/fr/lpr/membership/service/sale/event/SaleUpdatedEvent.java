package fr.lpr.membership.service.sale.event;

import fr.lpr.membership.domain.sale.Sale;

public class SaleUpdatedEvent extends AbstractSaleEvent {

	public SaleUpdatedEvent(Sale sale) {
		super(sale);
	}

}
