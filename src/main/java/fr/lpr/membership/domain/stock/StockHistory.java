package fr.lpr.membership.domain.stock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import fr.lpr.membership.domain.Article;

@Entity
@Table(name="STOCK_HISTORY")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StockHistory {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Enumerated(EnumType.STRING)
	@NotNull
	private StockEvent event;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private int quantity;

	@ManyToOne(optional = true)
	private Article article;

	@Column(nullable = false)
	private int price;

	public static StockHistory from(Reassort reassort, Article article) {
		return new StockHistory().event(StockEvent.REASSORT).quantity(reassort.getQuantity()).article(article);
	}

	public static StockHistory forSale(Article article, int quantity) {
		return forSale(article, quantity, article.getSalePrice());
	}

	public static StockHistory forSale(Article article, int quantity, int price) {
		return new StockHistory().event(StockEvent.SALE).quantity(quantity).article(article).price(price);
	}

	protected StockHistory() {
		this.createdAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StockHistory id(Long id) {
		setId(id);
		return this;
	}

	public StockEvent getEvent() {
		return event;
	}

	public void setEvent(StockEvent event) {
		this.event = event;
	}

	public StockHistory event(StockEvent event) {
		setEvent(event);
		return this;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public StockHistory createdAt(LocalDateTime createdAt) {
		setCreatedAt(createdAt);
		return this;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public StockHistory quantity(int quantity) {
		setQuantity(quantity);
		return this;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public StockHistory article(Article article) {
		setArticle(article);
		return this;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public StockHistory price(int price) {
		setPrice(price);
		return this;
	}

}
