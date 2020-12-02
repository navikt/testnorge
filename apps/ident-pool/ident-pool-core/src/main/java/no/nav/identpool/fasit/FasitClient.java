package no.nav.identpool.fasit;

import static no.nav.identpool.fasit.ResourceType.QUEUE_MANAGER;
import static no.nav.identpool.fasit.Zone.FSS;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FasitClient {

    private final FasitService fasitReadService;

    @Value("${application.name}")
    private String applicationName;

    public QueueManager getQueueManager(String env) {
        return fasitReadService.find("mqGateway", QUEUE_MANAGER, env, applicationName, FSS, QueueManager.class);
    }

    public List<String> getAllEnvironments(String... environments) {
        return Arrays.stream(environments)
                .map(fasitReadService::findEnvironmentNames)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}