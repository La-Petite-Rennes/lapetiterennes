package fr.lpr.membership.domain.stock;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class Reassort {

	@NotNull
	private Long id;

	@NotNull
	private Integer quantity;
}
