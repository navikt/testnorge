package no.nav.registre.testnorge.sykemelding.domain;

import no.kith.xmlstds.XMLCS;
import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;

import no.nav.testnav.libs.dto.sykemelding.v1.ArbeidsgiverDTO;

public class Arbeidsgiver {
    private XMLHelseOpplysningerArbeidsuforhet.Arbeidsgiver xmlArbeidsgiver;

    Arbeidsgiver(ArbeidsgiverDTO dto) {
        xmlArbeidsgiver = new XMLHelseOpplysningerArbeidsuforhet.Arbeidsgiver()
                .withHarArbeidsgiver(new XMLCS()
                        .withDN("En arbeidsgiver")
                        .withV("1"))
                .withNavnArbeidsgiver(dto.getNavn())
                .withYrkesbetegnelse(dto.getYrkesbetegnelse())
                .withStillingsprosent(dto.getStillingsprosent().intValue());
    }

    XMLHelseOpplysningerArbeidsuforhet.Arbeidsgiver getXmlObject() {
        return xmlArbeidsgiver;
    }

}
