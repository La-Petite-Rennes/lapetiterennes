package fr.lpr.membership.domain.stock;

import fr.lpr.membership.domain.Article;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "STOCK_HISTORY")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class StockHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StockEvent event;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne(optional = false)
    private Article article;

    public static StockHistory from(Reassort reassort, Article article) {
        return StockHistory.builder().event(StockEvent.REASSORT).quantity(reassort.getQuantity()).article(article).build();
    }

    public static StockHistory forSale(Article article, int quantity) {
        return StockHistory.builder().event(StockEvent.SALE).quantity(quantity).article(article).build();
    }

    public static StockHistory forRepairing(Article article) {
        return StockHistory.builder().event(StockEvent.FOR_REPAIRING).quantity(1).article(article).build();
    }

    protected StockHistory() {
        this.createdAt = LocalDateTime.now();
    }
}
