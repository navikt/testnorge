package no.nav.testnav.libs.dto.syntrest.v1;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArbeidsforholdResponse {
    @JsonAlias("RAPPORTERINGSMAANED")
    String rapporteringsmaaned;
    @JsonAlias("ARBEIDSFORHOLD_TYPE")
    String arbeidsforholdType;
    @JsonAlias("STARTDATO")
    LocalDate startdato;
    @JsonAlias("SLUTTDATO")
    String sluttdato;
    @JsonAlias("ANTALL_TIMER_PER_UKE_SOM_EN_FULL_STILLING_TILSVARER")
    Float antallTimerPerUkeSomEnFullStillingTilsvarer;
    @JsonAlias("YRKE")
    String yrke;
    @JsonAlias("ARBEIDSTIDSORDNING")
    String arbeidstidsordning;
    @JsonAlias("STILLINGSPROSENT")
    Float stillingsprosent;
    @JsonAlias("SISTE_LOENNSENDRINGSDATO")
    LocalDate sisteLoennsendringsdato;
    @JsonAlias("SISTE_DATO_FOR_STILLINGSPROSENTENDRING")
    LocalDate sisteDatoForStillingsprosentendring;
    @JsonAlias("PERMISJONER")
    List<PermisjonDTO> permisjoner;
    String historikk;
    @JsonAlias("FARTOEY")
    FartoeyDTO fartoey;
    @JsonAlias("INNTEKTER")
    List<InntektDTO> inntekter;
    @JsonAlias("AVVIK")
    AvvikDTO avvik;
}