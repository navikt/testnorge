package no.nav.registre.testnorge.sykemelding.domain;

import no.nav.registre.testnorge.sykemelding.external.xmlstds.XMLCS;
import no.nav.registre.testnorge.sykemelding.external.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;
import no.nav.testnav.libs.dto.sykemelding.v1.ArbeidsgiverDTO;

import static java.util.Objects.nonNull;

public class Arbeidsgiver {
    private final XMLHelseOpplysningerArbeidsuforhet.Arbeidsgiver xmlArbeidsgiver;

    Arbeidsgiver(ArbeidsgiverDTO dto) {
        xmlArbeidsgiver = new XMLHelseOpplysningerArbeidsuforhet.Arbeidsgiver()
                .withHarArbeidsgiver(new XMLCS()
                        .withDN(nonNull(dto) ? "En arbeidsgiver" : "Ingen arbeidsgiver")
                        .withV(nonNull(dto) ? "1" : "3"))
                .withNavnArbeidsgiver(nonNull(dto) ? dto.getNavn() : null)
                .withYrkesbetegnelse(nonNull(dto) ? dto.getYrkesbetegnelse() : null)
                .withStillingsprosent(nonNull(dto) ? getStillingsprosent(dto) : null);
    }

    private static int getStillingsprosent(ArbeidsgiverDTO dto) {
        return nonNull(dto.getStillingsprosent()) ? dto.getStillingsprosent().intValue() : 100;
    }

    XMLHelseOpplysningerArbeidsuforhet.Arbeidsgiver getXmlObject() {
        return xmlArbeidsgiver;
    }
}
