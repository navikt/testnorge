package no.nav.testnav.joarkdokumentservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.joarkdokumentservice.consumer.SafConsumer;
import no.nav.testnav.joarkdokumentservice.consumer.dto.JournalpostDTO;
import no.nav.testnav.joarkdokumentservice.domain.DokumentType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DokumentService {
    private final SafConsumer safConsumer;

    public Mono<JournalpostDTO> getJournalpost(String journalpostId, String miljo) {
        return safConsumer.getJournalpost(journalpostId, miljo);
    }

    public Mono<String> getDokument(String journalpostId, String dokumentInfoId, DokumentType dokumentType, String miljo) {
        return safConsumer.getDokument(journalpostId, dokumentInfoId, dokumentType, miljo);
    }

    public Mono<byte[]> getPDF(String journalpostId, String dokumentInfoId, String miljo) {
        return safConsumer.getPDF(journalpostId, dokumentInfoId, miljo);
    }
}
