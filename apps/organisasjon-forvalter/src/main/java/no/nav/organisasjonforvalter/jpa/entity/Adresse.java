package no.nav.organisasjonforvalter.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

import static no.nav.organisasjonforvalter.jpa.entity.Adresse.AdresseType.FADR;
import static no.nav.organisasjonforvalter.jpa.entity.Adresse.AdresseType.PADR;

@Getter
@Setter
@Entity
@Builder
@Table(name = "Adresse")
@NoArgsConstructor
@AllArgsConstructor
public class Adresse implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "adresse_seq")
    @SequenceGenerator(name = "adresse_seq", sequenceName = "ADRESSE_SEQ", allocationSize = 1)
    private Long id;
    @Getter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisasjon_id", nullable = false)
    private Organisasjon organisasjon;
    @Enumerated(EnumType.STRING)
    @Column(name = "adressetype")
    private AdresseType adressetype;
    @Column(name = "adresse")
    private String adresse;
    @Column(name = "postnr")
    private String postnr;
    @Column(name = "poststed")
    private String poststed;
    @Column(name = "kommunenr")
    private String kommunenr;
    @Column(name = "landkode")
    private String landkode;
    @Column(name = "vegadresse_id")
    private String vegadresseId;

    @JsonIgnore
    public boolean isForretningsadresse() {
        return FADR == getAdressetype();
    }

    @JsonIgnore
    public boolean isPostadresse() {
        return PADR == getAdressetype();
    }

    public enum AdresseType {FADR, PADR}
}
