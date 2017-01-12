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

	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
