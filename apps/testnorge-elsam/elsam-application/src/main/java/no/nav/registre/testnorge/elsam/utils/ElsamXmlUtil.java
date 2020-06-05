package no.nav.registre.testnorge.elsam.utils;

import static java.time.LocalDate.parse;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import no.kith.xmlstds.XMLCS;
import no.kith.xmlstds.XMLCV;
import no.kith.xmlstds.XMLURL;
import no.kith.xmlstds.felleskomponent1.XMLAddress;
import no.kith.xmlstds.felleskomponent1.XMLIdent;
import no.kith.xmlstds.felleskomponent1.XMLTeleCom;
import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLArsakType;
import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;
import no.kith.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLNavnType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import no.nav.registre.elsam.domain.Adresse;
import no.nav.registre.elsam.domain.Arbeidsgiver;
import no.nav.registre.elsam.domain.Detaljer;
import no.nav.registre.elsam.domain.Diagnose;
import no.nav.registre.elsam.domain.Ident;
import no.nav.registre.elsam.domain.Lege;
import no.nav.registre.elsam.domain.SykmeldingPeriode;

public class ElsamXmlUtil {

    private ElsamXmlUtil() {
        throw new IllegalStateException("Utility klasse");
    }

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static XMLHelseOpplysningerArbeidsuforhet lagHelseopplysninger(
            Ident ident,
            String syketilfelleStartDato,
            String utstedelsesdato,
            Lege lege,
            Diagnose hovedDiagnose,
            List<Diagnose> biDiagnoser,
            List<SykmeldingPeriode> perioder,
            String type,
            boolean skalLeggePaaManglendeTilrettelegging,
            Detaljer detaljer
    ) {
        final XMLHelseOpplysningerArbeidsuforhet sykmelding;

        LocalDate utstedelsesdatoNotNull = ofNullable(utstedelsesdato)
                .filter(u -> !"".equals(u))
                .map(u -> LocalDate.parse(u, dateTimeFormatter))
                .orElseGet(LocalDate::now);

        LocalDate syketilfelleStartDatoNotNull = ofNullable(syketilfelleStartDato)
                .filter(s -> !"".equals(s))
                .map(s -> LocalDate.parse(s, dateTimeFormatter))
                .orElseGet(LocalDate::now);

        if (type == null || "SM2013".equals(type)) {
            sykmelding = sykmelding(ident, syketilfelleStartDatoNotNull, lege, hovedDiagnose, biDiagnoser, utstedelsesdatoNotNull, perioder, detaljer);
        } else {
            throw new RuntimeException("smtype ikke støttet");
        }

        if (skalLeggePaaManglendeTilrettelegging) {
            sykmelding.getAktivitet().getPeriode().stream().filter(p -> p.getAktivitetIkkeMulig() != null).forEach(p -> p.setAktivitetIkkeMulig(p.getAktivitetIkkeMulig()
                    .withArbeidsplassen(new XMLArsakType()
                            .withArsakskode(new XMLCS()
                                    .withDN("Annet")
                                    .withV("1"))
                            .withBeskriv("her tilrettelegger ikke arbeidsgiver godt nok"))));
        }

        sykmelding.getKontaktMedPasient().setBehandletDato(perioder
                .stream()
                .map(SykmeldingPeriode::getFom)
                .map(fom -> parse(fom, dateTimeFormatter).atStartOfDay())
                .sorted()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sykmelding må inneholde perioder")));

        return sykmelding;
    }

    private static XMLHelseOpplysningerArbeidsuforhet sykmelding(
            Ident ident,
            LocalDate syketilfelleStartDato,
            Lege lege,
            Diagnose hovedDiagnose,
            List<Diagnose> biDiagnoser,
            LocalDate utstedelse,
            List<SykmeldingPeriode> perioder,
            Detaljer detaljer
    ) {
        LocalDate fom = parse(perioder.get(0).getFom(), dateTimeFormatter);

        return new XMLHelseOpplysningerArbeidsuforhet()
                .withSyketilfelleStartDato(syketilfelleStartDato)
                .withPasient(pasient(ident, lege))
                .withArbeidsgiver(arbeidsgiver(ident.getArbeidsgiver()))
                .withMedisinskVurdering(medisinskVurdering(fom, hovedDiagnose, biDiagnoser))
                .withAktivitet(aktivitet(perioder))
                .withPrognose(prognose(fom, detaljer))
                .withTiltak(tiltak(detaljer.getTiltakArbeidsplass(), detaljer.getTiltakNav()))
                .withKontaktMedPasient(kontaktMedPasient(utstedelse))
                .withBehandler(behandler(lege))
                .withAvsenderSystem(avsenderSystem("Synt", "1.0.0"));
    }

    private static XMLAddress adresse(Adresse adresse) {
        return new XMLAddress()
                .withType(new XMLCS()
                        .withDN("POSTADRESSE")
                        .withV("PST"))
                .withStreetAdr(adresse.getGate())
                .withPostalCode(adresse.getPostnummer())
                .withCity(adresse.getBy())
                .withCountry(new XMLCS()
                        .withDN(adresse.getLand())
                        .withV("Country"));

    }

    private static XMLHelseOpplysningerArbeidsuforhet.Behandler behandler(Lege lege) {
        return new XMLHelseOpplysningerArbeidsuforhet.Behandler()
                .withNavn(navn(lege.getFornavn(), lege.getMellomnavn(), lege.getEtternavn()))
                .withId(fodselsnummer(lege.getFnr()))
                .withAdresse(adresse(lege.getAdresse()))
                .withKontaktInfo(kontaktinfo(lege.getTelefon()));
    }

    private static XMLHelseOpplysningerArbeidsuforhet.KontaktMedPasient kontaktMedPasient(LocalDate utstedelsesdato) {
        return new XMLHelseOpplysningerArbeidsuforhet.KontaktMedPasient()
                .withBehandletDato(utstedelsesdato.atStartOfDay());
    }

    private static XMLHelseOpplysningerArbeidsuforhet.Tiltak tiltak(
            String tiltakArbeidsplass,
            String tiltakNav
    ) {
        return new XMLHelseOpplysningerArbeidsuforhet.Tiltak()
                .withTiltakArbeidsplassen(tiltakArbeidsplass)
                .withTiltakNAV(tiltakNav);
    }

    private static XMLHelseOpplysningerArbeidsuforhet.Prognose prognose(
            LocalDate fom,
            Detaljer detaljer
    ) {
        return new XMLHelseOpplysningerArbeidsuforhet.Prognose()
                .withArbeidsforEtterEndtPeriode(detaljer.getArbeidsforEtterEndtPeriode())
                .withBeskrivHensynArbeidsplassen(detaljer.getBeskrivHensynArbeidsplassen())
                .withErIArbeid(new XMLHelseOpplysningerArbeidsuforhet.Prognose.ErIArbeid()
                        .withVurderingDato(fom)
                        .withArbeidFraDato(fom)
                        .withAnnetArbeidPaSikt(true)
                        .withEgetArbeidPaSikt(true));
    }

    private static XMLHelseOpplysningerArbeidsuforhet.Aktivitet aktivitet(List<SykmeldingPeriode> perioder) {
        return new XMLHelseOpplysningerArbeidsuforhet.Aktivitet()
                .withPeriode(perioder.stream()
                        .map(ElsamXmlUtil::type)
                        .collect(toList()));
    }

    private static XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode type(SykmeldingPeriode periode) {
        XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode xmlperiode = new XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode()
                .withPeriodeFOMDato(parse(periode.getFom(), dateTimeFormatter))
                .withPeriodeTOMDato(parse(periode.getFom(), dateTimeFormatter));

        switch (periode.getType()) {
        case HUNDREPROSENT:
            return xmlperiode.withAktivitetIkkeMulig(hundreprosent());
        case GRADERT_20:
            return xmlperiode.withGradertSykmelding(gradert(20, false));
        case GRADERT_40:
            return xmlperiode.withGradertSykmelding(gradert(40, false));
        case GRADERT_50:
            return xmlperiode.withGradertSykmelding(gradert(50, false));
        case GRADERT_60:
            return xmlperiode.withGradertSykmelding(gradert(60, false));
        case GRADERT_80:
            return xmlperiode.withGradertSykmelding(gradert(80, false));
        case AVVENTENDE:
            return xmlperiode.withAvventendeSykmelding(avventende());
        case GRADERT_REISETILSKUDD:
            return xmlperiode.withGradertSykmelding(gradert(60, true));
        case BEHANDLINGSDAGER:
            return xmlperiode.withBehandlingsdager(behandlingsdager(4));
        case BEHANDLINGSDAG:
            return xmlperiode.withBehandlingsdager(behandlingsdager(1));
        case REISETILSKUDD:
            return xmlperiode.withReisetilskudd(true);
        }

        return xmlperiode.withBehandlingsdager(new XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.Behandlingsdager()
                .withAntallBehandlingsdagerUke(4));
    }

    private static XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.GradertSykmelding gradert(
            int grad,
            boolean reisetilskudd
    ) {
        return new XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.GradertSykmelding()
                .withSykmeldingsgrad(grad)
                .withReisetilskudd(reisetilskudd);
    }

    private static XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.AktivitetIkkeMulig hundreprosent() {
        return new XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.AktivitetIkkeMulig()
                .withArbeidsplassen(new XMLArsakType()
                        .withArsakskode(new XMLCS()
                                .withDN("Annet")
                                .withV("9"))
                        .withBeskriv("andre årsaker til sykefravær"))
                .withMedisinskeArsaker(new XMLArsakType()
                        .withArsakskode(new XMLCS()
                                .withDN("Annet")
                                .withV("3"))
                        .withBeskriv("andre årsaker til sykefravær"));
    }

    private static XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.AvventendeSykmelding avventende() {
        return new XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.AvventendeSykmelding()
                .withInnspillTilArbeidsgiver("Godt inspill");
    }

    private static XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.Behandlingsdager behandlingsdager(int antallBehandlingsdager) {
        return new XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.Behandlingsdager()
                .withAntallBehandlingsdagerUke(antallBehandlingsdager);
    }

    private static XMLHelseOpplysningerArbeidsuforhet.MedisinskVurdering medisinskVurdering(
            LocalDate fom,
            Diagnose hovedDiagnose,
            List<Diagnose> biDiagnoser
    ) {
        List<XMLCV> xmlBiDiagnoser = new ArrayList<>();
        if (biDiagnoser != null) {
            for (Diagnose diagnose : biDiagnoser) {
                xmlBiDiagnoser.add(new XMLCV()
                        .withDN(diagnose.getDn())
                        .withS(diagnose.getS())
                        .withV(diagnose.getV()));
            }
        }
        return new XMLHelseOpplysningerArbeidsuforhet.MedisinskVurdering()
                .withHovedDiagnose(new XMLHelseOpplysningerArbeidsuforhet.MedisinskVurdering.HovedDiagnose()
                        .withDiagnosekode(new XMLCV()
                                .withDN(hovedDiagnose.getDn())
                                .withS(hovedDiagnose.getS())
                                .withV(hovedDiagnose.getV())))
                .withBiDiagnoser(new XMLHelseOpplysningerArbeidsuforhet.MedisinskVurdering.BiDiagnoser()
                        .withDiagnosekode(xmlBiDiagnoser))
                .withYrkesskade(false)
                .withYrkesskadeDato(fom)
                .withSvangerskap(false)
                .withAnnenFraversArsak(new XMLArsakType()
                        .withBeskriv("Medising årsak i kategorien annet"))
                .withSkjermesForPasient(false);
    }

    private static XMLHelseOpplysningerArbeidsuforhet.Arbeidsgiver arbeidsgiver(Arbeidsgiver arbeidsgiver) {
        return new XMLHelseOpplysningerArbeidsuforhet.Arbeidsgiver()
                .withHarArbeidsgiver(new XMLCS()
                        .withDN("En arbeidsgiver")
                        .withV("1"))
                .withNavnArbeidsgiver(arbeidsgiver.getNavn())
                .withYrkesbetegnelse(arbeidsgiver.getYrkesbetegnelse())
                .withStillingsprosent(arbeidsgiver.getStillingsprosent().intValue());
    }

    private static XMLHelseOpplysningerArbeidsuforhet.Pasient pasient(
            Ident ident,
            Lege lege
    ) {
        return new XMLHelseOpplysningerArbeidsuforhet.Pasient()
                .withNavn(navn(ident.getFornavn(), ident.getMellomnavn(), ident.getEtternavn()))
                .withFodselsnummer(fodselsnummer(ident.getFnr()))
                .withKontaktInfo(kontaktinfo(ident.getTelefon()))
                .withNavnFastlege(lege.getFornavn() + " " + lege.getEtternavn())
                .withNAVKontor(ident.getNavKontor());
    }

    private static XMLHelseOpplysningerArbeidsuforhet.AvsenderSystem avsenderSystem(
            String systemNavn,
            String systemVersjon
    ) {
        return new XMLHelseOpplysningerArbeidsuforhet.AvsenderSystem()
                .withSystemNavn(systemNavn)
                .withSystemVersjon(systemVersjon);
    }

    private static XMLTeleCom kontaktinfo(String teleAddress) {
        return new XMLTeleCom()
                .withTypeTelecom(new XMLCS().withV("HP").withDN("Hovedtelefon"))
                .withTeleAddress(new XMLURL().withV("tel:" + teleAddress));
    }

    private static XMLIdent fodselsnummer(String fnr) {
        return new XMLIdent()
                .withId(fnr)
                .withTypeId(new XMLCV()
                        .withV("FNR")
                        .withDN("Fødselsnummer")
                        .withS("2.16.578.1.12.4.1.1.8116"));
    }

    private static XMLNavnType navn(
            String fornavn,
            String mellomnavn,
            String etternavn
    ) {
        return new XMLNavnType()
                .withFornavn(fornavn)
                .withMellomnavn(mellomnavn)
                .withEtternavn(etternavn);
    }
}
