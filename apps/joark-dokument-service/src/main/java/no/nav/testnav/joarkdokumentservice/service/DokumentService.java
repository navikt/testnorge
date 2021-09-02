package no.nav.testnav.joarkdokumentservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.joarkdokumentservice.consumer.SafConsumer;
import no.nav.testnav.joarkdokumentservice.domain.DokumentType;
import no.nav.testnav.joarkdokumentservice.domain.Journalpost;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DokumentService {
    private final SafConsumer safConsumer;

    public Journalpost getJournalpost(Integer journalpostId, String miljo) {
        return safConsumer.getJournalpost(journalpostId, miljo);
    }

    public String getDokument(Integer journalpostId, Integer dokumentInfoId, DokumentType dokumentType, String miljo) {
        return safConsumer.getDokument(journalpostId, dokumentInfoId, dokumentType, miljo);
    }

    public String getPDF(Integer journalpostId, Integer dokumentInfoId, String miljo) {
        return safConsumer.getPDF(journalpostId, dokumentInfoId, miljo);
    }
}
