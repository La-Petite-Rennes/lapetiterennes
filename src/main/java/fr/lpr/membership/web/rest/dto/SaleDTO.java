package fr.lpr.membership.web.rest.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fr.lpr.membership.domain.sale.PaymentType;
import fr.lpr.membership.domain.util.CustomDateTimeDeserializer;
import fr.lpr.membership.domain.util.CustomDateTimeSerializer;

public class SaleDTO {

	private Long id;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	@NotNull
	private DateTime date;

	@NotNull
	private Long adherentId;

	private String adherentFullName;

	@NotNull
	private PaymentType paymentType;

	@NotEmpty
	@JsonProperty("items")
	private List<SoldItemDTO> soldItems;

	private boolean finished;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public Long getAdherentId() {
		return adherentId;
	}

	public void setAdherentId(Long adherentId) {
		this.adherentId = adherentId;
	}

	public String getAdherentFullName() {
		return adherentFullName;
	}

	public void setAdherentFullName(String adherentFullName) {
		this.adherentFullName = adherentFullName;
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

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

}
