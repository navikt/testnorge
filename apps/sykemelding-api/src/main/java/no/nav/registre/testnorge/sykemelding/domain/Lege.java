package no.nav.registre.testnorge.sykemelding.domain;

import no.kith.xmlstds.XMLCV;
import no.kith.xmlstds.felleskomponent1.XMLAddress;
import no.kith.xmlstds.felleskomponent1.XMLIdent;
import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;
import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLNavnType;

import no.nav.registre.testnorge.dto.sykemelding.v1.LegeDTO;

class Lege {

    private final XMLHelseOpplysningerArbeidsuforhet.Behandler xmlLege;

    Lege(LegeDTO dto) {
        xmlLege = new XMLHelseOpplysningerArbeidsuforhet.Behandler()
                .withAdresse(new XMLAddress())
                .withNavn(new XMLNavnType()
                        .withFornavn(dto.getFornavn())
                        .withMellomnavn(dto.getMellomnavn())
                        .withEtternavn(dto.getEtternavn()))
                .withId(new XMLIdent()
                        .withId(dto.getIdent())
                        .withTypeId(new XMLCV()
                                .withV("FNR")
                                .withDN("Fødselsnummer")
                                .withS("2.16.578.1.12.4.1.1.8116"))
                );
    }

    XMLHelseOpplysningerArbeidsuforhet.Behandler getXmlObject() {
        return xmlLege;
    }
}
