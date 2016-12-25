package fr.lpr.membership.service.sale;

import org.joda.time.LocalDate;

import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.sale.PaymentType;

public interface SalableItem {

	LocalDate getSaleDate();

	String getName();

	int getQuantity();

	int getUnitPrice();

	default int getTotalPrice() {
		return getQuantity() * getUnitPrice();
	}

	PaymentType getPaymentType();

	Adherent getAdherent();

}
