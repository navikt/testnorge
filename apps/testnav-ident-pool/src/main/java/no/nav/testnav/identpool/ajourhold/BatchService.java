package no.nav.testnav.identpool.ajourhold;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.domain.Ajourhold;
import no.nav.testnav.identpool.domain.BatchStatus;
import no.nav.testnav.identpool.repository.AjourholdRepository;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {

    private final AjourholdRepository ajourholdRepository;
    private final AjourholdService ajourholdService;

    public Mono<Ajourhold> startGeneratingIdents(Integer yearToGenerate) {

        return updateStatus(BatchStatus.MINING_STARTED)
                .flatMap(ajourhold1 -> ajourholdService.checkCriticalAndGenerate(yearToGenerate))
                .flatMap(melding ->
                        updateStatus(BatchStatus.MINING_COMPLETED, melding, null))
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> updateStatus(BatchStatus.MINING_FAILED, null,
                        ExceptionUtils.getStackTrace(error).substring(0, 1023)));
    }

    public Mono<Ajourhold> updateDatabaseWithProdStatus(Integer yearToClean) {

        return updateStatus(BatchStatus.CLEAN_STARTED)
                .flatMap(ajourhold -> ajourholdService.getIdentsAndCheckProd(yearToClean))
                .flatMap(melding -> updateStatus(BatchStatus.CLEAN_COMPLETED, melding, null))
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> updateStatus(BatchStatus.CLEAN_FAILED, null,
                        ExceptionUtils.getStackTrace(error).substring(0, 1023)));
    }

    private Mono<Ajourhold> updateStatus(BatchStatus status) {

        return updateStatus(status, null, null);
    }

    private Mono<Ajourhold> updateStatus(BatchStatus status, String melding, String feilmelding) {

        return ajourholdRepository.save(Ajourhold.builder()
                .sistOppdatert(LocalDateTime.now())
                .status(status)
                .melding(melding)
                .feilmelding(feilmelding)
                .build());
    }
}