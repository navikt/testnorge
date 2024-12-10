package no.nav.registre.testnorge.sykemelding.domain;

import no.nav.registre.testnorge.sykemelding.external.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;
import no.nav.testnav.libs.dto.sykemelding.v1.KontaktMedPasientDTO;

class KontaktMedPasient {

    private final XMLHelseOpplysningerArbeidsuforhet.KontaktMedPasient xmlKontaktMedPasient;

    KontaktMedPasient(KontaktMedPasientDTO dto) {
        xmlKontaktMedPasient = new XMLHelseOpplysningerArbeidsuforhet.KontaktMedPasient()
                .withKontaktDato(dto.getKontaktDato())
                .withBegrunnIkkeKontakt(dto.getBegrunnelseIkkeKontakt());
    }

    XMLHelseOpplysningerArbeidsuforhet.KontaktMedPasient getXmlObject() {
        return xmlKontaktMedPasient;
    }
}
