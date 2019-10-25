package fr.lpr.membership.domain;

import fr.lpr.membership.domain.sale.PaymentType;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Adhesion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_adhesion", nullable = false)
    private TypeAdhesion typeAdhesion;

    @Column(name = "date_adhesion", nullable = false)
    @Field(name = "lastAdhesion", analyze = Analyze.NO)
    private LocalDate dateAdhesion;

    @ManyToOne
    @ContainedIn
    private Adherent adherent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    private Integer price;

    public Adhesion id(Long id) {
        setId(id);
        return this;
    }

    public LocalDate getDateFinAdhesion() {
        if (typeAdhesion != TypeAdhesion.Mensuelle) {
            return dateAdhesion.plusYears(1);
        } else {
            return dateAdhesion.plusMonths(1);
        }
    }
}
