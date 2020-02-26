package fr.lpr.membership.domain.sale;

import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.Article;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="SALE")
@EntityListeners(AuditingEntityListener.class)
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Sale {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	private Adherent adherent;

	@Enumerated(EnumType.STRING)
	@NotNull
	private PaymentType paymentType;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "sale", fetch = FetchType.EAGER)
	private List<SoldItem> soldItems;

	@Column(nullable = false, updatable = false)
    @CreatedDate
	private LocalDateTime createdAt;

    @LastModifiedDate
	private LocalDateTime updatedAt;

	@Column(nullable = false)
	private boolean finished;

	public void addSoldItem(Article article, int quantity, int price) {
        if (soldItems == null) {
            soldItems = new ArrayList<>();
        }

		SoldItem soldItem = new SoldItem(article, quantity, price);
		soldItem.setSale(this);
		this.soldItems.add(soldItem);
	}
}
