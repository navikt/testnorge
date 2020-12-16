package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.config.SyntetiseringProperties;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.ArbeidsforholdConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.MNOrganisasjonConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.SyntrestConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Arbeidsforhold;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Opplysningspliktig;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Organisajon;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {
    private final MNOrganisasjonConsumer mnorganisasjonConsumer;
    private final ArbeidsforholdConsumer arbeidsforholdConsumer;
    private final SyntrestConsumer syntrestConsumer;
    private final SyntetiseringProperties syntetiseringProperties;
    private final Random random = new Random();

    private List<Organisajon> getOpplysningspliktigeorganisasjoner(String miljo) {
        List<Organisajon> organisajoner = mnorganisasjonConsumer
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
        List<Organisajon> organisajoner = getOpplysningspliktigeorganisasjoner(miljo);
        for (var organisajon : organisajoner) {
            syntHistory(organisajon, kalendermaaned, miljo);
        }
    }

    private Arbeidsforhold createArbeidsforhold(LocalDate kalendermaaned, String ident, String virksomhetsnummer) {
        return syntrestConsumer.getFirstArbeidsforhold(kalendermaaned, ident, virksomhetsnummer);
    }

    public void startArbeidsforhold(String ident, LocalDate fom, LocalDate tom, String miljo) {

        List<Organisajon> organisajoner = getOpplysningspliktigeorganisasjoner(miljo);
        Organisajon organisajon = organisajoner.get(random.nextInt(organisajoner.size()));
        String virksomhetsnummer = organisajon.getRandomVirksomhentsnummer();
        Opplysningspliktig opplysningspliktig = getOpplysningspliktig(organisajon, fom, miljo);

        Arbeidsforhold arbeidsforhold = createArbeidsforhold(fom, ident, virksomhetsnummer);
        opplysningspliktig.addArbeidsforhold(arbeidsforhold);

        arbeidsforholdConsumer.sendOpplysningspliktig(opplysningspliktig, miljo);
        syntHistory(organisajon, arbeidsforhold, miljo, findAllDatesBetween(fom, tom).iterator(), Collections.emptyList());
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

    private Optional<Opplysningspliktig> getOpplysningspliktigForMonth(Organisajon organisajon, LocalDate kalendermaaned, String miljo) {
        return arbeidsforholdConsumer.getOpplysningspliktig(
                organisajon.getOrgnummer(),
                kalendermaaned,
                miljo
        ).map(value -> {
            value.setVersion(value.getVersion() + 1L);
            value.setKalendermaaned(kalendermaaned);
            return value;
        });
    }

    private Optional<Opplysningspliktig> getOpplysningspliktigForPreviousMonth(Organisajon organisajon, LocalDate kalendermaaned, String miljo) {
        return arbeidsforholdConsumer.getOpplysningspliktig(
                organisajon.getOrgnummer(),
                kalendermaaned.minusMonths(1),
                miljo
        ).map(value -> {
            value.setVersion(1L);
            value.setKalendermaaned(kalendermaaned);
            return value;
        });
    }

    private void syntHistoryForThisMonth(Opplysningspliktig opplysningspliktig, LocalDate kalendermaaned, String miljo) {
        opplysningspliktig.getVirksomheter().forEach(
                virksomhetDTO -> virksomhetDTO.getPersoner().forEach(personDTO -> personDTO.getArbeidsforhold().forEach(
                        arbeidsforholdDTO -> {
                            if (arbeidsforholdDTO.getSluttdato() == null || !arbeidsforholdDTO.getSluttdato().equals(kalendermaaned.minusMonths(1))) {
                                Arbeidsforhold nesteArbeidsforhold = getNesteArbeidsforhold(
                                        new Arbeidsforhold(
                                                arbeidsforholdDTO,
                                                personDTO.getIdent(),
                                                virksomhetDTO.getOrganisajonsnummer()
                                        ),
                                        kalendermaaned.minusMonths(1)
                                );
                                opplysningspliktig.addArbeidsforhold(nesteArbeidsforhold);
                            }
                        })
                )
        );
        arbeidsforholdConsumer.sendOpplysningspliktig(opplysningspliktig, miljo);
    }

    private void syntHistoryForPreviousMonth(Opplysningspliktig opplysningspliktig, LocalDate kalendermaaned, String miljo) {
        opplysningspliktig.getVirksomheter().forEach(
                virksomhetDTO -> virksomhetDTO.getPersoner().forEach(
                        personDTO -> personDTO.getArbeidsforhold().forEach(
                                arbeidsforholdDTO -> {
                                    if (arbeidsforholdDTO.getSluttdato() != null && arbeidsforholdDTO.getSluttdato().equals(kalendermaaned.minusMonths(1))) {
                                        log.info("Starter nytt arbeidsforhold for {}.", personDTO.getIdent());
                                        startArbeidsforhold(personDTO.getIdent(), kalendermaaned, null, miljo);
                                    } else {
                                        Arbeidsforhold nesteArbeidsforhold = getNesteArbeidsforhold(
                                                new Arbeidsforhold(
                                                        arbeidsforholdDTO,
                                                        personDTO.getIdent(),
                                                        virksomhetDTO.getOrganisajonsnummer()
                                                ),
                                                kalendermaaned.minusMonths(1)
                                        );
                                        opplysningspliktig.addArbeidsforhold(nesteArbeidsforhold);
                                    }
                                })
                )
        );
        arbeidsforholdConsumer.sendOpplysningspliktig(opplysningspliktig, miljo);
    }

    private void syntHistory(Organisajon organisajon, LocalDate kalendermaaned, String miljo) {
        Optional<Opplysningspliktig> thisMonth = getOpplysningspliktigForMonth(organisajon, kalendermaaned, miljo);
        Optional<Opplysningspliktig> lastMonth = getOpplysningspliktigForPreviousMonth(organisajon, kalendermaaned, miljo);
        if (thisMonth.isPresent()) {
            syntHistoryForThisMonth(thisMonth.get(), kalendermaaned, miljo);
        } else if (lastMonth.isPresent()) {
            syntHistoryForPreviousMonth(lastMonth.get(), kalendermaaned, miljo);
        } else {
            log.warn("Finner ikke opplysningspliktigdokument for {}.", organisajon.getOrgnummer());
        }
    }

    private void syntHistory(
            Organisajon opplysningspliktigOrganisajon,
            Arbeidsforhold previous,
            String miljo,
            Iterator<LocalDate> kalendermaander,
            List<Arbeidsforhold> historikk
    ) {
        if (!kalendermaander.hasNext()) {
            return;
        }
        LocalDate kalendermaaned = kalendermaander.next();
        boolean newArbeidsforhold = previous.getSluttdato() != null
                && previous.getSluttdato().getMonth() == kalendermaaned.minusMonths(1).getMonth()
                && previous.getSluttdato().getYear() == kalendermaaned.minusMonths(1).getYear();

        if (previous.getSluttdato() != null) {
            log.info("Sluttdato er satt til {} og neste mnd er {}. Derfor er nytt arbeidsforhold satt til {}.",
                    previous.getSluttdato(),
                    kalendermaaned,
                    newArbeidsforhold
            );
        }
        List<Arbeidsforhold> newHistorikk = createArbeidsforholdHistorikk(newArbeidsforhold, kalendermaaned, previous, historikk);
        Arbeidsforhold next = newHistorikk.get(0);
        newHistorikk.remove(0);
        if (newArbeidsforhold) {
            List<Organisajon> opplysningspliktigeorganisasjoner = getOpplysningspliktigeorganisasjoner(miljo);
            Organisajon newOpplysningspliktigOrganisajon = opplysningspliktigeorganisasjoner.get(random.nextInt(opplysningspliktigeorganisasjoner.size()));
            String virksomhentsnummer = newOpplysningspliktigOrganisajon.getRandomVirksomhentsnummer();
            next.setVirksomhentsnummer(virksomhentsnummer);
            Opplysningspliktig opplysningspliktig = getOpplysningspliktig(newOpplysningspliktigOrganisajon, kalendermaaned, miljo);
            opplysningspliktig.addArbeidsforhold(next);
            arbeidsforholdConsumer.sendOpplysningspliktig(opplysningspliktig, miljo);
            syntHistory(newOpplysningspliktigOrganisajon, next, miljo, kalendermaander, Collections.emptyList());
        } else {
            Opplysningspliktig opplysningspliktig = getOpplysningspliktig(opplysningspliktigOrganisajon, kalendermaaned, miljo);
            opplysningspliktig.addArbeidsforhold(next);
            arbeidsforholdConsumer.sendOpplysningspliktig(opplysningspliktig, miljo);
            syntHistory(opplysningspliktigOrganisajon, next, miljo, kalendermaander, newHistorikk);
        }
    }

    private List<Arbeidsforhold> createArbeidsforholdHistorikk(boolean newArbeidsforhold, LocalDate kalendermaaned, Arbeidsforhold previous, List<Arbeidsforhold> historikk) {
        if (newArbeidsforhold) {
            log.info("Bytter jobb for person {} med tidligere arbeidsforhold {}.", previous.getIdent(), previous.getArbeidsforholdId());
            Arbeidsforhold next = syntrestConsumer.getFirstArbeidsforhold(kalendermaaned, previous.getIdent(), previous.getVirksomhentsnummer());
            log.info("Nytt arbeidsforhold id {} for person {}.", next.getArbeidsforholdId(), next.getIdent());
            return Collections.singletonList(next);
        } else if (historikk.isEmpty()) {
            return syntrestConsumer.getArbeidsforholdHistorikk(previous, kalendermaaned.minusMonths(1));
        } else {
            return historikk;
        }
    }

    private Arbeidsforhold createArbeidsforhold(boolean newArbeidsforhold, LocalDate kalendermaaned, Arbeidsforhold previous) {
        if (newArbeidsforhold) {
            log.info("Bytter job for person {} med tidligere arbeidsforhold {}.", previous.getIdent(), previous.getArbeidsforholdId());
            Arbeidsforhold next = syntrestConsumer.getFirstArbeidsforhold(kalendermaaned, previous.getIdent(), previous.getVirksomhentsnummer());
            log.info("Nytt arbeidsforhold id {} for person {}.", next.getArbeidsforholdId(), next.getIdent());
            return next;
        }
        return getNesteArbeidsforhold(previous, kalendermaaned.minusMonths(1));
    }

    private Arbeidsforhold getNesteArbeidsforhold(Arbeidsforhold arbeidsforhold, LocalDate kalendermaaned) {
        if (random.nextFloat() > syntetiseringProperties.getEndringssannsynlighet()) {
            log.info("Returnerer samme arbeidsforhold for arbeidsforholdId {}.", arbeidsforhold.getArbeidsforholdId());
            return arbeidsforhold;
        }
        return syntrestConsumer.getNesteArbeidsforhold(arbeidsforhold, kalendermaaned);
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
