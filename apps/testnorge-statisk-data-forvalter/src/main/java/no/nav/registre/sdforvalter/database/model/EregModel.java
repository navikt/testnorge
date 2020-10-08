package no.nav.registre.sdforvalter.database.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import no.nav.registre.sdforvalter.domain.Ereg;

@Entity
@ToString
@Getter
@Setter
@Slf4j
@Table(name = "EREG")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class EregModel extends FasteDataModel<Ereg> {

    @Id
    private String orgnr;

    @NotNull
    private String enhetstype;

    private String navn;
    private String epost;
    private String internetAdresse;
    private String naeringskode;

    @Column(name = "redigert_navn")
    private String redigertNavn;

    @OneToOne
    @JoinColumn(name = "parent")
    private EregModel parent;


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "adresse", column = @Column(name = "forretnings_adresse")),
            @AttributeOverride(name = "postnr", column = @Column(name = "forretnings_postnr")),
            @AttributeOverride(name = "kommunenr", column = @Column(name = "forretnings_kommunenr")),
            @AttributeOverride(name = "landkode", column = @Column(name = "forretnings_landkode")),
            @AttributeOverride(name = "poststed", column = @Column(name = "forretnings_poststed")),
    })
    private AdresseModel forretningsAdresse;

    @Embedded
    private AdresseModel postadresse;

    public EregModel(Ereg ereg, EregModel parent, OpprinnelseModel opprinnelseModel, GruppeModel gruppeModel) {
        super(gruppeModel, opprinnelseModel);
        this.orgnr = ereg.getOrgnr();
        this.enhetstype = ereg.getEnhetstype();
        this.navn = ereg.getNavn();
        this.redigertNavn = ereg.getRedigertNavn();
        this.epost = ereg.getEpost();
        this.internetAdresse = ereg.getInternetAdresse();
        this.parent = parent;
        this.forretningsAdresse = ereg.getForretningsAdresse() != null ? new AdresseModel(ereg.getForretningsAdresse()) : null;
        this.postadresse = ereg.getPostadresse() != null ? new AdresseModel(ereg.getPostadresse()) : null;
    }

    @Override
    public Ereg toDomain() {
        throw new UnsupportedOperationException("Ikke mulig å convertere til model");
    }
}
