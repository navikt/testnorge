package no.nav.registre.inntektsmeldingstub.service;

import no.nav.registre.inntektsmeldingstub.database.model.Arbeidsforhold;
import no.nav.registre.inntektsmeldingstub.database.model.Arbeidsgiver;
import no.nav.registre.inntektsmeldingstub.database.model.DelvisFravaer;
import no.nav.registre.inntektsmeldingstub.database.model.EndringIRefusjon;
import no.nav.registre.inntektsmeldingstub.database.model.GraderingIForeldrepenger;
import no.nav.registre.inntektsmeldingstub.database.model.Inntektsmelding;
import no.nav.registre.inntektsmeldingstub.database.model.NaturalytelseDetaljer;
import no.nav.registre.inntektsmeldingstub.database.model.Periode;
import no.nav.registre.inntektsmeldingstub.database.model.UtsettelseAvForeldrepenger;
import no.nav.registre.inntektsmeldingstub.service.rs.RsArbeidsforhold;
import no.nav.registre.inntektsmeldingstub.service.rs.RsArbeidsgiver;
import no.nav.registre.inntektsmeldingstub.service.rs.RsArbeidsgiverPrivat;
import no.nav.registre.inntektsmeldingstub.service.rs.RsAvsendersystem;
import no.nav.registre.inntektsmeldingstub.service.rs.RsDelvisFravaer;
import no.nav.registre.inntektsmeldingstub.service.rs.RsEndringIRefusjon;
import no.nav.registre.inntektsmeldingstub.service.rs.RsGraderingIForeldrepenger;
import no.nav.registre.inntektsmeldingstub.service.rs.RsInntekt;
import no.nav.registre.inntektsmeldingstub.service.rs.RsInntektsmelding;
import no.nav.registre.inntektsmeldingstub.service.rs.RsKontaktinformasjon;
import no.nav.registre.inntektsmeldingstub.service.rs.RsNaturaYtelseDetaljer;
import no.nav.registre.inntektsmeldingstub.service.rs.RsOmsorgspenger;
import no.nav.registre.inntektsmeldingstub.service.rs.RsPeriode;
import no.nav.registre.inntektsmeldingstub.service.rs.RsRefusjon;
import no.nav.registre.inntektsmeldingstub.service.rs.RsSykepengerIArbeidsgiverperioden;
import no.nav.registre.inntektsmeldingstub.service.rs.RsUtsettelseAvForeldrepenger;

import java.util.ArrayList;
import java.util.List;

public class DBToRestMapper {

    public static RsInntektsmelding mapDBMelding(Inntektsmelding melding) {
        RsInntektsmelding.RsInntektsmeldingBuilder tmp = RsInntektsmelding.builder()
                .pleiepengerPerioder(mapPeriodeListe(melding.getPleiepengerPeriodeListe()))
                .aarsakTilInnsending(melding.getAarsakTilInnsending())
                .arbeidsforhold(mapArbeidsforhold(melding.getArbeidsforhold()))
                .arbeidstakerFnr(melding.getArbeidstakerFnr())
                .avsendersystem(RsAvsendersystem.builder()
                        .innsendingstidspunkt(melding.getInnsendingstidspunkt())
                        .systemversjon(melding.getAvsendersystemVersjon())
                        .systemnavn(melding.getAvsendersystemNavn())
                        .build())
                .naerRelasjon(melding.isNaerRelasjon())
                .ytelse(melding.getYtelse())
                .omsorgspenger(RsOmsorgspenger.builder()
                        .harUtbetaltPliktigeDager(melding.isOmsorgHarUtbetaltPliktigeDager())
                        .delvisFravaersListe(mapDelvisFravaersListe(melding.getOmsorgspengerDelvisFravaersListe()))
                        .fravaersPerioder(mapPeriodeListe(melding.getOmsorgspengerFravaersPeriodeListe()))
                        .build())
                .gjenopptakelseNaturalytelseListe(mapNaturaytelseDetaljerListe(melding.getGjenopptakelseNaturalytelseListe()))
                .opphoerAvNaturalytelseListe(mapNaturaytelseDetaljerListe(melding.getOpphoerAvNaturalytelseListe()))
                .refusjon(RsRefusjon.builder()
                        .endringIRefusjonListe(mapEndringIRefusjonListe(melding.getEndringIRefusjonListe()))
                        .refusjonsopphoersdato(melding.getRefusjonsopphoersdato())
                        .refusjonsbeloepPrMnd(melding.getRefusjonsbeloepPrMnd())
                        .build())
                .startdatoForeldrepengeperiode(melding.getStartdatoForeldrepengeperiode())
                .sykepengerIArbeidsgiverperioden(RsSykepengerIArbeidsgiverperioden.builder()
                        .bruttoUtbetalt(melding.getSykepengerBruttoUtbetalt())
                        .begrunnelseForReduksjonEllerIkkeUtbetalt(melding.getSykepengerBegrunnelseForReduksjonEllerIkkeUtbetalt())
                        .arbeidsgiverperiodeListe(mapPeriodeListe(melding.getSykepengerPerioder()))
                        .build());

        if (melding.getPrivatArbeidsgiver().isPresent()) { mapArbeidsgiver(tmp, melding.getPrivatArbeidsgiver().get()); }
        else if (melding.getArbeidsgiver().isPresent()) { mapArbeidsgiver(tmp, melding.getArbeidsgiver().get()); }

        return tmp.build();
    }

    private static RsArbeidsforhold mapArbeidsforhold(Arbeidsforhold arbeidsforhold) {
        return RsArbeidsforhold.builder()
                .foersteFravaersdag(arbeidsforhold.getFoersteFravaersdag())
                .arbeidsforholdId(arbeidsforhold.getArbeidforholdsId())
                .beregnetInntekt(RsInntekt.builder()
                        .aarsakVedEndring(arbeidsforhold.getAarsakVedEndring())
                        .beloep(arbeidsforhold.getBeloep())
                        .build())
                .utsettelseAvForeldrepengerListe(mapUtsettelseAvForeldrepengerListe(arbeidsforhold.getUtsettelseAvForeldrepengerListe()))
                .graderingIForeldrepengerListe(mapGraderingiForeldrepengerListe(arbeidsforhold.getGraderingIForeldrepengerListe()))
                .avtaltFerieListe(mapPeriodeListe(arbeidsforhold.getAvtaltFerieListe()))
                .build();
    }

    private static List<RsEndringIRefusjon> mapEndringIRefusjonListe(List<EndringIRefusjon> liste) {
        List<RsEndringIRefusjon> rsListe = new ArrayList<>(liste.size());
        liste.forEach(o -> rsListe.add(RsEndringIRefusjon.builder()
                .endringsdato(o.getEndringsDato())
                .refusjonsbeloepPrMnd(o.getRefusjonsbeloepPrMnd())
                .build()));
        return rsListe;
    }

    private static List<RsNaturaYtelseDetaljer> mapNaturaytelseDetaljerListe(List<NaturalytelseDetaljer> liste) {
        List<RsNaturaYtelseDetaljer> rsListe = new ArrayList<>(liste.size());
        liste.forEach(o -> rsListe.add(RsNaturaYtelseDetaljer.builder()
                .naturaytelseType(o.getType())
                .beloepPrMnd(o.getBeloepPrMnd())
                .fom(o.getFom())
                .build()));
        return rsListe;
    }

    private static List<RsDelvisFravaer> mapDelvisFravaersListe(List<DelvisFravaer> liste) {
        List<RsDelvisFravaer> rsListe = new ArrayList<>(liste.size());
        liste.forEach(o -> rsListe.add(RsDelvisFravaer.builder()
                .timer(o.getTimer()).dato(o.getDato()).build()));
        return rsListe;
    }

    private static List<RsGraderingIForeldrepenger> mapGraderingiForeldrepengerListe(List<GraderingIForeldrepenger> liste) {
        List<RsGraderingIForeldrepenger> rsListe = new ArrayList<>(liste.size());
        liste.forEach(o -> rsListe.add(RsGraderingIForeldrepenger.builder()
                .periode(RsPeriode.builder().fom(o.getPeriode().getFom()).tom(o.getPeriode().getTom()).build())
                .arbeidstidprosent(o.getGradering())
                .build()));
        return rsListe;
    }

    private static List<RsUtsettelseAvForeldrepenger> mapUtsettelseAvForeldrepengerListe(List<UtsettelseAvForeldrepenger> liste) {
        List<RsUtsettelseAvForeldrepenger> rsListe = new ArrayList<>(liste.size());
        liste.forEach(o -> rsListe.add(RsUtsettelseAvForeldrepenger.builder()
                .periode(RsPeriode.builder().fom(o.getPeriode().getFom()).tom(o.getPeriode().getTom()).build())
                .aarsakTilUtsettelse(o.getAarsakTilUtsettelse())
                .build()));
        return rsListe;
    }

    private static List<RsPeriode> mapPeriodeListe(List<Periode> perioder) {
        List<RsPeriode> rsPerioder = new ArrayList<>(perioder.size());
        perioder.forEach(periode -> rsPerioder.add(
                RsPeriode.builder().fom(periode.getFom()).tom(periode.getTom()).build()));
        return rsPerioder;
    }

    private static void mapArbeidsgiver(RsInntektsmelding.RsInntektsmeldingBuilder melding, Arbeidsgiver arbeidsgiver) {
        switch (arbeidsgiver.getVirksomhetsnummer().length()) {
            case 9:
                melding.arbeidsgiver(RsArbeidsgiver.builder()
                        .virksomhetsnummer(arbeidsgiver.getVirksomhetsnummer())
                        .kontaktinformasjon(RsKontaktinformasjon.builder()
                                .kontaktinformasjonNavn(arbeidsgiver.getKontaktinformasjonNavn())
                                .telefonnummer(arbeidsgiver.getTelefonnummer())
                                .build())
                        .build());
                break;
            case 11:
                melding.arbeidsgiverPrivat(RsArbeidsgiverPrivat.builder()
                        .arbeidsgiverFnr(arbeidsgiver.getVirksomhetsnummer())
                        .kontaktinformasjon(RsKontaktinformasjon.builder()
                                .kontaktinformasjonNavn(arbeidsgiver.getKontaktinformasjonNavn())
                                .telefonnummer(arbeidsgiver.getTelefonnummer())
                                .build())
                        .build());
                break;
            default: break;
        }
    }

}
