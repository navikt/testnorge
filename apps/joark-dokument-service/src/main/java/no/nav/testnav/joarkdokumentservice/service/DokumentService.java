package no.nav.testnav.joarkdokumentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import no.nav.testnav.joarkdokumentservice.consumer.SafConsumer;
import no.nav.testnav.joarkdokumentservice.domain.DokumentType;
import no.nav.testnav.joarkdokumentservice.domain.Journalpost;

@Service
@RequiredArgsConstructor
public class DokumentService {
    private final SafConsumer safConsumer;

    public Journalpost getJournalpost(Integer journalpostId, String miljo) {
        return safConsumer.getJournalpost(journalpostId, miljo);
    }

    public String getDokument(Integer journalpostId, Integer dokumentInfoId, DokumentType dokuemntType, String miljo) {
        return safConsumer.getDokument(journalpostId, dokumentInfoId, dokuemntType, miljo);
    }
}
