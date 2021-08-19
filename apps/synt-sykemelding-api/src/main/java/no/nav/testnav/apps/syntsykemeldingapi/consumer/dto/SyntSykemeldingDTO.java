package no.nav.testnav.apps.syntsykemeldingapi.consumer.dto;

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
public class SyntSykemeldingDTO {
    Boolean arbeidsforEtterEndtPeriode;
    List<SyntDiagnoserDTO> biDiagnoser;
    SyntDiagnoserDTO hovedDiagnose;
    LocalDate kontaktMedPasient;
    Boolean meldingTilNav;
    Boolean reisetilskudd;
    LocalDate sluttPeriode;
    LocalDate startPeriode;
    Double sykmeldingsprosent;
}
