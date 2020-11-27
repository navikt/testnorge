package no.nav.organisasjonforvalter.jpa.entity;

import javax.persistence.*;

@Table(name = "Adresse")
public class Adresse {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "adresse_seq")
    @SequenceGenerator(name = "adresse_seq", sequenceName = "ADRESSE_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "adressetype")
    private String adressetype;

    @Column(name = "adresselinje1")
    private String adresselinje1;

    @Column(name = "adresselinje2")
    private String adresselinje2;

    @Column(name = "adresselinje3")
    private String adresselinje3;

    @Column(name = "adresselinje4")
    private String adresselinje4;

    @Column(name = "adresselinje5")
    private String adresselinje5;

    @Column(name = "postnr")
    private String postnr;

    @Column(name = "kommunenr")
    private String kommunenr;

    @Column(name = "landkode")
    private String landkode;
}
