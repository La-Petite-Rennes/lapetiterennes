package fr.lpr.membership.domain.sale;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.Article;

@Entity
@Table(name="SALE")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Sale {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@ManyToOne(optional = false)
	private Adherent adherent;

	@Enumerated(EnumType.STRING)
	@NotNull
	private PaymentType paymentType;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "sale", fetch = FetchType.EAGER)
	private List<SoldItem> soldItems;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Column(nullable = false)
	private DateTime createdAt;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Column(nullable = false)
	private DateTime updatedAt;

	@Column(nullable = false)
	private boolean finished;

	public Sale() {
		this.soldItems = new ArrayList<>();
		this.createdAt = DateTime.now();
		this.updatedAt = DateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Sale id(Long id) {
		setId(id);
		return this;
	}

	public Adherent getAdherent() {
		return adherent;
	}

	public void setAdherent(Adherent adherent) {
		this.adherent = adherent;
	}

	public Sale adherent(Adherent adherent) {
		setAdherent(adherent);
		return this;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public Sale paymentType(PaymentType paymentType) {
		setPaymentType(paymentType);
		return this;
	}

	public List<SoldItem> getSoldItems() {
		return soldItems;
	}

	public void setSoldItems(List<SoldItem> soldItems) {
		this.soldItems = soldItems;
		this.soldItems.forEach(item -> item.setSale(this));
	}

	public Sale soldItems(List<SoldItem> soldItems) {
		setSoldItems(soldItems);
		return this;
	}

	public void addSoldItem(Article article, int quantity, int price) {
		SoldItem soldItem = new SoldItem(article, quantity, price);
		soldItem.setSale(this);
		this.soldItems.add(soldItem);
	}

	public void addSoldItem(Article article, int quantity) {
		SoldItem soldItem = new SoldItem(article, quantity);
		soldItem.setSale(this);
		this.soldItems.add(soldItem);
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Sale createdAt(DateTime createdAt) {
		setCreatedAt(createdAt);
		return this;
	}

	public DateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(DateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Sale updatedAt(DateTime updatedAt) {
		setUpdatedAt(updatedAt);
		return this;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public Sale finished(boolean finished) {
		setFinished(finished);
		return this;
	}

}
