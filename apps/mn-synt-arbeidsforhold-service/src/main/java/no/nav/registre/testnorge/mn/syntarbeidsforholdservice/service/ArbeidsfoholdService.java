package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.ArbeidsforholdConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.KodeverkConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.MNOrganiasjonConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.SyntrestConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Arbeidsforhold;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Kodeverk;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Opplysningspliktig;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Organisajon;

@Service
@RequiredArgsConstructor
public class ArbeidsfoholdService {
    private final MNOrganiasjonConsumer mnOrganiasjonConsumer;
    private final ArbeidsforholdConsumer arbeidsforholdConsumer;
    private final KodeverkConsumer kodeverkConsumer;
    private final SyntrestConsumer syntrestConsumer;
    private final Random random = new Random();


    private List<Opplysningspliktig> getOpplysningspliktige() {
        List<Organisajon> organisajoner = mnOrganiasjonConsumer
                .getOrganisajoner()
                .stream()
                .filter(Organisajon::isOpplysningspliktig)
                .filter(Organisajon::isDriverVirksomheter)
                .collect(Collectors.toList());

        if (organisajoner.isEmpty()) {
            throw new RuntimeException("Fant ingen opplysningspliktige i Mini-Norge som driver virksomheter");
        }

        return organisajoner
                .stream()
                .map(organisajon -> arbeidsforholdConsumer.getOpplysningspliktig(organisajon.getOrgnummer()))
                .collect(Collectors.toList());
    }


    public Set<String> getIdenterWithArbeidsforhold() {
        List<Opplysningspliktig> opplysningspliktige = getOpplysningspliktige();
        return opplysningspliktige
                .stream()
                .map(Opplysningspliktig::getIdenter)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public void startArbeidsforhold(String ident, LocalDate kalendermaaned, boolean historikk) {
        Kodeverk yrkeKodeverk = kodeverkConsumer.getYrkeKodeverk();
        Arbeidsforhold arbeidsforhold = Arbeidsforhold.from(ident, yrkeKodeverk.getRandomKode(), kalendermaaned);
        List<Opplysningspliktig> opplysningspliktige = getOpplysningspliktige();
        Opplysningspliktig opplysningspliktig = opplysningspliktige.get(random.nextInt(opplysningspliktige.size()));
        String virksomhetsnummer = opplysningspliktig.getRandomVirksomhetsnummer();
        opplysningspliktig.addArbeidsforhold(virksomhetsnummer, arbeidsforhold);
        opplysningspliktig.setKalendermaaned(kalendermaaned);
        arbeidsforholdConsumer.sendOppsyninspliktig(opplysningspliktig);

        if (historikk) {
            synt(opplysningspliktig, arbeidsforhold, findAllDatesBetweenNowAnd(kalendermaaned), virksomhetsnummer);
        }
    }

    public void synt(Opplysningspliktig opplysningspliktig, Arbeidsforhold arbeidsforhold, Set<LocalDate> kalendermaander, String virksomhentsnummer) {
        List<Opplysningspliktig> opprettelseListe = new ArrayList<>();
        opprettelseListe.add(opplysningspliktig);
        if (kalendermaander.isEmpty()) {
            return;
        }
        Arbeidsforhold forige = arbeidsforhold;
        for (LocalDate kalendermaaned : kalendermaander) {
            Arbeidsforhold neste = syntrestConsumer.getNesteArbeidsforhold(forige, kalendermaaned);
            opplysningspliktig.addArbeidsforhold(virksomhentsnummer, neste);
            opplysningspliktig.setKalendermaaned(kalendermaaned);
            arbeidsforholdConsumer.sendOppsyninspliktig(opplysningspliktig);
            forige = neste;
        }
    }

    private Set<LocalDate> findAllDatesBetweenNowAnd(LocalDate date) {
        Set<LocalDate> dates = new HashSet<>();

        Period between = Period.between(
                date.withDayOfMonth(1),
                LocalDate.now().withDayOfMonth(1)
        );
        int months = between.getYears() * 12 + between.getMonths();
        for (int index = 1; index <= months; index++) {
            dates.add(date.withDayOfMonth(1).plusMonths(index));
        }

        return dates;
    }
}
