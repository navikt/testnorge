package no.nav.registre.testnorge.jenkinsbatchstatusservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.JenkinsConsumer;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.retry.RetryConfig;

@Service
@RequiredArgsConstructor
public class EregBatchService {
    private final JenkinsConsumer consumer;
    private final RetryService retryService;

    public void register(Long itemId) {
        var jobNumber = consumer.getJobNumber(itemId);
        var retryConfig = new RetryConfig.Builder()
                .setRetryAttempts(10)
                .setSleepSeconds(60)
                .build();
        retryService.execute(retryConfig, () -> {

        });
    }

}
