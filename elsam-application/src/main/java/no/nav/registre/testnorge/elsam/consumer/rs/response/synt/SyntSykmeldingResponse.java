package no.nav.registre.testnorge.elsam.consumer.rs.response.synt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.elsam.domain.Diagnose;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyntSykmeldingResponse {

    private Boolean arbeidsforEtterEndtPeriode;
    private Diagnose hovedDiagnose;
    private List<Diagnose> biDiagnoser;
    private String kontaktMedPasient;
    private Boolean meldingTilNav;
    private Boolean reisetilskudd;
    private String sluttPeriode;
    private String startPeriode;
    private Double sykmeldingsprosent;
}
