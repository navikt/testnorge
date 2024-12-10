package no.nav.registre.testnorge.sykemelding.domain;

import no.nav.registre.testnorge.sykemelding.external.xmlstds.XMLCS;
import no.nav.registre.testnorge.sykemelding.external.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLDynaSvarType;
import no.nav.registre.testnorge.sykemelding.external.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;
import no.nav.testnav.libs.dto.sykemelding.v1.UtdypendeOpplysningerDTO;

import java.util.List;

public class UtdypendeOpplysninger {

    private final XMLHelseOpplysningerArbeidsuforhet.UtdypendeOpplysninger xmlUtdypendeOpplysninger;

    UtdypendeOpplysninger(List<UtdypendeOpplysningerDTO> utdypendeOpplysninger) {
        xmlUtdypendeOpplysninger = new XMLHelseOpplysningerArbeidsuforhet.UtdypendeOpplysninger()
                .withSpmGruppe(utdypendeOpplysninger.stream()
                        .map(value ->
                                new XMLHelseOpplysningerArbeidsuforhet.UtdypendeOpplysninger.SpmGruppe()
                                        .withSpmGruppeId(value.getSpmGruppeId())
                                        .withSpmGruppeTekst(value.getSpmGruppeTekst())
                                        .withSpmSvar(value.getSpmSvar().stream()
                                                .map(spmSvar -> new XMLDynaSvarType()
                                                        .withSpmId(spmSvar.getSpmId())
                                                        .withSpmTekst(spmSvar.getSpmTekst())
                                                        .withSvarTekst(spmSvar.getSvarTekst())
                                                        .withRestriksjon(new XMLDynaSvarType.Restriksjon(
                                                                List.of(new XMLCS()
                                                                        .withV(Integer.toString(spmSvar.getRestriksjon().ordinal()))
                                                                        .withDN(spmSvar.getRestriksjon().name())))))
                                                .toList()))
                        .toList());
    }

    XMLHelseOpplysningerArbeidsuforhet.UtdypendeOpplysninger getXmlObject() {
        return xmlUtdypendeOpplysninger;
    }
}
