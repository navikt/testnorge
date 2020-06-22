package no.nav.registre.testnorge.sykemelding.domain;

import no.kith.xmlstds.XMLCS;
import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLArsakType;
import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;

class AktivitetIkkeMulig {
    private final XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.AktivitetIkkeMulig xmlAktivitetIkkeMulig;

    private static final String ANNET = "Annet";

    AktivitetIkkeMulig(boolean manglendeTilretteleggingPaaArbeidsplassen) {
        xmlAktivitetIkkeMulig = new XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.AktivitetIkkeMulig()
                .withMedisinskeArsaker(new XMLArsakType()
                        .withArsakskode(new XMLCS()
                                .withDN(ANNET)
                                .withV("3"))
                        .withBeskriv("andre årsaker til sykefravær")
                );
        if (manglendeTilretteleggingPaaArbeidsplassen) {
            xmlAktivitetIkkeMulig.withArbeidsplassen(new XMLArsakType()
                    .withArsakskode(new XMLCS()
                            .withDN(ANNET)
                            .withV("1"))
                    .withBeskriv("her tilrettelegger ikke arbeidsgiver godt nok")
            );
        } else {
            xmlAktivitetIkkeMulig.withArbeidsplassen(new XMLArsakType()
                    .withArsakskode(new XMLCS()
                            .withDN(ANNET)
                            .withV("9"))
                    .withBeskriv("andre årsaker til sykefravær")
            );
        }
    }

    XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.AktivitetIkkeMulig getXmlObject() {
        return xmlAktivitetIkkeMulig;
    }
}