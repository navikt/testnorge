package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.OppsummeringsdokumentConsumer;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.Timeline;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding.Arbeidsforhold;

@Service
@RequiredArgsConstructor
public class ArbeidsforholdSerivce {
    private final OppsummeringsdokumentConsumer oppsummeringsdokumentConsumer;

    public Mono<Timeline<Arbeidsforhold>> findTimelineFor(String ident, String miljo) {
        var dokumenter = oppsummeringsdokumentConsumer.getAllForIdent(ident, miljo);
        return dokumenter.map(items -> new Timeline<>(map(ident, items)));
    }

    private Map<LocalDate, Arbeidsforhold> map(String ident, java.util.List<no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO> items) {
        return items.stream().flatMap(oppsummeringsdokument -> oppsummeringsdokument.getVirksomheter()
                .stream()
                .flatMap(virksomhet -> virksomhet.getPersoner()
                        .stream()
                        .filter(value -> value.getIdent().equals(ident))
                        .flatMap(person -> person.getArbeidsforhold()
                                .stream()
                                .map(arbeidsforhold ->
                                        Map.entry(
                                                oppsummeringsdokument.getKalendermaaned(),
                                                new Arbeidsforhold(
                                                        arbeidsforhold,
                                                        virksomhet.getOrganisajonsnummer(),
                                                        oppsummeringsdokument.getOpplysningspliktigOrganisajonsnummer()
                                                )
                                        )
                                )
                        )
                ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
