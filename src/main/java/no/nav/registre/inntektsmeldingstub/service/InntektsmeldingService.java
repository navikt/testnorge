package no.nav.registre.inntektsmeldingstub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntektsmeldingstub.service.rs.RsInntektsmelding;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.nav.registre.inntektsmeldingstub.MeldingsType;
import no.nav.registre.inntektsmeldingstub.database.model.Arbeidsgiver;
import no.nav.registre.inntektsmeldingstub.database.model.Eier;
import no.nav.registre.inntektsmeldingstub.database.model.Inntektsmelding;
import no.nav.registre.inntektsmeldingstub.database.repository.InntektsmeldingRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.ArbeidsgiverRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.EierRepository;

import javax.transaction.Transactional;
//import no.nav.registre.inntektsmeldingstub.database.repository.ArbeidsforholdRepository;
//import no.nav.registre.inntektsmeldingstub.database.repository.DelvisFravaerRepository;
//import no.nav.registre.inntektsmeldingstub.database.repository.GraderingIForeldrePengerRepository;
//import no.nav.registre.inntektsmeldingstub.database.repository.NaturalytelseDetaljerRepository;
//import no.nav.registre.inntektsmeldingstub.database.repository.PeriodeRepository;
//import no.nav.registre.inntektsmeldingstub.database.repository.EndringIRefusjonRepository;
//import no.nav.registre.inntektsmeldingstub.database.repository.UtsettelseAvForeldrepengerRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektsmeldingService {

    private final ArbeidsgiverRepository arbeidsgiverRepository;
    private final InntektsmeldingRepository inntektsmeldingRepository;
    private final EierRepository eierRepository;
//    private final NaturalytelseDetaljerRepository naturalytelseDetaljerRepository;
//    private final PeriodeRepository periodeRepository;
//    private final EndringIRefusjonRepository endringIRefusjonRepository;
//    private final UtsettelseAvForeldrepengerRepository utsettelseAvForeldrepengerRepository;
//    private final DelvisFravaerRepository delvisFravaerRepository;
//    private final GraderingIForeldrePengerRepository graderingIForeldrePengerRepository;
//    private final ArbeidsforholdRepository arbeidsforholdRepository;


    @Transactional
    public List<Inntektsmelding> saveMeldinger(List<RsInntektsmelding> inntektsmeldinger, MeldingsType type, String eier) {

        if (inntektsmeldinger.isEmpty()) {
            return Collections.emptyList();
        }

        switch(type) {
            case TYPE_2018_12:
                return lagreInntektsmelding(inntektsmeldinger, eier, RestToDatabaseModelMapper::map201812melding);
            case TYPE_2018_09:
                return lagreInntektsmelding(inntektsmeldinger, eier, RestToDatabaseModelMapper::map201809melding);
            default:
                log.warn("Prøvde å hente ugyldig meldingstype. Returenerer \'null\'");
                return null;
        }
    }

    private List<Inntektsmelding> lagreInntektsmelding(List<RsInntektsmelding> inntektsmeldinger, String eierNavn, Function<RsInntektsmelding, Inntektsmelding.InntektsmeldingBuilder> mappeMetode) {

        final Eier tmp = Eier.builder().navn(eierNavn).build();
        Eier eier = eierRepository.findEierByNavn(eierNavn).orElseGet(() -> eierRepository.save(tmp));

        List<Inntektsmelding> nyeMeldinger = inntektsmeldinger.stream()
                .map(melding -> mappeMetode.apply(melding).build())
                .collect(Collectors.toList());

        nyeMeldinger.forEach(melding -> {
            melding.setEier(eier);
            Arbeidsgiver l = lagreArbeidsgiver(melding);
            melding.setArbeidsgiver(l);
        });

        List<Inntektsmelding> lagredeMeldinger = StreamSupport
                .stream(inntektsmeldingRepository.saveAll(nyeMeldinger).spliterator(), false)
                .collect(Collectors.toList());

        List<Inntektsmelding> eierInntektsmeldinger = eier.getInntektsmeldinger();
        eierInntektsmeldinger.addAll(lagredeMeldinger);
        eier.setInntektsmeldinger(eierInntektsmeldinger);
        eierRepository.save(eier);

        lagredeMeldinger.forEach(melding -> {
            Optional<Arbeidsgiver> l = getArbeidsgiver(melding);
            if (l.isPresent()) {
                List<Inntektsmelding> arbeidsgiverInntektsmeldinger = l.get().getInntektsmeldinger();
                arbeidsgiverInntektsmeldinger.add(melding);
                l.get().setInntektsmeldinger(arbeidsgiverInntektsmeldinger);
                arbeidsgiverRepository.save(l.get());
            }
        });

        return lagredeMeldinger;
    }

    private Optional<Arbeidsgiver> getArbeidsgiver(Inntektsmelding melding) {
        if (melding.getArbeidsgiver().isPresent()) {
            return melding.getArbeidsgiver();
        } else if (melding.getPrivatArbeidsgiver().isPresent()) {
            return melding.getPrivatArbeidsgiver();
        }
        return Optional.empty();
    }

    private Arbeidsgiver lagreArbeidsgiver(Inntektsmelding melding) {

        Optional<Arbeidsgiver> innsendtArbeidsgiver = getArbeidsgiver(melding);
        if (innsendtArbeidsgiver.isPresent()) {
            final Arbeidsgiver temp = innsendtArbeidsgiver.get();

            return arbeidsgiverRepository
                    .findByVirksomhetsnummer(innsendtArbeidsgiver.get().getVirksomhetsnummer())
                    .orElseGet(() -> arbeidsgiverRepository.save(temp));
        }
        return null;
    }

    public Inntektsmelding findInntektsmelding(Long id) {
        return inntektsmeldingRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "Kunne ikke finne inntektsmeldingen"));
    }
}
