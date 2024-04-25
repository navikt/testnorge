package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.domain.dto.TestidentDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final List<ClientRegister> clientRegister;
    private final PdlDataConsumer pdlDataConsumer;

    @Async
    @Transactional
    public void recyclePersoner(List<TestidentDTO> testidenter) {

        pdlDataConsumer.slettPdl(testidenter.stream()
                        .map(TestidentDTO::getIdent)
                        .toList())
                .subscribe(response -> log.info("Slettet antall {} identer mot PDL-forvalter", testidenter.size()));

        releaseArtifacts(testidenter);
    }

    private void releaseArtifacts(List<TestidentDTO> identer) {

        clientRegister.forEach(register -> {
            try {
                register.release(identer.stream()
                        .map(TestidentDTO::getIdent)
                        .toList());

            } catch (RuntimeException e) {
                log.error("Feilet å slette fra register, {}", e.getMessage(), e);
            }
        });
    }
}