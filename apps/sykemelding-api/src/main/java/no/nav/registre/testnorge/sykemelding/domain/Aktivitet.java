package no.nav.registre.testnorge.sykemelding.domain;

import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;

import java.time.LocalDate;
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


    static LocalDate getFom(XMLHelseOpplysningerArbeidsuforhet.Aktivitet xml){
        return xml.getPeriode()
                .stream()
                .map(XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode::getPeriodeFOMDato)
                .min(LocalDate::compareTo)
                .orElseThrow();
    }

    static LocalDate getTom(XMLHelseOpplysningerArbeidsuforhet.Aktivitet xml) {
        return xml.getPeriode()
                .stream()
                .map(XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode::getPeriodeTOMDato)
                .max(LocalDate::compareTo)
                .orElseThrow();
    }

    XMLHelseOpplysningerArbeidsuforhet.Aktivitet getXmlObject() {
        return xmlAktivitet;
    }
}
