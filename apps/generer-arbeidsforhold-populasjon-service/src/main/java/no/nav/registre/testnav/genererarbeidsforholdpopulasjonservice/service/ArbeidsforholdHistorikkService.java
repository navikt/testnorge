package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.service;

import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.SyntArbeidsforholdConsumer;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding.Arbeidsforhold;

@Server
@RequiredArgsConstructor
public class ArbeidsforholdHistorikkService {
    private final SyntArbeidsforholdConsumer syntArbeidsforholdConsumer;

    public Arbeidsforhold genererStart(LocalDate startdato, String virksomhetsnummer) {
        var response = syntArbeidsforholdConsumer.genererStartArbeidsforhold(startdato);
        return new Arbeidsforhold(response, UUID.randomUUID().toString(), virksomhetsnummer);
    }

    public List<Arbeidsforhold> genererHistorikk(Arbeidsforhold previous, LocalDate kaldermnd, int antall) {
        var list = syntArbeidsforholdConsumer.genererArbeidsforholdHistorikk(previous.toSynt(antall, kaldermnd));
        return list.stream()
                .map(value -> new Arbeidsforhold(value, previous.getId(), previous.getVirksomhetsnummer()))
                .collect(Collectors.toList());
    }
}
