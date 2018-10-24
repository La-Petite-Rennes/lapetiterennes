package fr.lpr.membership.domain;

// FIXME Prix d'une adhésion soutien ??
public enum TypeAdhesion {
	Simple("Adhésion simple", 2000),
	Famille("Adhésion famille", 4000),
	Soutien("Adhésion soutien", 0),
    Mensuelle("Adhésion mensuelle", 0);

	private final String label;

	private final int price;

	TypeAdhesion(String label, int price) {
		this.label = label;
		this.price = price;
	}

	public String getLabel() {
		return label;
	}

	public int getPrice() {
		return price;
	}

}
