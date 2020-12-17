package no.nav.organisasjonforvalter.jpa.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

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

    @Column(name = "adressetype")
    private String adressetype;

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
}
