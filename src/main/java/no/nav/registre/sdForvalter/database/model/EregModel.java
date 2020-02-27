package no.nav.registre.sdForvalter.database.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdForvalter.domain.Ereg;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Table(name = "EREG")
public class EregModel extends AuditModel {

    @Id
    private String orgnr;

    @NotNull
    private String enhetstype;

    private String navn;
    private String epost;
    private String internetAdresse;
    private String naeringskode;

    @OneToOne
    private EregModel parent;

    @OneToOne
    @JoinColumn(name = "kilde_system_id")
    private KildeSystemModel kildeSystemModel;

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

    @Column(name = "ekskludert")
    private boolean excluded = false;

    public EregModel(Ereg ereg, EregModel parent, KildeSystemModel kildeSystemModel) {
        this.orgnr = ereg.getOrgnr();
        this.enhetstype = ereg.getEnhetstype();
        this.navn = ereg.getNavn();
        this.epost = ereg.getEpost();
        this.internetAdresse = ereg.getInternetAdresse();
        this.parent = parent;
        this.kildeSystemModel = kildeSystemModel;
        this.forretningsAdresse = ereg.getInternetAdresse() != null ? new AdresseModel(ereg.getForretningsAdresse()) : null;
        this.postadresse = ereg.getPostadresse() != null ? new AdresseModel(ereg.getPostadresse()) : null;
    }
}
