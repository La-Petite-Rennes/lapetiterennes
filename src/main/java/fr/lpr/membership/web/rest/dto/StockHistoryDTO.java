package fr.lpr.membership.web.rest.dto;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDateTime;

@Getter
@Setter
public class StockHistoryDTO {

	private String event;

	private LocalDateTime createdAt;

	private int quantity;
}
