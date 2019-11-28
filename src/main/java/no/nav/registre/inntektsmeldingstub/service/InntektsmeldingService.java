package no.nav.registre.inntektsmeldingstub.service;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntektsmeldingstub.database.model.Periode;
import no.nav.registre.inntektsmeldingstub.service.rs.RsInntektsmelding;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

        if (inntektsmeldinger.isEmpty()) {
            return Collections.emptyList();
        }

        switch(type) {
            case TYPE_2018_12:
                return lagreInntektsmelding(inntektsmeldinger, eier, InntektsmeldingMapper::map201812melding);
            case TYPE_2018_09:
                return lagreInntektsmelding(inntektsmeldinger, eier, InntektsmeldingMapper::map201809melding);
            default:
                log.warn("Prøvde å hente ugyldig meldingstype. Returenerer \'null\'");
                return null;
        }
    }

    private List<Inntektsmelding> lagreInntektsmelding(List<RsInntektsmelding> inntektsmeldinger, String eierNavn, Function<RsInntektsmelding, Inntektsmelding.InntektsmeldingBuilder> mappeMetode) {

        List<Inntektsmelding> nyeMeldinger = inntektsmeldinger.stream()
                .map(melding -> mappeMetode.apply(melding).build()).collect(Collectors.toList());

        List<Inntektsmelding> lagredeMeldinger = nyeMeldinger.stream()
                .map(m -> updateInntektsmeldingWithFoundOrCreatedDatabaseObjects(m, eierNavn)).collect(Collectors.toList());
        inntektsmeldingRepository.saveAll(lagredeMeldinger);

        return new ArrayList<>();
    }

    private Inntektsmelding updateInntektsmeldingWithFoundOrCreatedDatabaseObjects(Inntektsmelding inntektsmelding, String eierNavn) {
        inntektsmelding.setEier(eierRepository.findEierByNavn(eierNavn)
                .orElse(eierRepository.save(Eier.builder()
                        .navn(eierNavn)
                        .inntektsmeldinger(new ArrayList<>()).build())));

        inntektsmelding.setEndringIRefusjonListe(             Lists.newArrayList(endringIRefusjonRepository.saveAll(      inntektsmelding.getEndringIRefusjonListe())));
        inntektsmelding.setSykepengerPerioder(                Lists.newArrayList(periodeRepository.saveAll(               inntektsmelding.getSykepengerPerioder())));
        inntektsmelding.setOpphoerAvNaturalytelseListe(       Lists.newArrayList(naturalytelseDetaljerRepository.saveAll( inntektsmelding.getOpphoerAvNaturalytelseListe())));
        inntektsmelding.setGjenopptakelseNaturalytelseListe(  Lists.newArrayList(naturalytelseDetaljerRepository.saveAll( inntektsmelding.getGjenopptakelseNaturalytelseListe())));
        inntektsmelding.setPleiepengerPeriodeListe(           Lists.newArrayList(periodeRepository.saveAll(               inntektsmelding.getPleiepengerPeriodeListe())));
        inntektsmelding.setOmsorgspengerFravaersPeriodeListe( Lists.newArrayList(periodeRepository.saveAll(               inntektsmelding.getOmsorgspengerFravaersPeriodeListe())));
        inntektsmelding.setOmsorgspengerDelvisFravaersListe(  Lists.newArrayList(delvisFravaerRepository.saveAll(         inntektsmelding.getOmsorgspengerDelvisFravaersListe())));

        if (inntektsmelding.getArbeidsgiver().isPresent()) {
            inntektsmelding.setArbeidsgiver(arbeidsgiverRepository.findByVirksomhetsnummer(inntektsmelding.getArbeidsgiver().get().getVirksomhetsnummer())
                    .orElse(arbeidsgiverRepository.save(inntektsmelding.getArbeidsgiver().get())));
        }
        if (inntektsmelding.getPrivatArbeidsgiver().isPresent()) {
            inntektsmelding.setPrivatArbeidsgiver(arbeidsgiverRepository.findByVirksomhetsnummer(inntektsmelding.getArbeidsgiver().get().getVirksomhetsnummer())
                    .orElse(arbeidsgiverRepository.save(inntektsmelding.getPrivatArbeidsgiver().get())));
        }

        inntektsmelding.setArbeidsforhold(updateArbeidsforholdAndSaveDatabaseObjects(
                arbeidsforholdRepository.findByArbeidforholdsId(inntektsmelding.getArbeidsforhold().getArbeidforholdsId())
                .orElse(inntektsmelding.getArbeidsforhold()), inntektsmelding.getArbeidsforhold()));

        Inntektsmelding lagretMelding = inntektsmeldingRepository.save(inntektsmelding);

        List<Inntektsmelding> gamleInntektsmeldinger = Lists.newArrayList(lagretMelding.getEier().getInntektsmeldinger());
        gamleInntektsmeldinger.add(lagretMelding);
        lagretMelding.getEier().setInntektsmeldinger(gamleInntektsmeldinger);
        eierRepository.save(lagretMelding.getEier());

        if (lagretMelding.getArbeidsgiver().isPresent()) {
            List<Inntektsmelding> arbeidsgiverMeldinger = Lists.newArrayList(lagretMelding.getArbeidsgiver().get().getInntektsmeldinger());
            arbeidsgiverMeldinger.add(lagretMelding);
            lagretMelding.getArbeidsgiver().get().setInntektsmeldinger(arbeidsgiverMeldinger);
            arbeidsgiverRepository.save(lagretMelding.getArbeidsgiver().get());
        }
        if (lagretMelding.getPrivatArbeidsgiver().isPresent()) {
            List<Inntektsmelding> arbeidsgiverMeldinger = Lists.newArrayList(lagretMelding.getPrivatArbeidsgiver().get().getInntektsmeldinger());
            arbeidsgiverMeldinger.add(lagretMelding);
            lagretMelding.getPrivatArbeidsgiver().get().setInntektsmeldinger(arbeidsgiverMeldinger);
            arbeidsgiverRepository.save(lagretMelding.getPrivatArbeidsgiver().get());
        }

        return lagretMelding;
    }

    private Arbeidsforhold updateArbeidsforholdAndSaveDatabaseObjects(Arbeidsforhold arbeidsforhold, Arbeidsforhold nyttArbeidsforhold) {
        nyttArbeidsforhold.getGraderingIForeldrepengerListe().forEach(g -> g.setPeriode(periodeRepository.save(g.getPeriode())));
        nyttArbeidsforhold.getUtsettelseAvForeldrepengerListe().forEach(u -> u.setPeriode(periodeRepository.save(u.getPeriode())));
        nyttArbeidsforhold.getAvtaltFerieListe().forEach(u -> u = periodeRepository.save(u));

        List<GraderingIForeldrepenger> gamleGraderinger = Lists.newArrayList(arbeidsforhold.getGraderingIForeldrepengerListe());
        List<UtsettelseAvForeldrepenger> gamleUtsettelser = Lists.newArrayList(arbeidsforhold.getUtsettelseAvForeldrepengerListe());
        List<Periode> gammelFerieListe = Lists.newArrayList(arbeidsforhold.getAvtaltFerieListe());

        if (arbeidsforhold != nyttArbeidsforhold) {
            gamleGraderinger.addAll(nyttArbeidsforhold.getGraderingIForeldrepengerListe());
            gamleUtsettelser.addAll(nyttArbeidsforhold.getUtsettelseAvForeldrepengerListe());
            gammelFerieListe.addAll(nyttArbeidsforhold.getAvtaltFerieListe());
        }

        arbeidsforhold.setGraderingIForeldrepengerListe(Lists.newArrayList(graderingIForeldrePengerRepository.saveAll(gamleGraderinger)));
        arbeidsforhold.setUtsettelseAvForeldrepengerListe(Lists.newArrayList(utsettelseAvForeldrepengerRepository.saveAll(gamleUtsettelser)));
        arbeidsforhold.setAvtaltFerieListe(gammelFerieListe);

        return arbeidsforholdRepository.save(arbeidsforhold);
    }

    private void updateAndSaveArbeidsgiver(Arbeidsgiver arbeidsgiver, Inntektsmelding inntektsmelding) {
        List<Inntektsmelding> inntektsmeldinger = Lists.newArrayList(arbeidsgiver.getInntektsmeldinger());
        inntektsmeldinger.add(inntektsmelding);
        arbeidsgiver.setInntektsmeldinger(inntektsmeldinger);
        arbeidsgiverRepository.save(arbeidsgiver);
    }

    public Inntektsmelding findInntektsmelding(Long id) {
        return inntektsmeldingRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "Kunne ikke finne inntektsmeldingen"));
    }
}
