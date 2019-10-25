package fr.lpr.membership.domain.sale;

import fr.lpr.membership.domain.Article;
import fr.lpr.membership.domain.stock.StockHistory;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table(name="SOLD_ITEM")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
public class SoldItem {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@ManyToOne(optional = false)
	private Sale sale;

	@OneToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.EAGER)
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
