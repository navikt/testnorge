package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.SyntArbeidsforholdConsumer;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding.Arbeidsforhold;

@Service
@RequiredArgsConstructor
public class ArbeidsforholdHistorikkService {
    private final SyntArbeidsforholdConsumer syntArbeidsforholdConsumer;

    public Mono<Arbeidsforhold> genererStart(LocalDate startdato, String virksomhetsnummer, String opplysningspliktig) {
        var response = syntArbeidsforholdConsumer.genererStartArbeidsforhold(startdato);
        return response.map(item -> new Arbeidsforhold(item, UUID.randomUUID().toString(), virksomhetsnummer, opplysningspliktig));
    }

    public Mono<List<Arbeidsforhold>> genererHistorikk(Arbeidsforhold previous, LocalDate kaldermnd, int antall) {
        var responseList = syntArbeidsforholdConsumer.genererArbeidsforholdHistorikk(previous.toSynt(antall, kaldermnd));
        return responseList.map(list -> list
                .stream()
                .map(value -> new Arbeidsforhold(
                        value,
                        previous.getId(),
                        previous.getVirksomhetsnummer(),
                        previous.getOpplysningspliktig()
                ))
                .collect(Collectors.toList())
        );
    }
}
