package fr.lpr.membership.web.rest.dto;

import org.joda.time.DateTime;

public class StockHistoryDTO {

	private String event;

	private DateTime createdAt;

	private int quantity;

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
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
