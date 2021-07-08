package no.nav.registre.testnorge.sykemelding.domain;

import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;

import no.nav.testnav.libs.dto.sykemelding.v1.Aktivitet;
import no.nav.testnav.libs.dto.sykemelding.v1.AktivitetDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.PeriodeDTO;

class Periode {
    private final XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode xmlPeriode;

    Periode(PeriodeDTO dto, boolean manglendeTilretteleggingPaaArbeidsplassen) {
        xmlPeriode = new XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode()
                .withPeriodeFOMDato(dto.getFom())
                .withPeriodeTOMDato(dto.getTom());

        AktivitetDTO aktivitet = dto.getAktivitet();
        if (aktivitet.getAktivitet() == Aktivitet.INGEN) {
            AktivitetIkkeMulig aktivitetIkkeMulig = new AktivitetIkkeMulig(manglendeTilretteleggingPaaArbeidsplassen);
            xmlPeriode.withAktivitetIkkeMulig(aktivitetIkkeMulig.getXmlObject());
        } else if (aktivitet.getAktivitet() == Aktivitet.AVVENTENDE) {
            xmlPeriode.withAvventendeSykmelding(
                    new XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.AvventendeSykmelding()
                            .withInnspillTilArbeidsgiver("Godt innspill")
            );
        } else if (dto.getAktivitet().getGrad() != null) {
            xmlPeriode.withGradertSykmelding(
                    new XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.GradertSykmelding()
                            .withSykmeldingsgrad(dto.getAktivitet().getGrad())
                            .withReisetilskudd(dto.getAktivitet().getReisetilskudd() != null && dto.getAktivitet().getReisetilskudd())
            );
        } else if (dto.getAktivitet().getBehandlingsdager() != null) {
            xmlPeriode.withBehandlingsdager(
                    new XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.Behandlingsdager()
                            .withAntallBehandlingsdagerUke(dto.getAktivitet().getBehandlingsdager())
            );
        } else if (dto.getAktivitet().getReisetilskudd() != null) {
            xmlPeriode.withReisetilskudd(dto.getAktivitet().getReisetilskudd());
        } else {
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
