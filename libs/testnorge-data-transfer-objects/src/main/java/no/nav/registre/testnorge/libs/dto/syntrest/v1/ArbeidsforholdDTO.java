package no.nav.registre.testnorge.libs.dto.syntrest.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArbeidsforholdDTO {
    String rapporteringsmaaned;
    String arbeidsforholdType;
    String startdato;
    String sluttdato;
    Float antallTimerPerUkeSomEnFullStillingTilsvarer;
    String avloenningstype;
    String yrke;
    String arbeidsordning;
    Float stillingsprosent;
    String sisteLoennsendringsdato;
    String sisteDatoForStillingsprosentendring;
    Float permisjonMedForeldrepenger;
    Float permittering;
    Float permisjon;
    Float permisjonVedMilitaertjeneste;
    Float velferdspermisjon;
    Float utdanningspermisjon;
}