package no.nav.registre.testnorge.sykemelding.domain;

import no.kith.xmlstds.XMLCS;
import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLArsakType;
import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;

class AktivitetIkkeMulig {
    private final XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.AktivitetIkkeMulig xmlAktivitetIkkeMulig;

    AktivitetIkkeMulig(boolean manglendeTilretteleggingPaaArbeidsplassen) {
        xmlAktivitetIkkeMulig = new XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.AktivitetIkkeMulig()
                .withMedisinskeArsaker(new XMLArsakType()
                        .withArsakskode(new XMLCS()
                                .withDN("Annet")
                                .withV("3"))
                        .withBeskriv("andre årsaker til sykefravær")
                );
        if (manglendeTilretteleggingPaaArbeidsplassen) {
            xmlAktivitetIkkeMulig.withArbeidsplassen(new XMLArsakType()
                    .withArsakskode(new XMLCS()
                            .withDN("Annet")
                            .withV("1"))
                    .withBeskriv("her tilrettelegger ikke arbeidsgiver godt nok")
            );
        } else {
            xmlAktivitetIkkeMulig.withArbeidsplassen(new XMLArsakType()
                    .withArsakskode(new XMLCS()
                            .withDN("Annet")
                            .withV("9"))
                    .withBeskriv("andre årsaker til sykefravær")
            );
        }
    }

    XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.AktivitetIkkeMulig getXmlObject() {
        return xmlAktivitetIkkeMulig;
    }
}