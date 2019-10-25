package fr.lpr.membership.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A Coordonnees.
 */
@Entity
@Table(name = "COORDONNEES")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
public class Coordonnees implements Serializable {

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

	@JsonIgnore
	public String getAdresseComplete() {
		return Joiner.on(' ').skipNulls().join(adresse1, adresse2);
	}
}
