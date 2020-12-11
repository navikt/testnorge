package no.nav.organisasjonforvalter.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Organisasjon")
public class Organisasjon {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "adresse_seq")
    @SequenceGenerator(name = "adresse_seq", sequenceName = "ADRESSE_SEQ", allocationSize = 1)
    private String id;

    @Column(name = "organisasjonsnummer")
    private String organisasjonsnummer;

    @Column(name = "organisasjonsform")
    private String organisasjonsform;

    @Column(name = "naeringskode")
    private String naeringskode;

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

    @JoinColumn(name = "adresse_id")
    @OneToMany
    private List<Adresse> adresser;

    @JsonIgnore
    @JoinColumn(name = "parent_org", referencedColumnName = "id")
    @ManyToOne
    private Organisasjon parent;

    @OneToMany
    @JoinColumn(name = "id", referencedColumnName = "parent_org")
    private List<Organisasjon> underorgansisjoner;

    public List<Adresse> getAdresser() {
        if (isNull(adresser)) {
            adresser = new ArrayList<>();
        }
        return adresser;
    }

    public List<Organisasjon> getUnderorgansisjoner() {
        if (isNull(underorgansisjoner)) {
            underorgansisjoner = new ArrayList<>();
        }
        return underorgansisjoner;
    }
}
