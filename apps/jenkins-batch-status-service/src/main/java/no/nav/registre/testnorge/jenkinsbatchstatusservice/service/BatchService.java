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
        var jobNumber = jenkinsConsumer.getJobNumber(itemId);
        var retryConfig = new RetryConfig.Builder()
                .setRetryAttempts(10)
                .setSleepSeconds(60)
                .build();
        retryService.execute(retryConfig, () -> {
            var log = jenkinsConsumer.getJobLog(jobNumber);
            var jobId = findIDFromLog(log);
            organisasjonBestillingConsumer.registerBestilling(uuid, miljo, jobId);
        });
    }

    private Long findIDFromLog(String value) {
        log.info("Prøver å hente ut id fra log: {}", value);

        var pattern = Pattern.compile("^\\d{5}");
        var matcher = pattern.matcher(value);

        String id = null;
        while (matcher.find()) {
            if (id == null) {
                id = matcher.group();
            } else {
                throw new RuntimeException("Fant flere en ett eksempel som matcher.");
            }
        }
        if (id == null) {
            throw new RuntimeException("Fant ikke ingen som match for id'en.");
        }
        return Long.valueOf(id);
    }


}
