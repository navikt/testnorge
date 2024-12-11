package no.nav.registre.testnorge.sykemelding.domain;

import no.nav.registre.testnorge.sykemelding.external.xmlstds.XMLCS;
import no.nav.registre.testnorge.sykemelding.external.xmlstds.XMLCV;
import no.nav.registre.testnorge.sykemelding.external.xmlstds.XMLURL;
import no.nav.registre.testnorge.sykemelding.external.xmlstds.felleskomponent1.XMLIdent;
import no.nav.registre.testnorge.sykemelding.external.xmlstds.felleskomponent1.XMLTeleCom;
import no.nav.registre.testnorge.sykemelding.external.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;
import no.nav.registre.testnorge.sykemelding.external.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLNavnType;
import no.nav.testnav.libs.dto.sykemelding.v1.HelsepersonellDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.PasientDTO;

import static java.util.Objects.nonNull;

public class Pasient {

    private final XMLHelseOpplysningerArbeidsuforhet.Pasient xmlPasient;


    Pasient(PasientDTO pasientDTO, HelsepersonellDTO helsepersonellDTO) {
        var pasient = nonNull(pasientDTO) ? pasientDTO : PasientDTO.builder()
                .ident("12508407724")
                .fornavn("Test")
                .etternavn("Testesen")
                .build();
        xmlPasient = new XMLHelseOpplysningerArbeidsuforhet.Pasient()
                .withNavn(new XMLNavnType()
                        .withEtternavn(pasient.getEtternavn())
                        .withMellomnavn(pasient.getMellomnavn())
                        .withFornavn(pasient.getFornavn())
                )
                .withFodselsnummer(new XMLIdent()
                        .withId(pasient.getIdent())
                        .withTypeId(new XMLCV()
                                .withV("FNR")
                                .withDN("Fødselsnummer")
                                .withS("2.16.578.1.12.4.1.1.8116"))
                )
                .withKontaktInfo(new XMLTeleCom()
                        .withTypeTelecom(new XMLCS().withV("HP").withDN("Hovedtelefon"))
                        .withTeleAddress(new XMLURL().withV("tel:" + pasient.getTelefon()))
                )
                .withNavnFastlege(helsepersonellDTO.getFornavn() + " " + helsepersonellDTO.getEtternavn())
                .withNAVKontor(pasient.getNavKontor());
    }

    XMLHelseOpplysningerArbeidsuforhet.Pasient getXmlObject() {
        return xmlPasient;
    }
}
