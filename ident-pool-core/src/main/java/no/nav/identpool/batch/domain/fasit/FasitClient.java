package no.nav.identpool.batch.domain.fasit;

import static no.nav.freg.fasit.utils.domain.ResourceType.QUEUE_MANAGER;
import static no.nav.freg.fasit.utils.domain.Zone.FSS;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import no.nav.freg.fasit.utils.FasitReadService;
import no.nav.freg.fasit.utils.domain.QueueManager;

@Component
@RequiredArgsConstructor
public class FasitClient {

    @Value("${application.name}")
    private String applicationName;

    private final FasitReadService fasitReadService;

    public QueueManager getQueueManager(String environ) {
        return fasitReadService.find("mqGateway", QUEUE_MANAGER, environ, applicationName, FSS, QueueManager.class);
    }

    public List<String> getAllEnvironments(String... environmentclasses) {
        ArrayList<String> enviroments = new ArrayList<>();
        for(String envclass: environmentclasses) {
            enviroments.addAll(fasitReadService.findEnvironmentNames(envclass));
        }
        return enviroments;
    }
}