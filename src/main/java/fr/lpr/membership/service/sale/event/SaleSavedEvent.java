package fr.lpr.membership.service.sale.event;

import fr.lpr.membership.domain.sale.Sale;

public class SaleSavedEvent {

	private final Sale sale;

	public SaleSavedEvent(Sale sale) {
		super();
		this.sale = sale;
	}

	public Sale getSale() {
		return sale;
	}

}
