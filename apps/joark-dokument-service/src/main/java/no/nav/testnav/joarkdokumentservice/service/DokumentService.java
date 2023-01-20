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

    public Journalpost getJournalpost(String journalpostId, String miljo) {
        return safConsumer.getJournalpost(journalpostId, miljo);
    }

    public String getDokument(String journalpostId, String dokumentInfoId, DokumentType dokumentType, String miljo) {
        return safConsumer.getDokument(journalpostId, dokumentInfoId, dokumentType, miljo);
    }

    public byte[] getPDF(String journalpostId, String dokumentInfoId, String miljo) {
        return safConsumer.getPDF(journalpostId, dokumentInfoId, miljo);
    }
}
