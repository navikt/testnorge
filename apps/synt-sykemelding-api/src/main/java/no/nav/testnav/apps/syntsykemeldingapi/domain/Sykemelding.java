package no.nav.testnav.apps.syntsykemeldingapi.domain;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.testnav.libs.dto.sykemelding.v1.AdresseDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.Aktivitet;
import no.nav.testnav.libs.dto.sykemelding.v1.AktivitetDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.ArbeidsgiverDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.DetaljerDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.DiagnoseDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.OrganisasjonDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.PasientDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.PeriodeDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;
import no.nav.testnav.libs.dto.synt.sykemelding.v1.SyntSykemeldingDTO;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.dto.SyntDiagnoserDTO;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.dto.SyntSykemeldingHistorikkDTO;

@RequiredArgsConstructor
public class Sykemelding {
    private final Person pasient;
    private final SyntSykemeldingHistorikkDTO historikk;
    private final SyntSykemeldingDTO syntSykemelding;
    private final Helsepersonell helsepersonell;
    private final Arbeidsforhold arbeidsforhold;


    public String getIdent() {
        return pasient.getIdent();
    }

    public SykemeldingDTO toDTO() {
        var arbeidsgiverDTO = ArbeidsgiverDTO
                .builder()
                .navn(arbeidsforhold.getNavn())
                .stillingsprosent(arbeidsforhold.getStillingsprosent())
                .yrkesbetegnelse(arbeidsforhold.getYrkesbetegnelse())
                .build();

        var sykemeldinger = historikk.getSykmeldinger();
        var firstSykemelding = sykemeldinger.get(0);

        List<SyntDiagnoserDTO> syntBiDiagnoser = firstSykemelding.getBiDiagnoser() != null
                ? firstSykemelding.getBiDiagnoser()
                : Collections.emptyList();

        var biDiagnoser = syntBiDiagnoser.stream()
                .map(value -> DiagnoseDTO
                        .builder()
                        .diagnose(value.getDiagnose())
                        .system(value.getSystem())
                        .diagnosekode(value.getDiagnosekode())
                        .build()
                )
                .collect(Collectors.toList());

        var hovedDiagnose = DiagnoseDTO
                .builder()
                .diagnose(firstSykemelding.getHovedDiagnose().getDiagnose())
                .system(firstSykemelding.getHovedDiagnose().getSystem())
                .diagnosekode(firstSykemelding.getHovedDiagnose().getDiagnosekode())
                .build();

        var mottaker = OrganisasjonDTO
                .builder()
                .navn(arbeidsforhold.getNavn())
                .orgNr(arbeidsforhold.getOrgnr())
                .adresse(AdresseDTO
                        .builder()
                        .by(arbeidsforhold.getBy())
                        .gate(arbeidsforhold.getGatenavn())
                        .land(arbeidsforhold.getLand())
                        .postnummer(arbeidsforhold.getPostnummer())
                        .build()
                )
                .build();

        var pasient = PasientDTO
                .builder()
                .ident(this.pasient.getIdent())
                .fornavn(this.pasient.getFornvan())
                .mellomnavn(this.pasient.getMellomnavn())
                .etternavn(this.pasient.getEtternavn())
//                .foedselsdato(this.pasient.getFoedselsdato())
                .navKontor("ST.HANSHAUGEN")
                .build();
        var perioder = sykemeldinger
                .stream()
                .map(value -> new PeriodeDTO(
                        value.getStartPeriode(),
                        value.getSluttPeriode().minusDays(1),
                        AktivitetDTO
                                .builder()
                                .aktivitet(value.getSykmeldingsprosent() == 100.0 ? Aktivitet.INGEN : null)
                                .grad(value.getSykmeldingsprosent().intValue())
                                .reisetilskudd(value.getReisetilskudd())
                                .build()
                ))
                .collect(Collectors.toList());


        var sender = OrganisasjonDTO
                .builder()
                .navn("Mini-Norge Legekontor")
                .orgNr("992741090")
                .adresse(AdresseDTO
                        .builder()
                        .gate("Sannergata 2")
                        .postnummer("0557")
                        .by("Oslo")
                        .build()
                ).build();

        return SykemeldingDTO
                .builder()
                .startDato(syntSykemelding.getStartDato())
                .arbeidsgiver(arbeidsgiverDTO)
                .biDiagnoser(biDiagnoser)
                .hovedDiagnose(hovedDiagnose)
                .helsepersonell(helsepersonell.toDTO())
                .mottaker(mottaker)
                .pasient(pasient)
                .perioder(perioder)
                .umiddelbarBistand(true)
                .sender(sender)
                .detaljer(DetaljerDTO
                        .builder()
                        .arbeidsforEtterEndtPeriode(firstSykemelding.getArbeidsforEtterEndtPeriode())
                        .build()
                ).build();
    }

}
