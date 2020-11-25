package no.nav.registre.testnorge.libs.dto.syntrest.v1;

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
    String rapporteringsmaaned;
    String arbeidsforholdType;
    LocalDate startdato;
    String sluttdato;
    Float antallTimerPerUkeSomEnFullStillingTilsvarer;
    String yrke;
    String arbeidstidsordning;
    Float stillingsprosent;
    LocalDate sisteLoennsendringsdato;
    LocalDate sisteDatoForStillingsprosentendring;
    Float permisjonMedForeldrepenger;
    Float permittering;
    Float permisjon;
    Float permisjonVedMilitaertjeneste;
    Float velferdspermisjon;
    Float utdanningspermisjon;
}