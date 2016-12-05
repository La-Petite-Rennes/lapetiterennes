package fr.lpr.membership.web.rest.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SoldItemDTO {

	@NotNull
	private Long id;

	@NotNull
	@Min(1)
	private Integer quantity;

	@NotNull
	@Min(1)
	private Integer price;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

}
