package fr.lpr.membership.service.sale;

import org.joda.time.LocalDate;

import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.sale.PaymentType;
import fr.lpr.membership.domain.sale.SoldItem;

public class SoldItemProxy implements SalableItem {

	private final SoldItem soldItem;

	public SoldItemProxy(SoldItem soldItem) {
		this.soldItem = soldItem;
	}

	@Override
	public Long getId() {
		return soldItem.getId();
	}

	@Override
	public LocalDate getSaleDate() {
		return soldItem.getSale().getCreatedAt().toLocalDate();
	}

	@Override
	public String getName() {
		return soldItem.getStockHistory().getArticle().getName();
	}

	@Override
	public int getQuantity() {
		return soldItem.getStockHistory().getQuantity();
	}

	@Override
	public int getUnitPrice() {
		return soldItem.getPrice();
	}

	@Override
	public PaymentType getPaymentType() {
		return soldItem.getSale().getPaymentType();
	}

	@Override
	public Adherent getAdherent() {
		return soldItem.getSale().getAdherent();
	}

}
