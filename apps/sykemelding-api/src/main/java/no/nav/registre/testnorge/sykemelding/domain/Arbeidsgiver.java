package no.nav.registre.testnorge.sykemelding.domain;

import no.kith.xmlstds.XMLCS;
import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;

import no.nav.testnav.libs.dto.sykemelding.v1.ArbeidsgiverDTO;

import static java.util.Objects.nonNull;

public class Arbeidsgiver {
    private XMLHelseOpplysningerArbeidsuforhet.Arbeidsgiver xmlArbeidsgiver;

    Arbeidsgiver(ArbeidsgiverDTO dto) {
        xmlArbeidsgiver = new XMLHelseOpplysningerArbeidsuforhet.Arbeidsgiver()
                .withHarArbeidsgiver(new XMLCS()
                        .withDN("En arbeidsgiver")
                        .withV("1"))
                .withNavnArbeidsgiver(dto.getNavn())
                .withYrkesbetegnelse(dto.getYrkesbetegnelse())
                .withStillingsprosent(nonNull(dto.getStillingsprosent()) ? dto.getStillingsprosent().intValue() : 100);
    }

    XMLHelseOpplysningerArbeidsuforhet.Arbeidsgiver getXmlObject() {
        return xmlArbeidsgiver;
    }

}
