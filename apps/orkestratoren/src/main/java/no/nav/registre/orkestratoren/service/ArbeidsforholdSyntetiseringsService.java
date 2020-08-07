package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.orkestratoren.consumer.SyntArbeidsforholdConsumer;
import no.nav.registre.orkestratoren.consumer.dto.PersonDTO;
import no.nav.registre.testnorge.libs.reporting.Reporting;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdSyntetiseringsService {

    private final SyntArbeidsforholdConsumer syntArbeidsforholdConsumer;

    public void syntentiser(Set<PersonDTO> personer, Reporting reporting) {
        log.info("Oppretter arbeidsforhold for {} personer", personer.size());
        reporting.info("Oppretter arbeidsforhold for {} personer", personer.size());

        var futures = personer
                .stream()
                .map(value -> syntArbeidsforholdConsumer.createArbeidsforhold(value.getIdent(), value.getFoedselsdato().toLocalDate()))
                .collect(Collectors.toList());

        int count = 0;
        for (var future : futures) {
            try {
                future.get();
                count++;
            } catch (Exception e) {
                log.error("klarer ikke å opprette arbeidsforhold", e);
                reporting.error("klarer ikke å opprette arbeidsforhold: {}", e.getMessage());
            }
        }

        if (count < personer.size()) {
            log.warn("Klarte ikke å opprette alle arbeidsforhold {}/{}.", count, personer.size());
        }

        if (count == personer.size()) {
            log.info("Alle {} arbeidsforhold opprettet.", count);
        }
    }
}
