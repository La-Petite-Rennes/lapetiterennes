package fr.lpr.membership.domain.stock;

import javax.validation.constraints.NotNull;

public class Reassort {

	@NotNull
	private Long id;

	@NotNull
	private Integer quantity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Reassort id(Long id) {
		setId(id);
		return this;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Reassort quantity(Integer quantity) {
		setQuantity(quantity);
		return this;
	}

}
