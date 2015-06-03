package fr.lpr.membership.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fr.lpr.membership.domain.util.CustomLocalDateSerializer;

/**
 * A Adherent.
 */
@Entity
@Table(name = "ADHERENT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@JsonAutoDetect(getterVisibility = Visibility.PUBLIC_ONLY)
public class Adherent implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Column(name = "prenom", nullable = false)
	private String prenom;

	@NotNull
	@Column(name = "nom", nullable = false)
	private String nom;

	@Column(name = "benevole")
	private Boolean benevole;

	@Column(name = "remarque_benevolat")
	private String remarqueBenevolat;

	@Column(name = "genre")
	@Enumerated(EnumType.STRING)
	private Genre genre;

	@Column(name = "autre_remarque")
	private String autreRemarque;

	@OneToOne(cascade = CascadeType.ALL)
	private Coordonnees coordonnees;

	@OneToMany(mappedBy = "adherent", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	// @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private final Set<Adhesion> adhesions = new TreeSet<>((a1, a2) -> a1.getDateAdhesion().compareTo(a2.getDateAdhesion()));

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Boolean getBenevole() {
		return benevole;
	}

	public void setBenevole(Boolean benevole) {
		this.benevole = benevole;
	}

	public String getRemarqueBenevolat() {
		return remarqueBenevolat;
	}

	public void setRemarqueBenevolat(String remarqueBenevolat) {
		this.remarqueBenevolat = remarqueBenevolat;
	}

	public Genre getGenre() {
		return genre;
	}

	public void setGenre(Genre genre) {
		this.genre = genre;
	}

	public String getAutreRemarque() {
		return autreRemarque;
	}

	public void setAutreRemarque(String autreRemarque) {
		this.autreRemarque = autreRemarque;
	}

	public Coordonnees getCoordonnees() {
		return coordonnees;
	}

	public void setCoordonnees(Coordonnees coordonnees) {
		this.coordonnees = coordonnees;
	}

	@JsonIgnore
	public Set<Adhesion> getAdhesions() {
		return adhesions;
	}

	@JsonProperty
	public void setAdhesions(Set<Adhesion> adhesions) {
		this.adhesions.clear();
		this.adhesions.addAll(adhesions);
		this.adhesions.forEach(a -> a.setAdherent(this));
	}

	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonProperty
	public LocalDate getLastAdhesion() {
		return lastAdhesion().map(ad -> ad.getDateAdhesion()).orElse(null);
	}

	@JsonIgnore
	private Optional<Adhesion> lastAdhesion() {
		if (this.adhesions.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(this.adhesions.iterator().next());
		}
	}

	@JsonProperty
	public StatutAdhesion getStatutAdhesion() {
		final Optional<Adhesion> lastAdhesion = lastAdhesion();

		if (!lastAdhesion.isPresent()) {
			return StatutAdhesion.NONE;
		} else if (lastAdhesion.get().getDateFinAdhesion().isBefore(LocalDate.now())) {
			return StatutAdhesion.RED;
		} else if (lastAdhesion.get().getDateFinAdhesion().isBefore(LocalDate.now().plusMonths(1))) {
			return StatutAdhesion.ORANGE;
		} else {
			return StatutAdhesion.GREEN;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final Adherent adherent = (Adherent) o;

		if (!Objects.equals(id, adherent.id)) {
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
		return "Adherent{" + "id=" + id + ", prenom='" + prenom + "'" + ", nom='" + nom + "'" + ", benevole='" + benevole + "'" + ", remarqueBenevolat='"
				+ remarqueBenevolat + "'" + ", genre='" + genre + "'" + ", autreRemarque='" + autreRemarque + "'" + '}';
	}
}
