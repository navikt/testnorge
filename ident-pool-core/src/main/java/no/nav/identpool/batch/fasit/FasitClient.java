package no.nav.identpool.batch.fasit;

import static no.nav.freg.fasit.utils.domain.ResourceType.QUEUE_MANAGER;
import static no.nav.freg.fasit.utils.domain.Zone.FSS;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

import no.nav.freg.fasit.utils.FasitService;
import no.nav.freg.fasit.utils.domain.QueueManager;

@Component
@RequiredArgsConstructor
public class FasitClient {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Value("${application.name}")
    private String applicationName;

    private final FasitService fasitService;

    public QueueManager getQueueManager(String environ) {
        return fasitService.find("mqGateway", QUEUE_MANAGER, environ, applicationName, FSS, QueueManager.class);
    }
}