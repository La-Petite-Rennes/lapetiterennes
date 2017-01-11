package fr.lpr.membership.domain.stock;

public enum StockEvent {

	REASSORT("Réassort"),
	SALE("Vente"),
	FOR_REPAIRING("Pour Réparation");

	private final String label;

	private StockEvent(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
