package no.nav.identpool.fasit;

import static no.nav.freg.fasit.utils.domain.ResourceType.QUEUE_MANAGER;
import static no.nav.freg.fasit.utils.domain.Zone.FSS;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import no.nav.freg.fasit.utils.FasitService;
import no.nav.freg.fasit.utils.domain.QueueManager;

@Component
@Profile("fasit")
@RequiredArgsConstructor
public class FasitClient {

    @Value("${application.name}")
    private String applicationName;

    @Value("${application.environment}")
    private String environ;

    private final FasitService fasitService;

    public Map<String, Object> resolveFasitProperties() {

        Map<String, Object> properties = new HashMap<>();
        QueueManager queueManager = fasitService.find("mqGateway", QUEUE_MANAGER, environ, applicationName, FSS, QueueManager.class);
        properties.put("MQGATEWAY_NAME", queueManager.getName());
        properties.put("MQGATEWAY_HOSTNAME", queueManager.getHostname());
        properties.put("MQGATEWAY_PORT", queueManager.getPort());
        return properties;
    }
}