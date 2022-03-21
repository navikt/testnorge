package no.nav.dolly.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsmessagingservice.TpsMessagingClient;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrdreStatus;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.mapper.BestillingPdlForvalterStatusMapper;
import no.nav.dolly.mapper.BestillingTpsMessagingStatusMapper;
import no.nav.dolly.repository.IdentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdreService {

    private static final String IKKE_FUNNET = "Testperson med ident %s ble ikke funnet";
    private final IdentRepository identRepository;
    private final PdlDataConsumer pdlDataConsumer;
    private final TpsMessagingClient tpsMessagingClient;
    private final ExecutorService dollyForkJoinPool;
    private final ObjectMapper objectMapper;

    private static RsStatusRapport getStatus(List<RsStatusRapport> status) {

        return nonNull(status) ? status.stream().findFirst().orElse(null) : null;
    }

    @Transactional(readOnly = true)
    public RsOrdreStatus sendOrdre(String ident) {

        var testident = identRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException(String.format(IKKE_FUNNET, ident)));

        var progress = BestillingProgress.builder()
                .ident(ident)
                .master(testident.getMaster())
                .build();

        List.of(CompletableFuture.supplyAsync(
                                () -> pdlDataConsumer.sendOrdre(ident, testident.isTpsf()), dollyForkJoinPool),
                        CompletableFuture.supplyAsync(
                                () -> sendTpsMessaging(ident, progress), dollyForkJoinPool))
                .forEach(future -> {
                    try {
                        var resultat = future.get(1, TimeUnit.MINUTES);
                        if (!resultat.contains("$")) {
                            progress.setPdlDataStatus(resultat);
                        }
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        log.error("Future task exception {}", e.getMessage(), e);
                        Thread.currentThread().interrupt();
                        throw new ResponseStatusException(INTERNAL_SERVER_ERROR, e.getMessage(), e);
                    }
                });

        return RsOrdreStatus.builder()
                .status(Stream.of(getStatus(BestillingPdlForvalterStatusMapper.buildPdlForvalterStatusMap(List.of(progress), objectMapper)),
                                getStatus(BestillingTpsMessagingStatusMapper.buildTpsMessagingStatusMap(List.of(progress))))
                        .filter(Objects::nonNull)
                        .toList())
                .build();
    }

    private String sendTpsMessaging(String ident, BestillingProgress progress) {

        tpsMessagingClient.gjenopprett(new RsDollyUtvidetBestilling(),
                DollyPerson.builder()
                        .hovedperson(ident)
                        .build(),
                progress, false);

        return progress.getTpsMessagingStatus();
    }
}
