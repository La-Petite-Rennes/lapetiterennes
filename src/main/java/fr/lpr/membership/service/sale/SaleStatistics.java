package fr.lpr.membership.service.sale;

import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class SaleStatistics<T> {

	private final Multimap<T, SalableItem> itemsByPeriod;

	public SaleStatistics() {
		this.itemsByPeriod = HashMultimap.create();
	}

	public Multimap<T, SalableItem> getItemsByPeriod() {
		return itemsByPeriod;
	}

	public void addItems(Map<T, List<SalableItem>> soldItemsByMonth) {
		soldItemsByMonth.entrySet().stream().forEach(e -> itemsByPeriod.putAll(e.getKey(), e.getValue()));
	}

}
