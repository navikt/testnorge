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
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import no.nav.registre.inntektsmeldingstub.MeldingsType;
import no.nav.registre.inntektsmeldingstub.database.model.Arbeidsforhold;
import no.nav.registre.inntektsmeldingstub.database.model.Arbeidsgiver;
import no.nav.registre.inntektsmeldingstub.database.model.Eier;
import no.nav.registre.inntektsmeldingstub.database.model.Inntektsmelding;
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
                .map(melding -> mappeMetode.apply(melding).build())
                .collect(Collectors.toList());

        Eier eier = eierRepository.findEierByNavn(eierNavn).orElse(
                eierRepository.save(Eier.builder().navn(eierNavn).inntektsmeldinger(Collections.emptyList()).build()));

        return nyeMeldinger.stream()
                .map(m -> {
                    eier.addInntektsmelding(m);
                    return lagreMelding(m);
                }).collect(Collectors.toList());
    }

    private Inntektsmelding lagreMelding(Inntektsmelding melding) {
        melding.getArbeidsgiver().ifPresent(arbeidsgiver -> arbeidsgiver.addInntektsmelding(melding));
        Arbeidsforhold arbeidsforhold = melding.getArbeidsforhold();

        return inntektsmeldingRepository.save(melding);
    }

    public Inntektsmelding findInntektsmelding(Long id) {
        return inntektsmeldingRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "Kunne ikke finne inntektsmeldingen"));
    }
}
