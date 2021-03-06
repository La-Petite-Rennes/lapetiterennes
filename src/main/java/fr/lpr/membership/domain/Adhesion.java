package fr.lpr.membership.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.lpr.membership.domain.sale.PaymentType;
import fr.lpr.membership.domain.util.CustomLocalDateDeserializer;
import fr.lpr.membership.domain.util.CustomLocalDateSerializer;
import fr.lpr.membership.domain.util.LocalDateBridge;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.*;
import org.joda.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Adhesion.
 */
@Entity
@Indexed
@Table(name = "ADHESION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Adhesion implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "type_adhesion", nullable = false)
	private TypeAdhesion typeAdhesion;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = CustomLocalDateDeserializer.class)
	@Column(name = "date_adhesion", nullable = false)
	@Field(name = "lastAdhesion", analyze = Analyze.NO)
	@FieldBridge(impl = LocalDateBridge.class)
	private LocalDate dateAdhesion;

	@ManyToOne
	@ContainedIn
	private Adherent adherent;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentType paymentType;

	private Integer price;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Adhesion id(Long id) {
		setId(id);
		return this;
	}

	public TypeAdhesion getTypeAdhesion() {
		return typeAdhesion;
	}

	public void setTypeAdhesion(TypeAdhesion typeAdhesion) {
		this.typeAdhesion = typeAdhesion;
	}

	public Adhesion typeAdhesion(TypeAdhesion typeAdhesion) {
		setTypeAdhesion(typeAdhesion);
		return this;
	}

	public LocalDate getDateAdhesion() {
		return dateAdhesion;
	}

	@JsonSerialize(using = CustomLocalDateSerializer.class)
	public LocalDate getDateFinAdhesion() {
        return dateAdhesion.plusMonths(typeAdhesion.getDuree());
	}

	public void setDateAdhesion(LocalDate dateAdhesion) {
		this.dateAdhesion = dateAdhesion;
	}

	public Adhesion dateAdhesion(LocalDate dateAdhesion) {
		setDateAdhesion(dateAdhesion);
		return this;
	}

	public Adherent getAdherent() {
		return adherent;
	}

	public void setAdherent(Adherent adherent) {
		this.adherent = adherent;
	}

	public Adhesion adherent(Adherent adherent) {
		setAdherent(adherent);
		return this;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public Adhesion paymentType(PaymentType paymentType) {
		setPaymentType(paymentType);
		return this;
	}

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final Adhesion adhesion = (Adhesion) o;

		if (!Objects.equals(id, adhesion.id)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Adhesion{" + "id=" + id + ", typeAdhesion='" + typeAdhesion + "'" + ", dateAdhesion='" + dateAdhesion + "'" + '}';
	}
}
