package fr.lpr.membership.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name="ARTICLE")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Article {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@NotNull
	@Column(nullable=false, unique=true)
	private String name;

	private int quantity;

	private Integer salePrice;

	@ManyToOne
	private Provider provider;

	private Integer unitPrice;

	@Column(nullable=false)
	private String reference;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Article id(Long id) {
		setId(id);
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Article name(String name) {
		setName(name);
		return this;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Article quantity(int quantity) {
		setQuantity(quantity);
		return this;
	}

	public Integer getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Integer salePrice) {
		this.salePrice = salePrice;
	}

	public Article salePrice(Integer salePrice) {
		setSalePrice(salePrice);
		return this;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public Article provider(Provider provider) {
		setProvider(provider);
		return this;
	}

	public Integer getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Integer unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Article unitPrice(Integer unitPrice) {
		setUnitPrice(unitPrice);
		return this;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Article reference(String reference) {
		setReference(reference);
		return this;
	}

	public boolean isFreePrice() {
		return salePrice == null;
	}

}
