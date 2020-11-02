package no.nav.registre.testnorge.libs.dto.syntrest.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArbeidsforholdResponse {
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
    @JsonProperty("PERMISJONER")
    List<PermisjonDTO> permisjoner;

}