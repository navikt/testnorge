package no.nav.organisasjonforvalter.jpa.entity;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Organisasjon")
public class Organisasjon implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "adresse_seq")
    @SequenceGenerator(name = "adresse_seq", sequenceName = "ADRESSE_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "organisasjonsnummer")
    private String organisasjonsnummer;

    @Column(name = "enhetstype")
    private String enhetstype;

    @Column(name = "naeringskode")
    private String naeringskode;

    @Column(name = "sektorkode")
    private String sektorkode;

    @Column(name = "formaal")
    private String formaal;

    @Column(name = "organisasjonsnavn")
    private String organisasjonsnavn;

    @Column(name = "telefon")
    private String telefon;

    @Column(name = "epost")
    private String epost;

    @Column(name = "nettside")
    private String nettside;

    @Column(name = "maalform")
    private String maalform;

    @Column(name = "stiftelsesdato")
    private LocalDate stiftelsesdato;

    @OrderBy("id desc")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organisasjon", cascade = CascadeType.ALL)
    private List<Adresse> adresser;

    @ManyToOne(cascade={CascadeType.ALL})
    @JoinColumn(name="parent_org")
    private Organisasjon parent;

    @OneToMany(mappedBy = "parent")
    private List<Organisasjon> underenheter;

    public List<Adresse> getAdresser() {
        if (isNull(adresser)) {
            adresser = new ArrayList<>();
        }
        return adresser;
    }

    public List<Organisasjon> getUnderenheter() {
        if (isNull(underenheter)) {
            underenheter = new ArrayList<>();
        }
        return underenheter;
    }
}
