package no.nav.registre.testnorge.libs.dto.syntrest.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArbeidsforholdDTO {
    @JsonProperty("RAPPORTERINGSMAANED")
    String rapporteringsmaaned;
    @JsonProperty("ARBEIDSFORHOLD_TYPE")
    String arbeidsforholdType;
    @JsonProperty("STARTDATO")
    String startdato;
    @JsonProperty("SLUTTDATO")
    String sluttdato;
    @JsonProperty("ANTALL_TIMER_PER_UKE_SOM_EN_FULL_STILLING_TILSVARER")
    Float antallTimerPerUkeSomEnFullStillingTilsvarer;
    @JsonProperty("AVLOENNINGSTYPE")
    String avloenningstype;
    @JsonProperty("YRKE")
    String yrke;
    @JsonProperty("ARBEIDSORDING")
    String arbeidsordning;
    @JsonProperty("STILLINGSPROSENT")
    Float stillingsprosent;
    @JsonProperty("SISTE_LOENNSENDRINGSDATO")
    String sisteLoennsendringsdato;
    @JsonProperty("SISTE_DATO_FOR_STILLINGSPROSENTENDRING")
    String sisteDatoForStillingsprosentendring;
    @JsonProperty("PERMISJON_MED_FORELDREPENGER")
    Float permisjonMedForeldrepenger;
    @JsonProperty("PERMITTERING")
    Float permittering;
    @JsonProperty("PERMISJON")
    Float permisjon;
    @JsonProperty("PERMISJON_VED_MILITAERTJENESTE")
    Float permisjonVedMilitaertjeneste;
    @JsonProperty("VELFERDSPERMISJON")
    Float velferdspermisjon;
    @JsonProperty("UTDANNINGSPERMISJON")
    Float utdanningspermisjon;
}