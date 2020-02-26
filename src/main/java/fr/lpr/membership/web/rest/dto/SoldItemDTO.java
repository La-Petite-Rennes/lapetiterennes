package fr.lpr.membership.web.rest.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SoldItemDTO {

	private Long id;

	@NotNull
	@Min(1)
	private Integer quantity;

	@NotNull
	@Min(1)
	private Integer price;

	@NotNull
	private Long articleId;

	private String name;
}
