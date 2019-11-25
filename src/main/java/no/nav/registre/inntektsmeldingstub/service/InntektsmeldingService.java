package no.nav.registre.inntektsmeldingstub.service;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntektsmeldingstub.database.model.DelvisFravaer;
import no.nav.registre.inntektsmeldingstub.database.model.EndringIRefusjon;
import no.nav.registre.inntektsmeldingstub.database.model.NaturalytelseDetaljer;
import no.nav.registre.inntektsmeldingstub.database.model.Periode;
import no.nav.registre.inntektsmeldingstub.service.rs.RsArbeidsforhold;
import no.nav.registre.inntektsmeldingstub.service.rs.RsArbeidsgiver;
import no.nav.registre.inntektsmeldingstub.service.rs.RsArbeidsgiverPrivat;
import no.nav.registre.inntektsmeldingstub.service.rs.RsInntektsmelding;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import no.nav.registre.inntektsmeldingstub.MeldingsType;
import no.nav.registre.inntektsmeldingstub.database.model.Arbeidsforhold;
import no.nav.registre.inntektsmeldingstub.database.model.Arbeidsgiver;
import no.nav.registre.inntektsmeldingstub.database.model.Eier;
import no.nav.registre.inntektsmeldingstub.database.model.GraderingIForeldrepenger;
import no.nav.registre.inntektsmeldingstub.database.model.Inntektsmelding;
import no.nav.registre.inntektsmeldingstub.database.model.UtsettelseAvForeldrepenger;
import no.nav.registre.inntektsmeldingstub.database.repository.ArbeidsforholdRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.ArbeidsgiverRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.DelvisFravaerRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.EierRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.GraderingIForeldrePengerRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.InntektsmeldingRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.NaturalytelseDetaljerRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.PeriodeRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.EndringIRefusjonRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.UtsettelseAvForeldrepengerRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektsmeldingService {

    private final ArbeidsforholdRepository arbeidsforholdRepository;
    private final ArbeidsgiverRepository arbeidsgiverRepository;
    private final DelvisFravaerRepository delvisFravaerRepository;
    private final GraderingIForeldrePengerRepository graderingIForeldrePengerRepository;
    private final InntektsmeldingRepository inntektsmeldingRepository;
    private final NaturalytelseDetaljerRepository naturalytelseDetaljerRepository;
    private final PeriodeRepository periodeRepository;
    private final EndringIRefusjonRepository endringIRefusjonRepository;
    private final UtsettelseAvForeldrepengerRepository utsettelseAvForeldrepengerRepository;
    private final EierRepository eierRepository;

    public List<Inntektsmelding> saveMeldinger(List<RsInntektsmelding> inntektsmeldinger, MeldingsType type, String eier) {

        Eier opprettetAv = eierRepository.findEierByNavn(eier).orElse(null);
        if (opprettetAv == null && !inntektsmeldinger.isEmpty()) {
            opprettetAv = new Eier();
            opprettetAv.setNavn(eier);
            eierRepository.save(opprettetAv);
        }

        switch(type) {
            case TYPE_2018_12:
                return saveMeldinger(inntektsmeldinger, opprettetAv, this::bygg201812Inntektsmelding);
            case TYPE_2018_09:
                return saveMeldinger(inntektsmeldinger, opprettetAv, this::bygg201809Inntektsmelding);
            default:
                log.warn("Prøvde å hente ugyldig meldingstype. Returenerer \'null\'");
                return null;
        }

/*
        List<Inntektsmelding> lagredeMeldinger = new ArrayList<>(inntektsmeldinger.size());
        for (Inntektsmelding inntektsmelding : inntektsmeldinger) {

            if (inntektsmelding.getArbeidsgiver() != null) {
                inntektsmelding.setArbeidsgiver(createOrFindArbeidsgiver(inntektsmelding.getArbeidsgiver()));
            }

            if (type == MeldingsType.TYPE_2018_12) {
                if (inntektsmelding.getPrivatArbeidsgiver() != null) {
                    inntektsmelding.setPrivatArbeidsgiver(createOrFindArbeidsgiver(inntektsmelding.getPrivatArbeidsgiver()));
                }
            } else {
                inntektsmelding.setPrivatArbeidsgiver(null);
            }

            inntektsmelding.setArbeidsforhold(createOrFindArbeidsforhold(inntektsmelding.getArbeidsforhold()));
            inntektsmelding.setEndringIRefusjonListe(             Lists.newArrayList(endringIRefusjonRepository.saveAll(      inntektsmelding.getEndringIRefusjonListe())));
            inntektsmelding.setSykepengerPerioder(                Lists.newArrayList(periodeRepository.saveAll(               inntektsmelding.getSykepengerPerioder())));
            inntektsmelding.setOpphoerAvNaturalytelseListe(       Lists.newArrayList(naturalytelseDetaljerRepository.saveAll( inntektsmelding.getOpphoerAvNaturalytelseListe())));
            inntektsmelding.setGjenopptakelseNaturalytelseListe(  Lists.newArrayList(naturalytelseDetaljerRepository.saveAll( inntektsmelding.getGjenopptakelseNaturalytelseListe())));
            inntektsmelding.setPleiepengerPeriodeListe(           Lists.newArrayList(periodeRepository.saveAll(               inntektsmelding.getPleiepengerPeriodeListe())));
            inntektsmelding.setOmsorgspengerFravaersPeriodeListe( Lists.newArrayList(periodeRepository.saveAll(               inntektsmelding.getOmsorgspengerFravaersPeriodeListe())));
            inntektsmelding.setOmsorgspengerDelvisFravaersListe(  Lists.newArrayList(delvisFravaerRepository.saveAll(         inntektsmelding.getOmsorgspengerDelvisFravaersListe())));
            inntektsmelding.setEier(opprettetAv);

            Inntektsmelding lagret = inntektsmeldingRepository.save(inntektsmelding);
            List<Inntektsmelding> arbeidsgiverInntektsmeldinger = Lists.newArrayList(inntektsmeldingRepository.findAllById(
                    lagret.getArbeidsgiver().getInntektsmeldinger().stream()
                            .map(Inntektsmelding::getId)
                            .collect(Collectors.toList())
            ));

            arbeidsgiverInntektsmeldinger.add(inntektsmelding);
            inntektsmelding.getArbeidsgiver().setInntektsmeldinger(arbeidsgiverInntektsmeldinger);
            lagret.setArbeidsgiver(arbeidsgiverRepository.save(inntektsmelding.getArbeidsgiver()));
            lagredeMeldinger.add(lagret);
        }
        return lagredeMeldinger;

         */
    }

    private Inntektsmelding bygg201812Inntektsmelding(RsInntektsmelding melding) {
        Inntektsmelding basisMelding = bygg201809Inntektsmelding(melding);
        basisMelding.setPrivatArbeidsgiver(createOrFindArbeidsgiverPrivat(melding.getArbeidsgiverPrivat()));

        return basisMelding;
    }

    private Inntektsmelding bygg201809Inntektsmelding(RsInntektsmelding melding) {
        return Inntektsmelding.builder()
                .ytelse(melding.getYtelse())
                .arbeidstakerFnr(melding.getArbeidstakerFnr())
                .aarsakTilInnsending(melding.getAarsakTilInnsending())
                .avsendersystemNavn(melding.getAvsendersystem().getSystemnavn())
                .avsendersystemVersjon(melding.getAvsendersystem().getSystemversjon())
                .innsendingstidspunkt(melding.getAvsendersystem().getInnsendingstidspunkt())
                .naerRelasjon(melding.isNaerRelasjon())

                .arbeidsgiver(createOrFindArbeidsgiver(melding.getArbeidsgiver()))
                .arbeidsforhold(createOrFindArbeidsforhold(melding.getArbeidsforhold()))

                .endringIRefusjonListe(Lists.newArrayList(endringIRefusjonRepository.saveAll(
                        melding.getRefusjon().getEndringIRefusjonListe().stream().map(e -> EndringIRefusjon.builder()
                                .refusjonsbeloepPrMnd(e.getRefusjonsbeloepPrMnd())
                                .endringsDato(e.getEndringsdato())
                                .build()).collect(Collectors.toList()))))
                .refusjonsbeloepPrMnd(melding.getRefusjon().getRefusjonsbeloepPrMnd())
                .refusjonsopphoersdato(melding.getRefusjon().getRefusjonsopphoersdato())

                .gjenopptakelseNaturalytelseListe(Lists.newArrayList(naturalytelseDetaljerRepository.saveAll(
                        melding.getGjenopptakelseNaturalytelseListe().stream().map(e -> NaturalytelseDetaljer.builder()
                                .beloepPrMnd(e.getBeloepPrMnd())
                                .fom(e.getFom())
                                .type(e.getNaturaytelseType())
                                .build()).collect(Collectors.toList()))))
                .opphoerAvNaturalytelseListe(Lists.newArrayList(naturalytelseDetaljerRepository.saveAll(
                        melding.getOpphoerAvNaturalytelseListe().stream().map(e -> NaturalytelseDetaljer.builder()
                                .beloepPrMnd(e.getBeloepPrMnd())
                                .fom(e.getFom())
                                .type(e.getNaturaytelseType())
                                .build()).collect(Collectors.toList()))))

                .omsorgHarUtbetaltPliktigeDager(melding.getOmsorgspenger().isHarUtbetaltPliktigeDager())
                .omsorgspengerDelvisFravaersListe(Lists.newArrayList(delvisFravaerRepository.saveAll(
                        melding.getOmsorgspenger().getDelvisFravaersListe().stream().map(e -> DelvisFravaer.builder()
                                .dato(e.getDato()).timer(e.getTimer()).build()).collect(Collectors.toList()))))
                .omsorgspengerFravaersPeriodeListe(Lists.newArrayList(periodeRepository.saveAll(
                        melding.getOmsorgspenger().getFravaersPerioder().stream().map(e -> Periode.builder()
                                .fom(e.getFom()).tom(e.getTom()).build()).collect(Collectors.toList()))))

                .pleiepengerPeriodeListe(Lists.newArrayList(periodeRepository.saveAll(
                        melding.getPleiepengerPerioder().stream().map(e -> Periode.builder()
                                .tom(e.getTom()).fom(e.getFom()).build()).collect(Collectors.toList()))))

                .startdatoForeldrepengeperiode(melding.getStartdatoForeldrepengeperiode())

                .sykepengerBegrunnelseForReduksjonEllerIkkeUtbetalt(melding.getSykepengerIArbeidsgiverperioden().getBegrunnelseForReduksjonEllerIkkeUtbetalt())
                .sykepengerBruttoUtbetalt(melding.getSykepengerIArbeidsgiverperioden().getBruttoUtbetalt())
                .sykepengerPerioder(Lists.newArrayList(periodeRepository.saveAll(
                        melding.getSykepengerIArbeidsgiverperioden().getArbeidsgiverperiodeListe().stream().map(e -> Periode.builder()
                                .fom(e.getFom()).tom(e.getTom()).build()).collect(Collectors.toList()))))
                .build();
    }

    private List<Inntektsmelding> saveMeldinger(List<RsInntektsmelding> inntektsmeldinger, Eier eier, Function<RsInntektsmelding, Inntektsmelding> byggemetode) {
        List<Inntektsmelding> lagredeMeldinger = inntektsmeldinger.stream()
                .map(byggemetode).collect(Collectors.toList());
        lagredeMeldinger.forEach(m -> {
            m.setEier(eier);

            Inntektsmelding lagretMelding = inntektsmeldingRepository.save(m);
            List<Inntektsmelding> arbeidsgiverInntektsmeldinger = Lists.newArrayList(inntektsmeldingRepository.findAllById(
                    lagretMelding.getArbeidsgiver().getInntektsmeldinger().stream()
                    .map(Inntektsmelding::getId).collect(Collectors.toList())));

            arbeidsgiverInntektsmeldinger.add(m);
            m.getArbeidsgiver().setInntektsmeldinger(arbeidsgiverInntektsmeldinger);
            lagretMelding.setArbeidsgiver(arbeidsgiverRepository.save(m.getArbeidsgiver()));
        });

        return lagredeMeldinger;
    }

    public Inntektsmelding findInntektsmelding(Long id) {
        return inntektsmeldingRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "Kunne ikke finne inntektsmeldingen"));
    }

    private Arbeidsgiver createOrFindArbeidsgiverPrivat(RsArbeidsgiverPrivat arbeidsgiver) {
        Optional<Arbeidsgiver> optionalArbeidsgiver = arbeidsgiverRepository.findByVirksomhetsnummer(arbeidsgiver.getArbeidsgiverFnr());
        return optionalArbeidsgiver.orElseGet(() -> arbeidsgiverRepository.save(Arbeidsgiver.builder()
                .kontaktinformasjonNavn(arbeidsgiver.getKontaktinformasjon().getKontaktinformasjonNavn())
                .telefonnummer(arbeidsgiver.getKontaktinformasjon().getTelefonnummer())
                .virksomhetsnummer(arbeidsgiver.getArbeidsgiverFnr())
                .inntektsmeldinger(Collections.EMPTY_LIST)
                .build()));
    }

    private Arbeidsgiver createOrFindArbeidsgiver(RsArbeidsgiver arbeidsgiver) {
        Optional<Arbeidsgiver> optionalArbeidsgiver = arbeidsgiverRepository.findByVirksomhetsnummer(arbeidsgiver.getVirksomhetsnummer());
        return optionalArbeidsgiver.orElseGet(() -> arbeidsgiverRepository.save(Arbeidsgiver.builder()
                .kontaktinformasjonNavn(arbeidsgiver.getKontaktinformasjon().getKontaktinformasjonNavn())
                .telefonnummer(arbeidsgiver.getKontaktinformasjon().getTelefonnummer())
                .virksomhetsnummer(arbeidsgiver.getVirksomhetsnummer())
                .inntektsmeldinger(Collections.EMPTY_LIST)
                .build()));
    }

    private Arbeidsforhold createOrFindArbeidsforhold(RsArbeidsforhold arbeidsforhold) {
        Optional<Arbeidsforhold> optionalArbeidsforhold = arbeidsforholdRepository.findByArbeidforholdsId(arbeidsforhold.getArbeidsforholdId());


        return optionalArbeidsforhold.orElseGet(() -> arbeidsforholdRepository.save(Arbeidsforhold.builder()
                .aarsakVedEndring(arbeidsforhold.getBeregnetInntekt().getAarsakVedEndring())
                .beloep(arbeidsforhold.getBeregnetInntekt().getBeloep())
                .arbeidforholdsId(arbeidsforhold.getArbeidsforholdId())
                .foersteFravaersdag(arbeidsforhold.getFoersteFravaersdag())

                .graderingIForeldrepengerListe(Lists.newArrayList(graderingIForeldrePengerRepository.saveAll(
                        arbeidsforhold.getGraderingIForeldrepengerListe().stream().map(e -> GraderingIForeldrepenger.builder()
                                .gradering(e.getArbeidstidprosent())
                                .periode(periodeRepository.save(Periode.builder()
                                        .fom(e.getPeriode().getFom())
                                        .tom(e.getPeriode().getTom())
                                        .build()))
                                .build()).collect(Collectors.toList()))))

                .utsettelseAvForeldrepengerListe(Lists.newArrayList(utsettelseAvForeldrepengerRepository.saveAll(
                        arbeidsforhold.getUtsettelseAvForeldrepengerListe().stream().map(e -> UtsettelseAvForeldrepenger.builder()
                                .aarsakTilUtsettelse(e.getAarsakTilUtsettelse())
                                .periode(periodeRepository.save(Periode.builder()
                                        .fom(e.getPeriode().getFom())
                                        .tom(e.getPeriode().getTom())
                                        .build()))
                                .build()).collect(Collectors.toList()))))

                .avtaltFerieListe(Lists.newArrayList(periodeRepository.saveAll(
                        arbeidsforhold.getAvtaltFerieListe().stream().map(e -> Periode.builder()
                                .fom(e.getFom()).tom(e.getTom()).build())
                        .collect(Collectors.toList()))))
                .build()));
    }


    private Arbeidsforhold createOrFindArbeidsforhold(Arbeidsforhold arbeidsforhold) {

        Optional<Arbeidsforhold> optionalArbeidsforhold = Optional.empty();
        if (arbeidsforhold.getId() != null) {

            optionalArbeidsforhold = arbeidsforholdRepository.findById(arbeidsforhold.getId());
        }
        if (optionalArbeidsforhold.isEmpty()) {
            optionalArbeidsforhold = arbeidsforholdRepository.findByArbeidforholdsId(arbeidsforhold.getArbeidforholdsId());
        }

        if (arbeidsforhold.getGraderingIForeldrepengerListe() != null) {

            for (GraderingIForeldrepenger graderingIForeldrepenger : arbeidsforhold.getGraderingIForeldrepengerListe()) {
                graderingIForeldrepenger.setPeriode(periodeRepository.save(graderingIForeldrepenger.getPeriode()));
            }
            arbeidsforhold.setGraderingIForeldrepengerListe(Lists.newArrayList(graderingIForeldrePengerRepository.saveAll(arbeidsforhold.getGraderingIForeldrepengerListe())));
        }

        if (arbeidsforhold.getUtsettelseAvForeldrepengerListe() != null) {
            for (UtsettelseAvForeldrepenger utsettelseAvForeldrepenger : arbeidsforhold.getUtsettelseAvForeldrepengerListe()) {
                utsettelseAvForeldrepenger.setPeriode(periodeRepository.save(utsettelseAvForeldrepenger.getPeriode()));
            }
            arbeidsforhold.setUtsettelseAvForeldrepengerListe(Lists.newArrayList(utsettelseAvForeldrepengerRepository.saveAll(arbeidsforhold.getUtsettelseAvForeldrepengerListe())));
        }
        if (arbeidsforhold.getAvtaltFerieListe() != null) {
            arbeidsforhold.setAvtaltFerieListe(Lists.newArrayList(periodeRepository.saveAll(arbeidsforhold.getAvtaltFerieListe())));
        }

        return optionalArbeidsforhold.orElseGet(() -> arbeidsforholdRepository.save(Arbeidsforhold.builder()
                .aarsakVedEndring(arbeidsforhold.getAarsakVedEndring())
                .avtaltFerieListe(arbeidsforhold.getAvtaltFerieListe())
                .beloep(arbeidsforhold.getBeloep())
                .foersteFravaersdag(arbeidsforhold.getFoersteFravaersdag())
                .graderingIForeldrepengerListe(arbeidsforhold.getGraderingIForeldrepengerListe())
                .arbeidforholdsId(arbeidsforhold.getArbeidforholdsId())
                .utsettelseAvForeldrepengerListe(arbeidsforhold.getUtsettelseAvForeldrepengerListe())
                .build()
        ));
    }
}
