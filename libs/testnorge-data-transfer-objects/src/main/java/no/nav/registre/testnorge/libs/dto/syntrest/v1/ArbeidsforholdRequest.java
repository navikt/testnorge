package no.nav.registre.testnorge.libs.dto.syntrest.v1;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArbeidsforholdRequest {
    @JsonAlias("RAPPORTERINGSMAANED")
    String rapporteringsmaaned;
    @JsonAlias("ARBEIDSFORHOLD_TYPE")
    String arbeidsforholdType;
    @JsonAlias("STARTDATO")
    LocalDate startdato;
    @JsonAlias("SLUTTDATO")
    LocalDate sluttdato;
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
    @JsonAlias("PERMISJON_MED_FORELDREPENGER")
    Float permisjonMedForeldrepenger;
    @JsonAlias("PERMITTERING")
    Float permittering;
    @JsonAlias("PERMISJON")
    Float permisjon;
    @JsonAlias("PERMISJON_VED_MILITAERTJENESTE")
    Float permisjonVedMilitaertjeneste;
    @JsonAlias("VELFERDSPERMISJON")
    Float velferdspermisjon;
    @JsonAlias("UTDANNINGSPERMISJON")
    Float utdanningspermisjon;
}