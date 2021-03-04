package no.nav.registre.syntrest.domain.aareg.amelding;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArbeidsforholdAmelding {

    @JsonAlias({"RAPPORTERINGSMAANED", "rapporteringsmaaned"})
    private String rapporteringsmaaned;

    @JsonAlias({"ARBEIDSFORHOLD_TYPE", "arbeidsforholdType"})
    private String arbeidsforholdType;

    @JsonAlias({"STARTDATO", "startdato"})
    private String startdato;

    @JsonAlias({"SLUTTDATO", "sluttdato"})
    private String sluttdato;

    @JsonAlias({"ANTALL_TIMER_PER_UKE_SOM_EN_FULL_STILLING_TILSVARER", "antallTimerPerUkeSomEnFullStillingTilsvarer"})
    private float antallTimerPerUkeSomEnFullStillingTilsvarer;

    @JsonAlias({"YRKE", "yrke"})
    private String yrke;

    @JsonAlias({"ARBEIDSTIDSORDNING", "arbeidstidsordning"})
    private String arbeidstidsordning;

    @JsonAlias({"STILLINGSPROSENT", "stillingsprosent"})
    private float stillingsprosent;

    @JsonAlias({"SISTE_LOENNSENDRINGSDATO", "sisteLoennsendringsdato"})
    private String sisteLoennsendringsdato;

    @JsonAlias({"SISTE_DATO_FOR_STILLINGSPROSENTENDRING", "sisteDatoForStillingsprosentendring"})
    private String sisteDatoForStillingsprosentendring;

    @JsonAlias({"PERMISJON_MED_FORELDREPENGER", "permisjonMedForeldrepenger"})
    private Integer permisjonMedForeldrepenger;

    @JsonAlias({"PERMITTERING", "permittering"})
    private Integer permittering;

    @JsonAlias({"PERMISJON", "permisjon"})
    private Integer permisjon;

    @JsonAlias({"PERMISJON_VED_MILITAERTJENESTE", "permisjonVedMilitaertjeneste"})
    private Integer permisjonVedMilitaertjeneste;

    @JsonAlias({"VELFERDSPERMISJON", "velferdspermisjon"})
    private Integer velferdspermisjon;

    @JsonAlias({"UTDANNINGSPERMISJON", "utdanningspermisjon"})
    private Integer utdanningspermisjon;

    @JsonAlias({"PERMISJONER", "permisjoner"})
    private List<Permisjon> permisjoner;

    @JsonAlias({"HISTORIKK", "historikk", "change_history"})
    private String historikk;

    @JsonAlias({"NUM_ENDRINGER", "numEndringer", "num_changes"})
    private Integer numEndringer;

    @JsonAlias({"FARTOEY", "fartoey"})
    private Fartoey fartoey;

}
