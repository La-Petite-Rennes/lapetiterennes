package fr.lpr.membership.service.sale.event;

import fr.lpr.membership.domain.sale.Sale;

public class SaleDeletedEvent extends AbstractSaleEvent {

	public SaleDeletedEvent(Sale sale) {
		super(sale);
	}

}
