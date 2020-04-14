package no.nav.registre.elsam.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SykmeldingRequest {

    private Ident ident;
    private String syketilfelleStartDato;
    private String utstedelsesdato;
    private Diagnose hovedDiagnose;
    private List<Diagnose> biDiagnoser;
    private Lege lege;
    private Boolean manglendeTilretteleggingPaaArbeidsplassen;
    private Detaljer detaljer;
    private List<SykmeldingPeriode> sykmeldingPerioder;
    private Organisasjon senderOrganisasjon;
    private Organisasjon mottakerOrganisasjon;
}
