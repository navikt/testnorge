package no.nav.organisasjonforvalter.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;

import static no.nav.organisasjonforvalter.jpa.entity.Adresse.AdresseType.FADR;
import static no.nav.organisasjonforvalter.jpa.entity.Adresse.AdresseType.PADR;

@Data
@ToString
@Entity
@Builder
@Table(name = "Adresse")
@NoArgsConstructor
@AllArgsConstructor
public class Adresse implements Serializable {

    public enum AdresseType {FADR, PADR}

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
}
