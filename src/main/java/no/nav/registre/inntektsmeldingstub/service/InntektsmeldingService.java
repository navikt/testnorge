package no.nav.registre.inntektsmeldingstub.service;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntektsmeldingstub.service.rs.Inntekt;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    public List<Inntektsmelding> saveMeldinger(List<Inntektsmelding> inntektsmeldinger, MeldingsType type, String eier) {
        Eier opprettetAv = eierRepository.findEierByNavn(eier).orElse(null);
        if (opprettetAv == null && !inntektsmeldinger.isEmpty()) {
            opprettetAv = new Eier();
            opprettetAv.setNavn(eier);
            eierRepository.save(opprettetAv);
        }
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
            inntektsmelding.setEndringIRefusjonListe(Lists.newArrayList(endringIRefusjonRepository.saveAll(inntektsmelding.getEndringIRefusjonListe())));
            inntektsmelding.setSykepengerPerioder(Lists.newArrayList(periodeRepository.saveAll(inntektsmelding.getSykepengerPerioder())));
            inntektsmelding.setOpphoerAvNaturalytelseListe(Lists.newArrayList(naturalytelseDetaljerRepository.saveAll(inntektsmelding.getOpphoerAvNaturalytelseListe())));
            inntektsmelding.setGjenopptakelseNaturalytelseListe(Lists.newArrayList(naturalytelseDetaljerRepository.saveAll(inntektsmelding.getGjenopptakelseNaturalytelseListe())));
            inntektsmelding.setPleiepengerPeriodeListe(Lists.newArrayList(periodeRepository.saveAll(inntektsmelding.getPleiepengerPeriodeListe())));
            inntektsmelding.setOmsorgspengerFravaersPeriodeListe(Lists.newArrayList(periodeRepository.saveAll(inntektsmelding.getOmsorgspengerFravaersPeriodeListe())));
            inntektsmelding.setOmsorgspengerDelvisFravaersListe(Lists.newArrayList(delvisFravaerRepository.saveAll(inntektsmelding.getOmsorgspengerDelvisFravaersListe())));
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
    }

    public List<Inntektsmelding> save201812Melding(List<no.nav.registre.inntektsmeldingstub.service.rs.Inntektsmelding> inntektsmeldinger, Eier eier) {

        List<Inntektsmelding> lagredeMeldinger = new ArrayList<>(inntektsmeldinger.size());

        for (no.nav.registre.inntektsmeldingstub.service.rs.Inntektsmelding melding : inntektsmeldinger) {
            Inntektsmelding databaseInntektsMelding = Inntektsmelding.builder()
                    .eier(eier)
                    .ytelse(melding.getYtelse())
                    .arbeidstakerFnr(melding.getArbeidstakerFnr())
                    .aarsakTilInnsending(melding.getAarsakTilInnsending())
                    .avsendersystemNavn(melding.getAvsendersystem().getSystemnavn())
                    .avsendersystemVersjon(melding.getAvsendersystem().getSystemversjon())
                    .innsendingstidspunkt(melding.getAvsendersystem().getInnsendingstidspunkt())
                    .naerRelasjon(melding.isNaerRelasjon())

                    .privatArbeidsgiver()
                    .arbeidsgiver()
                    .arbeidsforhold()

                    .endringIRefusjonListe()
                    .refusjonsbeloepPrMnd()
                    .refusjonsopphoersdato()

                    .gjenopptakelseNaturalytelseListe()
                    .opphoerAvNaturalytelseListe()

                    .omsorgHarUtbetaltPliktigeDager()
                    .omsorgspengerDelvisFravaersListe()
                    .omsorgspengerFravaersPeriodeListe()

                    .pleiepengerPeriodeListe()

                    .startdatoForeldrepengeperiode()

                    .sykepengerBegrunnelseForReduksjonEllerIkkeUtbetalt()
                    .sykepengerBruttoUtbetalt()
                    .sykepengerPerioder()
                    .build();


        }


        return null;
    }

    public List<Inntektsmelding> save201809Melding(List<Inntektsmelding> inntektsmeldinger, Eier eier) {

        return null;
    }

    public Inntektsmelding findInntektsmelding(Long id) {
        return inntektsmeldingRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "Kunne ikke finne inntektsmeldingen"));
    }

    private Arbeidsgiver createOrFindArbeidsgiver(Arbeidsgiver arbeidsgiver) {
        Optional<Arbeidsgiver> optionalArbeidsgiver = arbeidsgiverRepository.findByVirksomhetsnummer(arbeidsgiver.getVirksomhetsnummer());
        return optionalArbeidsgiver.orElseGet(() -> arbeidsgiverRepository.save(Arbeidsgiver.builder()
                .kontaktinformasjonNavn(arbeidsgiver.getKontaktinformasjonNavn())
                .telefonnummer(arbeidsgiver.getTelefonnummer())
                .virksomhetsnummer(arbeidsgiver.getVirksomhetsnummer())
                .inntektsmeldinger(Collections.emptyList())
                .build())
        );
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
