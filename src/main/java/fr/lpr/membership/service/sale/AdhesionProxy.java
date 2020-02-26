package fr.lpr.membership.service.sale;

import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.Adhesion;
import fr.lpr.membership.domain.sale.PaymentType;

import java.time.LocalDate;
import java.util.Optional;

public class AdhesionProxy implements SalableItem {

	private final Adhesion adhesion;

	public AdhesionProxy(Adhesion adhesion) {
		this.adhesion = adhesion;
	}

	@Override
	public Long getId() {
		return adhesion.getId();
	}

	@Override
	public LocalDate getSaleDate() {
		return adhesion.getDateAdhesion();
	}

	@Override
	public String getName() {
		return adhesion.getTypeAdhesion().getLabel();
	}

	@Override
	public int getQuantity() {
		return 1;
	}

	@Override
	public int getUnitPrice() {
        return Optional.ofNullable(adhesion.getTypeAdhesion().getPrice())
            .orElse(adhesion.getPrice());
	}

	@Override
	public PaymentType getPaymentType() {
		return adhesion.getPaymentType();
	}

	@Override
	public Adherent getAdherent() {
		return adhesion.getAdherent();
	}

}
