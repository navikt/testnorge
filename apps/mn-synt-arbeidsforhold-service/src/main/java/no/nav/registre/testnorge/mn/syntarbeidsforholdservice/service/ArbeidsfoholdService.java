package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.ArbeidsforholdConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.KodeverkConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.MNOrganiasjonConsumer;
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

    public void startArbeidsforhold(String ident) {
        Kodeverk yrkeKodeverk = kodeverkConsumer.getYrkeKodeverk();
        Arbeidsforhold arbeidsforhold = Arbeidsforhold.from(ident, yrkeKodeverk.getRandomKode());
        List<Opplysningspliktig> opplysningspliktige = getOpplysningspliktige();
        Opplysningspliktig opplysningspliktig = opplysningspliktige.get(random.nextInt(opplysningspliktige.size()));

        opplysningspliktig.addArbeidsforhold(arbeidsforhold);
        opplysningspliktig.setKalendermaaned(LocalDate.now());

        arbeidsforholdConsumer.sendOppsyninspliktig(opplysningspliktig);
    }

    public void synt(LocalDate kalendermaaned) {

    }


}
