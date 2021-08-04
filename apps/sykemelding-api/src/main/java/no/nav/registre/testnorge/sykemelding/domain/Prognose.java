package no.nav.registre.testnorge.sykemelding.domain;

import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;

import java.time.LocalDate;

import no.nav.testnav.libs.dto.sykemelding.v1.DetaljerDTO;

class Prognose {
    private final XMLHelseOpplysningerArbeidsuforhet.Prognose xmlPrognose;

    Prognose(LocalDate fom, DetaljerDTO detaljerDTO) {
        xmlPrognose = new XMLHelseOpplysningerArbeidsuforhet.Prognose()
                .withArbeidsforEtterEndtPeriode(detaljerDTO.getArbeidsforEtterEndtPeriode())
                .withBeskrivHensynArbeidsplassen(detaljerDTO.getBeskrivHensynArbeidsplassen())
                .withErIArbeid(new XMLHelseOpplysningerArbeidsuforhet.Prognose.ErIArbeid()
                        .withVurderingDato(fom)
                        .withArbeidFraDato(fom)
                        .withAnnetArbeidPaSikt(true)
                        .withEgetArbeidPaSikt(true));

    }

    XMLHelseOpplysningerArbeidsuforhet.Prognose getXmlObject() {
        return xmlPrognose;
    }
}
