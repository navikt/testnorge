package no.nav.testnav.apps.organisasjonbestillingservice.service.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.organisasjonbestillingservice.consumer.JenkinsConsumer;
import no.nav.testnav.apps.organisasjonbestillingservice.domain.v2.Order;
import no.nav.testnav.apps.organisasjonbestillingservice.repository.v2.OrderRepositoryV2;
import no.nav.testnav.apps.organisasjonbestillingservice.repository.v2.entity.OrderEntity;
import no.nav.testnav.apps.organisasjonbestillingservice.retry.RetryConfig;
import no.nav.testnav.apps.organisasjonbestillingservice.service.RetryService;
import no.nav.testnav.libs.dto.organisajonbestilling.v2.Status;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceV2 {

    private final OrderRepositoryV2 repository;
    private final JenkinsConsumer jenkinsConsumer;
    private final RetryService retryService;

    public Order save(Order order) {
        OrderEntity saved = repository.save(order.toEntity());

        if (saved.getBuildId() == null) {
            log.info("Finner build id for ordre {} med uuid {}...", saved.getId(), saved.getUuid());

            var retryConfig = new RetryConfig.Builder()
                    .setRetryAttempts(5)
                    .setSleepSeconds(10)
                    .build();
            retryService.execute(retryConfig, () -> {
                var buildId = jenkinsConsumer.getBuildId(order.getQueueId()).block();
                saved.setBuildId(buildId);
                repository.save(saved);
                log.info("Build id er satt for ordre {} med uuid {}.", saved.getId(), saved.getUuid());
            });
        }
        return new Order(saved);
    }

    public Status getStatus(Long id) {
        var entity = repository.findById(id);

        if (entity.isEmpty()) {
            return Status.NOT_FOUND;
        }

        var order = entity.get();

        if (nonNull(order.getBatchId())) {

            return Status.PENDING_COMPLETE;
        }

        if (isNull(order.getBuildId())) {
            return Status.ADDING_TO_QUEUE;
        }

        var build = jenkinsConsumer.getBuild(order.getBuildId()).blockOptional();

        if (build.isEmpty()) {
            return Status.IN_QUEUE_WAITING_TO_START;
        }

        if (build.get().getResult() != null && build.get().getResult().equals("FAILURE")) {
            return Status.FAILED;
        }

        var content = jenkinsConsumer.getBuildLog(order.getBuildId()).blockOptional();

        if (content.isEmpty()) {
            return Status.IN_QUEUE_WAITING_TO_START;
        }

        try {
            var batchId = findIDFromLog(content.get());
            order.setBatchId(batchId);
        } catch (Exception e) {
            log.warn("Klarer ikke å finne batch id fra Jenkins loggen.", e);
            return Status.RUNNING;
        }
        repository.save(order);
        return Status.PENDING_COMPLETE;
    }

    public List<Order> find(String uuid) {
        return repository.findBy(uuid).stream().map(Order::new)
                .toList();
    }

    public void delete(String uuid) {
        repository.deleteAllByUuid(uuid);
    }

    public List<Order> findAll() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .map(Order::new)
                .toList();
    }

//    private Status getStatusFromBatchId(Order order) {
//        var statusKode = eregBatchStatusConsumer.getStatusKode(order);
//        return Status.from(statusKode);
//    }

    private Long findIDFromLog(String value) {
        log.info("Prøver å hente ut id fra log: {}.", value);

        var pattern = Pattern.compile("(executionId: )(\\d+)", Pattern.MULTILINE);
        var matcher = pattern.matcher(value);

        String id = null;
        while (matcher.find()) {
            if (id == null) {
                id = matcher.group(2);
            } else {
                throw new RuntimeException("Fant flere enn et eksempel som matcher.");
            }
        }
        if (id == null) {
            throw new RuntimeException("Fant ingen id som matchet.");
        }
        return Long.valueOf(id);
    }

}
