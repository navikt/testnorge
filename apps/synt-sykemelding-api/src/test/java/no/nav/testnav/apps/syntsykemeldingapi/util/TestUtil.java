package no.nav.testnav.apps.syntsykemeldingapi.util;


import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import no.nav.testnav.apps.syntsykemeldingapi.domain.pdl.Metadata;
import no.nav.testnav.apps.syntsykemeldingapi.domain.pdl.PdlPerson;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO;
import no.nav.testnav.libs.dto.helsepersonell.v1.HelsepersonellDTO;
import no.nav.testnav.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.dto.SyntDiagnoserDTO;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.dto.SyntSykemeldingDTO;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.dto.SyntSykemeldingHistorikkDTO;

public class TestUtil {

    public static PdlPerson getTestPdlPerson(String ident) {
        var metadata = new Metadata(false);

        var identer = PdlPerson.HentIdenter.builder()
                .identer(Collections.singletonList(PdlPerson.Identer.builder()
                        .gruppe(PdlPerson.Gruppe.FOLKEREGISTERIDENT)
                        .historisk(false)
                        .ident(ident)
                        .build()
                ))
                .build();
        var hentPersoner = PdlPerson.HentPerson.builder()
                .navn(Collections.singletonList(PdlPerson.Navn.builder()
                        .fornavn("Hans")
                        .mellomnavn("Ole")
                        .etternavn("Hansen")
                        .metadata(metadata)
                        .build()))
                .build();

        return PdlPerson.builder()
                .data(PdlPerson.Data.builder()
                        .hentPerson(hentPersoner)
                        .hentIdenter(identer)
                        .build())
                .build();
    }

    public static ArbeidsforholdDTO getTestArbeidsforholdDTO(String arbeidsforholdId, String orgnr) {
        return ArbeidsforholdDTO.builder()
                .arbeidsforholdId(arbeidsforholdId)
                .orgnummer(orgnr)
                .stillingsprosent(100.00F)
                .yrke("Test yrke")
                .build();
    }

    public static OrganisasjonDTO getTestOrganisasjonDTO(String orgnr) {
        return OrganisasjonDTO.builder()
                .enhetType("Type")
                .orgnummer(orgnr)
                .juridiskEnhet("Enhet")
                .navn("Test bedrift")
                .build();
    }

    public static Map<String, SyntSykemeldingHistorikkDTO> getTestHistorikk(String ident) {
        var resultat = new HashMap<String, SyntSykemeldingHistorikkDTO>();
        resultat.put(ident, getTestSyntSykemeldingHistorikkDTO());

        return resultat;
    }


    public static SyntSykemeldingHistorikkDTO getTestSyntSykemeldingHistorikkDTO() {
        var diagnose = SyntDiagnoserDTO.builder()
                .diagnose("Diagnose")
                .diagnosekode("Kode")
                .system("System")
                .build();

        return SyntSykemeldingHistorikkDTO.builder()
                .sykmeldinger(Collections.singletonList(SyntSykemeldingDTO.builder()
                        .arbeidsforEtterEndtPeriode(Boolean.FALSE)
                        .biDiagnoser(Collections.singletonList(diagnose))
                        .hovedDiagnose(diagnose)
                        .kontaktMedPasient(LocalDate.now())
                        .meldingTilNav(Boolean.FALSE)
                        .reisetilskudd(Boolean.FALSE)
                        .sluttPeriode(LocalDate.now().plusMonths(1))
                        .startPeriode(LocalDate.now())
                        .sykmeldingsprosent(50.0)
                        .build()))
                .build();
    }

    public static HelsepersonellListeDTO getTestLegeListeDTO() {
        return new HelsepersonellListeDTO(Collections.singletonList(HelsepersonellDTO.builder().fornavn("Lege")
                .mellomnavn("L.")
                .etternavn("Legesen")
                .fnr("123")
                .hprId("123").build()));
    }

}
