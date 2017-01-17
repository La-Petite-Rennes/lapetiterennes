package fr.lpr.membership.service.sale.event;

import fr.lpr.membership.domain.sale.Sale;

public abstract class AbstractSaleEvent {

	private final Sale sale;

	public AbstractSaleEvent(Sale sale) {
		super();
		this.sale = sale;
	}

	public Sale getSale() {
		return sale;
	}

}
