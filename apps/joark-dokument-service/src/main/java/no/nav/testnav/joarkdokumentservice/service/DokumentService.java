package no.nav.testnav.joarkdokumentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.testnav.joarkdokumentservice.consumer.SafConsumer;
import no.nav.testnav.joarkdokumentservice.domain.DokuemntType;
import no.nav.testnav.joarkdokumentservice.domain.DokumentInfo;

@Service
@RequiredArgsConstructor
public class DokumentService {
    private final SafConsumer safConsumer;

    public List<DokumentInfo> getDokumentInfoList(Integer journalpostId, String miljo) {
        return safConsumer.getDokumentInfo(journalpostId, miljo);
    }

    public String getDokument(Integer journalpostId, Integer dokumentInfoId, DokuemntType dokuemntType, String miljo) {
        return safConsumer.getDokument(journalpostId, dokumentInfoId, dokuemntType, miljo);
    }
}
