package fr.lpr.membership.domain;

import fr.lpr.membership.domain.sale.PaymentType;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Adhesion.
 */
@Entity
@Indexed
@Table(name = "ADHESION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Adhesion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAdhesion typeAdhesion;

    @Column(nullable = false)
    @Field(name = "lastAdhesion", analyze = Analyze.NO)
    @DateBridge(resolution = Resolution.DAY)
    private LocalDate dateAdhesion;

    @ManyToOne
    @ContainedIn
    private Adherent adherent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    private Integer price;

    public LocalDate getDateFinAdhesion() {
        if (typeAdhesion != TypeAdhesion.Mensuelle) {
            return dateAdhesion.plusYears(1);
        } else {
            return dateAdhesion.plusMonths(1);
        }
    }
}
