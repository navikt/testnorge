package no.nav.testnav.apps.organisasjonbestillingservice.service.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.organisasjonbestillingservice.consumer.JenkinsConsumer;
import no.nav.testnav.apps.organisasjonbestillingservice.domain.v2.Order;
import no.nav.testnav.apps.organisasjonbestillingservice.repository.v2.OrderRepositoryV2;
import no.nav.testnav.apps.organisasjonbestillingservice.repository.v2.entity.OrderEntity;
import no.nav.testnav.libs.dto.organisajonbestilling.v2.Status;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
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

    public Mono<Order> save(Order order) {
        return Mono.fromCallable(() -> repository.save(order.toEntity()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(saved -> {
                    if (isNull(saved.getBuildId())) {
                        log.info("Finner build id for ordre {} med uuid {}...", saved.getId(), saved.getUuid());
                        jenkinsConsumer.getBuildId(order.getQueueId())
                                .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(10)))
                                .flatMap(buildId -> Mono.fromCallable(() -> {
                                    saved.setBuildId(buildId);
                                    repository.save(saved);
                                    log.info("Build id er satt for ordre {} med uuid {}.", saved.getId(), saved.getUuid());
                                    return saved;
                                }).subscribeOn(Schedulers.boundedElastic()))
                                .doOnError(e -> log.error("Klarte ikke å finne build id for ordre {} med uuid {}.", saved.getId(), saved.getUuid(), e))
                                .subscribe();
                    }
                    return Mono.just(new Order(saved));
                });
    }

    public Mono<Status> getStatus(Long id) {
        return Mono.fromCallable(() -> repository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(entityOpt -> {
                    if (entityOpt.isEmpty()) {
                        return Mono.just(Status.NOT_FOUND);
                    }

                    var order = entityOpt.get();

                    if (nonNull(order.getBatchId())) {
                        return Mono.just(Status.PENDING_COMPLETE);
                    }

                    if (isNull(order.getBuildId())) {
                        return Mono.just(Status.ADDING_TO_QUEUE);
                    }

                    return jenkinsConsumer.getBuild(order.getBuildId())
                            .flatMap(build -> {
                                if (build.getResult() != null && build.getResult().equals("FAILURE")) {
                                    return Mono.just(Status.FAILED);
                                }
                                return jenkinsConsumer.getBuildLog(order.getBuildId())
                                        .flatMap(content -> {
                                            try {
                                                var batchId = findIDFromLog(content);
                                                order.setBatchId(batchId);
                                                return Mono.fromCallable(() -> {
                                                            repository.save(order);
                                                            return Status.PENDING_COMPLETE;
                                                        })
                                                        .subscribeOn(Schedulers.boundedElastic());
                                            } catch (Exception e) {
                                                log.warn("Klarer ikke å finne batch id fra Jenkins loggen.", e);
                                                return Mono.just(Status.RUNNING);
                                            }
                                        })
                                        .switchIfEmpty(Mono.just(Status.IN_QUEUE_WAITING_TO_START));
                            })
                            .switchIfEmpty(Mono.just(Status.IN_QUEUE_WAITING_TO_START));
                });
    }

    public Mono<List<Order>> find(String uuid) {
        return Mono.fromCallable(() -> repository.findBy(uuid).stream().map(Order::new).toList())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> delete(String uuid) {
        return Mono.fromRunnable(() -> repository.deleteAllByUuid(uuid))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<List<Order>> findAll() {
        return Mono.fromCallable(() -> StreamSupport
                        .stream(repository.findAll().spliterator(), false)
                        .map(Order::new)
                        .toList())
                .subscribeOn(Schedulers.boundedElastic());
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
                throw new RuntimeException("Fant flere enn et eksempel som matcher.");
            }
        }
        if (id == null) {
            throw new RuntimeException("Fant ingen id som matchet.");
        }
        return Long.valueOf(id);
    }

}
