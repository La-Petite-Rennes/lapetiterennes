package fr.lpr.membership.web.rest.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fr.lpr.membership.domain.sale.PaymentType;
import fr.lpr.membership.domain.util.CustomLocalDateDeserializer;
import fr.lpr.membership.domain.util.CustomLocalDateSerializer;

public class SaleDTO {

	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = CustomLocalDateDeserializer.class)
	@NotNull
	private LocalDateTime date;

	@NotNull
	private Long adherentId;

	@NotNull
	private PaymentType paymentType;

	@NotEmpty
	@JsonProperty("items")
	private List<SoldItemDTO> soldItems;

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public Long getAdherentId() {
		return adherentId;
	}

	public void setAdherentId(Long adherentId) {
		this.adherentId = adherentId;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public List<SoldItemDTO> getSoldItems() {
		return soldItems;
	}

	public void setSoldItems(List<SoldItemDTO> soldItems) {
		this.soldItems = soldItems;
	}

}
