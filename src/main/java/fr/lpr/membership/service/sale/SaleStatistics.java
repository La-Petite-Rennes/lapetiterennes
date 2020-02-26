package fr.lpr.membership.service.sale;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import java.util.List;
import java.util.Map;

public class SaleStatistics<T extends Comparable<?>> {

	private final Multimap<T, SalableItem> itemsByPeriod;

	public SaleStatistics() {
		this.itemsByPeriod = TreeMultimap.create();
	}

	public Multimap<T, SalableItem> getItemsByPeriod() {
		return itemsByPeriod;
	}

	public void addItems(Map<T, List<SalableItem>> soldItemsByMonth) {
		soldItemsByMonth.entrySet().forEach(e -> itemsByPeriod.putAll(e.getKey(), e.getValue()));
	}

}
