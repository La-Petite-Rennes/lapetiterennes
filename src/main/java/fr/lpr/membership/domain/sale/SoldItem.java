package fr.lpr.membership.domain.sale;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import fr.lpr.membership.domain.Article;
import fr.lpr.membership.domain.stock.StockHistory;

@Entity
@Table(name="SOLD_ITEM")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SoldItem {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@ManyToOne(optional = false)
	private Sale sale;

	@OneToOne(cascade = CascadeType.PERSIST, optional = false, fetch = FetchType.EAGER)
	private StockHistory stockHistory;

	@Column(nullable = false)
	private int price;

	protected SoldItem() {
		// Do nothing
	}

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

}
