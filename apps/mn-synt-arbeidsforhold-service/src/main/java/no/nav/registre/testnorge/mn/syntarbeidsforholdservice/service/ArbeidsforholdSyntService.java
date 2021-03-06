package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.SyntrestConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Arbeidsforhold;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Organisajon;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdSyntService {
    private final SyntrestConsumer syntrestConsumer;
    private final Random random = new Random();

    public Arbeidsforhold getFirstArbeidsforhold(List<Organisajon> organisajoner, LocalDate startdato, String ident) {
        var organisajon = organisajoner.get(random.nextInt(organisajoner.size()));
        return syntrestConsumer.getFirstArbeidsforhold(startdato, ident, organisajon.getRandomVirksomhetsnummer());
    }

    public List<Arbeidsforhold> getArbeidsforholdHistorikk(Arbeidsforhold arbeidsforhold, LocalDate kalendermaaned, Integer count) {
        return syntrestConsumer.getArbeidsforholdHistorikk(arbeidsforhold, kalendermaaned, count);
    }

}
