package fr.lpr.membership.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import lombok.*;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

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
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Adherent implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(nullable = false)
	@Field(analyze = Analyze.NO)
	private String prenom;

	@NotNull
	@Column(nullable = false)
	@Field(analyze = Analyze.NO)
	private String nom;

	private Boolean benevole;

	private String remarqueBenevolat;

	private String autreRemarque;

	@OneToOne(cascade = CascadeType.ALL)
	private Coordonnees coordonnees;

	@OneToMany(mappedBy = "adherent", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@IndexedEmbedded
	private Set<Adhesion> adhesions;

	private LocalDate reminderEmail;

	@Field(name = "lastAdhesion", analyze = Analyze.NO)
	@DateBridge(resolution = Resolution.DAY)
	@JsonIgnore
	private LocalDate lastAdhesion;

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

		if (lastAdhesion.isEmpty()) {
			return StatutAdhesion.NONE;
		} else if (lastAdhesion.get().getDateFinAdhesion().isBefore(LocalDate.now())) {
			return StatutAdhesion.RED;
		} else if (lastAdhesion.get().getDateFinAdhesion().isBefore(LocalDate.now().plusMonths(1))) {
			return StatutAdhesion.ORANGE;
		} else {
			return StatutAdhesion.GREEN;
		}
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
