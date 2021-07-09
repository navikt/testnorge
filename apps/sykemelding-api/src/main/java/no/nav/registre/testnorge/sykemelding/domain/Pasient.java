package no.nav.registre.testnorge.sykemelding.domain;

import no.kith.xmlstds.XMLCS;
import no.kith.xmlstds.XMLCV;
import no.kith.xmlstds.XMLURL;
import no.kith.xmlstds.felleskomponent1.XMLIdent;
import no.kith.xmlstds.felleskomponent1.XMLTeleCom;
import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;
import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLNavnType;

import no.nav.testnav.libs.dto.sykemelding.v1.HelsepersonellDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.PasientDTO;

public class Pasient {

    private final XMLHelseOpplysningerArbeidsuforhet.Pasient xmlPasient;


    Pasient(PasientDTO pasientDTO, HelsepersonellDTO helsepersonellDTO) {
        xmlPasient = new XMLHelseOpplysningerArbeidsuforhet.Pasient()
                .withNavn(new XMLNavnType()
                        .withEtternavn(pasientDTO.getEtternavn())
                        .withMellomnavn(pasientDTO.getMellomnavn())
                        .withFornavn(pasientDTO.getFornavn())
                )
                .withFodselsnummer(new XMLIdent()
                        .withId(pasientDTO.getIdent())
                        .withTypeId(new XMLCV()
                                .withV("FNR")
                                .withDN("Fødselsnummer")
                                .withS("2.16.578.1.12.4.1.1.8116"))
                )
                .withKontaktInfo(new XMLTeleCom()
                        .withTypeTelecom(new XMLCS().withV("HP").withDN("Hovedtelefon"))
                        .withTeleAddress(new XMLURL().withV("tel:" + pasientDTO.getTelefon()))
                )
                .withNavnFastlege(helsepersonellDTO.getFornavn() + " " + helsepersonellDTO.getEtternavn())
                .withNAVKontor(pasientDTO.getNavKontor());
    }

    XMLHelseOpplysningerArbeidsuforhet.Pasient getXmlObject() {
        return xmlPasient;
    }
}
