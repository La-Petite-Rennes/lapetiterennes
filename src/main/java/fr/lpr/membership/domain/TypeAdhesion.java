package fr.lpr.membership.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

// FIXME Prix d'une adhésion soutien ??
@Getter
@AllArgsConstructor
public enum TypeAdhesion {
	Simple("Adhésion simple", 2000),
	Famille("Adhésion famille", 4000),
	Soutien("Adhésion soutien", null),
    Mensuelle("Adhésion mensuelle", 0),
    TitreGracieux("Adhésion à titre gracieux", 0),
    CarteSortir("Adhésion carte Sortir !", null);

	private final String label;

	private final Integer price;
}
