package no.nav.organisasjonforvalter.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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

    @Column(name = "bruker_id")
    private String brukerId;

    @OrderBy("id desc")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organisasjon", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Adresse> adresser = new ArrayList<>();

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "parent_org")
    private Organisasjon parent;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<Organisasjon> underenheter = new ArrayList<>();

    @JsonIgnore
    public boolean hasAnsatte() {
        return "BEDR".equals(getEnhetstype()) || "AAFY".equals(getEnhetstype());
    }
}
