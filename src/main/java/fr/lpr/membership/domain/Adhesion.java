package fr.lpr.membership.domain;

import java.io.Serializable;
import java.util.Objects;

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
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fr.lpr.membership.domain.util.CustomLocalDateDeserializer;
import fr.lpr.membership.domain.util.CustomLocalDateSerializer;
import fr.lpr.membership.domain.util.LocalDateBridge;

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TypeAdhesion getTypeAdhesion() {
		return typeAdhesion;
	}

	public void setTypeAdhesion(TypeAdhesion typeAdhesion) {
		this.typeAdhesion = typeAdhesion;
	}

	public LocalDate getDateAdhesion() {
		return dateAdhesion;
	}

	@JsonSerialize(using = CustomLocalDateSerializer.class)
	public LocalDate getDateFinAdhesion() {
		return dateAdhesion.plusYears(1);
	}

	public void setDateAdhesion(LocalDate dateAdhesion) {
		this.dateAdhesion = dateAdhesion;
	}

	public Adherent getAdherent() {
		return adherent;
	}

	public void setAdherent(Adherent adherent) {
		this.adherent = adherent;
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
