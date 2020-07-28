package no.nav.registre.populasjoner.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.populasjoner.adapter.TenorIdenterAdapter;
import no.nav.registre.populasjoner.kafka.folkeregisterperson.Folkeregisteridentifikator;
import no.nav.registre.populasjoner.kafka.folkeregisterperson.PdlDokument;

@Slf4j
// @Component // TODO Disabled util release of Tenor
@RequiredArgsConstructor
public class PdlDokumentListener {
    private final TenorIdenterAdapter adapter;

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
        identer.forEach(adapter::saveIdent);
    }
}
