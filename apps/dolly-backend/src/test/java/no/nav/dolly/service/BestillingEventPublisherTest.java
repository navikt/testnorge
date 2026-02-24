package no.nav.dolly.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.Duration;

class BestillingEventPublisherTest {

    private BestillingEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new BestillingEventPublisher();
    }

    @Test
    void shouldPublishAndReceiveEvent() {
        var subscriber = publisher.subscribe(1L);

        StepVerifier.create(subscriber.take(1))
                .then(() -> publisher.publish(1L))
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void shouldFilterEventsByBestillingId() {
        var subscriber = publisher.subscribe(1L);

        StepVerifier.create(subscriber.take(1).timeout(Duration.ofSeconds(2)))
                .then(() -> {
                    publisher.publish(2L);
                    publisher.publish(3L);
                    publisher.publish(1L);
                })
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void shouldSupportMultipleSubscribers() {
        var subscriber1 = publisher.subscribe(1L);
        var subscriber2 = publisher.subscribe(1L);

        StepVerifier.create(subscriber1.take(1).mergeWith(subscriber2.take(1)))
                .then(() -> publisher.publish(1L))
                .expectNext(1L, 1L)
                .verifyComplete();
    }

    @Test
    void shouldNotEmitForNonMatchingIds() {
        var subscriber = publisher.subscribe(1L);

        StepVerifier.create(subscriber.take(Duration.ofMillis(200)))
                .then(() -> {
                    publisher.publish(2L);
                    publisher.publish(3L);
                })
                .verifyComplete();
    }
}
