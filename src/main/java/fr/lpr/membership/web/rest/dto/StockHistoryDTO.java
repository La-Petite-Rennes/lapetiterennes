package fr.lpr.membership.web.rest.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockHistoryDTO {

	private String event;

	private LocalDateTime createdAt;

	private int quantity;
}
