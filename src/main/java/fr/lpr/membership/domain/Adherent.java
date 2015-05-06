package fr.lpr.membership.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Adherent.
 */
@Entity
@Table(name = "ADHERENT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Adherent implements Serializable {

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

    @OneToOne
    private Coordonnees coordonnees;

    @OneToMany(mappedBy = "adherent")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Adhesion> adhesionss = new HashSet<>();

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

    public Set<Adhesion> getAdhesions() {
        return adhesionss;
    }

    public void setAdhesions(Set<Adhesion> adhesions) {
        this.adhesionss = adhesions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Adherent adherent = (Adherent) o;

        if ( ! Objects.equals(id, adherent.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Adherent{" +
                "id=" + id +
                ", prenom='" + prenom + "'" +
                ", nom='" + nom + "'" +
                ", benevole='" + benevole + "'" +
                ", remarqueBenevolat='" + remarqueBenevolat + "'" +
                ", genre='" + genre + "'" +
                ", autreRemarque='" + autreRemarque + "'" +
                '}';
    }
}
