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


    private List<Organisajon> getOpplysningspliktigeOrganiasjoner() {
        List<Organisajon> organisajoner = mnOrganiasjonConsumer
                .getOrganisajoner()
                .stream()
                .filter(Organisajon::isOpplysningspliktig)
                .filter(Organisajon::isDriverVirksomheter)
                .collect(Collectors.toList());

        if (organisajoner.isEmpty()) {
            throw new RuntimeException("Fant ingen opplysningspliktige i Mini-Norge som driver virksomheter");
        }

        return organisajoner;
    }

    public void reportAll(LocalDate kalendermaaned) {
        List<Organisajon> organisajoner = getOpplysningspliktigeOrganiasjoner();
        for (var organisajon : organisajoner) {
            synt(organisajon, kalendermaaned);
        }
    }

    public Arbeidsforhold createArbeidsforhold(LocalDate kalendermaaned, String ident) {
        return syntrestConsumer.getFirstArbeidsforhold(kalendermaaned, ident);
    }

    public Arbeidsforhold createArbeidsforhold(LocalDate kalendermaaned, Arbeidsforhold previous) {
        if (previous.getSluttdato() != null && previous.getSluttdato() == kalendermaaned.minusMonths(1)) {
            return syntrestConsumer.getFirstArbeidsforhold(kalendermaaned, previous.getIdent());
        }
        return syntrestConsumer.getNesteArbeidsforhold(previous, kalendermaaned);
    }

    public void startArbeidsforhold(String ident, LocalDate fom, LocalDate tom) {
        Arbeidsforhold arbeidsforhold = createArbeidsforhold(fom, ident);
        List<Organisajon> organisajoner = getOpplysningspliktigeOrganiasjoner();
        Organisajon organisajon = organisajoner.get(random.nextInt(organisajoner.size()));
        String virksomhetsnummer = organisajon.getRandomVirksomhentsnummer();
        Opplysningspliktig opplysningspliktig = getOpplysningspliktig(organisajon, fom);

        opplysningspliktig.addArbeidsforhold(virksomhetsnummer, arbeidsforhold);

        arbeidsforholdConsumer.sendOpplysningspliktig(opplysningspliktig);
        synt(organisajon, arbeidsforhold, findAllDatesBetween(fom, tom), virksomhetsnummer);
    }

    private Opplysningspliktig getOpplysningspliktig(Organisajon organisajon, LocalDate kalendermaaned) {
        Optional<Opplysningspliktig> opplysningspliktig = arbeidsforholdConsumer.getOpplysningspliktig(organisajon.getOrgnummer(), kalendermaaned);
        if (opplysningspliktig.isPresent()) {
            Opplysningspliktig temp = opplysningspliktig.get();
            temp.setVersion(temp.getVersion() + 1);
            temp.setKalendermaaned(kalendermaaned);
            return temp;
        }
        return new Opplysningspliktig(organisajon, kalendermaaned);
    }

    private Optional<Opplysningspliktig> getOpplysningspliktigFor(Organisajon organisajon, LocalDate kalendermaaned) {
        Optional<Opplysningspliktig> denne = arbeidsforholdConsumer.getOpplysningspliktig(
                organisajon.getOrgnummer(),
                kalendermaaned
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
                kalendermaaned.minusMonths(1)
        );

        forige.ifPresent(value -> {
            value.setVersion(1L);
            value.setKalendermaaned(kalendermaaned);
        });

        return forige;
    }

    public void synt(Organisajon organisajon, LocalDate kalendermaaned) {
        Optional<Opplysningspliktig> opplysningspliktig = getOpplysningspliktigFor(organisajon, kalendermaaned);
        if (opplysningspliktig.isEmpty()) {
            log.warn("Finner ikke opplysningspliktig {}.", organisajon.getOrgnummer());
            return;
        }

        Opplysningspliktig next = opplysningspliktig.get();
        next.getVirksomheter().forEach(virksomhetDTO -> virksomhetDTO.getPersoner().forEach(personDTO -> personDTO.getArbeidsforhold().forEach(arbeidsforholdDTO -> {
            Arbeidsforhold nesteArbeidsforhold = syntrestConsumer.getNesteArbeidsforhold(new Arbeidsforhold(arbeidsforholdDTO, personDTO.getIdent()), kalendermaaned);
            next.addArbeidsforhold(virksomhetDTO.getOrganisajonsnummer(), nesteArbeidsforhold);
        })));
        arbeidsforholdConsumer.sendOpplysningspliktig(next);
    }


    public void synt(Organisajon organisajon, Arbeidsforhold arbeidsforhold, Set<LocalDate> kalendermaander, String virksomhentsnummer) {
        if (kalendermaander.isEmpty()) {
            return;
        }
        Arbeidsforhold forige = arbeidsforhold;
        for (LocalDate kalendermaaned : kalendermaander) {
            Opplysningspliktig opplysningspliktig = getOpplysningspliktig(organisajon, kalendermaaned);
            Arbeidsforhold neste = createArbeidsforhold(kalendermaaned, forige);
            opplysningspliktig.addArbeidsforhold(virksomhentsnummer, neste);
            arbeidsforholdConsumer.sendOpplysningspliktig(opplysningspliktig);
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
