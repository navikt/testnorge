package no.nav.registre.syntrest.domain.amelding;

import java.time.LocalDate;
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
public class Arbeidsforhold {

    @JsonAlias({"RAPPORTERINGSMAANED", "rapporteringsmaaned"})
    private String rapporteringsmaaned;

    @JsonAlias({"ARBEIDSFORHOLD_TYPE", "arbeidsforholdType"})
    private String arbeidsforholdType;

    @JsonAlias({"STARTDATO", "startdato", "startDato"})
    private LocalDate startdato;

    @JsonAlias({"SLUTTDATO", "sluttdato", "sluttDato"})
    private LocalDate sluttdato;

    @JsonAlias({"ANTALL_TIMER_PER_UKE_SOM_EN_FULL_STILLING_TILSVARER", "antallTimerPerUkeSomEnFullStillingTilsvarer"})
    private float antallTimerPerUkeSomEnFullStillingTilsvarer;

    @JsonAlias({"AVLOENNINGSTYPE", "avloenningstype"})
    private String avloenningstype;

    @JsonAlias({"YRKE", "yrke"})
    private String yrke;

    @JsonAlias({"ARBEIDSORDING", "ARBEIDSTIDSORDNING", "arbeidsording", "arbeidstidsordning"})
    private String arbeidstidsordning;

    @JsonAlias({"STILLINGSPROSENT", "stillingsprosent"})
    private float stillingsprosent;

    @JsonAlias({"SISTE_LOENNSENDRINGSDATO", "sisteLoennsendringsdato"})
    private LocalDate sisteLoennsendringsdato;

    @JsonAlias({"SISTE_DATO_FOR_STILLINGSPROSENTENDRING", "sisteDatoForStillingsprosentendring"})
    private LocalDate sisteDatoForStillingsprosentendring;

    @JsonAlias({"PERMISJON_MED_FORELDREPENGER", "permisjonMedForeldrePenger"})
    private float permisjonMedForeldrePenger;

    @JsonAlias({"PERMITTERING", "permittering"})
    private float permittering;

    @JsonAlias({"PERMISJON", "permisjon"})
    private float permisjon;

    @JsonAlias({"PERMISJON_VED_MILITAERTJENESTE", "permisjonVedMilitaertjeneste"})
    private float permisjonVedMilitaertjeneste;

    @JsonAlias({"VELFERDSPERMISJON", "velferdspermisjon"})
    private float velferdspermisjon;

    @JsonAlias({"UTDANNINGSPERMISJON", "utdanningspermisjon"})
    private float utdanningspermisjon;

    @JsonAlias({"PERMISJONER", "permisjoner"})
    private List<Permisjon> permisjoner;

}
