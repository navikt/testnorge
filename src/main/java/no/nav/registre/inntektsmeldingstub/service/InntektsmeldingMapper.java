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
import no.nav.registre.inntektsmeldingstub.service.rs.RsDelvisFravaer;
import no.nav.registre.inntektsmeldingstub.service.rs.RsEndringIRefusjon;
import no.nav.registre.inntektsmeldingstub.service.rs.RsGraderingIForeldrepenger;
import no.nav.registre.inntektsmeldingstub.service.rs.RsInntekt;
import no.nav.registre.inntektsmeldingstub.service.rs.RsInntektsmelding;
import no.nav.registre.inntektsmeldingstub.service.rs.RsNaturaYtelseDetaljer;
import no.nav.registre.inntektsmeldingstub.service.rs.RsOmsorgspenger;
import no.nav.registre.inntektsmeldingstub.service.rs.RsPeriode;
import no.nav.registre.inntektsmeldingstub.service.rs.RsRefusjon;
import no.nav.registre.inntektsmeldingstub.service.rs.RsSykepengerIArbeidsgiverperioden;
import no.nav.registre.inntektsmeldingstub.service.rs.RsUtsettelseAvForeldrepenger;

import java.util.Collections;
import java.util.stream.Collectors;

public class InntektsmeldingMapper {

    public static Inntektsmelding.InntektsmeldingBuilder map201809melding(RsInntektsmelding melding) {
        return Inntektsmelding.builder()
                .ytelse(melding.getYtelse())
                .arbeidstakerFnr(melding.getArbeidstakerFnr())
                .aarsakTilInnsending(melding.getAarsakTilInnsending())
                .avsendersystemNavn(melding.getAvsendersystem().getSystemnavn())
                .avsendersystemVersjon(melding.getAvsendersystem().getSystemversjon())
                .innsendingstidspunkt(melding.getAvsendersystem().getInnsendingstidspunkt().orElse(null))
                .naerRelasjon(melding.isNaerRelasjon())
                .arbeidsgiver(melding.getArbeidsgiver().map(InntektsmeldingMapper::mapArbeidsgiver).orElse(null))
                .arbeidsforhold(InntektsmeldingMapper.mapArbeidsforhold(melding.getArbeidsforhold()))
                .refusjonsopphoersdato(melding.getRefusjon().flatMap(RsRefusjon::getRefusjonsopphoersdato).orElse(null))
                .refusjonsbeloepPrMnd(melding.getRefusjon().flatMap(RsRefusjon::getRefusjonsbeloepPrMnd).orElse(0.0))
                .endringIRefusjonListe(melding.getRefusjon().flatMap(refusjon -> refusjon.getEndringIRefusjonListe()
                        .map(liste -> liste.stream().map(InntektsmeldingMapper::mapEndringIRefusjon).collect(Collectors.toList())))
                        .orElse(Collections.emptyList()))
                .omsorgspengerFravaersPeriodeListe(melding.getOmsorgspenger().flatMap(omsorgspenger -> omsorgspenger.getFravaersPerioder()
                        .map(list -> list.stream().map(InntektsmeldingMapper::mapPeriode).collect(Collectors.toList())))
                        .orElse(Collections.emptyList()))
                .omsorgspengerDelvisFravaersListe(melding.getOmsorgspenger().flatMap(omsorgspenger -> omsorgspenger.getDelvisFravaersListe()
                        .map(list -> list.stream().map(InntektsmeldingMapper::mapDelvisFravaer).collect(Collectors.toList())))
                        .orElse(Collections.emptyList()))
                .omsorgHarUtbetaltPliktigeDager(melding.getOmsorgspenger().flatMap(RsOmsorgspenger::getHarUtbetaltPliktigeDager).orElse(true))
                .sykepengerPerioder(melding.getSykepengerIArbeidsgiverPerioden().flatMap(sykepenger -> sykepenger.getArbeidsgiverperiodeListe()
                        .map(liste -> liste.stream().map(InntektsmeldingMapper::mapPeriode).collect(Collectors.toList())))
                        .orElse(Collections.emptyList()))
                .sykepengerBruttoUtbetalt(melding.getSykepengerIArbeidsgiverPerioden()
                        .flatMap(RsSykepengerIArbeidsgiverperioden::getBruttoUtbetalt)
                        .orElse(0.0))
                .sykepengerBegrunnelseForReduksjonEllerIkkeUtbetalt(melding.getSykepengerIArbeidsgiverPerioden()
                        .flatMap(RsSykepengerIArbeidsgiverperioden::getBegrunnelseForReduksjonEllerIkkeUtbetalt)
                        .orElse(null))
                .startdatoForeldrepengeperiode(melding.getStartdatoForeldrepengeperiode().orElse(null))
                .opphoerAvNaturalytelseListe(melding.getOpphoerAvNaturalytelseListe()
                        .map(list -> list.stream().map(InntektsmeldingMapper::mapNaturaytelseDetaljer).collect(Collectors.toList()))
                        .orElse(Collections.emptyList()))
                .gjenopptakelseNaturalytelseListe(melding.getGjenopptakelseNaturalytelseListe()
                        .map(list -> list.stream().map(InntektsmeldingMapper::mapNaturaytelseDetaljer).collect(Collectors.toList()))
                        .orElse(Collections.emptyList()))
                .pleiepengerPeriodeListe(melding.getPleiepengerPerioder()
                        .map(list -> list.stream().map(InntektsmeldingMapper::mapPeriode).collect(Collectors.toList()))
                        .orElse(Collections.emptyList()));
    }

    public static Inntektsmelding.InntektsmeldingBuilder map201812melding(RsInntektsmelding melding) {
        return map201809melding(melding)
                .privatArbeidsgiver(melding.getArbeidsgiverPrivat()
                        .map(InntektsmeldingMapper::mapPrivatArbeidsgiver)
                        .orElse(null));
    }

    private static Arbeidsgiver mapPrivatArbeidsgiver(RsArbeidsgiverPrivat arbeidsgiver) {
        return Arbeidsgiver.builder()
                .kontaktinformasjonNavn(arbeidsgiver.getKontaktinformasjon().getKontaktinformasjonNavn())
                .telefonnummer(arbeidsgiver.getKontaktinformasjon().getTelefonnummer())
                .virksomhetsnummer(arbeidsgiver.getArbeidsgiverFnr())
                .build();
    }

    private static Arbeidsgiver mapArbeidsgiver(RsArbeidsgiver arbeidsgiver) {
        return Arbeidsgiver.builder()
                .kontaktinformasjonNavn(arbeidsgiver.getKontaktinformasjon().getKontaktinformasjonNavn())
                .telefonnummer(arbeidsgiver.getKontaktinformasjon().getTelefonnummer())
                .virksomhetsnummer(arbeidsgiver.getVirksomhetsnummer())
                .build();
    }

    private static Arbeidsforhold mapArbeidsforhold(RsArbeidsforhold arbeidsforhold) {
        return Arbeidsforhold.builder()
                .arbeidforholdsId(arbeidsforhold.getArbeidsforholdId().orElse(null))
                .foersteFravaersdag(arbeidsforhold.getFoersteFravaersdag().orElse(null))
                .beloep(arbeidsforhold.getBeregnetInntekt().flatMap(RsInntekt::getBeloep).orElse(0.0))
                .aarsakVedEndring(arbeidsforhold.getBeregnetInntekt().flatMap(RsInntekt::getAarsakVedEndring).orElse(null))
                .avtaltFerieListe(arbeidsforhold.getAvtaltFerieListe()
                        .map(list -> list.stream().map(InntektsmeldingMapper::mapPeriode).collect(Collectors.toList()))
                        .orElse(Collections.emptyList()))
                .utsettelseAvForeldrepengerListe(arbeidsforhold.getUtsettelseAvForeldrepengerListe()
                        .map(list -> list.stream().map(InntektsmeldingMapper::mapUtsettelseAvForeldrepenger).collect(Collectors.toList()))
                        .orElse(Collections.emptyList()))
                .graderingIForeldrepengerListe(arbeidsforhold.getGraderingIForeldrepengerListe()
                        .map(list -> list.stream().map(InntektsmeldingMapper::mapGraderingIForeldrepenger).collect(Collectors.toList()))
                        .orElse(Collections.emptyList()))
                .build();
    }

    private static NaturalytelseDetaljer mapNaturaytelseDetaljer(RsNaturaYtelseDetaljer detaljer) {
        return NaturalytelseDetaljer.builder()
                .type(detaljer.getNaturaytelseType().orElse(null))
                .fom(detaljer.getFom().orElse(null))
                .beloepPrMnd(detaljer.getBeloepPrMnd().orElse(0.0))
                .build();
    }

    private static DelvisFravaer mapDelvisFravaer(RsDelvisFravaer fravaer) {
        return DelvisFravaer.builder()
                .timer(fravaer.getTimer().orElse(0.0))
                .dato(fravaer.getDato().orElse(null))
                .build();
    }

    private static EndringIRefusjon mapEndringIRefusjon(RsEndringIRefusjon endring) {
        return EndringIRefusjon.builder()
                .endringsDato(endring.getEndringsdato().orElse(null))
                .refusjonsbeloepPrMnd(endring.getRefusjonsbeloepPrMnd().orElse(0.0))
                .build();
    }

    private static GraderingIForeldrepenger mapGraderingIForeldrepenger(RsGraderingIForeldrepenger gradering) {
        return GraderingIForeldrepenger.builder()
                .periode(gradering.getPeriode().map(InntektsmeldingMapper::mapPeriode).orElse(null))
                .gradering(gradering.getArbeidstidprosent().orElse(null))
                .build();
    }

    private static UtsettelseAvForeldrepenger mapUtsettelseAvForeldrepenger(RsUtsettelseAvForeldrepenger utsettelse) {
        return UtsettelseAvForeldrepenger.builder()
                .periode(utsettelse.getPeriode().map(InntektsmeldingMapper::mapPeriode).orElse(null))
                .aarsakTilUtsettelse(utsettelse.getAarsakTilUtsettelse().orElse(null))
                .build();
    }

    private static Periode mapPeriode(RsPeriode periode) {
        return Periode.builder()
                .fom(periode.getFom().orElse(null))
                .tom(periode.getTom().orElse(null))
                .build();
    }
}
