package fr.lpr.membership.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.lpr.membership.domain.sale.PaymentType;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SaleDTO {

	private Long id;

	@NotNull
	private LocalDateTime date;

	@NotNull
	private Long adherentId;

	private String adherentFullName;

	@NotNull
	private PaymentType paymentType;

	@NotEmpty
	@JsonProperty("items")
	private List<SoldItemDTO> soldItems;

	private boolean finished;
}
