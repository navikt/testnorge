package no.nav.registre.testnorge.jenkinsbatchstatusservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.JenkinsConsumer;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.OrganisasjonBestillingConsumer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {
    private final JenkinsConsumer jenkinsConsumer;
    private final OrganisasjonBestillingConsumer organisasjonBestillingConsumer;

    public Mono<Void> registerEregBestilling(String uuid, String miljo, Long itemId) {
        log.info("Registerer ereg bestilling uuid {} og jenkins id {}.", uuid, itemId);

        return organisasjonBestillingConsumer.save(uuid)
                .flatMap(id -> jenkinsConsumer.getJobNumber(itemId)
                        .doOnNext(jobNumber -> log.info("Fant jobb nummer {} for bestilling {}", jobNumber, uuid))
                        .flatMap(jenkinsConsumer::getJobLog)
                        .map(this::findIDFromLog)
                        .flatMap(jobId -> organisasjonBestillingConsumer.update(uuid, miljo, jobId, id))
                        .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(10))
                                .doBeforeRetry(signal -> log.warn(
                                        "Operasjonen ikke utført, {} forsøk igjen.",
                                        5 - signal.totalRetries(),
                                        signal.failure())))
                )
                .doOnSuccess(v -> log.info("Ereg bestilling registrert for uuid {}.", uuid))
                .doOnError(e -> log.error("Ereg bestilling feilet for uuid {}.", uuid, e))
                .then();
    }

    private Long findIDFromLog(String value) {
        log.info("Prøver å hente ut id fra log: {}.", value);

        var pattern = Pattern.compile("(executionId: )(\\d+)", Pattern.MULTILINE);
        var matcher = pattern.matcher(value);

        String id = null;
        while (matcher.find()) {
            if (id == null) {
                id = matcher.group(2);
            } else {
                throw new RuntimeException("Fant flere enn ett eksempel som matcher.");
            }
        }
        if (id == null) {
            throw new RuntimeException("Fant ingen id som matchet.");
        }
        return Long.valueOf(id);
    }


}
