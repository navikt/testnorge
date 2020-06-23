package no.nav.registre.testnorge.synt.sykemelding.domain;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.dto.sykemelding.v1.AdresseDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.Aktivitet;
import no.nav.registre.testnorge.dto.sykemelding.v1.AktivitetDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.ArbeidsgiverDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.DetaljerDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.DiagnoseDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.PasientDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.PeriodeDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.SykemeldingDTO;
import no.nav.registre.testnorge.dto.synt.sykemelding.v1.SyntSykemeldingDTO;
import no.nav.registre.testnorge.synt.sykemelding.consumer.dto.SyntDiagnoserDTO;
import no.nav.registre.testnorge.synt.sykemelding.consumer.dto.SyntSykemeldingHistorikkDTO;

@RequiredArgsConstructor
public class Sykemelding {
    private final SyntSykemeldingHistorikkDTO historikk;
    private final SyntSykemeldingDTO syntSykemelding;
    private final Lege lege;


    public String getIdent() {
        return syntSykemelding.getPasient().getIdent();
    }

    public SykemeldingDTO toDTO() {
        var arbeidsgiverDTO = ArbeidsgiverDTO
                .builder()
                .navn(syntSykemelding.getArbeidsgiver().getNavn())
                .stillingsprosent(syntSykemelding.getArbeidsgiver().getStillingsprosent())
                .yrkesbetegnelse(syntSykemelding.getArbeidsgiver().getYrkesbetegnelse())
                .build();

        var sykemeldinger = historikk.getSykmeldinger();
        var firstSykemelding = sykemeldinger.get(0);

        List<SyntDiagnoserDTO> syntBiDiagnoser = firstSykemelding.getBiDiagnoser() != null
                ? firstSykemelding.getBiDiagnoser()
                : Collections.emptyList();

        var biDiagnoser = syntBiDiagnoser.stream()
                .map(value -> DiagnoseDTO.builder().dn(value.getDn()).s(value.getS()).v(value.getV()).build())
                .collect(Collectors.toList());

        var hovedDiagnose = DiagnoseDTO
                .builder()
                .dn(firstSykemelding.getHovedDiagnose().getDn())
                .s(firstSykemelding.getHovedDiagnose().getS())
                .v(firstSykemelding.getHovedDiagnose().getV())
                .build();

        var mottaker = OrganisasjonDTO
                .builder()
                .navn(syntSykemelding.getArbeidsgiver().getNavn())
                .orgNr(syntSykemelding.getArbeidsgiver().getOrgnr())
                .adresse(AdresseDTO
                        .builder()
                        .by(syntSykemelding.getArbeidsgiver().getBy())
                        .gate(syntSykemelding.getArbeidsgiver().getGatenavn())
                        .land(syntSykemelding.getArbeidsgiver().getLand())
                        .postnummer(syntSykemelding.getArbeidsgiver().getPostnummer())
                        .build()
                )
                .build();

        var pasient = PasientDTO
                .builder()
                .ident(syntSykemelding.getPasient().getIdent())
                .fornavn(syntSykemelding.getPasient().getFornavn())
                .mellomnavn(syntSykemelding.getPasient().getMellomnavn())
                .etternavn(syntSykemelding.getPasient().getEtternavn())
                .foedselsdato(syntSykemelding.getPasient().getFoedselsdato())
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
                .lege(lege.toDTO())
                .mottaker(mottaker)
                .pasient(pasient)
                .perioder(perioder)
                .umidelbarBistand(true)
                .sender(sender)
                .detaljer(DetaljerDTO
                        .builder()
                        .arbeidsforEtterEndtPeriode(firstSykemelding.getArbeidsforEtterEndtPeriode())
                        .build()
                ).build();
    }

}
