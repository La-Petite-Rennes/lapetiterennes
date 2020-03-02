package fr.lpr.membership.domain;

// FIXME Prix d'une adhésion soutien ??
public enum TypeAdhesion {
	Simple("Adhésion simple", 2000, 12),
	Famille("Adhésion famille", 4000, 12),
	Soutien("Adhésion soutien", null, 12),
    TroisMois("Adhésion 3 mois", 0, 3),
    SixMoisVSE("Adhésion VSE 6 mois", 0, 6),
    TitreGracieux("Adhésion à titre gracieux", 0, 12),
    CarteSortir("Adhésion carte Sortir !", null, 12);

	private final String label;

	private final Integer price;

	// Durée de l'adhésion en mois
	private final int duree;

	TypeAdhesion(String label, Integer price, int duree) {
		this.label = label;
		this.price = price;
		this.duree = duree;
	}

	public String getLabel() {
		return label;
	}

	public Integer getPrice() {
		return price;
	}

    public int getDuree() {
        return duree;
    }
}
