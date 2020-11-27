package no.nav.organisasjonforvalter.jpa.entity;

import javax.persistence.*;
import java.util.List;

@Table(name = "Organisasjon")
public class Organisasjon {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "adresse_seq")
    @SequenceGenerator(name = "adresse_seq", sequenceName = "ADRESSE_SEQ", allocationSize = 1)
    private String id;

    @Column(name = "organisasjonsform")
    private String organisasjonsform;

    @Column(name = "naeringskode")
    private String naeringskode;

    @Column(name = "formaal")
    private String formaal;

    @Column(name = "firmanavn")
    private String firmanavn;

    @Column(name = "telefon")
    private String telefon;

    @Column(name = "epost")
    private String epost;

    @Column(name = "nettside")
    private String nettside;

    @JoinColumn(name = "adresse_id", nullable = false)
    private List<Adresse> adresser;

    @Column(name = "parent_org")
    @OneToMany()
    private Organisasjon parent;
}
