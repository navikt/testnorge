package no.nav.registre.testnorge.sykemelding.domain;

import no.nav.registre.testnorge.sykemelding.external.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;

import java.time.LocalDate;

class Dokument {
    private final XMLHelseOpplysningerArbeidsuforhet xmlHelseOpplysningerArbeidsuforhet;

    Dokument(SykemeldingDTO dto, ApplicationInfo applicationInfo) {

        var aktivitet = new Aktivitet(dto.getPerioder(), dto.getManglendeTilretteleggingPaaArbeidsplassen());
        var fom = Aktivitet.getFom(aktivitet.getXmlObject());
        var fomIArbeid = Aktivitet.getFomIArbeid(aktivitet.getXmlObject());

        var pasient = new Pasient(dto.getPasient(), dto.getHelsepersonell());
        var arbeidsgiver = new Arbeidsgiver(dto.getArbeidsgiver());
        var medisinskVurdering = new MedisinskVurdering(fom, dto.getHovedDiagnose(), dto.getBiDiagnoser());

        var prognose = new Prognose(fomIArbeid, dto.getDetaljer());
        var helsepersonell = new Helsepersonell(dto.getHelsepersonell());
        var kontaktMedPasient = new KontaktMedPasient(dto.getKontaktMedPasient());

        xmlHelseOpplysningerArbeidsuforhet = new XMLHelseOpplysningerArbeidsuforhet()
                .withSyketilfelleStartDato(dto.getStartDato())
                .withPasient(pasient.getXmlObject())
                .withArbeidsgiver(arbeidsgiver.getXmlObject())
                .withMedisinskVurdering(medisinskVurdering.getXmlObject())
                .withAktivitet(aktivitet.getXmlObject())
                .withPrognose(prognose.getXmlObject())
                .withUtdypendeOpplysninger(new UtdypendeOpplysninger(dto.getUtdypendeOpplysninger()).getXmlObject())
                .withTiltak(
                        new XMLHelseOpplysningerArbeidsuforhet.Tiltak()
                                .withTiltakArbeidsplassen(dto.getDetaljer().getTiltakArbeidsplass())
                                .withTiltakNAV(dto.getDetaljer().getTiltakNav())
                                .withAndreTiltak(dto.getDetaljer().getAndreTiltak())
                )
                .withKontaktMedPasient(kontaktMedPasient.getXmlObject())
                .withBehandler(helsepersonell.getXmlObject())
                .withAvsenderSystem(new XMLHelseOpplysningerArbeidsuforhet.AvsenderSystem()
                        .withSystemNavn(applicationInfo.getName())
                        .withSystemVersjon(applicationInfo.getVersion())
                );

        if (dto.getUmiddelbarBistand() != null) {
            xmlHelseOpplysningerArbeidsuforhet.withMeldingTilNav(
                    new XMLHelseOpplysningerArbeidsuforhet.MeldingTilNav()
                            .withBistandNAVUmiddelbart(dto.getUmiddelbarBistand())
            );
        }
    }

    public LocalDate getFom() {
        return Aktivitet.getFom(xmlHelseOpplysningerArbeidsuforhet.getAktivitet());
    }

    public LocalDate getTom() {
        return Aktivitet.getTom(xmlHelseOpplysningerArbeidsuforhet.getAktivitet());
    }

    XMLHelseOpplysningerArbeidsuforhet getXmlObject() {
        return xmlHelseOpplysningerArbeidsuforhet;
    }
}
