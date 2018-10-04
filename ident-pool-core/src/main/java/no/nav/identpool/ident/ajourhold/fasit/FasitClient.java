package no.nav.identpool.ident.ajourhold.fasit;

import static no.nav.freg.fasit.utils.domain.ResourceType.QUEUE_MANAGER;
import static no.nav.freg.fasit.utils.domain.Zone.FSS;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import no.nav.freg.fasit.utils.FasitService;
import no.nav.freg.fasit.utils.domain.QueueManager;

@Component
@RequiredArgsConstructor
public class FasitClient {

    @Value("${application.name}")
    private String applicationName;

    private final FasitService fasitReadService;

    public QueueManager getQueueManager(String environ) {
        return fasitReadService.find("mqGateway", QUEUE_MANAGER, environ, applicationName, FSS, QueueManager.class);
    }

    public List<String> getAllEnvironments(String... environmentclasses) {
        return Arrays.stream(environmentclasses)
                .map(fasitReadService::findEnvironmentNames)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.clean();
        flyway.setLocations("classpath:db/migration");
        flyway.migrate();
        return flyway;
    }
}