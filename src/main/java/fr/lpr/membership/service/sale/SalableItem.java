package fr.lpr.membership.service.sale;

import org.joda.time.LocalDate;

import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.sale.PaymentType;

import javax.validation.constraints.NotNull;

public interface SalableItem extends Comparable<SalableItem> {

	Long getId();

	LocalDate getSaleDate();

	String getName();

	int getQuantity();

	int getUnitPrice();

	default int getTotalPrice() {
		return getQuantity() * getUnitPrice();
	}

	PaymentType getPaymentType();

	Adherent getAdherent();

	@Override
	default int compareTo(@NotNull SalableItem o) {
		if (getClass().equals(o.getClass())) {
			return getId().compareTo(o.getId());
		} else {
			return getClass().getName().compareTo(o.getClass().getName());
		}
	}

}
