package fr.lpr.membership.domain.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reassort {

	@NotNull
	private Long id;

	@NotNull
	private Integer quantity;
}
