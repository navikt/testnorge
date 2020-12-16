package no.nav.organisasjonforvalter.jpa.entity;

import javax.persistence.*;

@Entity
@Table(name = "Adresse")
public class Adresse {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "adresse_seq")
    @SequenceGenerator(name = "adresse_seq", sequenceName = "ADRESSE_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "adressetype")
    private String adressetype;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "gatekode")
    private String gatekode;

    @Column(name = "boenhet")
    private String boenhet;

    @Column(name = "postnr")
    private String postnr;

    @Column(name = "kommunenr")
    private String kommunenr;

    @Column(name = "landkode")
    private String landkode;
}
