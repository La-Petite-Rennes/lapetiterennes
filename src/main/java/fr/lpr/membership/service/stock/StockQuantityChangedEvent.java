package fr.lpr.membership.service.stock;

import fr.lpr.membership.domain.Article;

public class StockQuantityChangedEvent {

	private final Article article;

	private final int quantity;

	private StockQuantityChangedEvent(Article article, int quantity) {
		this.article = article;
		this.quantity = quantity;
	}

	public static StockQuantityChangedEvent fromReassort(Article article, int quantity) {
		return new StockQuantityChangedEvent(article, quantity);
	}

	public static StockQuantityChangedEvent fromSale(Article article, int quantity) {
		return new StockQuantityChangedEvent(article, quantity * -1);
	}

	public Article getArticle() {
		return article;
	}

	public int getQuantity() {
		return quantity;
	}

}
