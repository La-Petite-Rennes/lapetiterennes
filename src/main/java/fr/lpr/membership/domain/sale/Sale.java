package fr.lpr.membership.domain.sale;

import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.Article;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SALE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    private Adherent adherent;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentType paymentType;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sale", fetch = FetchType.EAGER)
    private List<SoldItem> soldItems;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean finished;

    public Sale() {
        this.soldItems = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void setSoldItems(List<SoldItem> soldItems) {
        this.soldItems = soldItems;
        this.soldItems.forEach(item -> item.setSale(this));
    }

    public void addSoldItem(Article article, int quantity, int price) {
        SoldItem soldItem = new SoldItem(article, quantity, price);
        soldItem.setSale(this);
        this.soldItems.add(soldItem);
    }
}
