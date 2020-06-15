package no.nav.registre.testnorge.sykemelding.domain;

import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.dto.sykemelding.v1.PeriodeDTO;

class Aktivitet {
    private final XMLHelseOpplysningerArbeidsuforhet.Aktivitet xmlAktivitet;

    Aktivitet(List<PeriodeDTO> dtos, boolean manglendeTilretteleggingPaaArbeidsplassen) {
        this.xmlAktivitet = new XMLHelseOpplysningerArbeidsuforhet.Aktivitet()
                .withPeriode(
                        dtos.stream()
                                .map(value -> new Periode(value, manglendeTilretteleggingPaaArbeidsplassen))
                                .map(Periode::getXmlObject)
                                .collect(Collectors.toList())
                );
    }


    XMLHelseOpplysningerArbeidsuforhet.Aktivitet getXmlObject() {
        return xmlAktivitet;
    }
}
