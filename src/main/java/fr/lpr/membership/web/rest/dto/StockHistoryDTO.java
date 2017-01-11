package fr.lpr.membership.web.rest.dto;

import org.joda.time.DateTime;

import fr.lpr.membership.domain.stock.StockEvent;

public class StockHistoryDTO {

	private StockEvent event;

	private DateTime createdAt;

	private int quantity;

	public StockEvent getEvent() {
		return event;
	}

	public void setEvent(StockEvent event) {
		this.event = event;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
