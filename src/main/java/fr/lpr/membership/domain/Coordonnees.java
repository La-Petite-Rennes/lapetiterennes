package fr.lpr.membership.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Joiner;

/**
 * A Coordonnees.
 */
@Entity
@Table(name = "COORDONNEES")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Coordonnees implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "adresse1")
	private String adresse1;

	@Column(name = "adresse2")
	private String adresse2;

	@Column(name = "code_postal")
	private String codePostal;

	@Column(name = "ville")
	private String ville;

	@Column(name = "email")
	private String email;

	@Column(name = "telephone")
	private String telephone;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAdresse1() {
		return adresse1;
	}

	public void setAdresse1(String adresse1) {
		this.adresse1 = adresse1;
	}

	public String getAdresse2() {
		return adresse2;
	}

	public void setAdresse2(String adresse2) {
		this.adresse2 = adresse2;
	}

	@JsonIgnore
	public String getAdresseComplete() {
		return Joiner.on(' ').skipNulls().join(adresse1, adresse2);
	}

	public String getCodePostal() {
		return codePostal;
	}

	public void setCodePostal(String codePostal) {
		this.codePostal = codePostal;
	}

	public String getVille() {
		return ville;
	}

	public void setVille(String ville) {
		this.ville = ville;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final Coordonnees coordonnees = (Coordonnees) o;

		if (!Objects.equals(id, coordonnees.id)) {
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
		return "Coordonnees{" + "id=" + id + ", adresse1='" + adresse1 + "'" + ", adresse2='" + adresse2 + "'" + ", codePostal='" + codePostal + "'"
				+ ", ville='" + ville + "'" + ", email='" + email + "'" + ", telephone='" + telephone + "'" + '}';
	}
}
