package no.nav.registre.testnorge.jenkinsbatchstatusservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.JenkinsConsumer;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.OrganisasjonBestillingConsumer;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.retry.RetryConfig;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {
    private final JenkinsConsumer jenkinsConsumer;
    private final OrganisasjonBestillingConsumer organisasjonBestillingConsumer;
    private final RetryService retryService;

    public void registerEregBestilling(String uuid, String miljo, Long itemId) {
        log.info("Registerer ereg bestilling uuid {} og jenkins id {}.", uuid, itemId);

        var id = organisasjonBestillingConsumer.save(uuid);
        var retryConfig = new RetryConfig.Builder()
                .setRetryAttempts(5)
                .setSleepSeconds(10)
                .build();


        retryService.execute(retryConfig, () -> {
            var jobNumber = jenkinsConsumer.getJobNumber(itemId);
            log.info("Fant jobb nummer {} for besilling {}", jobNumber, uuid);
            var log = jenkinsConsumer.getJobLog(jobNumber);
            var jobId = findIDFromLog(log);
            organisasjonBestillingConsumer.update(uuid, miljo, jobId, id);
        });
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
