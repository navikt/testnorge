package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.ArbeidsforholdConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.MNOrganiasjonConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.SyntrestConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Arbeidsforhold;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Opplysningspliktig;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Organisajon;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsfoholdService {
    private final MNOrganiasjonConsumer mnOrganiasjonConsumer;
    private final ArbeidsforholdConsumer arbeidsforholdConsumer;
    private final SyntrestConsumer syntrestConsumer;
    private final Random random = new Random();


    private List<Organisajon> getOpplysningspliktigeOrganiasjoner(String miljo) {
        List<Organisajon> organisajoner = mnOrganiasjonConsumer
                .getOrganisajoner(miljo)
                .stream()
                .filter(Organisajon::isOpplysningspliktig)
                .filter(Organisajon::isDriverVirksomheter)
                .collect(Collectors.toList());

        if (organisajoner.isEmpty()) {
            throw new RuntimeException("Fant ingen opplysningspliktige i Mini-Norge som driver virksomheter");
        }

        return organisajoner;
    }

    public void reportAll(LocalDate kalendermaaned, String miljo) {
        List<Organisajon> organisajoner = getOpplysningspliktigeOrganiasjoner(miljo);
        for (var organisajon : organisajoner) {
            synt(organisajon, kalendermaaned, miljo);
        }
    }

    public Arbeidsforhold createArbeidsforhold(LocalDate kalendermaaned, String ident) {
        return syntrestConsumer.getFirstArbeidsforhold(kalendermaaned, ident);
    }

    public Arbeidsforhold createArbeidsforhold(LocalDate kalendermaaned, Arbeidsforhold previous) {
        if (previous.getSluttdato() != null && previous.getSluttdato().getMonth().equals(kalendermaaned.minusMonths(1).getMonth())) {
            log.info("Bytter job for person {} med tidligere arbeidsforhold {}.", previous.getIdent(), previous.getArbeidsforholdId());
            Arbeidsforhold next = syntrestConsumer.getFirstArbeidsforhold(kalendermaaned, previous.getIdent());
            log.info("Nytt arbeidsforhold id {} for person {}.", next.getArbeidsforholdId(), next.getIdent());
            return next;
        }
        return syntrestConsumer.getNesteArbeidsforhold(previous, kalendermaaned.minusMonths(1));
    }

    public void startArbeidsforhold(String ident, LocalDate fom, LocalDate tom, String miljo) {
        Arbeidsforhold arbeidsforhold = createArbeidsforhold(fom, ident);
        List<Organisajon> organisajoner = getOpplysningspliktigeOrganiasjoner(miljo);
        Organisajon organisajon = organisajoner.get(random.nextInt(organisajoner.size()));
        String virksomhetsnummer = organisajon.getRandomVirksomhentsnummer();
        Opplysningspliktig opplysningspliktig = getOpplysningspliktig(organisajon, fom, miljo);

        opplysningspliktig.addArbeidsforhold(virksomhetsnummer, arbeidsforhold);

        arbeidsforholdConsumer.sendOpplysningspliktig(opplysningspliktig, miljo);
        synt(organisajon, arbeidsforhold, findAllDatesBetween(fom, tom), virksomhetsnummer, miljo);
    }

    private Opplysningspliktig getOpplysningspliktig(Organisajon organisajon, LocalDate kalendermaaned, String miljo) {
        Optional<Opplysningspliktig> opplysningspliktig = arbeidsforholdConsumer.getOpplysningspliktig(organisajon.getOrgnummer(), kalendermaaned, miljo);
        if (opplysningspliktig.isPresent()) {
            Opplysningspliktig temp = opplysningspliktig.get();
            temp.setVersion(temp.getVersion() + 1);
            temp.setKalendermaaned(kalendermaaned);
            return temp;
        }
        return new Opplysningspliktig(organisajon, kalendermaaned);
    }

    private Optional<Opplysningspliktig> getOpplysningspliktigFor(Organisajon organisajon, LocalDate kalendermaaned, String miljo) {
        Optional<Opplysningspliktig> denne = arbeidsforholdConsumer.getOpplysningspliktig(
                organisajon.getOrgnummer(),
                kalendermaaned,
                miljo
        );

        denne.ifPresent(value -> {
            value.setVersion(value.getVersion() + 1L);
            value.setKalendermaaned(kalendermaaned);
        });

        if (denne.isPresent()) {
            return denne;
        }

        Optional<Opplysningspliktig> forige = arbeidsforholdConsumer.getOpplysningspliktig(
                organisajon.getOrgnummer(),
                kalendermaaned.minusMonths(1),
                miljo
        );

        forige.ifPresent(value -> {
            value.setVersion(1L);
            value.setKalendermaaned(kalendermaaned);
        });

        return forige;
    }

    public void synt(Organisajon organisajon, LocalDate kalendermaaned, String miljo) {
        Optional<Opplysningspliktig> opplysningspliktig = getOpplysningspliktigFor(organisajon, kalendermaaned, miljo);
        if (opplysningspliktig.isEmpty()) {
            log.warn("Finner ikke opplysningspliktig {}.", organisajon.getOrgnummer());
            return;
        }

        Opplysningspliktig next = opplysningspliktig.get();
        next.getVirksomheter().forEach(virksomhetDTO -> virksomhetDTO.getPersoner().forEach(personDTO -> personDTO.getArbeidsforhold().forEach(arbeidsforholdDTO -> {
            Arbeidsforhold nesteArbeidsforhold = syntrestConsumer.getNesteArbeidsforhold(new Arbeidsforhold(arbeidsforholdDTO, personDTO.getIdent()), kalendermaaned.minusMonths(1));
            next.addArbeidsforhold(virksomhetDTO.getOrganisajonsnummer(), nesteArbeidsforhold);
        })));
        arbeidsforholdConsumer.sendOpplysningspliktig(next, miljo);
    }


    public void synt(Organisajon organisajon, Arbeidsforhold arbeidsforhold, Set<LocalDate> kalendermaander, String virksomhentsnummer, String miljo) {
        if (kalendermaander.isEmpty()) {
            return;
        }
        Arbeidsforhold forige = arbeidsforhold;
        for (LocalDate kalendermaaned : kalendermaander) {
            Opplysningspliktig opplysningspliktig = getOpplysningspliktig(organisajon, kalendermaaned, miljo);
            Arbeidsforhold neste = createArbeidsforhold(kalendermaaned, forige);
            opplysningspliktig.addArbeidsforhold(virksomhentsnummer, neste);
            arbeidsforholdConsumer.sendOpplysningspliktig(opplysningspliktig, miljo);
            forige = neste;
        }
    }

    private Set<LocalDate> findAllDatesBetween(LocalDate fom, LocalDate tom) {
        Set<LocalDate> dates = new TreeSet<>();
        if (tom == null) {
            return dates;
        }
        Period between = Period.between(
                fom.withDayOfMonth(1),
                tom.withDayOfMonth(1)
        );
        int months = between.getYears() * 12 + between.getMonths();
        for (int index = 1; index <= months; index++) {
            dates.add(fom.withDayOfMonth(1).plusMonths(index));
        }
        return dates;
    }
}
