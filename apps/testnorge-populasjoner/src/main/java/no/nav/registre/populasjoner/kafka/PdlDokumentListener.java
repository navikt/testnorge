package no.nav.registre.populasjoner.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.populasjoner.kafka.folkeregisterperson.Folkeregisteridentifikator;
import no.nav.registre.populasjoner.kafka.folkeregisterperson.PdlDokument;
import no.nav.registre.populasjoner.service.IdentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PdlDokumentListener {
    private final IdentService identService;

    @KafkaListener(topics = "aapen-person-pdl-dokument-v1")
    public void register(@Payload List<PdlDokument> liste) {
        Set<String> identer = liste
                .stream()
                .filter(Objects::nonNull)
                .map(PdlDokument::getFolkeregisteridentifikator)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(Folkeregisteridentifikator::getIdentifikasjonsnummer)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (identer.isEmpty()) {
            log.info("Fant ingen nye identer fra Tenor");
        } else {
            log.info("Fant {} nye identer fra Tenor", identer.size());
        }
        identer.forEach(identService::saveIdent);
    }
}
