package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
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

    public void startArbeidsforhold(String ident, LocalDate kalendermaaned, boolean historikk) {
        Kodeverk yrkeKodeverk = kodeverkConsumer.getYrkeKodeverk();
        Arbeidsforhold arbeidsforhold = Arbeidsforhold.from(ident, yrkeKodeverk.getRandomKode(), kalendermaaned);
        List<Organisajon> organisajoner = getOpplysningspliktigeOrganiasjoner();
        Organisajon organisajon = organisajoner.get(random.nextInt(organisajoner.size()));
        String virksomhetsnummer = organisajon.getRandomVirksomhentsnummer();
        Opplysningspliktig opplysningspliktig = getOpplysningspliktig(organisajon, kalendermaaned);

        opplysningspliktig.addArbeidsforhold(virksomhetsnummer, arbeidsforhold);

        arbeidsforholdConsumer.sendOppsyninspliktig(opplysningspliktig);

        if (historikk) {
            synt(organisajon, arbeidsforhold, findAllDatesBetweenNowAnd(kalendermaaned), virksomhetsnummer);
        }
    }

    private Opplysningspliktig getOpplysningspliktig(Organisajon organisajon, LocalDate kalendermaaned) {
        Opplysningspliktig opplysningspliktig = arbeidsforholdConsumer.getOpplysningspliktig(organisajon.getOrgnummer(), kalendermaaned);
        if (opplysningspliktig != null) {
            opplysningspliktig.setVersion(opplysningspliktig.getVersion() + 1);
            opplysningspliktig.setKalendermaaned(kalendermaaned);
            return opplysningspliktig;
        }
        return new Opplysningspliktig(organisajon, kalendermaaned);
    }


    public void synt(Organisajon organisajon, Arbeidsforhold arbeidsforhold, Set<LocalDate> kalendermaander, String virksomhentsnummer) {
        if (kalendermaander.isEmpty()) {
            return;
        }
        Arbeidsforhold forige = arbeidsforhold;
        for (LocalDate kalendermaaned : kalendermaander) {
            Opplysningspliktig opplysningspliktig = getOpplysningspliktig(organisajon, kalendermaaned);
            Arbeidsforhold neste = syntrestConsumer.getNesteArbeidsforhold(forige, kalendermaaned);
            opplysningspliktig.addArbeidsforhold(virksomhentsnummer, neste);
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
