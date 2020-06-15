package no.nav.registre.testnorge.sykemelding.domain;

import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;

import no.nav.registre.testnorge.dto.sykemelding.v1.PeriodeDTO;

class Periode {
    private final XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode xmlPeriode;

    Periode(PeriodeDTO dto, boolean manglendeTilretteleggingPaaArbeidsplassen) {
        xmlPeriode = new XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode()
                .withPeriodeFOMDato(dto.getFom())
                .withPeriodeTOMDato(dto.getTom());

        switch (dto.getAktivitetGrad()) {
            case INGEN:
                AktivitetIkkeMulig aktivitetIkkeMulig = new AktivitetIkkeMulig(manglendeTilretteleggingPaaArbeidsplassen);
                xmlPeriode.withAktivitetIkkeMulig(aktivitetIkkeMulig.getXmlObject());
                break;
            case GRADERT_20:
            case GRADERT_40:
            case GRADERT_50:
            case GRADERT_60:
            case GRADERT_80:
            case GRADERT_REISETILSKUDD:
                xmlPeriode.withGradertSykmelding(
                        new XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.GradertSykmelding()
                                .withSykmeldingsgrad(dto.getAktivitetGrad().getGrad())
                                .withReisetilskudd(dto.getAktivitetGrad().getReisetilskudd())
                );
                break;
            case AVVENTENDE:
                xmlPeriode.withAvventendeSykmelding(
                        new XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.AvventendeSykmelding()
                                .withInnspillTilArbeidsgiver("Godt inspill")
                );
                break;
            case BEHANDLINGSDAGER:
            case BEHANDLINGSDAG:
                xmlPeriode.withBehandlingsdager(
                        new XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.Behandlingsdager()
                                .withAntallBehandlingsdagerUke(dto.getAktivitetGrad().getBehandlingsdager())
                );
                break;
            case REISETILSKUDD:
                xmlPeriode.withReisetilskudd(dto.getAktivitetGrad().getReisetilskudd());
                break;
            default:
                xmlPeriode.withBehandlingsdager(
                        new XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.Behandlingsdager()
                                .withAntallBehandlingsdagerUke(4)
                );
        }
    }

    XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode getXmlObject() {
        return xmlPeriode;
    }
}
