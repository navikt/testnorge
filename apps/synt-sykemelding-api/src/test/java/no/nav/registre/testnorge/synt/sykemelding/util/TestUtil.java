package no.nav.registre.testnorge.synt.sykemelding.util;

import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v1.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.helsepersonell.v1.LegeDTO;
import no.nav.registre.testnorge.libs.dto.helsepersonell.v1.LegeListeDTO;
import no.nav.registre.testnorge.libs.dto.hodejegeren.v1.PersondataDTO;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.synt.sykemelding.consumer.dto.SyntDiagnoserDTO;
import no.nav.registre.testnorge.synt.sykemelding.consumer.dto.SyntSykemeldingDTO;
import no.nav.registre.testnorge.synt.sykemelding.consumer.dto.SyntSykemeldingHistorikkDTO;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestUtil {

    public static PersondataDTO getTestPersonDataDTO(String ident){
        return PersondataDTO.builder()
                .fornavn("Hans")
                .mellomnavn("Ole")
                .etternavn("Hansen")
                .fnr(ident)
                .build();
    }

    public static ArbeidsforholdDTO getTestArbeidsforholdDTO(String arbeidsforholdId, String orgnr){
        return ArbeidsforholdDTO.builder()
                .arbeidsforholdId(arbeidsforholdId)
                .orgnummer(orgnr)
                .stillingsprosent(100.00)
                .yrke("Test yrke")
                .build();
    }

    public static OrganisasjonDTO getTestOrganisasjonDTO(String orgnr){
        return OrganisasjonDTO.builder()
                .enhetType("Type")
                .orgnummer(orgnr)
                .juridiskEnhet("Enhet")
                .navn("Test bedrift")
                .build();
    }

    public static Map<String, SyntSykemeldingHistorikkDTO> getTestHistorikk(String ident){
        var resultat = new HashMap<String, SyntSykemeldingHistorikkDTO>();
        resultat.put(ident, getTestSyntSykemeldingHistorikkDTO());

        return resultat;
    }


    public static SyntSykemeldingHistorikkDTO getTestSyntSykemeldingHistorikkDTO(){
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

    public static LegeListeDTO getTestLegeListeDTO(){
        return new LegeListeDTO(Collections.singletonList(LegeDTO.builder().fornavn("Lege")
                .mellomnavn("L.")
                .etternavn("Legesen")
                .fnr("123")
                .hprId("123").build()));
    }

}
