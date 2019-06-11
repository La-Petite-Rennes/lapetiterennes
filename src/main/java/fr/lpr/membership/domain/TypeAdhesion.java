package fr.lpr.membership.domain;

// FIXME Prix d'une adhésion soutien ??
public enum TypeAdhesion {
	Simple("Adhésion simple", 2000),
	Famille("Adhésion famille", 4000),
	Soutien("Adhésion soutien", null),
    Mensuelle("Adhésion mensuelle", 0),
    TitreGracieux("Adhésion à titre gracieux", 0),
    CarteSortir("Adhésion carte Sortir !", null);

	private final String label;

	private final Integer price;

	TypeAdhesion(String label, Integer price) {
		this.label = label;
		this.price = price;
	}

	public String getLabel() {
		return label;
	}

	public Integer getPrice() {
		return price;
	}
}
