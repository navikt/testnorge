package no.nav.registre.testnorge.sykemelding.domain;

import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import no.nav.registre.testnorge.dto.sykemelding.v1.PeriodeDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.SykemeldingDTO;

class Dokument {
    private final XMLHelseOpplysningerArbeidsuforhet xmlHelseOpplysningerArbeidsuforhet;

    Dokument(SykemeldingDTO dto, ApplicationInfo applicationInfo) {

        LocalDate fom = findFom(dto.getPerioder());
        Pasient pasient = new Pasient(dto.getPasient(), dto.getLege());
        Arbeidsgiver arbeidsgiver = new Arbeidsgiver(dto.getArbeidsgiver());
        MedisinskVurdering medisinskVurdering = new MedisinskVurdering(fom, dto.getHovedDiagnose(), dto.getBiDiagnoser());
        Aktivitet aktivitet = new Aktivitet(dto.getPerioder(), dto.getManglendeTilretteleggingPaaArbeidsplassen());
        Prognose prognose = new Prognose(fom, dto.getDetaljer());
        Lege lege = new Lege(dto.getLege());

        xmlHelseOpplysningerArbeidsuforhet = new XMLHelseOpplysningerArbeidsuforhet()
                .withSyketilfelleStartDato(dto.getStartDato())
                .withPasient(pasient.getXmlObject())
                .withArbeidsgiver(arbeidsgiver.getXmlObject())
                .withMedisinskVurdering(medisinskVurdering.getXmlObject())
                .withAktivitet(aktivitet.getXmlObject())
                .withPrognose(prognose.getXmlObject())
                .withTiltak(
                        new XMLHelseOpplysningerArbeidsuforhet.Tiltak()
                                .withTiltakArbeidsplassen(dto.getDetaljer().getTiltakArbeidsplass())
                                .withTiltakNAV(dto.getDetaljer().getTiltakNav())
                )
                .withKontaktMedPasient(
                        new XMLHelseOpplysningerArbeidsuforhet.KontaktMedPasient()
                                .withBehandletDato(fom.atStartOfDay())
                )
                .withBehandler(lege.getXmlObject())
                .withAvsenderSystem(new XMLHelseOpplysningerArbeidsuforhet.AvsenderSystem()
                        .withSystemNavn(applicationInfo.getName())
                        .withSystemVersjon(applicationInfo.getVersion())
                );
    }

    private static LocalDate findFom(List<PeriodeDTO> perioder) {
        return perioder.stream()
                .map(PeriodeDTO::getFom)
                .min(Comparator.comparing(LocalDate::toEpochDay))
                .orElseThrow();
    }

    XMLHelseOpplysningerArbeidsuforhet getXmlObject() {
        return xmlHelseOpplysningerArbeidsuforhet;
    }
}
