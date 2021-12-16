package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.SyntAmeldingConsumer;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Arbeidsforhold;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Organisajon;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdSyntService {
    private final SyntAmeldingConsumer syntAmeldingConsumer;
    private final Random random = new Random();

    public Arbeidsforhold getFirstArbeidsforhold(List<Organisajon> organisajoner, LocalDate startdato, String ident) {
        var organisajon = organisajoner.get(random.nextInt(organisajoner.size()));
        return syntAmeldingConsumer.getFirstArbeidsforhold(startdato, ident, organisajon.getRandomVirksomhetsnummer());
    }

    public List<Arbeidsforhold> getArbeidsforholdHistorikk(Arbeidsforhold arbeidsforhold, LocalDate kalendermaaned, Integer count) {
        return syntAmeldingConsumer.getArbeidsforholdHistorikk(arbeidsforhold, kalendermaaned, count, true);
    }

}
