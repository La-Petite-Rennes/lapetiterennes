package fr.lpr.membership.domain.stock;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StockEvent {

	REASSORT("Réassort"),
	SALE("Vente"),
	FOR_REPAIRING("Pour Réparation");

	private final String label;
}
