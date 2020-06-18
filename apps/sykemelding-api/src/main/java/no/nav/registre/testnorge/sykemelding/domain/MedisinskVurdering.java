package no.nav.registre.testnorge.sykemelding.domain;

import no.kith.xmlstds.XMLCV;
import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLArsakType;
import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.dto.sykemelding.v1.DiagnoseDTO;

class MedisinskVurdering {

    private final XMLHelseOpplysningerArbeidsuforhet.MedisinskVurdering medisinskVurdering;

    MedisinskVurdering(LocalDate fom, DiagnoseDTO hovedDiagnose, List<DiagnoseDTO> biDiagnoser) {

        medisinskVurdering = new XMLHelseOpplysningerArbeidsuforhet.MedisinskVurdering()
                .withHovedDiagnose(new XMLHelseOpplysningerArbeidsuforhet.MedisinskVurdering.HovedDiagnose()
                        .withDiagnosekode(new XMLCV()
                                .withDN(hovedDiagnose.getDn())
                                .withS(hovedDiagnose.getS())
                                .withV(hovedDiagnose.getV())))
                .withBiDiagnoser(new XMLHelseOpplysningerArbeidsuforhet.MedisinskVurdering.BiDiagnoser()
                        .withDiagnosekode(biDiagnoser
                                .stream()
                                .map(value -> new XMLCV()
                                        .withDN(value.getDn())
                                        .withS(value.getS())
                                        .withV(value.getV())
                                )
                                .collect(Collectors.toList())
                        )
                )
                .withYrkesskade(false)
                .withYrkesskadeDato(fom)
                .withSvangerskap(false)
                .withAnnenFraversArsak(
                        new XMLArsakType().withBeskriv("Medising årsak i kategorien annet")
                )
                .withSkjermesForPasient(false);
    }

    XMLHelseOpplysningerArbeidsuforhet.MedisinskVurdering getXmlObject() {
        return medisinskVurdering;
    }
}
