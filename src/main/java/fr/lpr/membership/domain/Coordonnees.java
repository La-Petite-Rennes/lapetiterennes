package fr.lpr.membership.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Joiner;
import lombok.Data;
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
@Data
public class Coordonnees implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String adresse1;

	private String adresse2;

	private String codePostal;

	private String ville;

	private String email;

	private String telephone;

	@JsonIgnore
	public String getAdresseComplete() {
		return Joiner.on(' ').skipNulls().join(adresse1, adresse2);
	}
}
