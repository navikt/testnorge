package no.nav.testnav.identpool.ajourhold;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.domain.Ajourhold;
import no.nav.testnav.identpool.domain.BatchStatus;
import no.nav.testnav.identpool.repository.AjourholdRepository;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {

    private final AjourholdRepository ajourholdRepository;
    private final AjourholdService ajourholdService;

    public Mono<Ajourhold> startGeneratingIdentsBatch() {

        return updateStatus(BatchStatus.MINING_STARTED)
                .flatMapMany(ajourhold1 -> ajourholdService.checkCriticalAndGenerate())
                .collectList()
                .flatMap(list -> updateStatus(BatchStatus.MINING_COMPLETED))
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> updateStatus(BatchStatus.MINING_FAILED,
                        ExceptionUtils.getStackTrace(error).substring(0, 1023)));
    }

    public Flux<String> startGeneratingIdents() {

        return ajourholdService.checkCriticalAndGenerate()
                .onErrorResume(error -> Mono.just(error.getMessage() + ", " + error.getCause()));
    }

    public Mono<Ajourhold> updateDatabaseWithProdStatus() {

        return updateStatus(BatchStatus.CLEAN_STARTED)
                .flatMap(ajourhold -> ajourholdService.getIdentsAndCheckProd())
                .flatMap(identer -> updateStatus(BatchStatus.CLEAN_COMPLETED))
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> updateStatus(BatchStatus.CLEAN_FAILED,
                        ExceptionUtils.getStackTrace(error).substring(0, 1023)));
    }

    private Mono<Ajourhold> updateStatus(BatchStatus status) {

        return updateStatus(status, null);
    }

    private Mono<Ajourhold> updateStatus(BatchStatus status, String feilmelding) {

        return ajourholdRepository.save(Ajourhold.builder()
                .sistOppdatert(LocalDateTime.now())
                .status(status)
                .feilmelding(feilmelding)
                .build());
    }
}