package no.nav.registre.testnorge.libs.dto.syntrest.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
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
    LocalDate startdato;
    @JsonProperty("SLUTTDATO")
    LocalDate sluttdato;
    @JsonProperty("ANTALL_TIMER_PER_UKE_SOM_EN_FULL_STILLING_TILSVARER")
    Float antallTimerPerUkeSomEnFullStillingTilsvarer;
    @JsonProperty("AVLOENNINGSTYPE")
    String avloenningstype;
    @JsonProperty("YRKE")
    String yrke;
    @JsonProperty("ARBEIDSTIDSORDNING")
    String arbeidstidsordning;
    @JsonProperty("STILLINGSPROSENT")
    Float stillingsprosent;
    @JsonProperty("SISTE_LOENNSENDRINGSDATO")
    LocalDate sisteLoennsendringsdato;
    @JsonProperty("SISTE_DATO_FOR_STILLINGSPROSENTENDRING")
    LocalDate sisteDatoForStillingsprosentendring;
    @JsonProperty("PERMISJONER")
    List<PermisjonDTO> permisjoner;

}