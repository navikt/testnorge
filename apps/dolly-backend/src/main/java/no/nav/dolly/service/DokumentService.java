package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.Dokument;
import no.nav.dolly.repository.DokumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DokumentService {

    private final DokumentRepository dokumentRepository;

    @Transactional(readOnly = true)
    public List<Dokument> getDokumenter(Long bestillingId) {

        return dokumentRepository.getDokumentsByBestillingId(bestillingId);
    }
}
