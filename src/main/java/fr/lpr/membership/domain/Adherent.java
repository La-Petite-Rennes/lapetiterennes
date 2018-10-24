package fr.lpr.membership.domain;

import java.io.Serializable;
import java.util.*;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import fr.lpr.membership.domain.util.CustomLocalDateDeserializer;
import fr.lpr.membership.domain.util.CustomLocalDateSerializer;
import fr.lpr.membership.domain.util.LocalDateBridge;

/**
 * A Adherent.
 */
@Entity
@Table(name = "ADHERENT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@JsonAutoDetect(getterVisibility = Visibility.PUBLIC_ONLY)
@Indexed
@AnalyzerDef(name = "nameAnalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
		@TokenFilterDef(factory = ASCIIFoldingFilterFactory.class), @TokenFilterDef(factory = LowerCaseFilterFactory.class) })
public class Adherent implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Column(name = "prenom", nullable = false)
	@Field(analyze = Analyze.NO)
	private String prenom;

	@NotNull
	@Column(name = "nom", nullable = false)
	@Field(analyze = Analyze.NO)
	private String nom;

	@Column(name = "benevole")
	private Boolean benevole;

	@Column(name = "remarque_benevolat")
	private String remarqueBenevolat;

	@Column(name = "autre_remarque")
	private String autreRemarque;

	@OneToOne(cascade = CascadeType.ALL)
	private Coordonnees coordonnees;

	@OneToMany(mappedBy = "adherent", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@IndexedEmbedded
	private Set<Adhesion> adhesions;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = CustomLocalDateDeserializer.class)
	@Column(name = "reminder_email")
	private LocalDate reminderEmail;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@Column(name = "last_adhesion")
	@Field(name = "lastAdhesion", analyze = Analyze.NO)
	@FieldBridge(impl = LocalDateBridge.class)
	@JsonIgnore
	private LocalDate lastAdhesion;

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

	public Adherent prenom(String prenom) {
		setPrenom(prenom);
		return this;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Adherent nom(String nom) {
		setNom(nom);
		return this;
	}

	public Boolean getBenevole() {
		return benevole;
	}

	public void setBenevole(Boolean benevole) {
		this.benevole = benevole;
	}

	public Adherent benevole(Boolean benevole) {
		setBenevole(benevole);
		return this;
	}

	public String getRemarqueBenevolat() {
		return remarqueBenevolat;
	}

	public void setRemarqueBenevolat(String remarqueBenevolat) {
		this.remarqueBenevolat = remarqueBenevolat;
	}

	public Adherent remarqueBenevolat(String remarqueBenevolat) {
		setRemarqueBenevolat(remarqueBenevolat);
		return this;
	}

	public String getAutreRemarque() {
		return autreRemarque;
	}

	public void setAutreRemarque(String autreRemarque) {
		this.autreRemarque = autreRemarque;
	}

	public Adherent autreRemarque(String autreRemarque) {
		setAutreRemarque(autreRemarque);
		return this;
	}

	public Coordonnees getCoordonnees() {
		return coordonnees;
	}

	public void setCoordonnees(Coordonnees coordonnees) {
		this.coordonnees = coordonnees;
	}

	public Adherent coordonnees(Coordonnees coordonnees) {
		setCoordonnees(coordonnees);
		return this;
	}

	@JsonIgnore
	public Set<Adhesion> getAdhesions() {
		return adhesions;
	}

	@JsonProperty
	public void setAdhesions(Set<Adhesion> adhesions) {
		this.adhesions = adhesions;
		this.adhesions.forEach(a -> a.setAdherent(this));

		resetLastAdhesion();
	}

	public void addAdhesion(Adhesion adhesion) {
		if (this.adhesions == null) {
			this.adhesions = Sets.newHashSet();
		}
		this.adhesions.add(adhesion);
		resetLastAdhesion();
	}

	private void resetLastAdhesion() {
		this.lastAdhesion = lastAdhesion().map(Adhesion::getDateAdhesion).orElse(null);
	}

	/**
	 * The date of the last adhesion.
	 *
	 * @return the last adhesion
	 */
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonProperty
	public LocalDate getLastAdhesion() {
		return lastAdhesion;
	}

	@JsonIgnore
	public Optional<Adhesion> lastAdhesion() {
		if (this.adhesions == null || this.adhesions.isEmpty()) {
			return Optional.empty();
		} else {
			final SortedSet<Adhesion> orderedAdhesions = new TreeSet<>(Comparator.comparing(Adhesion::getDateAdhesion).reversed());
			orderedAdhesions.addAll(adhesions);
			return Optional.of(orderedAdhesions.first());
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

	/**
	 * @return the reminderEmail
	 */
	public LocalDate getReminderEmail() {
		return reminderEmail;
	}

	/**
	 * @param reminderEmail
	 *            the reminderEmail to set
	 */
	public void setReminderEmail(LocalDate reminderEmail) {
		this.reminderEmail = reminderEmail;
	}

	@Field(analyzer = @Analyzer(definition = "nameAnalyzer"))
	@Transient
	@JsonIgnore
	public String getFullName() {
		return Joiner.on(' ').join(prenom, nom);
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

        return Objects.equals(id, adherent.id);
    }

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Adherent{" + "id=" + id + ", prenom='" + prenom + "'" + ", nom='" + nom + "'" + ", benevole='" + benevole + "'" + ", remarqueBenevolat='"
				+ remarqueBenevolat + "'" + ", autreRemarque='" + autreRemarque + "'" + '}';
	}
}
