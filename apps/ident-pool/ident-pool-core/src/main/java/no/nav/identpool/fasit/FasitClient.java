package no.nav.identpool.fasit;

import static no.nav.freg.fasit.utils.domain.ResourceType.QUEUE_MANAGER;
import static no.nav.freg.fasit.utils.domain.Zone.FSS;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import no.nav.freg.fasit.utils.FasitService;
import no.nav.freg.fasit.utils.domain.QueueManager;

@Component
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