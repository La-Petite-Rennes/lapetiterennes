package fr.lpr.membership.domain.sale;

import fr.lpr.membership.domain.Article;
import fr.lpr.membership.domain.stock.StockHistory;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table(name="SOLD_ITEM")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SoldItem {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	private Sale sale;

	@OneToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.EAGER)
	private StockHistory stockHistory;

	@Column(nullable = false)
	private int price;

	public SoldItem(Article article, int quantity) {
		if (article.getSalePrice() == null) {
			// FIXME Throws Exception
		}

		this.stockHistory = StockHistory.forSale(article, quantity);
		this.price = article.getSalePrice();
	}

	public SoldItem(Article article, int quantity, int price) {
		if (article.getSalePrice() != null && price < article.getSalePrice()) {
			// FIXME Throws Exception
		}

		this.stockHistory = StockHistory.forSale(article, quantity);
		this.price = price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	public SoldItem sale(Sale sale) {
		setSale(sale);
		return this;
	}

	public StockHistory getStockHistory() {
		return stockHistory;
	}

	protected void setStockHistory(StockHistory stockHistory) {
		this.stockHistory = stockHistory;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public SoldItem price(int price) {
		setPrice(price);
		return this;
	}

	public Article getArticle() {
		return stockHistory.getArticle();
	}

	public int getQuantity() {
		return stockHistory.getQuantity();
	}

	public int changeQuantity(int quantity) {
		int previousQuantity = stockHistory.getQuantity();
		stockHistory.setQuantity(quantity);
		return quantity - previousQuantity;
	}
}
