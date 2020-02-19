package no.nav.registre.sdForvalter.database.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import no.nav.registre.sdForvalter.database.Ownable;
import no.nav.registre.sdForvalter.domain.Ereg;

@Entity
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Table(name = "EREG")
public class EregModel extends AuditModel implements Ownable {

    @Id
    @GeneratedValue
    Long id;

    @NotNull
    @Column(unique = true)
    private String orgnr;

    @NotNull
    private String enhetstype;

    private String navn;
    private String epost;
    private String internetAdresse;

    private String naeringskode;

    private String parent;

    @JsonBackReference(value = "ereg")
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @JsonBackReference(value = "ereg-varighet")
    @ManyToOne
    @JoinColumn(name = "varighet_id")
    private Varighet varighet;

    @ManyToOne
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

    public EregModel(Ereg ereg, KildeSystemModel kildeSystemModel) {
        this.orgnr = ereg.getOrgnr();
        this.enhetstype = ereg.getEnhetstype();
        this.navn = ereg.getNavn();
        this.epost = ereg.getEpost();
        this.internetAdresse = ereg.getInternetAdresse();
        this.parent = ereg.getParent();
        this.team = ereg.getTeam();
        this.varighet = ereg.getVarighet();
        this.kildeSystemModel = kildeSystemModel;
        this.forretningsAdresse = ereg.getInternetAdresse() != null ? new AdresseModel(ereg.getForretningsAdresse()) : null;
        this.postadresse = ereg.getPostadresse() != null ? new AdresseModel(ereg.getPostadresse()) : null;
    }
}
