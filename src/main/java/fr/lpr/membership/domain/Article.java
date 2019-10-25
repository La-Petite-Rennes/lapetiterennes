package fr.lpr.membership.domain;

import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="ARTICLE")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SQLDelete(sql = "UPDATE article SET disabled = true where id = ?")
@Where(clause = "disabled = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Article {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@NotNull
	@Column(nullable=false, unique=true)
	private String name;

	@ManyToOne
	private Provider provider;

	private String reference;

	private int quantity;
	private int stockWarningLevel;

	private Integer salePrice;
	private Integer unitPrice;

	private boolean disabled;

	public boolean isFreePrice() {
		return salePrice == null;
	}
}
